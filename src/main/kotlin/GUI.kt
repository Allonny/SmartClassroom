import java.awt.*
import javax.swing.Box
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JPanel

class GUI {
    private val mainFrame: JFrame = JFrame("SmartLab terminal")

    private val topPanel: JPanel = JPanel()
    private val welcomePanel: JPanel = JPanel()

    private val powerMenuPanel: JPanel = JPanel()
    private val settingsBasicPanel: JPanel = JPanel()
    private val settingsExtendedPanel: JPanel = JPanel()

    private val menuBasicPanel: JPanel = JPanel()
    private val menuExtendedPanel: JPanel = JPanel()
    private val menuAdminPanel: JPanel = JPanel()

    private val lightPanel: JPanel = JPanel()
    private val windowPanel: JPanel = JPanel()
    private val powerSupplyPanel: JPanel = JPanel()
    private val addUserPanel: JPanel = JPanel()

    private var currentPanel: TreeNode<JPanel>? = null
    private val rootTree: TreeNode<JPanel> = TreeNode("root", welcomePanel)
    private val basicSubTree: TreeNode<JPanel> = TreeNode("basic", menuBasicPanel)
    private val extendedSubTree: TreeNode<JPanel> = TreeNode("extended", menuExtendedPanel)
    private val adminSubTree: TreeNode<JPanel> = TreeNode("admin", menuAdminPanel)

    private val topPanelButtonsSize = Dimension(70, 70)
    private val menuButtonsSize = Dimension(400, 200)

    private var settings: MutableMap<String, Any> = mutableMapOf()

    init {
        basicSubTree.addChildren(
            "settings" to settingsBasicPanel,
            "light" to lightPanel,
            "window" to windowPanel
        )
        extendedSubTree.addChildren(
            "settings" to settingsBasicPanel,
            "light" to lightPanel,
            "window" to windowPanel,
            "powerSupply" to powerSupplyPanel
        )
        adminSubTree.addChildren(
            "settings" to settingsExtendedPanel,
            "light" to lightPanel,
            "window" to windowPanel,
            "powerSupply" to powerSupplyPanel,
            "addUser" to addUserPanel
        )
        rootTree.addChildren(
            "powerMenu" to powerMenuPanel,
            "settings" to settingsBasicPanel
        )
        rootTree += basicSubTree
        rootTree += extendedSubTree
        rootTree += adminSubTree

        currentPanel = rootTree

        mainFrame.setLocationRelativeTo(null)
        mainFrame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        mainFrame.size = Dimension(1000, 500)
        System.setProperty("awt.useSystemAAFontSettings", "on")
        System.setProperty("swing.aatext", "true")
        setPanel(rootTree)
        updateFrame()
    }

    public fun updateFrame() {
        println(currentPanel!!.toString())
        mainFrame.contentPane.isVisible = false
        mainFrame.contentPane.removeAll()
        setTopPanel()
        mainFrame.contentPane.add(BorderLayout.NORTH, topPanel)
        mainFrame.contentPane.add(BorderLayout.CENTER, currentPanel!!.value)
        mainFrame.contentPane.isVisible = true
        mainFrame.title = "SmartLab " + currentPanel!!.name
        mainFrame.isVisible = true
    }

    private fun setTopPanel() {
        if(currentPanel == null) return

        topPanel.isVisible = false
        topPanel.removeAll()
        topPanel.background = ColorPalette.BACKGROUND_ALT
        topPanel.layout = GridBagLayout()
        val constraints = GridBagConstraints()
        constraints.fill = GridBagConstraints.HORIZONTAL

        fun addElement(element: JComponent, width: Int, setSize: Dimension? = null) {
            constraints.gridwidth = width
            if (setSize != null) {
                constraints.ipadx = setSize.width - element.minimumSize.width
                constraints.ipady = setSize.height - element.minimumSize.height
                constraints.weightx = 0.0
            } else {
                constraints.ipadx = 0
                constraints.ipady = 0
                constraints.weightx = 1.0
            }
            topPanel.add(element, constraints)
            constraints.gridx += width
        }

        constraints.gridx = 0
        constraints.insets = Insets(10, 10, 10, 10)

        if(currentPanel!!.parent == null) {
            val powerButton = customButton("Питание", ColorPalette.ACCENT_HIGH, ColorPalette.FOREGROUND, labelSize = 15)
            powerButton.addActionListener{
                currentPanel = currentPanel!!["powerMenu"]
                updateFrame()
            }
            addElement(powerButton, 1, topPanelButtonsSize)
        } else {
            //val backButton = JButton(ImageIcon(ImageIO.read(File("resources/Back.png"))))
            val backButton = customButton("Назад", ColorPalette.ACCENT_NORMAL, ColorPalette.FOREGROUND, labelSize = 15)
            backButton.addActionListener {
                currentPanel = currentPanel!!.parent
                updateFrame()
            }
            addElement(backButton, 1, topPanelButtonsSize)
        }

        addElement(JLabel("Hello world"), 4)

        val settingsButton = customButton("Настройки", ColorPalette.ACCENT_NORMAL, ColorPalette.FOREGROUND, labelSize = 15)
        if("settings" in currentPanel!!.names)
            settingsButton.addActionListener{
                currentPanel = currentPanel!!["settings"]
                updateFrame()
            }
        else settingsButton.isEnabled = false
        addElement(settingsButton, 1, topPanelButtonsSize)

        topPanel.isVisible = true
    }

    private fun setPanel(panel: TreeNode<JPanel>?) {
        when(panel!!.name) {
            "root" -> setWelcomePanel(panel.value)
            "basic" -> setBasicMenuPanel(panel.value, panel.children)
            "extended" -> setExtendedMenuPanel(panel.value, panel.children)
            "admin" -> setAdminMenuPanel(panel.value, panel.children)
        }
        panel.forEach { setPanel(it) }
    }

    private fun setWelcomePanel(panel: JPanel) {
        panel.removeAll()
        panel.layout = BorderLayout()
        panel.background = ColorPalette.BACKGROUND

        panel.add(BorderLayout.NORTH, setTitle("Добро пожаловать"))

        val buttonGrid = JPanel(GridLayout(0, 1, 10, 10))
        buttonGrid.background = ColorPalette.BACKGROUND
        val basicEnterButton = customButton("Войти как студент", ColorPalette.ACCENT_NORMAL, ColorPalette.FOREGROUND)
        basicEnterButton.addActionListener{
            currentPanel = rootTree["basic"]
            updateFrame()
        }
        buttonGrid.add(basicEnterButton)

        val extendedEnterButton = customButton("Войти как преподаватель", ColorPalette.ACCENT_NORMAL, ColorPalette.FOREGROUND)
        extendedEnterButton.addActionListener{
            currentPanel = rootTree["extended"]
            updateFrame()
        }
        buttonGrid.add(extendedEnterButton)

        val adminEnterButton = customButton("Войти как администратор", ColorPalette.ACCENT_NORMAL, ColorPalette.FOREGROUND)
        adminEnterButton.addActionListener{
            currentPanel = rootTree["admin"]
            updateFrame()
        }
        buttonGrid.add(adminEnterButton)

        panel.add(BorderLayout.CENTER, buttonGrid)
        panel.add(BorderLayout.WEST, Box.createHorizontalStrut(mainFrame.width / 5))
        panel.add(BorderLayout.EAST, Box.createHorizontalStrut(mainFrame.width / 5))
        panel.add(BorderLayout.SOUTH, Box.createVerticalStrut(mainFrame.height / 5))
    }

    private fun setBasicSettingsPanel(panel: JPanel) {

    }

    private fun setExtendedSettingsPanel(panel: JPanel) {

    }

    private fun menuButton(title: String, panel: TreeNode<JPanel>): JButton {
        var label = ""
        when (title) {
            "light" -> label = "Управление светом"
            "window" -> label = "Управление проветриванием"
            "powerSupply" -> label = "Управление питанием"
            "addUser" -> label = "Добавить пользователя"
        }

        val button = customButton(label, ColorPalette.ACCENT_NORMAL, ColorPalette.FOREGROUND, size = menuButtonsSize)
        button.addActionListener{
            currentPanel = panel
            updateFrame()
        }

        return button
    }

    private fun setBasicMenuPanel(panel: JPanel, subPanels: Collection<TreeNode<JPanel>>) {
        val buttonsInLine = 2

        panel.removeAll()
        panel.layout = GridBagLayout()
        panel.background = ColorPalette.BACKGROUND
        panel.size = Dimension(mainFrame.width, mainFrame.height - topPanel.height)
        val constraints = GridBagConstraints()
        constraints.fill = GridBagConstraints.HORIZONTAL

        fun setSize(element: JComponent, setSize: Dimension? = null) {
            if (setSize != null) {
                constraints.ipadx = setSize.width - element.minimumSize.width
                constraints.ipady = setSize.height - element.minimumSize.height
                constraints.weightx = 0.0
            } else {
                constraints.ipadx = 0
                constraints.ipady = 0
                constraints.weightx = 1.0
            }
        }

        constraints.gridx = 0
        constraints.gridy = 0
        constraints.weightx = 1.0
        constraints.gridwidth = buttonsInLine + 2
        panel.add(setTitle("Меню"), constraints)

        var counter = 0
        subPanels.forEach {
            if (it.name != "settings") {
                constraints.weightx = 0.0
                constraints.gridwidth = 1
                constraints.gridx = 1 + counter % buttonsInLine
                constraints.gridy = 1 + counter / buttonsInLine
                counter++
                val button = menuButton(it.name, it)
                setSize(button, menuButtonsSize)
                panel.add(button, constraints)
            }
        }

        for (i in 1..(currentPanel!!.count - 1) / 2 + 1) {
            constraints.gridy = i
            constraints.weightx = 1.0
            constraints.gridwidth = 1
            for (j in arrayOf(0, buttonsInLine + 1)) {
                val space = JLabel()
                setSize(space, null)
                constraints.gridx = j
                panel.add(space, constraints)
            }

        }
    }

    private fun setExtendedMenuPanel(panel: JPanel, subPanels: Collection<TreeNode<JPanel>>) {
        setBasicMenuPanel(panel, subPanels)
    }

    private fun setAdminMenuPanel(panel: JPanel, subPanels: Collection<TreeNode<JPanel>>) {
        setBasicMenuPanel(panel, subPanels)
    }

    private fun customButton(title: String, background: Color = Color.WHITE, foreground: Color = Color.BLACK, borderRadius: Int = 50, labelSize: Int = 18, size: Dimension? = null): JButton {
        val button = JButton()
        button.background = ColorPalette.TRANSPARENT
        button.foreground = ColorPalette.TRANSPARENT
        button.border = RoundedBorder(borderRadius, background)

        val label = JLabel(title)
        if (size != null) {
            label.size = size
            label.horizontalTextPosition = JLabel.CENTER
        }
        label.foreground = foreground
        label.font = Font(button.font.name, button.font.style, labelSize)
        button.add(label)

        return button
    }

    private fun setTitle(text: String): JPanel {
        val title = JLabel(text)
        title.font = Font(title.font.name, title.font.style, 25)
        title.foreground = ColorPalette.FOREGROUND
        val titlePanel = JPanel(FlowLayout(FlowLayout.LEFT))
        titlePanel.add(title)
        titlePanel.background = ColorPalette.BACKGROUND
        return titlePanel
    }
}