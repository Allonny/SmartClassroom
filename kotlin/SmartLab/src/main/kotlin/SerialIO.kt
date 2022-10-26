import jssc.SerialPort
import jssc.SerialPortException
import jssc.SerialPortList
import kotlinx.serialization.json.*
import kotlin.Exception

class SerialIO (
    var serialSpeed: Int = 9600,
    var serialDataBits: Int = 8,
    var serialStopBits: Int = 1,
    var serialPartly: Int = 0,
    val logMaxLength: Int = 1000,
    val autoConnect: Boolean = false
        ) {
    companion object {
        const val LABEL_SYSTEM = "system"
        const val LABEL_RESET = "reset"
        const val LABEL_ECHO = "echo"
        const val LABEL_ERROR = "error"
        const val LABEL_UID = "uid"
        const val LABEL_LIGHT = "light"
        const val LABEL_WINDOW = "window"
        const val LABEL_POWER_SUPPLY = "power_supply"

        const val LABEL_STARTUP = "startup"
        const val LABEL_SAVE = "save"
        const val LABEL_LOAD = "load"

        const val KEYWORD_GET = "GET"
        const val KEYWORD_RESET = "RESET"
        const val KEYWORD_SETTINGS = "SETTINGS"
        const val KEYWORD_EEPROM = "EEPROM"
        const val KEYWORD_REBOOT = "REBOOT"
        const val KEYWORD_SAVE = "SAVE"
        const val KEYWORD_LOAD = "LOAD"
        const val KEYWORD_ECHO = "ECHO"
        const val KEYWORD_DONE = "DONE"
        const val KEYWORD_TRUE = "TRUE"
        const val KEYWORD_FALSE = "FALSE"
        const val KEYWORD_EMPTY = "EMPTY"
        const val KEYWORD_ENDLINE = "ENDLINE"
        const val KEYWORD_SCANNER_NOT_FOUND = "SCANNER_NOT_FOUND"
        const val KEYWORD_INVALID_INPUT = "INVALID_INPUT"

        const val KEYWORD_INPUT = "INPUT"
        const val KEYWORD_OUTPUT = "OUTPUT"
    }

    private var serialPort: SerialPort? = null
    private var receivedLine: String = ""
    private var receivedData: MutableMap<String, String> = mutableMapOf()

    private val serialLog: ArrayList<Pair<String, String>> = arrayListOf()
    fun getLog(): Array<Pair<String, String>> = serialLog.toTypedArray()

    var findingPort: Boolean = false
    val serialPortNames: Array<String> get() = SerialPortList.getPortNames()
    val serialPortName: String? get() = if (serialPort == null) null else serialPort!!.portName

    init {
        if (autoConnect) scanPorts(responseDelay = 2000)
    }

    fun openPort(chosenPort: String): Boolean {
        if (serialPortNames.find { it == chosenPort } == null) return false

        closePort()
        serialPort = SerialPort(chosenPort)

        try {
            serialPort!!.openPort()
            serialPort!!.setParams(serialSpeed, serialDataBits, serialStopBits, serialPartly)

            serialPort!!.addEventListener { event ->
                if (event.isRXCHAR) {
                    try {
                        var receivedSubLine = serialPort!!.readString()
                        if (receivedSubLine != null) receivedLine += receivedSubLine
                    } finally { }
                    if (receivedLine.contains(KEYWORD_ENDLINE)) {
                        receivedLine.trim().split(KEYWORD_ENDLINE).forEach {
                            if (it.isEmpty()) return@forEach
                            try {
                                receivedData.putAll(parserJSON(it))
                                this.listener.dataReceived(receivedData as Map<String, String>)
                                appendLog(KEYWORD_INPUT to it)
                            } catch (e: Exception) {
                                println(e.toString() + "\'$it\'")
                            }
                        }
                        receivedLine = ""
                    }
                }
            }
        } catch (e: Exception) {
            println(e)
        }

        return if(serialPort == null) false else serialPort!!.isOpened
    }

    fun closePort() {
        if (serialPort != null) {
            if (serialPort!!.isOpened) try { serialPort!!.closePort() } catch (e: Exception) {}
            serialPort = null
        }
    }

    fun serialIsOpen(): Boolean = serialPort != null && serialPort!!.portName in serialPortNames && serialPort!!.isOpened

    fun sendData(data: Map<String, String>) {
        if (serialIsOpen() && data.isNotEmpty()) {
            val sendLine = buildJsonObject { data.forEach{ put(it.key, it.value) } }.toString()
            appendLog(KEYWORD_OUTPUT to sendLine)
            serialPort!!.writeString(sendLine)
        }
    }

    fun sendData(vararg data: Pair<String, String>) = sendData(mapOf(*data))

    fun sendData(data: String) {
        try {
            sendData(parserJSON(data))
        } catch (e: Exception) {
            appendLog(KEYWORD_OUTPUT to "Некорректный синтаксис!")
        }
    }

    fun getData(vararg params: String): Map<String, String> {
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

    fun removeData(vararg params: String) = params.forEach { receivedData.remove(it) }

    private fun parserJSON(line: String): Map<String, String> = Json.parseToJsonElement(line).jsonObject.toMap().mapValues { it.value.jsonPrimitive.content }

    fun scanPorts(attempts: Int = 5, responseDelay: Long = 2000, startUpDelay: Long = responseDelay) {
        closePort()
        var foundPort: String? = null
        Thread {
            findingPort = true
            serialPortNames.forEach {
                try {
                    openPort(it)
                    Thread.sleep(startUpDelay)
                    for (attempt in 1..attempts) {
                        val response = getData(LABEL_ECHO, LABEL_STARTUP)
                        if (response.isNotEmpty() && (response[LABEL_ECHO] == KEYWORD_ECHO || response[LABEL_STARTUP] == KEYWORD_DONE) ) {
                            removeData(LABEL_ECHO, LABEL_STARTUP)
                            foundPort = it
                            return@forEach
                        }
                        sendData(LABEL_ECHO to KEYWORD_ECHO)
                        Thread.sleep(responseDelay)
                    }
                    closePort()
                    foundPort = null
                } catch (e: SerialPortException) {
                    println(e)
                }
            }

//            if (foundPort != null) openPort(foundPort as String)
            findingPort = false
            listener.portFound(foundPort)
        }.start()
    }

    private fun appendLog(newRecord: Pair<String, String>) {
        serialLog.add(newRecord)
        if (serialLog.count() > logMaxLength) serialLog.removeAt(0)
        this.listener.updateLog(serialLog)
    }

    interface SerialPortListener {
        var dataReceived: (Map<String, String>) -> Unit
            get() = dataReceived
            set(value) { dataReceived = value }
        var portFound: (String?) -> Unit
            get() = portFound
            set(value) { portFound = value }
        var updateLog: (ArrayList<Pair<String, String>>) -> Unit
            get() = updateLog
            set(value) { updateLog = value }
    }

    private var listener: SerialPortListener = object : SerialPortListener {
        override var dataReceived: (Map<String, String>) -> Unit = {}
        override var portFound: (String?) -> Unit = {}
        override var updateLog: (ArrayList<Pair<String, String>>) -> Unit = {}
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
}