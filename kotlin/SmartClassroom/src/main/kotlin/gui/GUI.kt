package gui

import SerialBus
import auxiliary.*
import gui.materialSwing.*
import gui.panels.BasePanel
import gui.panels.LoginPanel
import gui.panels.MenuPanel
import gui.panels.WelcomePanel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.awt.*
import java.awt.event.*
import java.lang.Integer.max
import java.lang.Integer.min
import java.text.SimpleDateFormat
import java.util.*
import javax.swing.*
import javax.swing.Timer


class GUI (val serialBus: SerialBus)
{
    private val mainFrame: JFrame = JFrame(Labels.TITLE)

    private val topPanel: JPanel = JPanel()
    private val timeDatePanel: JPanel = JPanel()

    private val welcomePanel: BasePanel = WelcomePanel(mainFrame)
    private val loginPanel: BasePanel = LoginPanel(mainFrame)
    private val powerMenuPanel: BasePanel = BasePanel(mainFrame)
    private val menuPanel: BasePanel = MenuPanel(mainFrame)

//    private val settingsRootPanel: JPanel = JPanel(BorderLayout())
//    private val settingsBasicPanel: JPanel = JPanel(BorderLayout())
//    private val settingsExtendedPanel: JPanel = JPanel(BorderLayout())
//    private val settingsAdminPanel: JPanel = JPanel(BorderLayout())
//
//    private val portConnectSetting: JPanel = JPanel(BorderLayout())
//    private val serialLogSetting: JPanel = JPanel(BorderLayout())
//
//    private val lightPanel: JPanel = JPanel(BorderLayout())
//    private val windowPanel: JPanel = JPanel(BorderLayout())
//    private val powerSupplyPanel: JPanel = JPanel(BorderLayout())
//    private val addUserPanel: JPanel = JPanel(BorderLayout())

    private val rootTree: TreeNode<BasePanel> = TreeNode(Labels.ROOT, welcomePanel)
    private val userTree: TreeNode<BasePanel> = TreeNode(Labels.USER, menuPanel)

    private var currentSubTree: TreeNode<BasePanel> = rootTree
    private var currentPanel: TreeNode<BasePanel> = rootTree

    private val topPanelSize = Dimension(0, 95)
    private val topPanelButtonsSize = Dimension(75, 75)
    private val topPanelButtonsInsets = Insets((topPanelSize.height - topPanelButtonsSize.height) / 2, 10, (topPanelSize.height - topPanelButtonsSize.height) / 2, 10)
    private val loginButtonsSize = Dimension(450, 50)
    private val menuButtonsSize = Dimension(400, 125)
    private val menuButtonsInsets = Insets(10, 10, 10, 10)
    private val menuFieldSize = Dimension(400, 400)
    private val menuSwitchButtonSize = Dimension(150, 150)
    private val menuSwitchButtonInsets = Insets(25, 10, 25, 10)
    private val settingsFieldSize = Dimension(800, 100)
    private val settingsFieldInsets = Insets(10, 10, 10, 10)
    private val settingButtonSize = Dimension(50, 50)
    private val settingTerminalSize = Dimension(800, 500)
    private val settingTerminalRecordInsets = Insets(10, 10, 10, 10)
    private val buttonCornerRadius = 50
    private val scrollSpeed = 10
    private var menuButtonsInLine = 2
    private val sideFieldsWidth = 150
    private val maxTitleHeight = 100

    private val topPanelIcons = Icons(
        min(topPanelButtonsSize.width, topPanelButtonsSize.height),
        Labels.POWER_MENU to Icons.Foreground.POWER_MENU,
        Labels.BACK to Icons.Foreground.BACK,
        Labels.SETTINGS to Icons.Foreground.SETTINGS,
        "${Labels.SETTINGS}_alt" to Icons.ForegroundAlt.SETTINGS
    )
    private val menuButtonIcons = Icons(
        min(menuButtonsSize.width, menuButtonsSize.height),
        Labels.LIGHT to Icons.Foreground.LIGHT_ON,
        Labels.WINDOW to Icons.Foreground.WINDOW,
        Labels.POWER_SUPPLY to Icons.Foreground.POWER_SUPPLY,
        Labels.ADD_USER to Icons.Foreground.ADD_USER
    )
    private val loginButtonIcon =
        Icons(min(loginButtonsSize.width, loginButtonsSize.height), Labels.LOGIN to Icons.ForegroundAlt.LOGIN)
    private val settingsIcons = Icons(
        settingButtonSize.width,
        Labels.EXPAND to Icons.Foreground.EXPAND,
        Labels.COLLAPSE to Icons.Foreground.COLLAPSE,
        Labels.PORT_CONNECT to Icons.ForegroundAlt.SERIAL,
        Labels.SERIAL_LOG to Icons.ForegroundAlt.CONSOLE
    )
    private val menuLightIcons = Icons(
        menuSwitchButtonSize.width,
        Labels.SWITCH_ON to Icons.Foreground.LIGHT_ON,
        Labels.SWITCH_OFF to Icons.Foreground.LIGHT_OFF
    )
    private val expandedSettings: MutableMap<String, Boolean> = mutableMapOf(
        Labels.SERIAL_LOG to false,
        Labels.PORT_CONNECT to false
    ).withDefault { false }

    private var settings: MutableMap<String, Any> = mutableMapOf(
        Labels.LIGHT_GROUP + Labels.COUNT to 4
    )

    init {
//        grabSerialBus()
//        setTimeDatePanel()

//        if (settings.contains(Labels.LIGHT_GROUP + Labels.COUNT)) {
//            for (i in 1 .. settings[Labels.LIGHT_GROUP + Labels.COUNT] as Int) {
//                settings[Labels.LIGHT_GROUP + "_$i"] = 0
//            }
//        }
//        println(settings)

        rootTree.addChildren(
            Labels.POWER_MENU to powerMenuPanel,
            Labels.LOGIN to loginPanel
        )
        rootTree[Labels.LOGIN] += userTree
        rootTree += userTree

        welcomePanel.addActionListener { action -> grubPanelAction(action) }
        loginPanel.addActionListener { action -> grubPanelAction(action) }
        loginPanel.actionContent = arrayOf(Labels.BASIC, Labels.EXTENDED, Labels.ADMIN)

        currentPanel = rootTree

        mainFrame.addComponentListener(object : ComponentAdapter() {
            override fun componentResized(componentEvent: ComponentEvent?) {
                val panelsForUpdate = arrayListOf(Labels.TITLE, Labels.ROOT)
                val newButtonsInLine = max((mainFrame.width - 2 * sideFieldsWidth) / menuButtonsSize.width, 1)
                if (menuButtonsInLine != newButtonsInLine) {
                    menuButtonsInLine = newButtonsInLine
                    (menuPanel as MenuPanel).buttonsInLine = menuButtonsInLine
                    panelsForUpdate.add(Labels.USER)
                }

                setPanel(rootTree, include = panelsForUpdate.toTypedArray(), force = true)
            }
        })

        mainFrame.background = Palette.BACKGROUND
        mainFrame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        mainFrame.size = Dimension(1280, 720)
        mainFrame.setLocationRelativeTo(null)
        mainFrame.iconImage = ImageIcon(Icons.LOGO).image

        setPanel(rootTree)
        updateFrame(force = true)
    }

    private fun grabSerialBus() {
        serialBus.addDataReceivedListener {
            it.forEach { (param, value) ->
                when (param) {
                    SerialBus.LABEL_SYSTEM -> println("$param - $value")
                    SerialBus.LABEL_RESET -> println("$param - $value")
                    SerialBus.LABEL_ECHO -> println("$param - $value")
                    SerialBus.LABEL_ERROR -> println("$param - $value")
                    SerialBus.LABEL_UID -> println("$param - $value")
                    SerialBus.LABEL_LIGHT -> println("$param - $value")
                    SerialBus.LABEL_WINDOW -> println("$param - $value")
                    SerialBus.LABEL_POWER_SUPPLY -> println("$param - $value")
                    SerialBus.LABEL_STARTUP -> println("$param - $value")
                    SerialBus.LABEL_SAVE -> println("$param - $value")
                    SerialBus.LABEL_LOAD -> println("$param - $value")
                }
            }
            serialBus.remove()
        }

        serialBus.addPortFoundListener {
            if (currentPanel.name == Labels.SETTINGS) setPanel(currentPanel[Labels.PORT_CONNECT], force = true)
            println("Найден $it")
        }

        serialBus.addUpdateLogListener {
            if (currentPanel.name == Labels.SETTINGS) setPanel(currentPanel[Labels.SERIAL_LOG], force = true)
        }
    }

    private fun grubPanelAction(action: String?) {
        when(action) {
            Labels.LOGIN -> updateFrame(rootTree[Labels.LOGIN])
            Labels.BASIC, Labels.EXTENDED, Labels.ADMIN -> {
                menuPanel.actionContent = GUIConstants.menusOptions[action] as Array<String>
                setPanel(userTree)
                updateFrame(rootTree[Labels.USER])
            }
        }
    }

    private fun updateFrame(newCurrentPanel: TreeNode<BasePanel> = currentPanel, force: Boolean = false) {
        mainFrame.contentPane.isVisible = false
        if (newCurrentPanel != currentPanel || force) {

            if (currentSubTree.name == Labels.ROOT && newCurrentPanel.name == Labels.USER ||
                newCurrentPanel.name == Labels.ROOT && currentSubTree.name == Labels.USER) {
                currentSubTree = newCurrentPanel
                setPanel(currentSubTree)
            }

            if (currentPanel.name == Labels.SETTINGS) {
                var flag = false
                expandedSettings.keys.forEach {
                    flag = flag || expandedSettings.getValue(it); expandedSettings[it] = false
                }
                if (flag) setPanel(currentSubTree, include = arrayOf(Labels.SETTINGS))
            }

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

        if (currentPanel.parent == currentPanel) {
            val powerButton = MaterialButton(Labels[Labels.POWER_MENU].title, topPanelIcons[Labels.POWER_MENU])
            powerButton.cornerRadius = buttonCornerRadius
            powerButton.backingColor = Palette.BACKGROUND_ALT
            powerButton.backgroundColor = Palette.ACCENT_HIGH
            powerButton.foregroundColor = Palette.FOREGROUND

            powerButton.addActionListener { updateFrame(rootTree[Labels.POWER_MENU]) }
            addElement(powerButton, 1, topPanelButtonsSize)
        } else {
            val backButton = MaterialButton(Labels[Labels.BACK].title, topPanelIcons[Labels.BACK])
            backButton.cornerRadius = buttonCornerRadius
            backButton.backingColor = Palette.BACKGROUND_ALT
            backButton.backgroundColor = Palette.ACCENT_NORMAL
            backButton.foregroundColor = Palette.FOREGROUND

            backButton.addActionListener { updateFrame(currentPanel.parent) }
            addElement(backButton, 1, topPanelButtonsSize)
        }

        addElement(timeDatePanel, 1)

        val settingsButton = MaterialButton(Labels[Labels.SETTINGS].title, topPanelIcons[Labels.SETTINGS])
        settingsButton.cornerRadius = buttonCornerRadius
        settingsButton.disableFaceIcon = topPanelIcons[Labels.SETTINGS + "_alt"]
        settingsButton.backingColor = Palette.BACKGROUND_ALT
        settingsButton.backgroundColor = Palette.ACCENT_NORMAL
        settingsButton.disableBackgroundColor = Palette.DISABLE
        settingsButton.foregroundColor = Palette.FOREGROUND
        settingsButton.disableForegroundColor = Palette.FOREGROUND_ALT

        settingsButton.isEnabled = Labels.SETTINGS in currentPanel.names
        settingsButton.addActionListener { updateFrame(currentPanel[Labels.SETTINGS]) }

        addElement(settingsButton, 1, topPanelButtonsSize)

        topPanel.isVisible = true
    }

    private fun setTimeDatePanel() {
        timeDatePanel.isVisible = false
        timeDatePanel.removeAll()
        timeDatePanel.background = Palette.BACKGROUND_ALT
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
            time.text = SimpleDateFormat("HH : mm").format(Date())
            date.text = SimpleDateFormat("EEEE, dd MMMM yyyy").format(Date())
        }.start()

        this.timeDatePanel.isVisible = true
    }

    private fun setPanel(panel: TreeNode<BasePanel>, exclude: Array<String> = arrayOf(), include: Array<String> = arrayOf(), force: Boolean = false) {
        fun drawTitle(panel: TreeNode<BasePanel>) {
            panel.value.setTitle()
        }

        fun drawContent(panel: TreeNode<BasePanel>) {
            var scrollPosition = 0
            panel.value.components.forEach { if (it.javaClass.name.contains("JScrollPane")) scrollPosition = (it as JScrollPane).verticalScrollBar.value }
            panel.value.setContent()
        }

        suspend fun recursiveSet(panel: TreeNode<BasePanel>, exclude: Array<String> = arrayOf(), include: Array<String> = arrayOf()) {
            coroutineScope {
                if (panel.name != Labels.LOGIN) panel.forEach { recursiveSet(it, exclude, include) }
                if (exclude.isNotEmpty()) {
                    launch { if (Labels.TITLE !in exclude) drawTitle(panel) }
                    launch { if (panel.name !in exclude && (panel.parent.name == currentSubTree.name || force)) drawContent(panel) }
                } else if (include.isNotEmpty()) {
                    launch { if (Labels.TITLE in include) drawTitle(panel) }
                    launch { if (panel.name in include && (panel.parent.name == currentSubTree.name || force)) drawContent(panel) }
                } else {
                    panel.value.background = Palette.BACKGROUND
                    launch { drawTitle(panel) }
                    launch { if (panel.parent.name == currentSubTree.name || force) drawContent(panel) }
                }
            }
        }

        if (exclude.isNotEmpty() && include.isNotEmpty()) return
        runBlocking { recursiveSet(panel, exclude, include) }
        updateFrame()
    }

    private fun setSettingsPanel(panel: TreeNode<BasePanel>, scrollPosition: Int = 0) {
        val settingsPanel = JPanel()
        val settingsScroll = JScrollPane(settingsPanel)
        val constraints = GridBagConstraints()
        constraints.fill = GridBagConstraints.BOTH
        settingsScroll.border = BorderFactory.createMatteBorder(0, 0, 0, 0, Palette.BACKGROUND)
        settingsScroll.background = Palette.BACKGROUND
        settingsPanel.background = Palette.BACKGROUND

        val layout = GridBagLayout()
        settingsPanel.layout = layout

        var counter = 0
        panel.forEach {
            constraints.gridy = counter
            constraints.weightx = 1.0
            constraints.gridwidth = 1
            for (j in arrayOf(0, 2)) {
                val space = JLabel()
                setSize(space, constraints, null)
                constraints.gridx = j
                settingsPanel.add(space, constraints)
            }
            constraints.weightx = 0.0
            constraints.gridwidth = 1
            constraints.gridx = 1
            constraints.gridy = counter
            constraints.insets = settingsFieldInsets

            setSettingField(it)
            setSize(it.value, constraints, settingsFieldSize, changeableHeight = true)
            settingsPanel.add(it.value, constraints)

            counter++
        }

        layout.setConstraints(settingsScroll, constraints)
        settingsScroll.verticalScrollBar.unitIncrement = scrollSpeed
        settingsScroll.verticalScrollBar.value = scrollPosition
        panel.value.add(settingsScroll, BorderLayout.CENTER)
    }

    private fun setSettingField(panel: TreeNode<BasePanel>) {
        panel.value.removeAll()
        val fieldPanel = JPanel(GridBagLayout())
        fieldPanel.background = Palette.BACKGROUND
        fieldPanel.border = RoundedBorder(50, Palette.BACKGROUND_ALT)

        val constraints = GridBagConstraints()
        constraints.fill = GridBagConstraints.BOTH
        constraints.insets = settingsFieldInsets

        constraints.gridx = 0
        val fieldIcon = JLabel(settingsIcons[panel.name])
        setSize(fieldIcon, constraints, settingButtonSize)
        fieldPanel.add(fieldIcon, constraints)

        constraints.gridx = 1
        val title = JLabel(Labels[panel.name].title)
        title.foreground = Palette.FOREGROUND
        title.font = Fonts.REGULAR_ALT
        title.font = title.font.deriveFont(25f)
        setSize(title, constraints, null)
        fieldPanel.add(title, constraints)

        val expanded = expandedSettings.getValue(panel.name)

        val expandButton = MaterialButton()
        expandButton.cornerRadius = buttonCornerRadius
        expandButton.enableFaceTitle = if (expanded) Labels[Labels.COLLAPSE].title else Labels[Labels.EXPAND].title
        expandButton.enableFaceIcon = if (expanded) settingsIcons[Labels.COLLAPSE]!! else settingsIcons[Labels.EXPAND]!!
        expandButton.backingColor = Palette.BACKGROUND_ALT
        expandButton.backgroundColor = Palette.ACCENT_NORMAL
        expandButton.foregroundColor = Palette.FOREGROUND

        expandButton.addActionListener {
            expandedSettings[panel.name] = !(expandedSettings.getValue(panel.name))
            setPanel(currentSubTree, include = arrayOf(panel.name), force = true)
        }
        constraints.gridx = 2
        setSize(expandButton, constraints, settingButtonSize)
        fieldPanel.add(expandButton, constraints)

        if (expanded) {
            constraints.gridx = 0
            constraints.gridy = 1
            constraints.weightx = 1.0
            constraints.gridwidth = 3

            val content = JPanel(BorderLayout())
            content.background = Palette.BACKGROUND_ALT
            //panel[Labels.CONTENT] = content

            when (panel.name) {
                Labels.PORT_CONNECT -> setPortConnectSetting(panel[Labels.CONTENT])
                Labels.SERIAL_LOG -> setSerialLogSetting(panel[Labels.CONTENT])
            }

            fieldPanel.add(panel[Labels.CONTENT].value, constraints)
        }
        panel.value.add(fieldPanel, BorderLayout.CENTER)
    }

    private fun setPortConnectSetting(panel: TreeNode<BasePanel>) {
        val contentPanel = JPanel(GridBagLayout())
        contentPanel.background = Palette.BACKGROUND_ALT

        val constraints = GridBagConstraints()
        constraints.fill = GridBagConstraints.HORIZONTAL
        constraints.insets = settingsFieldInsets

        val title = JLabel()
        val status = serialBus.portName
        title.foreground = Palette.FOREGROUND
        title.font = Fonts.REGULAR_ALT
        title.font = title.font.deriveFont(20f)
        title.preferredSize = Dimension(0, settingButtonSize.height)

        constraints.gridx = 0
        setSize(title, constraints, null)
        contentPanel.add(title, constraints)

        val reconnectButton = MaterialButton(Labels[Labels.RECONNECT].title)
        reconnectButton.cornerRadius = buttonCornerRadius
        reconnectButton.backingColor = contentPanel.background
        reconnectButton.foregroundColor = Palette.FOREGROUND
        reconnectButton.disableBackgroundColor = Palette.DISABLE
        reconnectButton.disableForegroundColor = Palette.FOREGROUND_ALT

        if (serialBus.scanningPorts) {
            title.text = Labels[Labels.RECONNECT].other[Labels.SEARCH] as String
            reconnectButton.isEnabled = false
        } else {
            if (status == null) {
                title.text = Labels[Labels.RECONNECT].other[Labels.NOT_FOUND] as String
                reconnectButton.backgroundColor = Palette.ACCENT_HIGH
            } else {
                title.text = Labels[Labels.RECONNECT].other[Labels.FOUND] as String + status
                reconnectButton.backgroundColor = Palette.ACCENT_NORMAL
            }
        }

        reconnectButton.font = Fonts.REGULAR.deriveFont(20f)

        reconnectButton.addActionListener {
            serialBus.scan()
            setSettingField(panel.parent)
        }

        constraints.gridx = 1
        setSize(reconnectButton, constraints, Dimension(250, settingButtonSize.height))
        contentPanel.add(reconnectButton, constraints)

        panel.value.add(contentPanel, BorderLayout.CENTER)
    }

    private fun setSerialLogSetting(panel: TreeNode<BasePanel>) {
        val terminalView = JPanel()
        var logScroll = JScrollPane()
        val serialLogTextField = JTextField("", 1)
        val serialLogSendButton = MaterialButton(Labels[Labels.SEND].title)

        fun setLogPanel()  {
            logScroll.isVisible = false
            logScroll.removeAll()
            var logPanel = JPanel(BorderLayout())
            logScroll = JScrollPane(logPanel)

            logScroll.border = BorderFactory.createEmptyBorder()
            logScroll.background = Palette.BACKGROUND

            logPanel.background = logScroll.background

            serialBus.log.forEach {
                if (it.second.isEmpty()) return@forEach
                val linePanel = JPanel(GridBagLayout())
                linePanel.background = Palette.BACKGROUND
                val lineConstraints = GridBagConstraints()
                lineConstraints.fill = GridBagConstraints.HORIZONTAL

                lineConstraints.insets = settingTerminalRecordInsets
                lineConstraints.weightx = 0.0
                lineConstraints.gridwidth = 1
                lineConstraints.gridy = 0
                val gridx = if (it.first == SerialBus.KEYWORD_INPUT) 0 else 1
                lineConstraints.gridx = gridx
                val color = if (it.first == SerialBus.KEYWORD_INPUT) Palette.ACCENT_LOW else Palette.DISABLE

                val message = JPanel(BorderLayout())
                message.background = logPanel.background
                message.border = RoundedBorder(20, color)

                val text = JTextArea(it.second.replace(",", ",\n"))
                text.background = color
                text.foreground = Palette.FOREGROUND_ALT
                text.border = BorderFactory.createEmptyBorder()
                text.isEditable = false
                text.lineWrap = true
                text.columns = 40
                text.font = Fonts.MONO.deriveFont(20f)

                text.addMouseListener(object : MouseAdapter() {
                    override fun mouseReleased(e: MouseEvent?) {
                        serialLogTextField.text = text.text.replace("\n", "")
                    }
                })

                setSize(message, lineConstraints, message.minimumSize, changeableHeight = true)
                message.add(text, BorderLayout.CENTER)
                linePanel.add(message, lineConstraints)

                lineConstraints.weightx = 1.0
                lineConstraints.gridx = 1 - gridx

                val space = JLabel()
                setSize(space, lineConstraints, null)
                linePanel.add(space, lineConstraints)

                val newLogPanel = JPanel(BorderLayout())
                newLogPanel.background = Palette.BACKGROUND
                newLogPanel.add(linePanel, BorderLayout.NORTH)
                logPanel.add(newLogPanel, BorderLayout.SOUTH)
                logPanel = newLogPanel
            }

            logScroll.verticalScrollBar.unitIncrement = scrollSpeed
            logScroll.verticalScrollBar.value = logScroll.verticalScrollBar.maximum

            logScroll.isVisible = true
        }

        fun sendData() {
            if (serialLogTextField.text.isEmpty()) return
            serialBus.send(serialLogTextField.text.trim())
            serialLogTextField.text = ""
        }

        panel.parent.value.isVisible = false
        panel.value.isVisible = false
        val contentPanel = JPanel(GridBagLayout())
        contentPanel.background = Palette.BACKGROUND_ALT

        val constraints = GridBagConstraints()
        constraints.fill = GridBagConstraints.BOTH

        constraints.insets = settingsFieldInsets

        constraints.gridx = 0
        constraints.gridy = 0
        constraints.gridwidth = 2
        constraints.weightx = 0.0

        terminalView.layout = BorderLayout()
        terminalView.background = Palette.BACKGROUND_ALT
        terminalView.border = RoundedBorder(20, Palette.BACKGROUND, Palette.ACCENT_NORMAL, 3)

        setLogPanel()

        terminalView.add(logScroll, BorderLayout.CENTER)
        terminalView.minimumSize = Dimension(0, 0)
        terminalView.maximumSize = terminalView.minimumSize
        terminalView.size = terminalView.maximumSize
        terminalView.preferredSize = terminalView.size

        setSize(terminalView, constraints, settingTerminalSize, changeableWidth = true)
        contentPanel.add(terminalView, constraints)

        constraints.gridx = 0
        constraints.gridy = 1
        constraints.gridwidth = 1
        constraints.weightx = 1.0

        val backing = JPanel(BorderLayout())
        backing.background = Palette.BACKGROUND_ALT
        backing.border = RoundedBorder(20, Palette.BACKGROUND, Palette.ACCENT_NORMAL, 3)

        serialLogTextField.background = Palette.BACKGROUND
        serialLogTextField.foreground = Palette.FOREGROUND_ALT
        serialLogTextField.border = BorderFactory.createEmptyBorder()
        serialLogTextField.font = Fonts.MONO.deriveFont(20f)

        serialLogTextField.addKeyListener(object : KeyAdapter() {
            override fun keyReleased(e: KeyEvent?) {
                if (e!!.keyCode == KeyEvent.VK_ENTER) sendData()
                serialLogSendButton.isEnabled = serialLogTextField.text.isNotEmpty()
            }
        })

        backing.add(serialLogTextField, BorderLayout.CENTER)
        setSize(backing, constraints, null)
        contentPanel.add(backing, constraints)

        serialLogSendButton.cornerRadius = buttonCornerRadius
        serialLogSendButton.backingColor = Palette.BACKGROUND_ALT
        serialLogSendButton.backgroundColor = Palette.ACCENT_NORMAL
        serialLogSendButton.disableBackgroundColor = Palette.DISABLE
        serialLogSendButton.foregroundColor = Palette.FOREGROUND
        serialLogSendButton.disableForegroundColor = Palette.FOREGROUND_ALT
        serialLogSendButton.font = Fonts.REGULAR.deriveFont(20f)

        serialLogSendButton.isEnabled = serialLogTextField.text.isNotEmpty()
        serialLogSendButton.addActionListener {
            sendData()
            serialLogSendButton.isEnabled = serialLogTextField.text.isNotEmpty()
        }

        constraints.gridx = 1
        setSize(serialLogSendButton, constraints, Dimension(250, settingButtonSize.height))
        contentPanel.add(serialLogSendButton, constraints)
        panel.value.add(contentPanel, BorderLayout.CENTER)
        panel.value.isVisible = true
        panel.parent.value.isVisible = true
    }

    private fun setLightPanel(panel: TreeNode<BasePanel>, scrollPosition: Int = 0) {
        val buttonPanel = JPanel()
        val buttonScroll = JScrollPane(buttonPanel)
        val constraints = GridBagConstraints()
        constraints.fill = GridBagConstraints.BOTH
        buttonScroll.border = BorderFactory.createMatteBorder(0, 0, 0, 0, Palette.BACKGROUND)
        buttonScroll.background = Palette.BACKGROUND
        buttonPanel.background = Palette.BACKGROUND

        val layout = GridBagLayout()
        buttonPanel.layout = layout

        val buttonsCount = if (settings.contains(Labels.LIGHT_GROUP + Labels.COUNT)) settings[Labels.LIGHT_GROUP + Labels.COUNT] as Int else 0

        for (counter in 0 until buttonsCount) {
            constraints.insets = menuButtonsInsets
            constraints.weightx = 0.0
            constraints.gridwidth = 1
            constraints.gridx = 1 + counter % menuButtonsInLine
            constraints.gridy = counter / menuButtonsInLine

            val groupPanel = JPanel(GridBagLayout())
            groupPanel.background = Palette.BACKGROUND
            groupPanel.border = RoundedBorder(50, Palette.BACKGROUND_ALT)

            val groupConstraints = GridBagConstraints()
            groupConstraints.fill = GridBagConstraints.VERTICAL
            groupConstraints.insets = menuSwitchButtonInsets

            val title = InscribedLabel(
                Labels[Labels.GROUP].title + (counter + 1).toString(),
                Fonts.REGULAR_ALT,
                menuFieldSize.width - 100,
                menuFieldSize.height - 100
            )
            title.foreground = Palette.FOREGROUND_ALT

            val value = settings[Labels.LIGHT_GROUP + "_${counter + 1}"] as Int

            val lightSlider = MaterialSlider(DefaultBoundedRangeModel(value, 0, 0, 100))
            lightSlider.sliderBackgroundLineColor = Palette.DISABLE
            lightSlider.sliderForegroundLineColor = Palette.ACCENT_NORMAL
            lightSlider.thumbColor = Palette.ACCENT_HIGH
//            lightSlider.thumbIcon = ImageIcon(menuLightIcons[Auxiliary.Labels.SWITCH_ON]?.image?.getScaledInstance(35, 35, Image.SCALE_SMOOTH))
            lightSlider.preferredSize = Dimension(300, 50)
            lightSlider.background = Palette.BACKGROUND_ALT

            val switchButton = MaterialButton()

            fun setButton() {
                val state = settings[Labels.LIGHT_GROUP + "_${counter + 1}"] as Int != 0
                switchButton.cornerRadius = 150
                switchButton.backingColor = Palette.BACKGROUND_ALT
                switchButton.backgroundColor = Palette.ACCENT_NORMAL
                switchButton.foregroundColor = Palette.FOREGROUND

                if (state) {
                    switchButton.enableFaceTitle = Labels[Labels.SWITCH].other[Labels.SWITCH_OFF] as String
                    switchButton.enableFaceIcon = menuLightIcons[Labels.SWITCH_ON]
                } else {
                    switchButton.enableFaceTitle = Labels[Labels.SWITCH].other[Labels.SWITCH_ON] as String
                    switchButton.enableFaceIcon = menuLightIcons[Labels.SWITCH_OFF]
                }

                switchButton.addActionListener {
                    lightSlider.value = if (state) 0 else 50
                    setPanel(currentSubTree, include = arrayOf(Labels.LIGHT), force = true)
                }
            }

            lightSlider.addChangeListener {
                val oldValue = settings[Labels.LIGHT_GROUP + "_${counter + 1}"] as Int == 0
                val newValue =  lightSlider.value == 0

                settings[Labels.LIGHT_GROUP + "_${counter + 1}"] = lightSlider.value
                if (oldValue != newValue) setPanel(currentSubTree, include = arrayOf(Labels.LIGHT), force = true)
            }

            setButton()

            groupConstraints.gridy = 0
            setSize(title, groupConstraints, null)
            groupPanel.add(title, groupConstraints)

            groupConstraints.gridy = 1
            setSize(switchButton, groupConstraints, menuSwitchButtonSize)
            groupPanel.add(switchButton, groupConstraints)

            groupConstraints.gridy = 2
            setSize(lightSlider, groupConstraints, null)
            groupPanel.add(lightSlider, groupConstraints)

            setSize(groupPanel, constraints, menuFieldSize)
            buttonPanel.add(groupPanel, constraints)
        }

        for (i in 0..(currentPanel.count - 1) / 2) {
            constraints.gridy = i
            constraints.weightx = 1.0
            constraints.gridwidth = 1
            for (j in arrayOf(0, menuButtonsInLine + 1)) {
                val space = JLabel()
                setSize(space, constraints, null)
                constraints.gridx = j
                buttonPanel.add(space, constraints)
            }
        }

        layout.setConstraints(buttonScroll, constraints)
        panel.value.add(buttonScroll, BorderLayout.CENTER)
        buttonScroll.verticalScrollBar.unitIncrement = scrollSpeed
        buttonScroll.verticalScrollBar.value = scrollPosition
    }

    private fun setSize(element: JComponent, constraints: GridBagConstraints, setSize: Dimension? = null, changeableWidth: Boolean = false, changeableHeight: Boolean = false) {
        if (setSize != null) {
            if (!changeableWidth) constraints.ipadx = setSize.width - element.minimumSize.width
            if (!changeableHeight) constraints.ipady = setSize.height - element.minimumSize.height
            constraints.weightx = 0.0
        } else {
            constraints.ipadx = 0
            constraints.ipady = 0
            constraints.weightx = 1.0
        }
    }

    private fun setTitle(text: String): JPanel {
        val title = InscribedLabel(text.uppercase(), Fonts.TITLE, mainFrame.width - sideFieldsWidth, maxTitleHeight)
        title.foreground = Palette.FOREGROUND
        val titlePanel = JPanel(FlowLayout(FlowLayout.LEFT))
        titlePanel.add(title)
        titlePanel.background = Palette.BACKGROUND
        return titlePanel
    }
}