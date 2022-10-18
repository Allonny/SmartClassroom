import java.awt.Color
import java.awt.Dimension
import java.awt.Font
import javax.swing.Icon
import javax.swing.ImageIcon
import javax.swing.JButton
import javax.swing.border.Border

class MaterialButton(
    var title: String? = null,
    var icon: ImageIcon? = null
): JButton() {
    companion object {
        const val ICON_CENTER = 0
        const val ICON_LEFT = -1
        const val ICON_RIGHT = 1
        const val ICON_TOP = 2
        const val ICON_BOTTOM = -2
    }
    private val positions = arrayOf(ICON_CENTER, ICON_LEFT, ICON_RIGHT, ICON_TOP, ICON_BOTTOM)

    var cornerRadius: Int = 0
        get() = field
        set(value) {
            field = value
            border = RoundedBorder(cornerRadius, backgroundColor, borderColor, borderThickness)
        }

    var borderThickness: Int = 0
        get() = field
        set(value) {
            field = value
            border = RoundedBorder(cornerRadius, backgroundColor, borderColor, borderThickness)
        }

    var backingColor: Color = Color.WHITE
        get() = field
        set(value) {
            field = value
            background = field
        }

    var backgroundColor: Color = Color.GRAY
        get() = field
        set(value) {
            field = value
            border = RoundedBorder(cornerRadius, backgroundColor, borderColor, borderThickness)
        }

    var borderColor: Color = Color.LIGHT_GRAY
        get() = field
        set(value) {
            field = value
            border = RoundedBorder(cornerRadius, backgroundColor, borderColor, borderThickness)
        }

    var foregroundColor: Color = Color.BLACK
        get() = field
        set(value) { field = value }

    var iconOnly: Boolean = false
        get() = field
        set(value) { field = value }

    var iconPosition: Int = 0
        get() = field
        set(value) {if (value !in positions) field = ICON_CENTER else field = value}

    var iconSize: Dimension = Dimension(0, 0)
        get() = field
        set(value) { field = value }

    override fun getText(): String? = title

    override fun setText(text: String?) {
        title = text
    }

    override fun getIcon(): Icon? = icon

    override fun setIcon(defaultIcon: Icon?) {
        icon = defaultIcon as ImageIcon?
    }

    override fun getBorder(): Border {
        return super.getBorder()
    }

    override fun setBorder(border: Border?) {
        super.setBorder(border)
    }

    init {

    }
}