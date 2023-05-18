package gui.panels

import auxiliary.Fonts
import auxiliary.Labels
import auxiliary.Palette
import gui.GUIConstants
import gui.materialSwing.MaterialButton
import java.awt.*
import java.text.SimpleDateFormat
import java.util.*
import javax.swing.BoxLayout
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.Timer

class TopPanel(val context: Context): GUIPanel() {

    private val timeDatePanel: JPanel = JPanel()

    override fun paint(g: Graphics?) {
        val g2d = g as Graphics2D
        g2d.setRenderingHints(mapOf(
            RenderingHints.KEY_ANTIALIASING to RenderingHints.VALUE_ANTIALIAS_ON,
            RenderingHints.KEY_TEXT_ANTIALIASING to RenderingHints.VALUE_TEXT_ANTIALIAS_ON))
        super.paint(g2d)
    }

    override fun setTitle() {
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

    override fun setContent() {
        isVisible = false
        removeAll()
        background = Palette.BACKGROUND_ALT

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
            add(element, constraints)
            constraints.gridx += width
        }

        constraints.gridx = 0
        constraints.insets = GUIConstants.topPanelButtonsInsets

        if (context.currentPanel!!.parent == context.currentPanel) {
            val powerButton = MaterialButton(Labels[Labels.POWER_MENU].title, GUIConstants.topPanelIcons[Labels.POWER_MENU])
            powerButton.cornerRadius = GUIConstants.buttonCornerRadius
            powerButton.backingColor = Palette.BACKGROUND_ALT
            powerButton.backgroundColor = Palette.ACCENT_HIGH
            powerButton.foregroundColor = Palette.FOREGROUND

            powerButton.addActionListener { this.listener.action(Labels.POWER_MENU) } //  updateFrame(rootTree[Labels.POWER_MENU])
            addElement(powerButton, 1, GUIConstants.topPanelButtonsSize)
        } else {
            val backButton = MaterialButton(Labels[Labels.BACK].title, GUIConstants.topPanelIcons[Labels.BACK])
            backButton.cornerRadius = GUIConstants.buttonCornerRadius
            backButton.backingColor = Palette.BACKGROUND_ALT
            backButton.backgroundColor = Palette.ACCENT_NORMAL
            backButton.foregroundColor = Palette.FOREGROUND

            backButton.addActionListener { this.listener.action(Labels.BACK) } // updateFrame(currentPanel.parent)
            addElement(backButton, 1, GUIConstants.topPanelButtonsSize)
        }

        addElement(timeDatePanel, 1)

        val settingsButton = MaterialButton(Labels[Labels.SETTINGS].title, GUIConstants.topPanelIcons[Labels.SETTINGS])
        settingsButton.cornerRadius = GUIConstants.buttonCornerRadius
        settingsButton.disableFaceIcon = GUIConstants.topPanelIcons[Labels.SETTINGS + "_alt"]
        settingsButton.backingColor = Palette.BACKGROUND_ALT
        settingsButton.backgroundColor = Palette.ACCENT_NORMAL
        settingsButton.disableBackgroundColor = Palette.DISABLE
        settingsButton.foregroundColor = Palette.FOREGROUND
        settingsButton.disableForegroundColor = Palette.FOREGROUND_ALT

        settingsButton.isEnabled = Labels.SETTINGS in context.currentPanel!!.names
        settingsButton.addActionListener { this.listener.action(Labels.SETTINGS) } //updateFrame(currentPanel[Labels.SETTINGS])

        addElement(settingsButton, 1, GUIConstants.topPanelButtonsSize)

        isVisible = true
    }

    override fun update() {
        isVisible = false
        isVisible = true
    }

    var listener: PanelActionListener = object : PanelActionListener {
        override var action: (String?) -> Unit = {}
    }

    override fun addActionListener(action: (String?) -> Unit) {
        this.listener.action = action
    }

    init {
        layout = GridBagLayout()
        setTitle()
    }
}