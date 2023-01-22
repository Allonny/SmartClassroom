package gui.materialSwing

import java.awt.Color
import java.awt.Component
import java.awt.Graphics
import java.awt.Insets
import javax.swing.border.Border
import java.awt.Graphics2D
import java.awt.RenderingHints

class RoundedBorder(
    private val radius: Int,
    private val background: Color,
    private val border: Color = background,
    private val borderThickness: Int = 0,
    private val insetsDivider: Int = 5
) : Border {
    override fun getBorderInsets(c: Component?): Insets = Insets(radius / insetsDivider + borderThickness, radius / insetsDivider + borderThickness, radius / insetsDivider + borderThickness, radius / insetsDivider + borderThickness)

    override fun isBorderOpaque(): Boolean = true

    override fun paintBorder(c: Component?, g: Graphics, x: Int, y: Int, width: Int, height: Int) {
        val g2d = g as Graphics2D
        g2d.setRenderingHints(mapOf(RenderingHints.KEY_ANTIALIASING to RenderingHints.VALUE_ANTIALIAS_ON))

        if (borderThickness > 0) {
            g2d.color = border
            g2d.fillRoundRect(x, y, width - 1, height - 1, radius, radius)
        }
        g2d.color = background
        g2d.fillRoundRect(
            x + borderThickness,
            y + borderThickness,
            width - 2 * borderThickness - 1,
            height - 2 * borderThickness - 1,
            radius - borderThickness,
            radius - borderThickness)
    }
}
