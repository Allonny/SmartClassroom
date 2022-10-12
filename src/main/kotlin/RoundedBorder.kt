import java.awt.Color
import java.awt.Component
import java.awt.Graphics
import java.awt.Insets
import javax.swing.border.Border

class RoundedBorder (private val radius: Int, private val background: Color, private  val border: Color = background) : Border {
    override fun getBorderInsets(c: Component?): Insets = Insets(radius / 5, radius / 5, radius / 5, radius / 5)

    override fun isBorderOpaque(): Boolean = true

    override fun paintBorder(c: Component?, g: Graphics, x: Int, y: Int, width: Int, height: Int) {
        g.color = background
        g.fillRoundRect(x, y, width - 1, height - 1, radius, radius)
        g.drawRoundRect(x, y, width - 1, height - 1, radius, radius)

    }
}
