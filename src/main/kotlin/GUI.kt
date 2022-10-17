import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.awt.*
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.lang.Integer.max
import java.lang.Integer.min
import java.text.SimpleDateFormat
import java.util.*
import javax.swing.*
import javax.swing.Timer
import kotlin.collections.ArrayList


class GUI {
    private val mainFrame: JFrame = JFrame(Labels.TITLE)

    private val topPanel: JPanel = JPanel()
    private val timeDatePanel: JPanel = JPanel()

    private val welcomePanel: JPanel = JPanel(BorderLayout())
    private val loginPanel: JPanel = JPanel(BorderLayout())
    private val powerMenuPanel: JPanel = JPanel(BorderLayout())

    private val settingsRootPanel: JPanel = JPanel(BorderLayout())
    private val settingsBasicPanel: JPanel = JPanel(BorderLayout())
    private val settingsExtendedPanel: JPanel = JPanel(BorderLayout())
    private val settingsAdminPanel: JPanel = JPanel(BorderLayout())

    private val portConnectSetting: JPanel = JPanel(BorderLayout())
    private val serialLogSetting: JPanel = JPanel(BorderLayout())
    private var serialLog: ArrayList<Pair<String, String>> = arrayListOf(
        SerialIO.KEYWORD_INPUT to "123123123123123123123123123123123123123123",
        SerialIO.KEYWORD_INPUT to "456456456456456456456456456456456456456456456456456456456456456456456",
        SerialIO.KEYWORD_OUTPUT to "abcabcabcabcabcabc",
        SerialIO.KEYWORD_INPUT to "789789789789789789789789789",
        SerialIO.KEYWORD_OUTPUT to "defdefdefdefdefdefdefdefdefdefdefdefdefdefdefdefdefdef",
        SerialIO.KEYWORD_INPUT to "123123123123123123123123123123123123123123",
        SerialIO.KEYWORD_INPUT to "456456456456456456456456456456456456456456456456456456456456456456456",
        SerialIO.KEYWORD_OUTPUT to "abcabcabcabcabcabc",
        SerialIO.KEYWORD_INPUT to "789789789789789789789789789",
        SerialIO.KEYWORD_OUTPUT to "defdefdefdefdefdefdefdefdefdefdefdefdefdefdefdefdefdef",
        SerialIO.KEYWORD_INPUT to "123123123123123123123123123123123123123123",
        SerialIO.KEYWORD_INPUT to "456456456456456456456456456456456456456456456456456456456456456456456",
        SerialIO.KEYWORD_OUTPUT to "abcabcabcabcabcabc",
        SerialIO.KEYWORD_INPUT to "789789789789789789789789789",
        SerialIO.KEYWORD_OUTPUT to "defdefdefdefdefdefdefdefdefdefdefdefdefdefdefdefdefdef",
        SerialIO.KEYWORD_INPUT to "123123123123123123123123123123123123123123",
        SerialIO.KEYWORD_INPUT to "456456456456456456456456456456456456456456456456456456456456456456456",
        SerialIO.KEYWORD_OUTPUT to "abcabcabcabcabcabc",
        SerialIO.KEYWORD_INPUT to "789789789789789789789789789",
        SerialIO.KEYWORD_OUTPUT to "defdefdefdefdefdefdefdefdefdefdefdefdefdefdefdefdefdef",
        SerialIO.KEYWORD_INPUT to "123123123123123123123123123123123123123123",
        SerialIO.KEYWORD_INPUT to "456456456456456456456456456456456456456456456456456456456456456456456",
        SerialIO.KEYWORD_OUTPUT to "abcabcabcabcabcabc",
        SerialIO.KEYWORD_INPUT to "789789789789789789789789789",
        SerialIO.KEYWORD_OUTPUT to "defdefdefdefdefdefdefdefdefdefdefdefdefdefdefdefdefdef",
        SerialIO.KEYWORD_INPUT to "123123123123123123123123123123123123123123",
        SerialIO.KEYWORD_INPUT to "456456456456456456456456456456456456456456456456456456456456456456456",
        SerialIO.KEYWORD_OUTPUT to "abcabcabcabcabcabc",
        SerialIO.KEYWORD_INPUT to "789789789789789789789789789",
        SerialIO.KEYWORD_OUTPUT to "defdefdefdefdefdefdefdefdefdefdefdefdefdefdefdefdefdef"
    )

    private val menuBasicPanel: JPanel = JPanel(BorderLayout())
    private val menuExtendedPanel: JPanel = JPanel(BorderLayout())
    private val menuAdminPanel: JPanel = JPanel(BorderLayout())

    private val lightPanel: JPanel = JPanel(BorderLayout())
    private val windowPanel: JPanel = JPanel(BorderLayout())
    private val powerSupplyPanel: JPanel = JPanel(BorderLayout())
    private val addUserPanel: JPanel = JPanel(BorderLayout())

    private val rootTree: TreeNode<JPanel> = TreeNode(Labels.ROOT, welcomePanel)
    private val basicSubTree: TreeNode<JPanel> = TreeNode(Labels.BASIC, menuBasicPanel)
    private val extendedSubTree: TreeNode<JPanel> = TreeNode(Labels.EXTENDED, menuExtendedPanel)
    private val adminSubTree: TreeNode<JPanel> = TreeNode(Labels.ADMIN, menuAdminPanel)

    private val settingsRootSubTree: TreeNode<JPanel> = TreeNode(Labels.SETTINGS, settingsRootPanel)
    private val settingsBasicSubTree: TreeNode<JPanel> = TreeNode(Labels.SETTINGS, settingsBasicPanel)
    private val settingsExtendedSubTree: TreeNode<JPanel> = TreeNode(Labels.SETTINGS, settingsExtendedPanel)
    private val settingsAdminSubTree: TreeNode<JPanel> = TreeNode(Labels.SETTINGS, settingsAdminPanel)

    private var currentSubTree: TreeNode<JPanel> = rootTree
    private var currentPanel: TreeNode<JPanel> = rootTree

    private val topPanelButtonsSize = Dimension(75, 75)
    private val topPanelButtonsInsets = Insets(10, 10, 10, 10)
    private val loginButtonsSize = Dimension(450, 50)
    private val loginButtonsInsets = Insets(10, 10, 10, 10)
    private val menuButtonsSize = Dimension(400, 125)
    private val menuButtonsInsets = Insets(10, 10, 10, 10)
    private val settingsFieldSize = Dimension(800, 100)
    private val settingsFieldInsets = Insets(10, 10, 10, 10)
    private val settingButtonSize = Dimension(50, 50)
    private val settingTerminalSize = Dimension(800, 500)
    private val settingTerminalRecordInsets = Insets(10, 10, 10, 10)
    private val scrollSpeed = 10
    private var menuButtonsInLine = 2
    private val menuMinFieldWidth = 150
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
        Labels.LIGHT to Icons.Foreground.LIGHT_OFF,
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
    private val expandedSettings: MutableMap<String, Boolean> = mutableMapOf(
        Labels.SERIAL_LOG to false,
        Labels.PORT_CONNECT to false
    ).withDefault { false }

    private var settings: MutableMap<String, Any> = mutableMapOf()
    private val arduinoSerial: SerialIO = SerialIO(19200, autoConnect = true)

    init {
        setArduino()
        setTimeDatePanel()

        rootTree.addChildren(
            Labels.POWER_MENU to powerMenuPanel,
            Labels.LOGIN to loginPanel
        )
        rootTree += settingsRootSubTree

        basicSubTree.addChildren(
            Labels.LIGHT to lightPanel,
            Labels.WINDOW to windowPanel
        )
        basicSubTree += settingsBasicSubTree

        extendedSubTree.addChildren(
            Labels.LIGHT to lightPanel,
            Labels.WINDOW to windowPanel,
            Labels.POWER_SUPPLY to powerSupplyPanel
        )
        extendedSubTree += settingsExtendedSubTree

        adminSubTree.addChildren(
            Labels.LIGHT to lightPanel,
            Labels.WINDOW to windowPanel,
            Labels.POWER_SUPPLY to powerSupplyPanel,
            Labels.ADD_USER to addUserPanel
        )
        adminSubTree += settingsAdminSubTree

        settingsRootSubTree.addChildren(
            Labels.PORT_CONNECT to portConnectSetting,
            Labels.SERIAL_LOG to serialLogSetting
        )
        settingsBasicSubTree.addChildren(
            Labels.PORT_CONNECT to portConnectSetting,
            Labels.SERIAL_LOG to serialLogSetting
        )
        settingsExtendedSubTree.addChildren(
            Labels.PORT_CONNECT to portConnectSetting,
            Labels.SERIAL_LOG to serialLogSetting
        )
        settingsAdminSubTree.addChildren(
            Labels.PORT_CONNECT to portConnectSetting,
            Labels.SERIAL_LOG to serialLogSetting
        )

        rootTree[Labels.LOGIN] += basicSubTree
        rootTree[Labels.LOGIN] += extendedSubTree
        rootTree[Labels.LOGIN] += adminSubTree

        rootTree += basicSubTree
        rootTree += extendedSubTree
        rootTree += adminSubTree

        currentPanel = rootTree

        mainFrame.addComponentListener(object : ComponentAdapter() {
            override fun componentResized(componentEvent: ComponentEvent?) {
                val panelsForUpdate = arrayListOf(Labels.TITLE, Labels.ROOT)
                val newButtonsInLine = max((mainFrame.width - 2 * menuMinFieldWidth) / menuButtonsSize.width, 1)
                if (menuButtonsInLine != newButtonsInLine) {
                    menuButtonsInLine = newButtonsInLine
                    panelsForUpdate.addAll(arrayOf(Labels.BASIC, Labels.EXTENDED, Labels.ADMIN))
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

    private fun setArduino() {
        arduinoSerial.addDataReceivedListener {
            it.forEach { (param, value) ->
                when (param) {
                    SerialIO.LABEL_SYSTEM -> println("$param - $value")
                    SerialIO.LABEL_RESET -> println("$param - $value")
                    SerialIO.LABEL_ECHO -> println("$param - $value")
                    SerialIO.LABEL_ERROR -> println("$param - $value")
                    SerialIO.LABEL_UID -> println("$param - $value")
                    SerialIO.LABEL_LIGHT -> println("$param - $value")
                    SerialIO.LABEL_WINDOW -> println("$param - $value")
                    SerialIO.LABEL_POWER_SUPPLY -> println("$param - $value")
                    SerialIO.LABEL_STARTUP -> println("$param - $value")
                    SerialIO.LABEL_SAVE -> println("$param - $value")
                    SerialIO.LABEL_LOAD -> println("$param - $value")
                }
            }
            arduinoSerial.removeData()
        }

        arduinoSerial.addPortFoundListener {
//            setSettingField(currentSubTree[Labels])
//            setPortConnectSetting(currentSubTree[Labels.SETTINGS][Labels.PORT_CONNECT][Labels.CONTENT])
            if (currentPanel.name == Labels.SETTINGS) setPanel(currentPanel[Labels.PORT_CONNECT], force = true)
            println("Найден $it")
        }

        arduinoSerial.addUpdateLogListener { log ->
            serialLog.clear()
            serialLog.addAll(log)
            if (currentPanel.name == Labels.SETTINGS) setPanel(currentPanel[Labels.SERIAL_LOG], force = true)
//            if (currentPanel.name == Labels.SETTINGS) setSettingField(currentPanel[Labels.PORT_CONNECT])
            //setPanel(currentSubTree, include = arrayOf(Labels.SETTINGS))
        }
    }

    private fun updateFrame(newCurrentPanel: TreeNode<JPanel> = currentPanel, force: Boolean = false) {
        mainFrame.contentPane.isVisible = false
        if (newCurrentPanel != currentPanel || force) {

            if (currentSubTree.name == Labels.ROOT && newCurrentPanel.name in arrayOf(Labels.BASIC, Labels.EXTENDED, Labels.ADMIN) ||
                newCurrentPanel.name == Labels.ROOT && currentSubTree.name in arrayOf(Labels.BASIC, Labels.EXTENDED, Labels.ADMIN)) {
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
            val powerButton = customButton(
                Labels[Labels.POWER_MENU].title,
                Palette.ACCENT_HIGH,
                Palette.FOREGROUND,
                Palette.BACKGROUND_ALT,
                icon = topPanelIcons[Labels.POWER_MENU],
                iconOnly = true
            )
            powerButton.addActionListener { updateFrame(rootTree[Labels.POWER_MENU]) }
            addElement(powerButton, 1, topPanelButtonsSize)
        } else {
            val backButton = customButton(
                Labels[Labels.BACK].title,
                Palette.ACCENT_NORMAL,
                Palette.FOREGROUND,
                Palette.BACKGROUND_ALT,
                icon = topPanelIcons[Labels.BACK],
                iconOnly = true
            )
            backButton.addActionListener { updateFrame(currentPanel.parent) }
            addElement(backButton, 1, topPanelButtonsSize)
        }

        addElement(timeDatePanel, 1)

        val settingsButton: JButton
        if ("settings" in currentPanel.names) {
            settingsButton = customButton(
                "Настройки",
                Palette.ACCENT_NORMAL,
                Palette.FOREGROUND,
                Palette.BACKGROUND_ALT,
                icon = topPanelIcons[Labels.SETTINGS],
                iconOnly = true
            )
            settingsButton.addActionListener { updateFrame(currentPanel[Labels.SETTINGS]) }
        } else {
            settingsButton = customButton(
                "Настройки",
                Palette.DISABLE,
                Palette.FOREGROUND_ALT,
                Palette.BACKGROUND_ALT,
                icon = topPanelIcons[Labels.SETTINGS + "_alt"],
                iconOnly = true
            )
            settingsButton.isEnabled = false
        }
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

    private fun setPanel(panel: TreeNode<JPanel>, exclude: Array<String> = arrayOf(), include: Array<String> = arrayOf(), force: Boolean = false) {
        fun drawTitle(panel: TreeNode<JPanel>) {
            if (panel.name.contains(Labels.SET)) return
            if (panel.value.layout.javaClass == BorderLayout().javaClass) {
                val component = (panel.value.layout as BorderLayout).getLayoutComponent(BorderLayout.NORTH)
                if (component != null) panel.value.remove(component)
                panel.value.add(setTitle(Labels[panel.name].description), BorderLayout.NORTH)
            }
        }

        fun drawContent(panel: TreeNode<JPanel>) {
            var scrollPosition = 0
            panel.value.components.forEach { if (it.javaClass.name.contains("JScrollPane")) scrollPosition = (it as JScrollPane).verticalScrollBar.value }
            if (panel.value.layout.javaClass == BorderLayout().javaClass) {
                val component = (panel.value.layout as BorderLayout).getLayoutComponent(BorderLayout.NORTH)
                panel.value.removeAll()
                if (component != null) panel.value.add(component, BorderLayout.NORTH)
            } else {
                panel.value.removeAll()
            }
            when (panel.name) {
                Labels.ROOT, Labels.WELCOME -> setWelcomePanel(panel)
                Labels.LOGIN -> setLoginPanel(panel)
                Labels.BASIC, Labels.EXTENDED, Labels.ADMIN -> setMenuPanel(panel, scrollPosition)
                Labels.SETTINGS -> setSettingsPanel(panel, scrollPosition)
                Labels.PORT_CONNECT, Labels.SERIAL_LOG -> setSettingField(panel)
            }
        }

        suspend fun recursiveSet(
            panel: TreeNode<JPanel>,
            exclude: Array<String> = arrayOf(),
            include: Array<String> = arrayOf()
        ) {
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

        val loginButton = customButton(
            Labels[Labels.LOGIN].title,
            Palette.ACCENT_LOW,
            Palette.FOREGROUND_ALT,
            Palette.BACKGROUND,
            25,
            20,
            loginButtonIcon[Labels.LOGIN]
        )
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

        var counter = 0
        panel.forEach {
            constraints.gridy = counter
            constraints.weightx = 1.0
            constraints.gridwidth = 1
            for (j in arrayOf(0, 2)) {
                val space = JLabel()
                setSize(space, constraints, null)
                constraints.gridx = j
                buttonPanel.add(space, constraints)
            }

            constraints.weightx = 0.0
            constraints.gridwidth = 1
            constraints.gridx = 1
            constraints.gridy = counter
            counter++
            constraints.insets = loginButtonsInsets
            val button = customButton(
                Labels[it.name].title,
                Palette.ACCENT_NORMAL,
                Palette.FOREGROUND,
                Palette.BACKGROUND,
                25,
                20
            )
            button.addActionListener { _ -> updateFrame(it) }
            setSize(button, constraints, loginButtonsSize)
            buttonPanel.add(button, constraints)
        }

        buttonScroll.verticalScrollBar.unitIncrement = scrollSpeed
        layout.setConstraints(buttonScroll, constraints)
        panel.value.add(buttonScroll, BorderLayout.CENTER)
    }

    private fun setSettingsPanel(panel: TreeNode<JPanel>, scrollPosition: Int = 0) {
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
//            setPanel(currentSubTree, include = arrayOf(it.name), force = true)


            setSize(it.value, constraints, settingsFieldSize, changeableHeight = true)
            settingsPanel.add(it.value, constraints)

            counter++
        }

        layout.setConstraints(settingsScroll, constraints)
        panel.value.add(settingsScroll, BorderLayout.CENTER)
        settingsScroll.verticalScrollBar.unitIncrement = scrollSpeed
        settingsScroll.verticalScrollBar.value = scrollPosition
    }

    private fun setSettingField(panel: TreeNode<JPanel>) {
        val fieldPanel = JPanel(GridBagLayout())
        fieldPanel.background = Palette.BACKGROUND
        fieldPanel.border = RoundedBorder(50, Palette.BACKGROUND_ALT)

        val constraints = GridBagConstraints()
        constraints.fill = GridBagConstraints.BOTH

        constraints.gridx = 0
        constraints.insets = settingsFieldInsets
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
        val text: String
        val icon: ImageIcon
        if (expanded) {
            text = "Раскрыть"
            icon = settingsIcons[Labels.COLLAPSE]!!
        } else {
            text = "Скрыть"
            icon = settingsIcons[Labels.EXPAND]!!
        }

        var expandButton = customButton(
            text,
            Palette.ACCENT_NORMAL,
            Palette.FOREGROUND,
            Palette.BACKGROUND_ALT,
            icon = icon,
            iconOnly = true
        )
        expandButton.addActionListener { _ ->
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
            panel[Labels.CONTENT] = content

            when (panel.name) {
                Labels.PORT_CONNECT -> setPortConnectSetting(panel[Labels.CONTENT])
                Labels.SERIAL_LOG -> setSerialLogSetting(panel[Labels.CONTENT])
            }

            fieldPanel.add(panel[Labels.CONTENT].value, constraints)
        }
        panel.value.add(fieldPanel, BorderLayout.CENTER)
    }

    private fun setPortConnectSetting(panel: TreeNode<JPanel>) {
        val contentPanel = JPanel(GridBagLayout())
        contentPanel.background = Palette.BACKGROUND_ALT

        val constraints = GridBagConstraints()
        constraints.fill = GridBagConstraints.HORIZONTAL

        constraints.gridx = 0
        constraints.insets = settingsFieldInsets

        val title = JLabel()
        val status = arduinoSerial.serialPortName
        val buttonColor: Color
        var textColor: Color = Palette.FOREGROUND
        if (status == null) {
            if (arduinoSerial.findingPort) {
                title.text = "Происходит поиск устройств"
                buttonColor = Palette.DISABLE
            } else {
                title.text = "Устройство не подключено!"
                buttonColor = Palette.ACCENT_HIGH
                textColor = Palette.FOREGROUND_ALT
            }
        } else {
            title.text = "Устройство подключекно к порту: $status"
            buttonColor = Palette.ACCENT_NORMAL
        }

        title.foreground = Palette.FOREGROUND
        title.font = Fonts.REGULAR_ALT
        title.font = title.font.deriveFont(20f)
        setSize(title, constraints, null)
        contentPanel.add(title, constraints)

        val reconnectButton = customButton("Переподключить", buttonColor, textColor, Palette.BACKGROUND_ALT, labelSize = 20)
        reconnectButton.isEnabled = !arduinoSerial.findingPort
        if (reconnectButton.isEnabled) {
            reconnectButton.addActionListener {
                arduinoSerial.scanPorts()
                setSettingField(panel.parent)
            }
        }
        constraints.gridx = 1
        setSize(reconnectButton, constraints, Dimension(250, settingButtonSize.height))
        contentPanel.add(reconnectButton, constraints)
        panel.value.add(contentPanel, BorderLayout.CENTER)
    }

    private fun setSerialLogSetting(panel: TreeNode<JPanel>) {
        val terminalView = JPanel()
        var logScroll = JScrollPane()
        val serialLogTextField = JTextField("", 1)
        val serialLogSendButton = JButton()

        fun setLogPanel()  {
            logScroll.isVisible = false
            logScroll.removeAll()
            var logPanel = JPanel(BorderLayout())
            logScroll = JScrollPane(logPanel)

            logScroll.border = BorderFactory.createEmptyBorder()
            logScroll.background = Palette.BACKGROUND

            logPanel.background = logScroll.background

            serialLog.forEach {
                if (it.second.isEmpty()) return@forEach
                val linePanel = JPanel(GridBagLayout())
                linePanel.background = Palette.BACKGROUND
                val lineConstraints = GridBagConstraints()
                lineConstraints.fill = GridBagConstraints.HORIZONTAL

                lineConstraints.insets = settingTerminalRecordInsets
                lineConstraints.weightx = 0.0
                lineConstraints.gridwidth = 1
                lineConstraints.gridy = 0
                val gridx = if (it.first == SerialIO.KEYWORD_INPUT) 0 else 1
                lineConstraints.gridx = gridx
                val color = if (it.first == SerialIO.KEYWORD_INPUT) Palette.ACCENT_LOW else Palette.DISABLE

                val message = JPanel(BorderLayout())
                message.background = logPanel.background
                message.border = RoundedBorder(20, color)

                val text = JTextArea(it.second)
                text.background = color
                text.foreground = Palette.FOREGROUND_ALT
                text.border = BorderFactory.createEmptyBorder()
                text.isEditable = false
                text.lineWrap = true
                text.columns = 40
                text.font = Fonts.MONO.deriveFont(20f)

                message.minimumSize = Dimension(400, 0)
                message.maximumSize = Dimension(400, 1000)

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
            arduinoSerial.sendData(serialLogTextField.text.trim())
            serialLogTextField.text = ""
        }

        fun setButton(enabled: Boolean) {
            if (enabled) {
                customButton("Отправить", Palette.ACCENT_NORMAL, Palette.FOREGROUND, Palette.BACKGROUND_ALT, labelSize = 20, modifiedButton = serialLogSendButton)
                serialLogSendButton.isEnabled = true
            } else {
                customButton("Отправить", Palette.DISABLE, Palette.FOREGROUND_ALT, Palette.BACKGROUND_ALT, labelSize = 20, modifiedButton = serialLogSendButton)
                serialLogSendButton.isEnabled = false
            }
            serialLogSendButton.addActionListener {
                sendData()
            }
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

        backing.add(serialLogTextField, BorderLayout.CENTER)
        setSize(backing, constraints, null)

        contentPanel.add(backing, constraints)

        setButton(serialLogTextField.text.isNotEmpty())

        serialLogTextField.addKeyListener(object : KeyAdapter() {
            override fun keyReleased(e: KeyEvent?) {
                if (e!!.keyCode == KeyEvent.VK_ENTER) sendData()
                setButton(serialLogTextField.text.isNotEmpty())
            }
        })

        constraints.gridx = 1
        setSize(serialLogSendButton, constraints, Dimension(250, settingButtonSize.height))
        contentPanel.add(serialLogSendButton, constraints)
        panel.value.add(contentPanel, BorderLayout.CENTER)
        panel.value.isVisible = true
        panel.parent.value.isVisible = true
    }

    private fun menuButton(panel: TreeNode<JPanel>): JButton {
        var icon: ImageIcon? = null
        when (panel.name) {
            Labels.LIGHT -> icon = menuButtonIcons[Labels.LIGHT]
            Labels.WINDOW -> icon = menuButtonIcons[Labels.WINDOW]
            Labels.POWER_SUPPLY -> icon = menuButtonIcons[Labels.POWER_SUPPLY]
            Labels.ADD_USER -> icon = menuButtonIcons[Labels.ADD_USER]
        }

        val button = customButton(
            Labels[panel.name].title,
            Palette.ACCENT_NORMAL,
            Palette.FOREGROUND,
            Palette.BACKGROUND,
            50,
            30,
            icon
        )
        button.addActionListener { updateFrame(panel) }
        return button
    }

    private fun setMenuPanel(panel: TreeNode<JPanel>, scrollPosition: Int = 0) {
        val buttonPanel = JPanel()
        val buttonScroll = JScrollPane(buttonPanel)
        val constraints = GridBagConstraints()
        constraints.fill = GridBagConstraints.BOTH
        buttonScroll.border = BorderFactory.createMatteBorder(0, 0, 0, 0, Palette.BACKGROUND)
        buttonScroll.background = Palette.BACKGROUND
        buttonPanel.background = Palette.BACKGROUND

        val layout = GridBagLayout()
        buttonPanel.layout = layout

        var counter = 0
        panel.forEach {
            if (it.name == Labels.SETTINGS) return@forEach

            constraints.insets = menuButtonsInsets
            constraints.weightx = 0.0
            constraints.gridwidth = 1
            constraints.gridx = 1 + counter % menuButtonsInLine
            constraints.gridy = counter / menuButtonsInLine
            counter++

            val button = menuButton(it)
            setSize(button, constraints, menuButtonsSize)
            buttonPanel.add(button, constraints)
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

    private fun customButton(title: String, background: Color = Color.WHITE, foreground: Color = Color.BLACK, backing: Color = Color.GRAY, borderRadius: Int = 50, labelSize: Int = 25, icon: ImageIcon? = null, iconOnly: Boolean = false, modifiedButton: JButton = JButton()): JButton {
        modifiedButton.isVisible = false
        modifiedButton.removeAll()
        modifiedButton.layout = BorderLayout()
        modifiedButton.background = backing
        modifiedButton.foreground = backing
        modifiedButton.border = RoundedBorder(borderRadius, background)

        val buttonPanel = JPanel()
        buttonPanel.layout = BorderLayout()
        buttonPanel.background = background
        modifiedButton.add(buttonPanel, BorderLayout.CENTER)

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
            modifiedButton.toolTipText = title
        } else {
            buttonPanel.add(getLabel(), BorderLayout.CENTER)
            buttonPanel.add(getIcon(), BorderLayout.WEST)
        }

        modifiedButton.isVisible = true
        return modifiedButton
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
        val title =
            adaptiveWidthLabel(text.uppercase(), Fonts.TITLE, mainFrame.width - menuMinFieldWidth, maxTitleHeight)
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
        } while ((maxWidth - label.maximumSize.width) > 10 && (maxHeight - label.maximumSize.height) > 10 && (iteration++ < maxIteration || maxIteration == 0))
        return label
    }
}