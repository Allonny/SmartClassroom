package gui.panels

import auxiliary.Fonts
import auxiliary.Palette
import gui.GUIConstants
import gui.materialSwing.InscribedLabel
import java.awt.*
import java.lang.Exception
import javax.swing.JComponent
import javax.swing.JFrame
import javax.swing.JPanel

open class BasePanel(private val mainFrame: JFrame): GUIPanel() {
    open var actionContent = arrayOf<String>()
    open val titleText = ""

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

        val title = InscribedLabel(titleText.uppercase(), Fonts.TITLE, mainFrame.width - GUIConstants.sideFieldsWidth, GUIConstants.maxTitleHeight)
        title.foreground = Palette.FOREGROUND
        val titlePanel = JPanel(FlowLayout(FlowLayout.LEFT))
        titlePanel.add(title)
        titlePanel.background = Palette.BACKGROUND
        add(titlePanel, BorderLayout.NORTH)
    }

    override fun setContent() {
        arrayOf(BorderLayout.CENTER, BorderLayout.WEST, BorderLayout.EAST, BorderLayout.SOUTH).forEach {
            try { remove((layout as BorderLayout).getLayoutComponent(it))
            } catch (e: Exception) {} }
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
    }
}