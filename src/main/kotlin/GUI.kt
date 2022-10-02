import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.Rectangle
import javax.swing.JButton
import javax.swing.JComboBox
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JPanel

class GUI {
    private val mainFrame: JFrame = JFrame("SmartLab terminal")
    private val welcomePanel: JPanel = JPanel()
    private val menuPanel: JPanel = JPanel()
    private val lightPanel: JPanel = JPanel()
    private var windowPanel: JPanel = JPanel()
    private val settingsPanel: JPanel = JPanel()

    private var currentPanel: TreeNode<JPanel>? = null
    private var panelTree: TreeNode<JPanel> = TreeNode("root", welcomePanel)

//    var portComboBox: JComboBox<String> = JComboBox<String>()

    init {
        panelTree.addChild("menu", menuPanel)
        panelTree.addChild("settings", settingsPanel)
        panelTree["menu"]!!.addChild("light", lightPanel)
        panelTree["menu"]!!.addChild("window", windowPanel)

        panelTree.addChildren("one" to JPanel(), "two" to JPanel())
        panelTree["three"] = JPanel()
        println(panelTree.names)
        println(panelTree.children)
        println(panelTree.count)

        setPanel(panelTree)
        currentPanel = panelTree

        mainFrame.setLocationRelativeTo(null)
        mainFrame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        mainFrame.size = Dimension(400, 500)
        updateFrame()

    }

    private fun setPanel(panel: TreeNode<JPanel>?) {
        if (panel == null) return
        panel.value.size = Dimension(300, 400)
        panel.value.add(JLabel(panel.name))
        if (panel.parent != null) {
            val btn = JButton("Back")
            btn.addActionListener {
                currentPanel = panel.parent
                updateFrame()
            }
            panel.value.add(btn)
        }

        panel.forEach { subPanel ->
            val btn = JButton(subPanel.name)
            btn.addActionListener {
                currentPanel = subPanel
                updateFrame()
            }
            panel.value.add(btn)
            setPanel(subPanel)
        }
    }

    private fun updateFrame() {
        println(currentPanel!!.toString())
        mainFrame.contentPane.isVisible = false
        mainFrame.contentPane.removeAll()
        mainFrame.contentPane.add(BorderLayout.CENTER, currentPanel!!.value)
        mainFrame.contentPane.isVisible = true
        mainFrame.isVisible = true
    }
}