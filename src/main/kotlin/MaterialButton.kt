import java.awt.*
import javax.swing.*

class MaterialButton : JButton {

    companion object {
        const val ICON_CENTER = BorderLayout.CENTER
        const val ICON_LEFT = BorderLayout.WEST
        const val ICON_RIGHT = BorderLayout.EAST
        const val ICON_TOP = BorderLayout.NORTH
        const val ICON_BOTTOM = BorderLayout.SOUTH
        val ICON_ORIGINAL_SIZE = Dimension(-1, -1)
    }
    private val positions: Set<String> = setOf(ICON_CENTER, ICON_LEFT, ICON_RIGHT, ICON_TOP, ICON_BOTTOM)

    private val facePanel: JPanel = JPanel(BorderLayout())

    private var faceText: Array<JLabel> = arrayOf()

    private var currentBackground: Color = Color.GRAY
    private var currentForeground: Color = Color.BLACK

    var enableFaceTitle: String? = null
        set(value) {
            field = value
            setFace()
        }
    var disableFaceTitle: String? = null
        set(value) {
            field = value
            setFace()
        }

    var enableFaceIcon: ImageIcon? = null
        set(value) {
            field = value
            iconSize = iconSize
            setFace()
        }
    var disableFaceIcon: ImageIcon? = null
        set(value) {
            field = value
            iconSize = iconSize
            setFace()
        }

    var cornerRadius: Int = 0
        set(value) {
            field = value
            setDecor()
        }

    var borderThickness: Int = 0
        set(value) {
            field = value
            setDecor()
        }

    var backingColor: Color = Color.WHITE
        set(value) {
            field = value
            setDecor()
        }

    var backgroundColor: Color = Color.GRAY
        set(value) {
            field = value
            setDecor()
        }

    var disableBackgroundColor: Color = Color.LIGHT_GRAY
        set(value) {
            field = value
            setDecor()
        }

    var borderColor: Color = Color.LIGHT_GRAY
        set(value) {
            field = value
            setDecor()
        }

    var foregroundColor: Color = Color.BLACK
        set(value) {
            field = value
            setDecor()
        }

    var disableForegroundColor: Color = Color.GRAY
        set(value) {
            field = value
            setDecor()
        }

    override fun setEnabled(b: Boolean) {
        super.setEnabled(b)
        setDecor()
        setFace()
    }

    var iconOnly: Boolean = false
        set(value) {
            field = value
            if (field) iconPosition = ICON_CENTER
            setFace()
        }

    var iconPosition: String = ICON_CENTER
        set(value) {
            field = if (value !in positions) ICON_CENTER else value
            iconOnly = field == ICON_CENTER
            setFace()
        }

    var iconSize: Dimension = ICON_ORIGINAL_SIZE
        set(value) {
            field = value
            enableFaceIcon = ImageIcon(enableFaceIcon!!.image.getScaledInstance(field.width, field.height, Image.SCALE_SMOOTH))
            disableFaceIcon = ImageIcon(disableFaceIcon!!.image.getScaledInstance(field.width, field.height, Image.SCALE_SMOOTH))
            setFace()
        }


//
//    override fun getFont(): Font = faceText.font

//    override fun setFont(font: Font?) {
//        faceText.font = font
//        setFace()
//    }


    private fun setFace() {
        val currentTitle = if (this.isEnabled) enableFaceTitle else disableFaceTitle
        val currentIcon = if (this.isEnabled) enableFaceIcon else disableFaceIcon

        facePanel.removeAll()
        this.toolTipText = currentTitle
        if (enableFaceIcon == null || !iconOnly) {
            setFaceText(currentTitle)
            val faceTextBox = Box(BoxLayout.PAGE_AXIS)
            faceText.forEach { faceTextBox.add(it) }
            facePanel.add(faceTextBox, BorderLayout.CENTER)
        }
        if (currentIcon != null) facePanel.add(JLabel(currentIcon), iconPosition)
    }

    private fun setDecor() {
        this.background = backingColor
        this.foreground = backingColor
        currentBackground = if (this.isEnabled) backgroundColor else disableBackgroundColor
        currentForeground = if (this.isEnabled) foregroundColor else disableForegroundColor
        border = RoundedBorder(cornerRadius, currentBackground, borderColor, borderThickness)
        facePanel.background = currentBackground
        faceText.forEach { it.background = currentBackground; it.foreground = currentForeground }
    }

    private fun setFaceText(text: String?) {
        faceText = text?.split("\n")?.map { JLabel(it) }?.toTypedArray() ?: arrayOf()
        faceText.forEach { it.font = this.font; it.background = currentBackground; it.foreground = currentForeground }
    }

    constructor(title: String?) {
        this.enableFaceTitle = title
        this.disableFaceTitle = this.enableFaceTitle
    }

    constructor(icon: ImageIcon?) {
        this.enableFaceIcon = icon
        this.disableFaceIcon = this.enableFaceIcon
    }

    constructor(title: String?, icon: ImageIcon?) {
        this.enableFaceTitle = title
        this.disableFaceTitle = this.enableFaceTitle
        this.enableFaceIcon = icon
        this.disableFaceIcon = this.enableFaceIcon
    }

    init {
//        this.font = Fonts.REGULAR.deriveFont(30f)
        this.add(facePanel)
        setDecor()
        setFace()
    }
}