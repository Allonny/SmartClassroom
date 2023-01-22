import gui.GUI

fun main() {
    val serialBus: SerialBus = SerialBus(19200, autoConnect = true)

    GUI(serialBus)
}