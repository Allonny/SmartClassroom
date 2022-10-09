import kotlinx.coroutines.*
import java.awt.*
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.lang.Integer.max
import java.lang.Integer.min
import java.text.SimpleDateFormat
import java.util.*
import javax.swing.*
import javax.swing.Timer

class GUI {
    private val mainFrame: JFrame = JFrame(Labels.TITLE)

    private val topPanel: JPanel = JPanel()
    private val timeDatePanel: JPanel = JPanel()
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

    private val topPanelButtonsSize = Dimension(75, 75)
    private val topPanelButtonsInsets = Insets(10, 10, 10, 10)
    private val loginButtonsSize = Dimension(450, 50)
    private val loginButtonsInsets = Insets(10, 10, 10, 10)
    private val menuButtonsSize = Dimension(400, 125)
    private val menuButtonsInsets = Insets(10, 10, 10, 10)
    private val settingsFieldSize = Dimension(800, 100)
    private var menuButtonsInLine = 2
    private val menuMinFieldWidth = 150
    private val maxTitleHeight = 100

    private val topPanelIcons = Icons(min(topPanelButtonsSize.width, topPanelButtonsSize.height),
        Labels.POWER_MENU to Icons.Foreground.POWER_MENU,
        Labels.BACK to Icons.Foreground.BACK,
        Labels.SETTINGS to Icons.Foreground.SETTINGS,
        "${Labels.SETTINGS}_alt" to Icons.ForegroundAlt.SETTINGS)
    private val menuButtonIcons = Icons(min(menuButtonsSize.width, menuButtonsSize.height),
        Labels.LIGHT to Icons.Foreground.LIGHT_OFF,
        Labels.WINDOW to Icons.Foreground.WINDOW,
        Labels.POWER_SUPPLY to Icons.Foreground.POWER_SUPPLY,
        Labels.ADD_USER to Icons.Foreground.ADD_USER)
    private val loginButtonIcon = Icons(min(loginButtonsSize.width, loginButtonsSize.height), Labels.LOGIN to Icons.ForegroundAlt.LOGIN)

    private var settings: MutableMap<String, Any> = mutableMapOf()

    init {
        setTimeDatePanel()
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
        mainFrame.background = Palette.BACKGROUND
        mainFrame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        mainFrame.size = Dimension(1000, 500)

        mainFrame.addComponentListener(object : ComponentAdapter() {
            override fun componentResized(componentEvent: ComponentEvent?) {
                val panelsForUpdate = arrayListOf(Labels.TITLE, Labels.ROOT)
                val newButtonsInLine = max((mainFrame.width - 2 * menuMinFieldWidth) / menuButtonsSize.width, 1)
                if (menuButtonsInLine != newButtonsInLine) {
                    menuButtonsInLine = newButtonsInLine
                    panelsForUpdate.addAll(arrayOf(Labels.BASIC, Labels.EXTENDED, Labels.ADMIN))
                }
                setPanel(rootTree, include = panelsForUpdate.toTypedArray())
                updateFrame(forceUpdate = true)
            }
        })

        setPanel(rootTree)
        updateFrame(forceUpdate = true)
    }

//    fun updateValues(newSettings: MutableMap<String, Any>) {
//        settings.putAll(newSettings)
//        setPanel(rootTree, include = arrayOf(Labels.SETTINGS))
//        updateFrame()
//    }

    private fun updateFrame(newCurrentPanel: TreeNode<JPanel> = currentPanel, forceUpdate: Boolean = true) {
        mainFrame.contentPane.isVisible = false
        if (newCurrentPanel != currentPanel || forceUpdate) {
            currentPanel = newCurrentPanel
            mainFrame.contentPane.removeAll()
            setTopPanel()
            mainFrame.contentPane.add(BorderLayout.NORTH, topPanel)
            mainFrame.contentPane.add(BorderLayout.CENTER, currentPanel.value)
            mainFrame.title = Labels[Labels.NAME].title + " | " + Labels[currentPanel.name].titleAlt
        }
        mainFrame.contentPane.isVisible = true
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
        constraints.insets = topPanelButtonsInsets

        if(currentPanel.parent == currentPanel) {
            val powerButton = customButton(Labels[Labels.POWER_MENU].title, Palette.ACCENT_HIGH, Palette.FOREGROUND, Palette.BACKGROUND_ALT, icon = topPanelIcons[Labels.POWER_MENU], iconOnly = true)
            powerButton.addActionListener{
                updateFrame(rootTree[Labels.POWER_MENU])
            }
            addElement(powerButton, 1, topPanelButtonsSize)
        } else {
            val backButton = customButton(Labels[Labels.BACK].title, Palette.ACCENT_NORMAL, Palette.FOREGROUND, Palette.BACKGROUND_ALT, icon = topPanelIcons[Labels.BACK], iconOnly = true)
            backButton.addActionListener { updateFrame(currentPanel.parent) }
            addElement(backButton, 1, topPanelButtonsSize)
        }

        addElement(timeDatePanel, 1)

        val settingsButton: JButton
        if("settings" in currentPanel.names) {
            settingsButton = customButton("Настройки", Palette.ACCENT_NORMAL, Palette.FOREGROUND, Palette.BACKGROUND_ALT, icon = topPanelIcons[Labels.SETTINGS], iconOnly = true)
            settingsButton.addActionListener { updateFrame(currentPanel[Labels.SETTINGS]) }
        } else {
            settingsButton = customButton("Настройки", Palette.DISABLE, Palette.FOREGROUND_ALT, Palette.BACKGROUND_ALT, icon = topPanelIcons[Labels.SETTINGS + "_alt"], iconOnly = true)
            settingsButton.isEnabled = false
        }
        addElement(settingsButton, 1, topPanelButtonsSize)

        topPanel.isVisible = true
    }

    private fun setTimeDatePanel() {
        timeDatePanel.isVisible = false
        timeDatePanel.removeAll()
        timeDatePanel.background = Palette.BACKGROUND_ALT
//        timeDatePanel.layout = BorderLayout()
        timeDatePanel.layout = BoxLayout(timeDatePanel, BoxLayout.PAGE_AXIS)
        timeDatePanel.alignmentX = Component.CENTER_ALIGNMENT

        val time = JLabel()
        time.font = Fonts.REGULAR_ALT.deriveFont(45f)
        time.foreground = Palette.FOREGROUND_ALT
        time.alignmentX = Component.CENTER_ALIGNMENT
        val date = JLabel()
        date.font = Fonts.REGULAR_ALT.deriveFont(25f)
        date.foreground = Palette.FOREGROUND_ALT
        date.alignmentX = Component.CENTER_ALIGNMENT

        timeDatePanel.add(time)
        timeDatePanel.add(date)

        Timer(1000) {
            time.text = SimpleDateFormat("hh : mm").format(Date())
            date.text = SimpleDateFormat("EEEE, dd MMMM yyyy").format(Date())
        }.start()

        this.timeDatePanel.isVisible = true
    }

    private fun setPanel(panel: TreeNode<JPanel>, exclude: Array<String> = arrayOf(), include: Array<String> = arrayOf()) {
        fun drawTitle() {
            try {
                val component = (panel.value.layout as BorderLayout).getLayoutComponent(BorderLayout.NORTH)
                if (component != null) panel.value.remove(component)
            } finally {
                panel.value.add(setTitle(Labels[panel.name].description), BorderLayout.NORTH)
            }
        }

        fun drawContent() {
            try {
                val component = (panel.value.layout as BorderLayout).getLayoutComponent(BorderLayout.NORTH)
                panel.value.removeAll()
                if (component != null) panel.value.add(component, BorderLayout.NORTH)
            } finally {
                when(panel.name) {
                    Labels.ROOT, Labels.WELCOME -> setWelcomePanel(panel)
                    Labels.BASIC, Labels.EXTENDED, Labels.ADMIN -> setMenuPanel(panel)
                    Labels.SETTINGS -> setSettingsPanel(panel)
                    Labels.LOGIN -> setLoginPanel(panel)
                }
            }

        }

        if (exclude.isNotEmpty() && include.isNotEmpty()) return

        runBlocking {
            coroutineScope {
                panel.value.isVisible = false
                if (exclude.isNotEmpty()) {
                    launch { if (Labels.TITLE !in exclude) drawTitle() }
                    launch { if (panel.name !in exclude) drawContent() }
                } else if (include.isNotEmpty()) {
                    launch { if (Labels.TITLE in include) drawTitle() }
                    launch { if (panel.name in include) drawContent() }
                } else {
                    if (panel.value.layout != BorderLayout()) panel.value.layout = BorderLayout()
                    panel.value.background = Palette.BACKGROUND
                    launch { drawTitle() }
                    launch { drawContent() }
                }
                panel.value.isVisible = true
                mainFrame.isVisible = true
                panel.forEach { launch { setPanel(it, exclude, include) } }
            }
        }
    }

    private fun setWelcomePanel(panel: TreeNode<JPanel>) {
        val text = Box(BoxLayout.Y_AXIS)
        Labels[Labels.MESSAGE].other.forEach {
            val minHeight = mainFrame.height -
                    (topPanelButtonsSize.height + topPanelButtonsInsets.top + topPanelButtonsInsets.bottom) -
                    (loginButtonsSize.height + loginButtonsInsets.top + loginButtonsInsets.bottom)
            val minWidth = mainFrame.width - menuMinFieldWidth
            val maxSize = min(minWidth, minHeight)
            val line = adaptiveWidthLabel(it.value.toString(), Fonts.TITLE_ALT, maxSize, maxSize)
            line.foreground = Palette.DISABLE
            text.add(line)
        }
        panel.value.add(text, BorderLayout.EAST)

        val buttonPanel = JPanel()
        val constraints = GridBagConstraints()
        constraints.fill = GridBagConstraints.HORIZONTAL
        buttonPanel.background = Palette.BACKGROUND

        val layout = GridBagLayout()
        buttonPanel.layout = layout

        constraints.weightx = 1.0
        constraints.gridwidth = 1
        for (j in arrayOf(0, 2)) {
            val space = JLabel()
            constraints.gridx = j
            buttonPanel.add(space, constraints)
        }

        val loginButton = customButton(Labels[Labels.LOGIN].title, Palette.ACCENT_LOW, Palette.FOREGROUND_ALT, Palette.BACKGROUND, 25, 20, loginButtonIcon[Labels.LOGIN])
        loginButton.addActionListener { updateFrame(rootTree[Labels.LOGIN]) }

        constraints.ipadx = loginButtonsSize.width - loginButton.minimumSize.width
        constraints.ipady = loginButtonsSize.height - loginButton.minimumSize.height
        constraints.weightx = 0.0
        constraints.gridwidth = 1
        constraints.gridx = 1
        constraints.insets = loginButtonsInsets
        buttonPanel.add(loginButton, constraints)

        layout.setConstraints(buttonPanel, constraints)
        panel.value.add(buttonPanel, BorderLayout.SOUTH)
    }

    private fun setLoginPanel(panel: TreeNode<JPanel>) {
        val buttonPanel = JPanel()
        val buttonScroll = JScrollPane(buttonPanel)
        val constraints = GridBagConstraints()
        constraints.fill = GridBagConstraints.BOTH
        buttonScroll.border = BorderFactory.createMatteBorder(0, 0, 0, 0, Palette.BACKGROUND)
        buttonScroll.background = Palette.BACKGROUND
        buttonPanel.background = Palette.BACKGROUND

        val layout = GridBagLayout()
        buttonPanel.layout = layout

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
        panel.forEach {
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
            constraints.insets = loginButtonsInsets
            val button = customButton(Labels[it.name].title, Palette.ACCENT_NORMAL, Palette.FOREGROUND, Palette.BACKGROUND, 25, 20)
            button.addActionListener { _ -> updateFrame(it) }
            setSize(button, loginButtonsSize)
            buttonPanel.add(button, constraints)
        }

        layout.setConstraints(buttonScroll, constraints)
        panel.value.add(buttonScroll, BorderLayout.CENTER)
    }

    private fun setSettingsPanel(panel: TreeNode<JPanel>) {
        val settingsPanel = JPanel()
        val settingsScroll = JScrollPane(settingsPanel)
        val constraints = GridBagConstraints()
        constraints.fill = GridBagConstraints.BOTH
        settingsScroll.border = BorderFactory.createMatteBorder(0, 0, 0, 0, Palette.BACKGROUND)
        settingsScroll.background = Palette.BACKGROUND
        settingsPanel.background = Palette.BACKGROUND

        val layout = GridBagLayout()
        settingsPanel.layout = layout

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

        layout.setConstraints(settingsScroll, constraints)
        panel.value.add(settingsScroll, BorderLayout.CENTER)


//
//        panel.add(portTitle)
//        panel.add(portStatus)
//        panel.add(portUpdate)
    }

    private fun menuButton(panel: TreeNode<JPanel>): JButton {
        var icon : ImageIcon? = null
        when(panel.name) {
            Labels.LIGHT -> icon = menuButtonIcons[Labels.LIGHT]
            Labels.WINDOW -> icon = menuButtonIcons[Labels.WINDOW]
            Labels.POWER_SUPPLY -> icon = menuButtonIcons[Labels.POWER_SUPPLY]
            Labels.ADD_USER -> icon = menuButtonIcons[Labels.ADD_USER]
        }

        val button = customButton(Labels[panel.name].title, Palette.ACCENT_NORMAL, Palette.FOREGROUND, Palette.BACKGROUND, 50, 30, icon)
        button.addActionListener{ updateFrame(panel) }
        return button
    }

    private fun setMenuPanel(panel: TreeNode<JPanel>) {
        val buttonPanel = JPanel()
        val buttonScroll = JScrollPane(buttonPanel)
        val constraints = GridBagConstraints()
        constraints.fill = GridBagConstraints.BOTH
        buttonScroll.border = BorderFactory.createMatteBorder(0, 0, 0, 0, Palette.BACKGROUND)
        buttonScroll.background = Palette.BACKGROUND
        buttonPanel.background = Palette.BACKGROUND

        val layout = GridBagLayout()
        buttonPanel.layout = layout

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
        panel.forEach {
            if (it.name != Labels.SETTINGS) {
                constraints.weightx = 0.0
                constraints.gridwidth = 1
                constraints.gridx = 1 + counter % menuButtonsInLine
                constraints.gridy = counter / menuButtonsInLine
                counter++
                constraints.insets = menuButtonsInsets
                val button = menuButton(it)
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

        layout.setConstraints(buttonScroll, constraints)
        panel.value.add(buttonScroll, BorderLayout.CENTER)
    }

    private fun customButton(
        title: String,
        background: Color = Color.WHITE,
        foreground: Color = Color.BLACK,
        backing: Color = Color.GRAY,
        borderRadius: Int = 50,
        labelSize: Int = 25,
        icon: ImageIcon? = null,
        iconOnly: Boolean = false
    ): JButton {
        val button = JButton()
        button.isDoubleBuffered = true
        button.background = backing
        button.foreground = Palette.TRANSPARENT
        button.border = RoundedBorder(borderRadius, background)
        val buttonPanel = JPanel()
        buttonPanel.layout = BorderLayout()
        buttonPanel.background = Palette.TRANSPARENT
        button.add(buttonPanel)

        fun getLabel(): JPanel {
            val text = JPanel()
            text.layout = GridLayout(0, 1)
            text.background = background
            title.split("\n").forEach {
                val line = JLabel(it)
                line.background = background
                line.foreground = foreground
                line.horizontalAlignment = JLabel.CENTER
                line.font = Fonts.REGULAR.deriveFont(labelSize.toFloat())
                text.add(line)
            }
            return text
        }

        fun getIcon(): JLabel {
            return JLabel(icon)
        }

        if (icon == null) {
            buttonPanel.add(getLabel(), BorderLayout.CENTER)
        } else if (iconOnly) {
            buttonPanel.add(getIcon(), BorderLayout.CENTER)
            button.toolTipText = title
        } else {
            buttonPanel.add(getLabel(), BorderLayout.CENTER)
            buttonPanel.add(getIcon(), BorderLayout.WEST)
        }

        return button
    }

    private fun setTitle(text: String): JPanel {
        val title = adaptiveWidthLabel(text.uppercase(), Fonts.TITLE, mainFrame.width - menuMinFieldWidth, maxTitleHeight)
        title.foreground = Palette.FOREGROUND
        val titlePanel = JPanel(FlowLayout(FlowLayout.LEFT))
        titlePanel.add(title)
        titlePanel.background = Palette.BACKGROUND
        return titlePanel
    }

    private fun adaptiveWidthLabel(text: String, font: Font, maxWidth: Int, maxHeight: Int, maxIteration: Int = 0): JLabel {
        val label = JLabel(text)
        label.font = font
        var fontSize = 1f
        var iteration = 0
        do {
            label.font = label.font.deriveFont(fontSize)
            fontSize += (maxWidth - label.maximumSize.width) / 250f
        } while ( (maxWidth - label.maximumSize.width) > 10  && (maxHeight - label.maximumSize.height) > 10 && (iteration++ < maxIteration || maxIteration == 0) )
        return label
    }
}