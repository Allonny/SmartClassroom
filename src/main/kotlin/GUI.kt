import java.awt.*
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.lang.Exception
import java.lang.Integer.max
import javax.swing.*

class GUI {
    private val mainFrame: JFrame = JFrame(Labels.TITLE)

    private val topPanel: JPanel = JPanel()
    private val welcomePanel: JPanel = JPanel()
    private val loginPanel: JPanel = JPanel()

    private val powerMenuPanel: JPanel = JPanel()
    private val settingsBasicPanel: JPanel = JPanel()
    private val settingsExtendedPanel: JPanel = JPanel()
    private val settingsAdminPanel: JPanel = JPanel()

    private val menuBasicPanel: JPanel = JPanel()
    private val menuExtendedPanel: JPanel = JPanel()
    private val menuAdminPanel: JPanel = JPanel()

    private val lightPanel: JPanel = JPanel()
    private val windowPanel: JPanel = JPanel()
    private val powerSupplyPanel: JPanel = JPanel()
    private val addUserPanel: JPanel = JPanel()

    private val rootTree: TreeNode<JPanel> = TreeNode(Labels.ROOT, welcomePanel)
    private val basicSubTree: TreeNode<JPanel> = TreeNode(Labels.BASIC, menuBasicPanel)
    private val extendedSubTree: TreeNode<JPanel> = TreeNode(Labels.EXTENDED, menuExtendedPanel)
    private val adminSubTree: TreeNode<JPanel> = TreeNode(Labels.ADMIN, menuAdminPanel)

    private var currentPanel: TreeNode<JPanel> = rootTree

    private val topPanelButtonsSize = Dimension(70, 70)
    private val loginButtonSize = Dimension(500, 150)
    private val menuButtonsSize = Dimension(300, 150)
    private val settingsFieldSize = Dimension(800, 100)
    private var menuButtonsInLine = 2
    private val menuMinFieldWidth = 100

    private var settings: MutableMap<String, Any> = mutableMapOf()

    init {
        basicSubTree.addChildren(
            Labels.SETTINGS to settingsBasicPanel,
            Labels.LIGHT to lightPanel,
            Labels.WINDOW to windowPanel
        )
        extendedSubTree.addChildren(
            Labels.SETTINGS to settingsExtendedPanel,
            Labels.LIGHT to lightPanel,
            Labels.WINDOW to windowPanel,
            Labels.POWER_SUPPLY to powerSupplyPanel
        )
        adminSubTree.addChildren(
            Labels.SETTINGS to settingsAdminPanel,
            Labels.LIGHT to lightPanel,
            Labels.WINDOW to windowPanel,
            Labels.POWER_SUPPLY to powerSupplyPanel,
            Labels.ADD_USER to addUserPanel
        )
        rootTree.addChildren(
            Labels.POWER_MENU to powerMenuPanel,
            Labels.SETTINGS to settingsBasicPanel,
            Labels.LOGIN to loginPanel
        )
        rootTree[Labels.LOGIN] += basicSubTree
        rootTree[Labels.LOGIN] += extendedSubTree
        rootTree[Labels.LOGIN] += adminSubTree

        rootTree += basicSubTree
        rootTree += extendedSubTree
        rootTree += adminSubTree

        currentPanel = rootTree

        mainFrame.setLocationRelativeTo(null)
        mainFrame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        mainFrame.size = Dimension(1000, 500)

        mainFrame.addComponentListener(object : ComponentAdapter() {
            override fun componentResized(componentEvent: ComponentEvent?) {
//                val newButtonsInLine = max((mainFrame.width - 2 * menuMinFieldWidth) / menuButtonsSize.width, 1)
//                if (menuButtonsInLine != newButtonsInLine) {
//                    menuButtonsInLine = newButtonsInLine
//                    setPanel(rootTree)
//                }

                menuButtonsInLine = max((mainFrame.width - 2 * menuMinFieldWidth) / menuButtonsSize.width, 1)
                setPanel(rootTree)
            }
        })

        setPanel(rootTree)
        updateFrame()
    }

    fun updateValues(newSettings: MutableMap<String, Any>) {
        settings.putAll(newSettings)
        setPanel(rootTree)
        updateFrame()
    }

    private fun updateFrame() {
        println(currentPanel.toString())
        mainFrame.contentPane.isVisible = false
        mainFrame.contentPane.removeAll()
        setTopPanel()
        mainFrame.contentPane.add(BorderLayout.NORTH, topPanel)
        mainFrame.contentPane.add(BorderLayout.CENTER, currentPanel.value)
        mainFrame.contentPane.isVisible = true
        mainFrame.title = Labels[Labels.TITLE].title + " " + Labels[currentPanel.name].titleAlt
        mainFrame.isVisible = true
    }

    private fun setTopPanel() {
        topPanel.isVisible = false
        topPanel.removeAll()
        topPanel.background = Palette.BACKGROUND_ALT
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

        if(currentPanel.parent == currentPanel) {
            val powerIcon = getImage("""resources/images/power.png""", topPanelButtonsSize)
            val powerButton = customButton("Питание", Palette.ACCENT_HIGH, Palette.FOREGROUND, labelSize = 15, icon = powerIcon)
            powerButton.background = Palette.BACKGROUND_ALT
            powerButton.addActionListener{
                currentPanel = currentPanel["powerMenu"]
                updateFrame()
            }
            addElement(powerButton, 1, topPanelButtonsSize)
        } else {
            val backIcon = getImage("""resources/images/corner-up-left.png""", topPanelButtonsSize)
            val backButton = customButton("Назад", Palette.ACCENT_NORMAL, Palette.FOREGROUND, labelSize = 15, icon = backIcon)
            backButton.background = Palette.BACKGROUND_ALT
            backButton.addActionListener {
                currentPanel = currentPanel.parent
                updateFrame()
            }
            addElement(backButton, 1, topPanelButtonsSize)
        }

        addElement(JLabel("Hello world"), 4)

        val settingsIcon = getImage("""resources/images/settings.png""", topPanelButtonsSize)
        var settingsButton = customButton("Настройки", Palette.ACCENT_NORMAL, Palette.FOREGROUND, labelSize = 15, icon = settingsIcon)
        if("settings" in currentPanel.names) {
            settingsButton.addActionListener {
                currentPanel = currentPanel["settings"]
                updateFrame()
            }
        } else {
            settingsButton = customButton("Настройки", Palette.DISABLE, Palette.FOREGROUND_ALT, labelSize = 15, icon = settingsIcon)
            settingsButton.isEnabled = false
        }
        settingsButton.background = Palette.BACKGROUND_ALT
        addElement(settingsButton, 1, topPanelButtonsSize)

        topPanel.isVisible = true
    }

    private fun setPanel(panel: TreeNode<JPanel>?) {
        panel!!.value.isVisible = false
        panel.forEach { setPanel(it) }

        panel.value.removeAll()
        panel.value.layout = BorderLayout()
        panel.value.background = Palette.BACKGROUND
        panel.value.add(setTitle(Labels[panel.name].description), BorderLayout.NORTH)

        when(panel.name) {
            Labels.ROOT, Labels.WELCOME -> setWelcomePanel(panel.value)
            Labels.BASIC, Labels.EXTENDED, Labels.ADMIN -> setMenuPanel(panel.value, panel.children)
            Labels.SETTINGS -> setSettingsPanel(panel.value, panel.children)
            Labels.LOGIN -> setLoginPanel(panel.value, panel.children)
        }

        panel.value.isVisible = true
    }

    private fun setWelcomePanel(panel: JPanel) {
        //val loginButton = menuButton(Labels.LOGIN, rootTree[Labels.LOGIN]!!)
        val loginButton = customButton(Labels[Labels.LOGIN].title, Palette.ACCENT_LOW, Palette.FOREGROUND_ALT, 20, 20)
        loginButton.addActionListener {
            currentPanel = currentPanel[Labels.LOGIN]
            updateFrame()
        }
        panel.add(loginButton, BorderLayout.SOUTH)
    }

    private fun setLoginPanel(panel: JPanel, subPanels: Collection<TreeNode<JPanel>>) {
        val buttonPanel = JPanel()
        val buttonScroll = JScrollPane(buttonPanel)
        val constraints = GridBagConstraints()
        constraints.fill = GridBagConstraints.BOTH
        buttonScroll.border = BorderFactory.createMatteBorder(0, 0, 0, 0, Palette.BACKGROUND)
        buttonScroll.background = Palette.BACKGROUND
        buttonPanel.background = Palette.BACKGROUND

        val gridbag = GridBagLayout()
        buttonPanel.layout = gridbag

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

        var counter = 0
        subPanels.forEach {
            constraints.gridy = counter
            constraints.weightx = 1.0
            constraints.gridwidth = 1
            for (j in arrayOf(0, 2)) {
                val space = JLabel()
                setSize(space, null)
                constraints.gridx = j
                buttonPanel.add(space, constraints)
            }

            constraints.weightx = 0.0
            constraints.gridwidth = 1
            constraints.gridx = 1
            constraints.gridy = counter
            counter++
            constraints.insets = Insets(10, 10, 10, 10)
            val button = menuButton(it.name, it)
            setSize(button, loginButtonSize)
            buttonPanel.add(button, constraints)
        }

        gridbag.setConstraints(buttonScroll, constraints)
        panel.add(buttonScroll, BorderLayout.CENTER)
    }

    private fun setSettingsPanel(panel: JPanel, subPanels: Collection<TreeNode<JPanel>>) {
        val settingsPanel = JPanel()
        val settingsScroll = JScrollPane(settingsPanel)
        val constraints = GridBagConstraints()
        constraints.fill = GridBagConstraints.BOTH
        settingsScroll.border = BorderFactory.createMatteBorder(0, 0, 0, 0, Palette.BACKGROUND)
        settingsScroll.background = Palette.BACKGROUND
        settingsPanel.background = Palette.BACKGROUND

        val gridbag = GridBagLayout()
        settingsPanel.layout = gridbag

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

        val portTitle = JLabel("Порт")
        val portStatus = JLabel(if (settings.contains("port")) settings["port"].toString() else "Устройство отсутствует")
        val portUpdate = JButton("Поиск порта")

        for (i in 1..5) {
            constraints.gridy = i
            constraints.weightx = 1.0
            constraints.gridwidth = 1
            for (j in arrayOf(0, 2)) {
                val space = JLabel()
                setSize(space, null)
                constraints.gridx = j
                settingsPanel.add(space, constraints)
            }
            constraints.weightx = 0.0
            constraints.gridwidth = 1
            constraints.gridx = 1
            constraints.gridy = i
            constraints.insets = Insets(10, 10, 10, 10)
            val field = JPanel()
            field.background = Palette.BACKGROUND
            field.border = RoundedBorder(50, Palette.BACKGROUND_ALT)

            val portSetup = JPanel()
            portSetup.layout = GridLayout(1, 3, 5, 5)
            portSetup.add(portTitle)
            portSetup.add(portStatus)
            portSetup.add(portUpdate)
            field.add(portSetup)

            setSize(field, settingsFieldSize)
            settingsPanel.add(field, constraints)

        }

        gridbag.setConstraints(settingsScroll, constraints)
        panel.add(settingsScroll, BorderLayout.CENTER)


//
//        panel.add(portTitle)
//        panel.add(portStatus)
//        panel.add(portUpdate)
    }

    private fun menuButton(title: String, panel: TreeNode<JPanel>): JButton {
        val button = customButton(Labels[title].title, Palette.ACCENT_NORMAL, Palette.FOREGROUND)
        button.addActionListener{
            currentPanel = panel
            updateFrame()
        }
        return button
    }

    private fun setMenuPanel(panel: JPanel, subPanels: Collection<TreeNode<JPanel>>) {
        val buttonPanel = JPanel()
        val buttonScroll = JScrollPane(buttonPanel)
        val constraints = GridBagConstraints()
        constraints.fill = GridBagConstraints.BOTH
        buttonScroll.border = BorderFactory.createMatteBorder(0, 0, 0, 0, Palette.BACKGROUND)
        buttonScroll.background = Palette.BACKGROUND
        buttonPanel.background = Palette.BACKGROUND

        val gridbag = GridBagLayout()
        buttonPanel.layout = gridbag

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

        var counter = 0
        subPanels.forEach {
            if (it.name != "settings") {
                constraints.weightx = 0.0
                constraints.gridwidth = 1
                constraints.gridx = 1 + counter % menuButtonsInLine
                constraints.gridy = counter / menuButtonsInLine
                counter++
                constraints.insets = Insets(10, 10, 10, 10)
                val button = menuButton(it.name, it)
                setSize(button, menuButtonsSize)
                buttonPanel.add(button, constraints)
            }
        }

        for (i in 0..(currentPanel.count - 1) / 2) {
            constraints.gridy = i
            constraints.weightx = 1.0
            constraints.gridwidth = 1
            for (j in arrayOf(0, menuButtonsInLine + 1)) {
                val space = JLabel()
                setSize(space, null)
                constraints.gridx = j
                buttonPanel.add(space, constraints)
            }
        }

        gridbag.setConstraints(buttonScroll, constraints)
        panel.add(buttonScroll, BorderLayout.CENTER)
    }

    private fun customButton(title: String, background: Color = Color.WHITE, foreground: Color = Color.BLACK, borderRadius: Int = 50, labelSize: Int = 18, icon: ImageIcon? = null): JButton {
        val button = JButton()
        button.isDoubleBuffered = true
        button.background = Palette.BACKGROUND
        button.foreground = Palette.TRANSPARENT
        button.border = RoundedBorder(borderRadius, background)
        val buttonPanel = JPanel()
        buttonPanel.layout = BorderLayout()
        buttonPanel.background = Palette.TRANSPARENT
        button.add(buttonPanel)

        if (icon == null) {
            val label = JLabel(title)

            label.horizontalAlignment = JLabel.CENTER
            label.background = background
            label.foreground = foreground
            label.font = Fonts.REGULAR_FONT.deriveFont(labelSize.toFloat())//Font(button.font.name, button.font.style, labelSize)
            buttonPanel.add(label)
        } else {
            val label = JLabel(icon)
            label.size = Dimension(labelSize, labelSize)
            button.toolTipText = title
            buttonPanel.add(JLabel(icon))
        }

        return button
    }

    private fun setTitle(text: String): JPanel {
        val title = JLabel(text.uppercase())
        title.font = Fonts.TITLE_FONT.deriveFont(50f)
        while (title.maximumSize.width > (mainFrame.width - 20f)) {
            title.font = Fonts.TITLE_FONT.deriveFont(title.font.size2D - 1f)
        }
        title.foreground = Palette.FOREGROUND
        val titlePanel = JPanel(FlowLayout(FlowLayout.LEFT))
        titlePanel.add(title)
        titlePanel.background = Palette.BACKGROUND
        return titlePanel
    }

    private fun getImage(pathname: String, size: Dimension? = null) : ImageIcon {
        return try {
            var icon = ImageIcon(pathname)
            if (size != null) icon = ImageIcon(icon.image.getScaledInstance(topPanelButtonsSize.width / 2, topPanelButtonsSize.height / 2, Image.SCALE_SMOOTH))
            icon
        } catch (e: Exception) {
            ImageIcon()
        }
    }
}