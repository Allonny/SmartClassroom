import java.awt.*
import java.io.File
import javax.imageio.ImageIO
import javax.swing.BorderFactory
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.ImageIcon
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.Spring
import javax.swing.SpringLayout
import javax.swing.border.Border

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

    private val topPanelButtonsSize = Dimension(100, 100)
    private val menuButtonsSize = Dimension(400, 300)

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
        topPanel.background = ColorPalette().backgroundAlt
        topPanel.size = Dimension(mainFrame.width, 40)
        topPanel.layout = GridBagLayout()
        val constraints = GridBagConstraints()
        constraints.fill = GridBagConstraints.HORIZONTAL

        fun addElement(element: JComponent, width: Int, setSize: Dimension? = null) {
            constraints.gridwidth = width
            if (setSize != null) {
                constraints.ipadx = topPanelButtonsSize.width - element.minimumSize.width
                constraints.ipady = topPanelButtonsSize.height - element.minimumSize.height
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
            val powerButton = JButton("Питание")
            setButtonStyle(powerButton, 15)
            powerButton.addActionListener{
                currentPanel = currentPanel!!["powerMenu"]
                updateFrame()
            }
            addElement(powerButton, 1, topPanelButtonsSize)
        } else {
            val backButton = JButton(ImageIcon(ImageIO.read(File("resources/Back.png"))))

            setButtonStyle(backButton, 15)
            backButton.addActionListener {
                currentPanel = currentPanel!!.parent
                updateFrame()
            }
            addElement(backButton, 1, topPanelButtonsSize)
        }

        addElement(JLabel("Hello world"), 4)

        val settingsButton = JButton("Настройки")
        setButtonStyle(settingsButton, 15)
        if("settings" in currentPanel!!.names)
            settingsButton.addActionListener{
                currentPanel = currentPanel!!["settings"]
                updateFrame()
            }
        else settingsButton.isEnabled = false
        addElement(settingsButton, 1, topPanelButtonsSize)

        topPanel.isVisible = true

//
//        topPanel.isVisible = false
//        topPanel.removeAll()
//        topPanel.layout = BorderLayout()
//        topPanel.size = Dimension(mainFrame.width, 50)
//        topPanel.background = ColorPalette().backgroundAlt
//        topPanel.border = BorderFactory.createMatteBorder(10, 10, 10, 10, ColorPalette().backgroundAlt)
//
//        if(currentPanel!!.parent == null) {
//            val powerButton = JButton("Питание")
//            setButtonStyle(powerButton, 15)
//            powerButton.addActionListener{
//                currentPanel = currentPanel!!["powerMenu"]
//                updateFrame()
//            }
//            topPanel.add(BorderLayout.WEST, powerButton)
//        } else {
//            val backButton = JButton("Назад")
//            setButtonStyle(backButton, 15)
//            backButton.addActionListener {
//                currentPanel = currentPanel!!.parent
//                updateFrame()
//            }
//            topPanel.add(BorderLayout.WEST, backButton)
//        }
//
//        if("settings" in currentPanel!!.names) {
//            val settingsButton = JButton("Настройки")
//            setButtonStyle(settingsButton)
//            settingsButton.addActionListener{
//                currentPanel = currentPanel!!["settings"]
//                updateFrame()
//            }
//            topPanel.add(BorderLayout.EAST, settingsButton)
//        }
//
//        topPanel.isVisible = true
    }

    private fun setPanel(panel: TreeNode<JPanel>?) {
        when(panel!!.name) {
            "root" -> setWelcomePanel(panel.value)
            "basic" -> setBasicMenuPanel(panel.value)
            "extended" -> setExtendedMenuPanel(panel.value)
            "admin" -> setAdminMenuPanel(panel.value)
        }
        panel.forEach { setPanel(it) }
    }

    private fun setWelcomePanel(panel: JPanel) {
        panel.removeAll()
        panel.layout = BorderLayout()
        panel.background = ColorPalette().background

        panel.add(BorderLayout.NORTH, setTitle("Добро пожаловать"))

        val buttonGrid = JPanel(GridLayout(0, 1, 10, 10))
        buttonGrid.background = ColorPalette().background
        val basicEnterButton = JButton("Войти как студент")
        setButtonStyle(basicEnterButton)
        basicEnterButton.addActionListener{
            currentPanel = rootTree["basic"]
            updateFrame()
        }
        buttonGrid.add(basicEnterButton)

        val extendedEnterButton = JButton("Войти как преподаватель")
        setButtonStyle(extendedEnterButton)
        extendedEnterButton.addActionListener{
            currentPanel = rootTree["extended"]
            updateFrame()
        }
        buttonGrid.add(extendedEnterButton)

        val adminEnterButton = JButton("Войти как администратор")
        setButtonStyle(adminEnterButton)
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

    private fun setBasicMenuPanel(panel: JPanel) {
        panel.removeAll()
        panel.layout = BorderLayout()
        panel.background = ColorPalette().background

        panel.add(BorderLayout.NORTH, setTitle("Меню"))

        val buttonGrid = JPanel(GridLayout(0, 2, 10, 10))
        buttonGrid.background = ColorPalette().background
        val lightButton = JButton("Управление светом")
        setButtonStyle(lightButton)
        lightButton.addActionListener{
            currentPanel = currentPanel!!["light"]
            updateFrame()
        }
        buttonGrid.add(lightButton)

        val windowButton = JButton("Управление проветриванием")
        setButtonStyle(windowButton)
        windowButton.addActionListener{
            currentPanel = currentPanel!!["window"]
            updateFrame()
        }
        buttonGrid.add(windowButton)

        panel.add(BorderLayout.CENTER, buttonGrid)
        panel.add(BorderLayout.WEST, Box.createHorizontalStrut(mainFrame.width / 5))
        panel.add(BorderLayout.EAST, Box.createHorizontalStrut(mainFrame.width / 5))
        panel.add(BorderLayout.SOUTH, Box.createVerticalStrut(mainFrame.height / 5))
    }

    private fun setExtendedMenuPanel(panel: JPanel) {
        panel.removeAll()
        panel.layout = BorderLayout()
        panel.background = ColorPalette().background

        panel.add(BorderLayout.NORTH, setTitle("Меню"))

        val buttonGrid = JPanel(GridLayout(0, 2, 10, 10))
        buttonGrid.background = ColorPalette().background
        val lightButton = JButton("Управление светом")
        setButtonStyle(lightButton)
        lightButton.addActionListener{
            currentPanel = currentPanel!!["light"]
            updateFrame()
        }
        buttonGrid.add(lightButton)

        val windowButton = JButton("Управление проветриванием")
        setButtonStyle(windowButton)
        windowButton.addActionListener{
            currentPanel = currentPanel!!["window"]
            updateFrame()
        }
        buttonGrid.add(windowButton)

        val powerButton = JButton("Управление питанием")
        setButtonStyle(powerButton)
        powerButton.addActionListener{
            currentPanel = currentPanel!!["powerSupply"]
            updateFrame()
        }
        buttonGrid.add(powerButton)

        panel.add(BorderLayout.CENTER, buttonGrid)
        panel.add(BorderLayout.WEST, Box.createHorizontalStrut(mainFrame.width / 5))
        panel.add(BorderLayout.EAST, Box.createHorizontalStrut(mainFrame.width / 5))
        panel.add(BorderLayout.SOUTH, Box.createVerticalStrut(mainFrame.height / 5))
    }

    private fun setAdminMenuPanel(panel: JPanel) {
        panel.removeAll()
        panel.layout = BorderLayout()
        panel.background = ColorPalette().background

        panel.add(BorderLayout.NORTH, setTitle("Меню"))

        val buttonGrid = JPanel(GridLayout(0, 2, 10, 10))
        buttonGrid.background = ColorPalette().background
        val lightButton = JButton("Управление светом")
        setButtonStyle(lightButton)
        lightButton.addActionListener{
            currentPanel = currentPanel!!["light"]
            updateFrame()
        }
        buttonGrid.add(lightButton)

        val windowButton = JButton("Управление проветриванием")
        setButtonStyle(windowButton)
        windowButton.addActionListener{
            currentPanel = currentPanel!!["window"]
            updateFrame()
        }
        buttonGrid.add(windowButton)

        val powerButton = JButton("Управление питанием")
        setButtonStyle(powerButton)
        powerButton.addActionListener{
            currentPanel = currentPanel!!["powerSupply"]
            updateFrame()
        }
        buttonGrid.add(powerButton)

        val addUserButton = JButton("Добавить пользователя")
        setButtonStyle(addUserButton)
        addUserButton.addActionListener{
            currentPanel = currentPanel!!["addUser"]
            updateFrame()
        }
        buttonGrid.add(addUserButton)

        panel.add(BorderLayout.CENTER, buttonGrid)
        panel.add(BorderLayout.WEST, Box.createHorizontalStrut(mainFrame.width / 5))
        panel.add(BorderLayout.EAST, Box.createHorizontalStrut(mainFrame.width / 5))
        panel.add(BorderLayout.SOUTH, Box.createVerticalStrut(mainFrame.height / 5))
    }

    private fun setButtonStyle(button: JButton, size: Int = 18) {
        button.background = ColorPalette().accent
        //button.margin = Insets(10, 10, 10, 10)
        button.isBorderPainted = false
        button.foreground = ColorPalette().foreground
        button.font = Font(button.font.name, button.font.style, size)
    }

    private fun setTitle(text: String): JPanel {
        val title = JLabel(text)
        title.font = Font(title.font.name, title.font.style, 25)
        title.foreground = ColorPalette().foreground
        val titlePanel = JPanel(FlowLayout(FlowLayout.LEFT))
        titlePanel.add(title)
        titlePanel.background = ColorPalette().background
        return titlePanel
    }
}