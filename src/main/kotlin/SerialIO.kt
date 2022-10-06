import jssc.SerialPort
import jssc.SerialPortException
import jssc.SerialPortList
import kotlinx.serialization.json.*
import java.lang.Exception

class SerialIO (
    var serialSpeed: Int = 9600,
    var serialDataBits: Int = 8,
    var serialStopBits: Int = 1,
    var serialPartly: Int = 0
        ) {
    private var serialPort: SerialPort? = null
    private var receivedData: MutableMap<String, String> = mutableMapOf("one" to "1", "two" to "2")
    private var echoMessage: Pair<String, String> = "echo" to "ECHO"

    val serialPortNames: Array<String> get() = SerialPortList.getPortNames()
    val serialPortName: String? get() = if (serialPort == null) null else serialPort!!.portName

    init {
//        scanPorts(responseDelay = 1500)
//        println(serialPortNames.size)
//        println(serialPortName)
//        closePort()
    }

    fun openPort(chosenPort: String): Boolean {
        if (serialPortNames.find { it == chosenPort } == null) return false

        closePort()
        serialPort = SerialPort(chosenPort)

        try {
            serialPort!!.openPort()
            serialPort!!.setParams(serialSpeed, serialDataBits, serialStopBits, serialPartly)

            serialPort!!.addEventListener {
                if (it.isRXCHAR) receivedData.putAll(parserJSON(serialPort!!.readString()!!.trim()))
            }
        } catch (e: Exception) {
            println(e)
        }

        return if(serialPort == null) false else serialPort!!.isOpened
    }

    fun closePort(): Boolean = if (serialPort != null && serialPort!!.isOpened) serialPort!!.closePort() else true

    fun serialIsOpen(): Boolean = serialPort != null && serialPort!!.portName in serialPortNames && serialPort!!.isOpened

    fun sendData(data: Map<String, String>) {
        if (serialIsOpen() && data.isNotEmpty()) {
            serialPort!!.writeString(buildJsonObject { data.forEach{ put(it.key, it.value) } }.toString())
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

    private fun parserJSON(line: String): Map<String, String> = Json.parseToJsonElement(line).jsonObject.toMap().mapValues { it.value.jsonPrimitive.content }

    fun scanPorts(attempts: Int = 5, responseDelay: Long = 2000, startUpDelay: Long = responseDelay): String? {

        closePort()
        var foundPort: String? = null
        serialPortNames.forEach {
            try {
                openPort(it)
                Thread.sleep(startUpDelay)
                for (attempt in 1..attempts) {
                    sendData(mapOf(echoMessage))
                    Thread.sleep(responseDelay)
                    val response = getData(echoMessage.first)

                    if (response.isNotEmpty() && response[echoMessage.first] == echoMessage.second) {
                        foundPort = it
                        return@forEach
                    }
                }
            } catch (e: SerialPortException) {
                println(e)
            }
        }
        if (foundPort != null) openPort(foundPort as String)
        return serialPortName
    }
}