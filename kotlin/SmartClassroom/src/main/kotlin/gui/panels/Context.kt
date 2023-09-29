package gui.panels

import DBManager
import SerialManager
import auxiliary.TreeNode
import javax.swing.JFrame

object Context {
    var mainFrame: JFrame? = null
    var buttonsInLine: Int = 2
    var serialManager: SerialManager? = null
    var dbManager: DBManager? = null
    var currentPanel: TreeNode<BasePanel>? = null
}