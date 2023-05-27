package gui.panels

import auxiliary.Fonts
import auxiliary.Labels
import auxiliary.Palette
import gui.materialSwing.MaterialButton
import gui.materialSwing.RoundedBorder
import java.awt.*
import java.lang.Exception
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel

open class FieldPanel: GUIPanel() {
    open var isExpanded = false
    open val titleText = ""
    open val titleIcon = JLabel()
    private val expandIcon = GUIConstants.settingsIcons[Labels.EXPAND]!!
    private val collapseIcon = GUIConstants.settingsIcons[Labels.COLLAPSE]!!

    override fun paint(g: Graphics?) {
        val g2d = g as Graphics2D
        g2d.setRenderingHints(mapOf(
            RenderingHints.KEY_ANTIALIASING to RenderingHints.VALUE_ANTIALIAS_ON,
            RenderingHints.KEY_TEXT_ANTIALIASING to RenderingHints.VALUE_TEXT_ANTIALIAS_ON))
        super.paint(g2d)
    }

    override fun setTitle() {
        try { remove((layout as BorderLayout).getLayoutComponent(BorderLayout.NORTH))
        } catch (e: Exception) {}

        val titlePanel = JPanel(GridBagLayout())
        titlePanel.background = Palette.TRANSPARENT
        titlePanel.border = RoundedBorder(50, Palette.BACKGROUND_ALT)

        val constraints = GridBagConstraints()
        constraints.fill = GridBagConstraints.BOTH
        constraints.insets = GUIConstants.settingsFieldInsets

        constraints.gridx = 0
        constraints.weightx = 0.0
        constraints.weighty = 1.0
        setSize(titleIcon, constraints, GUIConstants.settingButtonSize)
        titlePanel.add(titleIcon, constraints)

        constraints.gridx = 1
        constraints.weightx = 0.0
        constraints.weighty = 1.0
        val title = JLabel(titleText)
        title.foreground = Palette.FOREGROUND
        title.font = Fonts.SUBTITLE
        title.font = title.font.deriveFont(30f)
        setSize(title, constraints, GUIConstants.settingLabelSize)
        titlePanel.add(title, constraints)

        val expandButton = MaterialButton()
        expandButton.cornerRadius = GUIConstants.buttonCornerRadius
        expandButton.enableFaceTitle = if (isExpanded) Labels[Labels.COLLAPSE].title else Labels[Labels.EXPAND].title
        expandButton.enableFaceIcon = if (isExpanded) collapseIcon else expandIcon
        expandButton.backingColor = Palette.BACKGROUND_ALT
        expandButton.backgroundColor = Palette.ACCENT_NORMAL
        expandButton.foregroundColor = Palette.FOREGROUND

        expandButton.addActionListener { this.listener.action(if (isExpanded) Labels.COLLAPSE else Labels.EXPAND) }

        constraints.gridx = 2
        constraints.weightx = 0.0
        constraints.weighty = 1.0
        setSize(expandButton, constraints, GUIConstants.settingButtonSize)
        titlePanel.add(expandButton, constraints)

        add(titlePanel, BorderLayout.NORTH)
    }

    override fun setContent() {
        arrayOf(BorderLayout.CENTER, BorderLayout.WEST, BorderLayout.EAST, BorderLayout.SOUTH).forEach {
            try { remove((layout as BorderLayout).getLayoutComponent(it))
            } catch (e: Exception) {} }
    }

    override fun update() {
        isVisible = false
        setTitle()
        setContent()
        isVisible = true
    }

    var listener: PanelActionListener = object : PanelActionListener {
        override var action: (String?) -> Unit = {}
    }

    override fun addActionListener(action: (String?) -> Unit) {
        this.listener.action = action
    }

    protected fun setSize(element: JComponent, constraints: GridBagConstraints, size: Dimension? = null, changeableWidth: Boolean = false, changeableHeight: Boolean = false) {
        if (size != null) {
            if (!changeableWidth) constraints.ipadx = size.width - element.minimumSize.width
            if (!changeableHeight) constraints.ipady = size.height - element.minimumSize.height
            constraints.weightx = 0.0
        } else {
            constraints.ipadx = 0
            constraints.ipady = 0
            constraints.weightx = 1.0
        }
    }

    init {
        layout = BorderLayout()
        background = Palette.BACKGROUND
        border = RoundedBorder(50, Palette.BACKGROUND_ALT)
    }
}