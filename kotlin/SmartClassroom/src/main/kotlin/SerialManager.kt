import jssc.SerialPort
import jssc.SerialPortException
import jssc.SerialPortList
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.*
import java.util.regex.Pattern
import kotlin.Exception

class SerialManager (val dataBus: DataBus) {
    companion object {
        const val ALLOW_PIN                     = 0b00000001
        const val PWM_PIN                       = 0b00000010
        const val ADC_PIN                       = 0b00000100
        const val UART_PIN                      = 0b00001000
        const val I2C_PIN                       = 0b00010000
        const val R0_PIN                        = 0b00100000
        const val R1_PIN                        = 0b01000000
        const val R2_PIN                        = 0b10000000

        const val NONE_ID                       = 0x00
        const val LIGHT_ID                      = 0x10
        const val WINDOW_ID                     = 0x20
        const val POWER_ID                      = 0x30
        const val R0_ID                         = 0x40
        const val R1_ID                         = 0x50
        const val R2_ID                         = 0x60
        const val R3_ID                         = 0x70
        const val R4_ID                         = 0x80
        const val R5_ID                         = 0x90
        const val R6_ID                         = 0xA0
        const val R7_ID                         = 0xB0
        const val R8_ID                         = 0xC0
        const val NFC_ID                        = 0xD0
        const val RESET_ID                      = 0xE0
        const val HOLD_LED_ID                   = 0xF0
        const val ID_MASK                       = 0xF0

        const val GROUP_MAX_COUNT               = 16
        
        const val LABEL_SYSTEM                  = "system"
        const val LABEL_ECHO                    = "echo"
        const val LABEL_ERROR                   = "error"
        const val LABEL_CONFIG                  = "config"
        const val LABEL_MAP                     = "pinmap"
        const val LABEL_UID                     = "uid"
        const val LABEL_LIGHT                   = "light"
        const val LABEL_WINDOW                  = "window"
        const val LABEL_POWER_SUPPLY            = "power_supply"
        const val LABEL_STARTUP                 = "startup"
        const val LABEL_SAVE                    = "save"
        const val LABEL_LOAD                    = "load"

        const val KEYWORD_ECHO                  = "ECHO"
        const val KEYWORD_GET                   = "GET"
        const val KEYWORD_RESET                 = "RESET"
        const val KEYWORD_MAP                   = "MAP"
        const val KEYWORD_EEPROM                = "EEPROM"
        const val KEYWORD_REBOOT                = "REBOOT"
        const val KEYWORD_SAVE                  = "SAVE"
        const val KEYWORD_LOAD                  = "LOAD"
        const val KEYWORD_DONE                  = "DONE"
        const val KEYWORD_TRUE                  = "TRUE"
        const val KEYWORD_FALSE                 = "FALSE"
        const val KEYWORD_EMPTY                 = "EMPTY"
        const val KEYWORD_SCANNER_NOT_FOUND     = "SCANNER_NOT_FOUND"
        const val KEYWORD_INVALID_INPUT         = "INVALID_INPUT"
        const val KEYWORD_UNASSIGNED_PIN        = "UNASSIGNED_PIN"

        const val KEYWORD_ENDLINE               = "$"
        const val KEYWORD_INPUT                 = "INPUT"
        const val KEYWORD_OUTPUT                = "OUTPUT"
    }

    class Pin(val type: Int, var id: Int) {
        override fun toString() = "{ type=%08d, id=%02X }".format(type.toString(2).toInt(), id)
    }
    val pinmap = ArrayList<Pin>()

    class Pinstate(val pin: Int, val type: Int, var state: Int) {
        override fun toString() = "{ pin=%02X, type=%08d, state=%02X }".format(pin, type.toString(2).toInt(), state)
    }

    val lightGroup = ArrayList<Pinstate>()
    val windowGroup = ArrayList<Pinstate>()
    val powerGroup = ArrayList<Pinstate>()

    val funcGroups: Map<String, ArrayList<Pinstate>> = mapOf(
        LABEL_LIGHT to lightGroup,
        LABEL_WINDOW to windowGroup,
        LABEL_POWER_SUPPLY to powerGroup
    )

    var serialSpeed: Int = 9600
    var serialDataBits: Int = 8
    var serialStopBits: Int = 1
    var serialPartly: Int = 0
    var logMaxLength: Int = 1000
    var autoConnect: Boolean = false
        set(value) {
            field = value
            if (field) scan(responseDelay = 2000)
        }

    private var serialPort: SerialPort? = null
    private var receivedLine: String = ""
    private var receivedData: MutableMap<String, String> = mutableMapOf()

    val log: ArrayList<Pair<String, String>> = arrayListOf()

    var scanningPorts: Boolean = false
    val portNames: Array<String> get() = SerialPortList.getPortNames()
    val portName: String? get() = if (serialPort == null) null else serialPort!!.portName

    fun open(port: String): Boolean {
        if (portNames.find { it == port } == null) return false

        close()
        serialPort = SerialPort(port)

        try {
            serialPort!!.openPort()
            serialPort!!.setParams(serialSpeed, serialDataBits, serialStopBits, serialPartly)

            serialPort!!.addEventListener { event ->
                if (! event.isRXCHAR) return@addEventListener

                try {
                    var receivedSubLine = serialPort!!.readString()
                    if (receivedSubLine != null) receivedLine += receivedSubLine
                } finally { }

                if (! receivedLine.contains(KEYWORD_ENDLINE)) return@addEventListener

                receivedLine.trim().split(KEYWORD_ENDLINE).forEach {
                    if (it.isEmpty()) return@forEach
                    val jsonString = '{' + it.substringBefore('}').substringAfterLast('{') + '}'

                    try {
                        appendLog(KEYWORD_INPUT to jsonString)
                    } catch (e: Exception) { println("$e - \'$jsonString\'") }
                    try {
                        receivedData.clear()
                        receivedData.putAll(parserJSON(jsonString))
                        listener.dataReceived(receivedData as Map<String, String>)
                    } catch (e: Exception) { println("$e - \'$receivedData\'") }
                }
                receivedLine = ""
            }
        } catch (e: Exception) {
            println(e)
        }

        return if(serialPort == null) false else serialPort!!.isOpened
    }

    fun close() {
        if (serialPort == null) return
        if (serialPort!!.isOpened) try { serialPort!!.closePort() } catch (e: Exception) {}
        serialPort = null
    }

    fun isOpen(): Boolean = serialPort != null && serialPort!!.portName in portNames && serialPort!!.isOpened

    fun send(data: Map<String, String>) {
        if (! isOpen() || data.isEmpty()) return

        val sendLine = buildJsonObject { data.forEach{ put(it.key, it.value) } }.toString() + KEYWORD_ENDLINE
        appendLog(KEYWORD_OUTPUT to sendLine)
        serialPort!!.writeString(sendLine)
    }

    fun send(vararg data: Pair<String, String>) = send(mapOf(*data))

    fun send(data: String) {
        try {
            send(parserJSON(data.replace(KEYWORD_ENDLINE, "")))
        } catch (e: Exception) {
            appendLog(KEYWORD_OUTPUT to "Некорректный синтаксис!")
        }
    }

    fun get(vararg params: String): Map<String, String> {
        val data = mutableMapOf<String, String>()
        if (params.isEmpty()) {
            data.putAll(receivedData)
            receivedData.clear()
        } else {
            params.forEach {
                if (receivedData.contains(it)) {
                    data[it] = receivedData[it]!!
                    receivedData.remove(it)
                }
            }
        }
        return data
    }

    fun get(param: String): String? {
        val value = receivedData[param]
        return value
    }

    fun remove(vararg params: String) = params.forEach { receivedData.remove(it) }

    private fun parserJSON(line: String): Map<String, String> = Json.parseToJsonElement(line).jsonObject.toMap().mapValues { it.value.jsonPrimitive.content }

    fun scan(attempts: Int = 5, responseDelay: Long = 2000, startUpDelay: Long = responseDelay) {
        close()
        var foundPort: String? = null
        listener.portFound(foundPort)
        Thread {
            scanningPorts = true
            portNames.forEach {
                try {
                    open(it)
                    Thread.sleep(startUpDelay)
                    for (attempt in 1..attempts) {
                        val response = get(LABEL_ECHO, LABEL_STARTUP)
                        if (response.isNotEmpty() && (response[LABEL_ECHO] == KEYWORD_ECHO || response[LABEL_STARTUP] == KEYWORD_DONE) ) {
                            foundPort = it
                            return@forEach
                        }
                        send(LABEL_ECHO to KEYWORD_ECHO)
                        Thread.sleep(responseDelay)
                    }
                    close()
                    foundPort = null
                } catch (e: SerialPortException) {
                    println(e)
                }
            }

            scanningPorts = false
            listener.portFound(foundPort)
        }.start()
    }

    private fun syncConfig(params: Map<String, String>) {
        fun isHex(value: String) = Pattern.compile("\\p{XDigit}+").matcher(value).matches()

        if (LABEL_MAP in params) {
            val pintypes: String = params[LABEL_MAP]!!
            if(!isHex(pintypes)) return
            receivedData.remove(LABEL_MAP)

            pinmap.clear()
            for (i in pintypes.indices step 2) {
                val type = pintypes.substring(i, i + 2)
                pinmap.add(Pin(type.toInt(16), 0))
            }
            println(pinmap)
            
            send(LABEL_CONFIG to KEYWORD_GET)
        }

        if (LABEL_CONFIG in params) {
            val value = get(LABEL_CONFIG)

            if(value == KEYWORD_DONE) {
                if(pinmap.isEmpty()) send(LABEL_CONFIG to KEYWORD_MAP)
                else send(LABEL_CONFIG to KEYWORD_SAVE)
                return
            }
            val pinid: String = value!!
            if(!isHex(pinid)) return

            lightGroup.clear()
            windowGroup.clear()
            powerGroup.clear()

            for (i in pinid.indices step 2) {
                val id = pinid.substring(i, i + 2)
                pinmap[i / 2].id = id.toInt(16)
            }

            listener.syncConfig(pinmap)
            
            pinmap.forEachIndexed { index, pin ->
                if(pin.type and ALLOW_PIN != ALLOW_PIN) return@forEachIndexed
                when(pin.id and ID_MASK ) {
                    LIGHT_ID    -> if ( lightGroup.count()  < GROUP_MAX_COUNT)  lightGroup.add(pin.id and ID_MASK.inv(), Pinstate(index, pin.type, 0))
                    WINDOW_ID   -> if ( windowGroup.count() < GROUP_MAX_COUNT) windowGroup.add(pin.id and ID_MASK.inv(), Pinstate(index, pin.type, 0))
                    POWER_ID    -> if ( powerGroup.count()  < GROUP_MAX_COUNT)  powerGroup.add(pin.id and ID_MASK.inv(), Pinstate(index, pin.type, 0))
                }
            }

            listener.groupsUpdated(funcGroups)
        }

        if (LABEL_SAVE in params) {
            if(get(LABEL_SAVE) == KEYWORD_DONE) send(LABEL_CONFIG to KEYWORD_GET)
        }
    }

    private fun appendLog(newRecord: Pair<String, String>) {
        this.log.add(newRecord)
        if (this.log.count() > logMaxLength) this.log.removeAt(0)
        this.listener.updateLog(this.log)
    }

    interface SerialPortListener {
        val dataReceivedListeners: MutableSet<(Map<String, String>) -> Unit>
        val portFoundListeners: MutableSet<(String?) -> Unit>
        val updateLogListeners: MutableSet<(ArrayList<Pair<String, String>>) -> Unit>
        val syncConfigListeners: MutableSet<(ArrayList<Pin>) -> Unit>
        var groupsUpdatedListeners: MutableSet<(Map<String, ArrayList<Pinstate>>) -> Unit>

        var dataReceived: (params: Map<String, String>) -> Unit
        var portFound: (String?) -> Unit
        var updateLog: (ArrayList<Pair<String, String>>) -> Unit
        var syncConfig: (ArrayList<Pin>) -> Unit
        var groupsUpdated: (Map<String, ArrayList<Pinstate>>) -> Unit
    }

    private var listener: SerialPortListener = object : SerialPortListener {
        override val dataReceivedListeners: MutableSet<(Map<String, String>) -> Unit> = mutableSetOf()
        override val portFoundListeners: MutableSet<(String?) -> Unit> = mutableSetOf()
        override val updateLogListeners: MutableSet<(ArrayList<Pair<String, String>>) -> Unit> = mutableSetOf()
        override val syncConfigListeners: MutableSet<(ArrayList<Pin>) -> Unit> = mutableSetOf()
        override var groupsUpdatedListeners: MutableSet<(Map<String, ArrayList<Pinstate>>) -> Unit> = mutableSetOf()

        override var portFound: (String?) -> Unit = { value ->
                runBlocking { coroutineScope { portFoundListeners.forEach { launch { it(value) } } } }
        }
            set(value) { portFoundListeners.add(value) }
        override var dataReceived: (Map<String, String>) -> Unit = { value ->
                runBlocking { coroutineScope { dataReceivedListeners.forEach { launch { it(value) } } } }
        }
            set(value) { dataReceivedListeners.add(value) }

        override var updateLog: (ArrayList<Pair<String, String>>) -> Unit = { value ->
                runBlocking { coroutineScope { updateLogListeners.forEach { launch { it(value) } } } }
        }
            set(value) { updateLogListeners.add(value) }

        override var syncConfig: (ArrayList<Pin>) -> Unit = { value ->
                runBlocking { coroutineScope { syncConfigListeners.forEach { launch { it(value) } } } }
            }
            set(value) { syncConfigListeners.add(value) }

        override var groupsUpdated: (Map<String, ArrayList<Pinstate>>) -> Unit = { value ->
                runBlocking { coroutineScope { groupsUpdatedListeners.forEach { launch { it(value) } } } }
            }
            set(value) { groupsUpdatedListeners.add(value) }
    }

    fun addPortFoundListener(portFount: (String?) -> Unit) {
        this.listener.portFound = portFount
    }

    fun addDataReceivedListener(dataReceived: (Map<String, String>) -> Unit) {
        this.listener.dataReceived = dataReceived
    }

    fun addUpdateLogListener(updateLog: (ArrayList<Pair<String, String>>) -> Unit) {
        this.listener.updateLog = updateLog
    }

    fun addSyncConfigListener(syncConfig: (ArrayList<Pin>) -> Unit) {
        this.listener.syncConfig = syncConfig
    }

    fun addGroupsUpdatedListener(groupsUpdated: (Map<String, ArrayList<Pinstate>>) -> Unit) {
        this.listener.groupsUpdated = groupsUpdated
    }

    init {
        dataBus.serialManager = this

        this.addDataReceivedListener { syncConfig(it) }
    }
}