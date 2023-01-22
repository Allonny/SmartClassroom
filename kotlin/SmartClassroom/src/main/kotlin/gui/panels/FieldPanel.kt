package gui.panels

import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.RenderingHints

open class FieldPanel: GUIPanel() {
    override fun paint(g: Graphics?) {
        val g2d = g as Graphics2D
        g2d.setRenderingHints(mapOf(RenderingHints.KEY_ANTIALIASING to RenderingHints.VALUE_ANTIALIAS_ON))
        super.paint(g2d)
    }

    override fun setTitle() {

    }

    override fun setContent() {
    }

    override fun addActionListener(action: (String?) -> Unit) {
    }
}