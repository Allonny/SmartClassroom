import jssc.SerialPort
import jssc.SerialPortException
import jssc.SerialPortList
import jssc.SerialPortTimeoutException
import java.net.CacheRequest
import java.util.*
import kotlinx.coroutines.*

class SerialIO (
    var serialSpeed: Int = 9600,
    var serialDataBits: Int = 8,
    var serialStopBits: Int = 1,
    var serialPartly: Int = 0
        )
{
    var serialPortNames: Array<String> = arrayOf<String>()
    private var serialPort: SerialPort? = null

    private var receivedData: MutableMap<String, String> = mutableMapOf("one" to "1", "two" to "2")

    private var requestMessage: String = "REQUEST"
    private var responseMessage: String = "RESPONSE"

    init {
        println(scanPorts().toString())
        //updatePortList()
    }

    fun openPort(chosenPort: String): Boolean {
        updatePortList()
        if (serialPortNames.find { it == chosenPort } == null) return false

        if (serialPort!!.isOpened) serialPort!!.closePort()
        serialPort = SerialPort(chosenPort)

        try {
            serialPort!!.openPort()
            serialPort!!.setParams(serialSpeed, serialDataBits, serialStopBits, serialPartly)

            serialPort!!.addEventListener {
                if (it.isRXCHAR) receivedData.putAll(serialLineParser(serialPort!!.readString()!!.trim()))
            }
        } catch (e: SerialPortException) {
            println(e)
        }

        return serialPort!!.isOpened
    }

    fun updatePortList(): Int {
        serialPortNames = SerialPortList.getPortNames()
        return serialPortNames.size
    }

    fun sendData(data: Map<String, String>) {
        if(serialPort!!.isOpened and data.isNotEmpty()) {
            var dataLine: String = ""
            data.forEach{ dataLine += it.key + "=" + it.value + ";" }
            serialPort!!.writeString(dataLine)
        }
    }

    fun getData(): Map<String, String>? {
        val data = receivedData
        receivedData.clear()
        return data
    }

    fun serialLineParser(line: String): MutableMap<String, String> {
        val params = mutableMapOf<String, String>()
        try {
            line.split(";".toRegex()).forEach {
                val param = Regex("(\\w+)=(\\w+)").find(it)!!.destructured.toList()
                params[param[0]] = param[1]
                println(param[0] + " : " + param[1])
            }
        } finally {
            return params
        }
    }

    fun scanPorts(): String? {


        if (serialPort == null) serialPort = SerialPort("")
        else if (serialPort!!.isOpened and (serialPort!!.portName in SerialPortList.getPortNames())) return serialPort!!.portName

        updatePortList()

        if(serialPort!!.portName !in serialPortNames) {
            var foundPort: String? = null
            serialPortNames.forEach {
                try {
                    if (serialPort!!.isOpened) serialPort!!.closePort()
                    serialPort = SerialPort(it)
                    serialPort!!.openPort()
                    serialPort!!.setParams(serialSpeed, serialDataBits, serialStopBits, serialPartly)
                    if (timer(serialPort!!, 5, 1000)) {
                        foundPort = it
                        return@forEach
                    }
                } catch (e: SerialPortException) {
                    println(e)
                }
            }
            if(foundPort == null) return null
            openPort(foundPort!!)
        }
        return serialPort!!.portName
    }

    fun timer(serialPort: SerialPort, requestAttempts: Int, requestDelay: Long): Boolean {
        return if (requestAttempts <= 0) false
        else {
            //val timer = Timer()
            var receiveString: String? = null
            serialPort!!.writeString("setup=$requestMessage;")
            Thread.sleep(requestDelay)
            receiveString = serialPort!!.readString()
            println(receiveString)

            if (receiveString in arrayOf(null, "")) timer(serialPort, requestAttempts - 1, requestDelay)
            else serialLineParser(receiveString!!)["setup"] == responseMessage
        }
    }
}