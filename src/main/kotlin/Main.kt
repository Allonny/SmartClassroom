import javax.swing.*
import jssc.*
import java.awt.BorderLayout

fun main(args: Array<String>) {
    val gui = GUI()
    //println(gui.panelTree.toString())

}

//fun serialLineParser(line: String) {
//    val params = mutableMapOf<String, String>()
//    line.split(";".toRegex()).forEach {
//        val param = Regex("(\\w+)=(\\w+)").find(it)!!.destructured.toList()
//        params.put(param[0], param[1])
//        println(param[0] + " : " + param[1])
//    }
//
//    params.keys.forEach{
//        when(it) {
//            "err" -> JOptionPane.showMessageDialog(mainFrame, "Ошибка: " + params["err"].toString(), "Ошибка", JOptionPane.ERROR_MESSAGE)
//            "uid" -> JOptionPane.showMessageDialog(mainFrame, "UID: " + params["uid"].toString().uppercase(), "Найдена новая карточка", JOptionPane.INFORMATION_MESSAGE)
//        }
//    }
//}

fun errMessage(title: String, description: String) {

}

