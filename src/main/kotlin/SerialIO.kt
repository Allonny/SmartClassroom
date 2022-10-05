import jssc.SerialPort
import jssc.SerialPortException
import jssc.SerialPortList
import kotlinx.serialization.json.*

class SerialIO (
    var serialSpeed: Int = 9600,
    var serialDataBits: Int = 8,
    var serialStopBits: Int = 1,
    var serialPartly: Int = 0
        ) {
    var serialPortNames: Array<String> = arrayOf<String>()
    private var serialPort: SerialPort? = null

    private var receivedData: MutableMap<String, String> = mutableMapOf("one" to "1", "two" to "2")

    private var echoMessage: String = "ECHO"

    init {
        println(scanPorts().toString())
        closePort()
        //updatePortList()
    }

    fun openPort(chosenPort: String, disableListener: Boolean = false): Boolean {
        updatePortList()
        if (serialPortNames.find { it == chosenPort } == null) return false

        if (serialPort != null && serialPort!!.isOpened) serialPort!!.closePort()
        serialPort = SerialPort(chosenPort)

        try {
            serialPort!!.openPort()
            serialPort!!.setParams(serialSpeed, serialDataBits, serialStopBits, serialPartly)

            if (!disableListener)
                serialPort!!.addEventListener {
                    if (it.isRXCHAR) receivedData.putAll(parserJSON(serialPort!!.readString()!!.trim()))
                }
        } catch (e: SerialPortException) {
            println(e)
        }

        return serialPort!!.isOpened
    }

    fun closePort() {
        if (serialPort != null && serialPort!!.isOpened) serialPort!!.closePort()
    }

    fun updatePortList(): Int {
        serialPortNames = SerialPortList.getPortNames()
        return serialPortNames.size
    }

    fun sendData(data: Map<String, String>) {
        if (serialPort!!.isOpened and data.isNotEmpty()) {
            var dataLine: String = ""
            data.forEach { dataLine += it.key + "=" + it.value + ";" }
            serialPort!!.writeString(dataLine)
        }
    }

    fun getData(): Map<String, String>? {
        val data = receivedData
        receivedData.clear()
        return data
    }

    private fun parserJSON(line: String): Map<String, String> = Json.parseToJsonElement(line).jsonObject.toMap().mapValues { it.value.toString().replace("\"", "") }

    fun scanPorts(): String? {
        updatePortList()
        if (serialPort != null && !serialPort!!.isOpened && serialPort!!.portName in serialPortNames) {
            openPort(serialPort!!.portName)
            return serialPort!!.portName
        }
        if (serialPort != null && serialPort!!.isOpened) serialPort!!.closePort()

        var foundPort: String? = null
        serialPortNames.forEach {
            try {
                openPort(it, true)
                for (attempt in 1..5) {
                    serialPort!!.writeString(buildJsonObject { put("echo", echoMessage) }.toString())
                    Thread.sleep(1200)
                    val receiveString = serialPort!!.readString()
                    println(receiveString)

                    if (receiveString !in arrayOf(null, "") && parserJSON(receiveString!!)["echo"] == echoMessage) {
                        foundPort = it
                        return@forEach
                    }
                }
            } catch (e: SerialPortException) {
                println(e)
            }
        }
        if (foundPort == null) return null
        openPort(foundPort!!)
        return serialPort!!.portName
    }
}