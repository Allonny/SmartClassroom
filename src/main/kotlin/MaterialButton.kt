import java.awt.*
import javax.swing.*

class MaterialButton : JButton {

    companion object {
        const val ICON_CENTER = BorderLayout.CENTER
        const val ICON_LEFT = BorderLayout.WEST
        const val ICON_RIGHT = BorderLayout.EAST
        const val ICON_TOP = BorderLayout.NORTH
        const val ICON_BOTTOM = BorderLayout.SOUTH
        val ICON_ORIGINAL_SIZE = Dimension(100, 100)
    }
    private val positions: Set<String> = setOf(ICON_CENTER, ICON_LEFT, ICON_RIGHT, ICON_TOP, ICON_BOTTOM)

    private val facePanel: JPanel = JPanel(BorderLayout())

    private var faceText: JPanel = JPanel(GridLayout(0, 1))

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
            setFace()
        }
    var disableFaceIcon: ImageIcon? = null
        set(value) {
            field = value
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

    var iconOnly: Boolean = true
        set(value) {
            field = value
            if (field && iconPosition != ICON_CENTER) iconPosition = ICON_CENTER
            if (!field && iconPosition == ICON_CENTER) iconPosition = ICON_LEFT
            setFace()
        }

    var iconPosition: String = ICON_CENTER
        set(value) {
            field = if (value !in positions) ICON_CENTER else value
            if (!iconOnly && field == ICON_CENTER) iconOnly = true
            if (iconOnly && field != ICON_CENTER) iconOnly = false
            setFace()
        }

    var iconSize: Dimension = ICON_ORIGINAL_SIZE
        set(value) {
            field = value
            enableFaceIcon = ImageIcon(enableFaceIcon!!.image.getScaledInstance(field.width, field.height, Image.SCALE_SMOOTH))
            disableFaceIcon = ImageIcon(disableFaceIcon!!.image.getScaledInstance(field.width, field.height, Image.SCALE_SMOOTH))
            setFace()
        }

    override fun setFont(font: Font?) {
        super.setFont(font)
        if (facePanel != null) setFace()
    }

    private fun setFace() {
        val currentTitle = if (this.isEnabled) enableFaceTitle else disableFaceTitle
        val currentIcon = if (this.isEnabled) enableFaceIcon else disableFaceIcon

        this.isVisible = false
        facePanel.removeAll()
        this.toolTipText = currentTitle
        setFaceText(currentTitle)
        if (enableFaceIcon == null || !iconOnly) facePanel.add(faceText, BorderLayout.CENTER)
        if (currentIcon != null) facePanel.add(JLabel(currentIcon), iconPosition)
        this.isVisible = true
    }

    private fun setDecor() {
        this.isVisible = false
        this.background = backingColor
        this.foreground = backingColor
        currentBackground = if (this.isEnabled) backgroundColor else disableBackgroundColor
        currentForeground = if (this.isEnabled) foregroundColor else disableForegroundColor
        border = RoundedBorder(cornerRadius, currentBackground, borderColor, borderThickness)
        facePanel.background = currentBackground
        faceText.background = currentBackground
        faceText.foreground = currentForeground
        this.isVisible = true
    }

    private fun setFaceText(text: String?) {
        faceText.removeAll()
        text?.split("\n")?.forEach {
            val line = JLabel(it)
            line.background = faceText.background
            line.foreground = faceText.foreground
            line.horizontalAlignment = JLabel.CENTER
            line.font = this.font
            faceText.add(line)
        }
    }

    constructor() {}

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
        this.add(facePanel)
        setDecor()
        setFace()
    }
}