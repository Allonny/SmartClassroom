import auxiliary.*
import com.couchbase.lite.Document
import gui.panels.GUIConstants
import gui.panels.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.awt.*
import java.awt.event.*
import java.lang.Integer.max
import java.util.ArrayList
import javax.swing.*


class GUIManager (dataBus: DataBus) {
    val mainFrame: JFrame = JFrame(Labels.TITLE)

    private val topPanel: TopPanel = TopPanel(Context)
    private val welcomePanel: BasePanel = WelcomePanel(Context)
    private val loginPanel: BasePanel = LoginPanel(Context)
    private val powerMenuPanel: BasePanel = BasePanel(Context)
    private val menuPanel: BasePanel = MenuPanel(Context)
    private val settingsRootPanel: BasePanel = SettingsPanel(Context)
    private val settingsUserPanel: BasePanel = SettingsPanel(Context)

    private val lightPanel: BasePanel = LightPanel(Context)
    private val windowPanel: BasePanel = WindowPanel(Context)
    private val powerSupplyPanel: BasePanel = PowerSupplyPanel(Context)
    private val userControlPanel: BasePanel = UserControlPanel(Context)
    private val userAddPanel: BasePanel = UserAddPanel(Context)
    private val userEditPanel: BasePanel = UserEditPanel(Context)

    private val rootTree: TreeNode<BasePanel> = TreeNode(Labels.ROOT, welcomePanel)
    private val userTree: TreeNode<BasePanel> = TreeNode(Labels.USER, menuPanel)

    private var currentSubTree: TreeNode<BasePanel> = rootTree

    private fun grubPanelAction(action: String?) {
        when (action?.substringBefore('.')) {
            Labels.LOGIN -> updateFrame(rootTree[Labels.LOGIN])
            Labels.ENTER -> authorisation((loginPanel as LoginPanel).login, (loginPanel).password)
            Labels.SETTINGS -> updateFrame(Context.currentPanel!![Labels.SETTINGS])
            Labels.POWER_MENU -> updateFrame(rootTree[Labels.POWER_MENU])
            Labels.BACK -> updateFrame(Context.currentPanel!!.parent)
            Labels.UPDATE -> updateFrame()
            Labels.LIGHT -> updateFrame(Context.currentPanel!![Labels.LIGHT])
            Labels.WINDOW -> updateFrame(Context.currentPanel!![Labels.WINDOW])
            Labels.POWER_SUPPLY -> updateFrame(Context.currentPanel!![Labels.POWER_SUPPLY])
            Labels.USER_CONTROL -> updateFrame(Context.currentPanel!![Labels.USER_CONTROL])
            Labels.LIGHT_GROUP -> funcGroupControl(action)
            Labels.WINDOW_GROUP -> funcGroupControl(action)
            Labels.POWER_SUPPLY_GROUP -> funcGroupControl(action)
        }
    }

    private fun funcGroupControl(action: String) {
        val instruction = action.split('.')
        when (instruction[0]) {
            Labels.LIGHT_GROUP -> Context.serialManager!!.send(SerialManager.LABEL_LIGHT to instruction[1])
            Labels.WINDOW_GROUP -> Context.serialManager!!.send(SerialManager.LABEL_WINDOW to instruction[1])
            Labels.POWER_SUPPLY_GROUP -> Context.serialManager!!.send(SerialManager.LABEL_POWER_SUPPLY to instruction[1])
        }
    }

    private fun grabSerialBus() {
        Context.serialManager!!.addDataReceivedListener {
            it.forEach { (param, value) ->
                when (param) {
                    SerialManager.LABEL_SYSTEM -> println("$param - $value")
                    SerialManager.LABEL_ECHO -> println("$param - $value")
                    SerialManager.LABEL_ERROR -> println("$param - $value")
                    SerialManager.LABEL_CONFIG -> println("$param - $value")
                    SerialManager.LABEL_MAP -> println("$param - $value")
                    SerialManager.LABEL_UID -> authorisation(uid = Context.serialManager!!.get(SerialManager.LABEL_UID)!!)
                    SerialManager.LABEL_LIGHT -> println("$param - $value")
                    SerialManager.LABEL_WINDOW -> println("$param - $value")
                    SerialManager.LABEL_POWER_SUPPLY -> println("$param - $value")
                    SerialManager.LABEL_STARTUP -> println("$param - $value")
                    SerialManager.LABEL_SAVE -> println("$param - $value")
                    SerialManager.LABEL_LOAD -> println("$param - $value")
                }
            }
            Context.serialManager!!.remove()
        }

        Context.serialManager!!.addPortFoundListener {
            settingsRootPanel.update()
            settingsUserPanel.update()

            println("Найден $it")
        }

        Context.serialManager!!.addUpdateLogListener {
            settingsRootPanel.update()
            settingsUserPanel.update()
        }

        Context.serialManager!!.addSyncConfigListener {
            println("Pinmap")
            println(it)
        }

        Context.serialManager!!.addGroupsUpdatedListener {
            println("Funcgroups")
            println(it)

            settingsRootPanel.update()
            settingsUserPanel.update()

            lightPanel.actionContent = it[SerialManager.LABEL_LIGHT]!!.toTypedArray()
            lightPanel.setContent()

            windowPanel.actionContent = it[SerialManager.LABEL_WINDOW]!!.toTypedArray()
            windowPanel.setContent()

            powerSupplyPanel.actionContent = it[SerialManager.LABEL_POWER_SUPPLY]!!.toTypedArray()
            powerMenuPanel.setContent()
        }
    }

    private fun authorisation(user: Document) {
        val options = ArrayList<String>()
        if (user.getBoolean(DBManager.superuserKeyword)) options.addAll(GUIConstants.menusOptions[Labels.FULL]!!)
        else GUIConstants.menusOptions[Labels.FULL]?.forEach { if (user.getBoolean(it)) options.add(it) }

        menuPanel.actionContent = options.toTypedArray()
        setPanel(userTree)
        updateFrame(rootTree[Labels.USER])
    }

    private fun authorisation(login: String, password: String) {
        val user = Context.dbManager!!.getUser(login, password)
        if(user == null) updateFrame(rootTree, force = true)
        else authorisation(user)
    }

    private fun authorisation(uid: String) {
        if(currentSubTree == userTree) {
            return
        }
        val user = Context.dbManager!!.getUser(uid)
        if(user == null) updateFrame(rootTree, force = true)
        else authorisation(user)
    }

    private fun updateFrame(newCurrentPanel: TreeNode<BasePanel> = Context.currentPanel!!, force: Boolean = false) {
        mainFrame.contentPane.isVisible = false
        if (newCurrentPanel != Context.currentPanel!! || force) {

            if (currentSubTree.name == Labels.ROOT && newCurrentPanel.name == Labels.USER ||
                newCurrentPanel.name == Labels.ROOT && currentSubTree.name == Labels.USER
            ) {
                currentSubTree = newCurrentPanel
                setPanel(currentSubTree)
            }

            Context.currentPanel = newCurrentPanel
            mainFrame.contentPane.removeAll()
            topPanel.setContent()
            mainFrame.contentPane.add(BorderLayout.NORTH, topPanel)
            mainFrame.contentPane.add(BorderLayout.CENTER, Context.currentPanel!!.value)
            mainFrame.title = Labels[Labels.NAME].title + " | " + Labels[Context.currentPanel!!.name].titleAlt
        }
        mainFrame.contentPane.isVisible = true
        mainFrame.isVisible = true
    }

    private fun setPanel(
        panel: TreeNode<BasePanel>,
        exclude: Array<String> = arrayOf(),
        include: Array<String> = arrayOf(),
        force: Boolean = false
    ) {
        fun drawTitle(panel: TreeNode<BasePanel>) {
            panel.value.setTitle()
        }

        fun drawContent(panel: TreeNode<BasePanel>) {
//            panel.value.components.forEach {
//                if (it.javaClass.name.contains("JScrollPane")) scrollPosition =
//                    (it as JScrollPane).verticalScrollBar.value
//            }
            panel.value.setContent()
        }

        suspend fun recursiveSet(
            panel: TreeNode<BasePanel>,
            exclude: Array<String> = arrayOf(),
            include: Array<String> = arrayOf()
        ) {
            coroutineScope {
                if (panel.name != Labels.LOGIN) panel.forEach { recursiveSet(it, exclude, include) }
                if (exclude.isNotEmpty()) {
                    launch { if (Labels.TITLE !in exclude) drawTitle(panel) }
                    launch {
                        if (panel.name !in exclude && (panel.parent.name == currentSubTree.name || force)) drawContent(
                            panel
                        )
                    }
                } else if (include.isNotEmpty()) {
                    launch { if (Labels.TITLE in include) drawTitle(panel) }
                    launch {
                        if (panel.name in include && (panel.parent.name == currentSubTree.name || force)) drawContent(
                            panel
                        )
                    }
                } else {
                    panel.value.background = Palette.BACKGROUND
                    launch { drawTitle(panel) }
                    launch { if (panel.parent.name == currentSubTree.name || force) drawContent(panel) }
                }
                panel.value.update()
            }
        }

        if (exclude.isNotEmpty() && include.isNotEmpty()) return
        runBlocking { recursiveSet(panel, exclude, include) }
        updateFrame()
    }

    init {
        dataBus.guiManager = this

        Context.serialManager = dataBus.serialManager
        Context.dbManager = dataBus.dbManager
        Context.mainFrame = mainFrame
        Context.currentPanel = rootTree

        grabSerialBus()

        rootTree.addChildren(
            Labels.POWER_MENU to powerMenuPanel,
            Labels.LOGIN to loginPanel,
            Labels.SETTINGS to settingsRootPanel
        )
        rootTree[Labels.LOGIN] += userTree
        rootTree += userTree

        userTree.addChildren(
            Labels.SETTINGS to settingsUserPanel,
            Labels.LIGHT to lightPanel,
            Labels.WINDOW to windowPanel,
            Labels.POWER_SUPPLY to powerSupplyPanel,
            Labels.USER_CONTROL to userControlPanel
        )

        userTree[Labels.USER_CONTROL].addChildren(
            Labels.USER_ADD to userAddPanel,
            Labels.USER_EDIT to userEditPanel
        )

        topPanel.addActionListener { action -> grubPanelAction(action) }
        welcomePanel.addActionListener { action -> grubPanelAction(action) }
        loginPanel.addActionListener { action -> grubPanelAction(action) }
        menuPanel.addActionListener { action -> grubPanelAction(action) }
        lightPanel.addActionListener { action -> grubPanelAction(action) }
        windowPanel.addActionListener { action -> grubPanelAction(action) }
        powerSupplyPanel.addActionListener { action -> grubPanelAction(action) }

        lightPanel.actionContent = Context.serialManager!!.funcGroups[SerialManager.LABEL_LIGHT]!!.toTypedArray()
        windowPanel.actionContent = Context.serialManager!!.funcGroups[SerialManager.LABEL_WINDOW]!!.toTypedArray()
        powerSupplyPanel.actionContent = Context.serialManager!!.funcGroups[SerialManager.LABEL_POWER_SUPPLY]!!.toTypedArray()
        settingsRootPanel.actionContent = arrayOf(Labels.PORT_CONNECT, Labels.SERIAL_LOG, Labels.CONFIG, Labels.ABOUT)
        settingsUserPanel.actionContent = arrayOf(Labels.PORT_CONNECT, Labels.SERIAL_LOG, Labels.CONFIG, Labels.ABOUT)

        Context.currentPanel = rootTree

        mainFrame.addComponentListener(object : ComponentAdapter() {
            override fun componentResized(componentEvent: ComponentEvent?) {
                val panelsForUpdate = arrayListOf(Labels.TITLE, Labels.ROOT)
                val newButtonsInLine = max((mainFrame.width - 2 * GUIConstants.sideFieldsWidth) / GUIConstants.menuButtonsSize.width, 1)
                if (Context.buttonsInLine != newButtonsInLine) {
                    Context.buttonsInLine = newButtonsInLine
                    panelsForUpdate.add(Labels.USER)
                    panelsForUpdate.add(Labels.LIGHT)
                    panelsForUpdate.add(Labels.WINDOW)
                    panelsForUpdate.add(Labels.POWER_SUPPLY)
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
}