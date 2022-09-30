import jssc.SerialPort
import jssc.SerialPortException
import jssc.SerialPortList

class SerialIO (
    var serialSpeed: Int = 9600,
    var serialDataBits: Int = 8,
    var serialStopBits: Int = 1,
    var serialPartly: Int = 0
        )
{
    var serialPortNames: Array<String> = arrayOf<String>()
    private var serialPort: SerialPort? = null

    init {
        updatePortList()
    }

    fun openPort(choosenPort: String, listenerFunction: (input: String) -> Unit = { line:String -> println(line) } ): Boolean {
        updatePortList()
        if (serialPortNames.find { it == choosenPort } == null) return false

        if (serialPort!!.isOpened) serialPort!!.closePort()
        serialPort = SerialPort(choosenPort)

        try {
            serialPort!!.openPort()
            serialPort!!.setParams(serialSpeed, serialDataBits, serialStopBits, serialPartly)

            serialPort!!.addEventListener {
                if (it.isRXCHAR) listenerFunction(serialPort!!.readString()!!.trim())
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
}