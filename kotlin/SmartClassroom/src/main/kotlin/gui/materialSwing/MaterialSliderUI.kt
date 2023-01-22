package gui.materialSwing

import java.awt.*
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.awt.geom.RoundRectangle2D
import javax.swing.ImageIcon
import javax.swing.JSlider
import javax.swing.plaf.basic.BasicSliderUI
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

class MaterialSliderUI(c: JSlider?) : BasicSliderUI() {
    private var foregroundActualThickness: Int = 0
    private var backgroundActualThickness: Int = 0
    private var thumbActualThickness: Int = 0
    private var thumbActualLength: Int = 0

    val foregroundThickness: Int get() = foregroundActualThickness
    val backgroundThickness: Int get() = backgroundActualThickness
    val thumbThickness: Int get() = thumbActualThickness
    val thumbLength: Int get() = thumbActualLength

    var foregroundRelativeThickness: Double = 1.0
        set(value) {
            field = max(value, 1.0)
            setDecor()
        }

    var backgroundRelativeThickness: Double = 5.0
        set(value) {
            field = max(value, 1.0)
            setDecor()
        }

    var thumbRelativeThickness: Double = 1.0
        set(value) {
            field = max(value, 1.0)
            setDecor()
        }

    var thumbRelativeLength: Double = 1.0
        set(value) {
            field = max(value, 1.0)
            setDecor()
        }

    var sliderBackgroundLineColor: Color = Color.LIGHT_GRAY
        set(value) {
            field = value
            setDecor()
        }

    var sliderForegroundLineColor: Color = Color.BLUE
        set(value) {
            field = value
            setDecor()
        }

    var thumbColor: Color = Color.CYAN
        set(value) {
            field = value
            setDecor()
        }

    var thumbIcon: ImageIcon? = null
        set(value) {
            field = value
            setDecor()
        }

    var isForegroundPaint = true

    var isBackgroundPaint = true

    var isThumbPaint = true

    override fun calculateThumbLocation() {
        super.calculateThumbLocation()

        if (slider.snapToTicks) {
            val upperValue = slider.value + slider.extent
            var snappedValue = upperValue
            val majorTickSpacing = slider.majorTickSpacing
            val minorTickSpacing = slider.minorTickSpacing
            var tickSpacing = 0
            if (minorTickSpacing > 0) {
                tickSpacing = minorTickSpacing
            } else if (majorTickSpacing > 0) {
                tickSpacing = majorTickSpacing
            }
            if (tickSpacing != 0) {
                if ((upperValue - slider.minimum) % tickSpacing != 0) {
                    val temp = (upperValue - slider.minimum).toFloat() / tickSpacing.toFloat()
                    val whichTick = temp.roundToInt()
                    snappedValue = slider.minimum + whichTick * tickSpacing
                }
                if (snappedValue != upperValue) {
                    slider.extent = snappedValue - slider.value
                }
            }
        }

        if (slider.orientation == JSlider.HORIZONTAL) {
            val upperPosition = xPositionForValue(slider.value + slider.extent)
            thumbRect.x = upperPosition - thumbRect.width / 2
            thumbRect.y = trackRect.y
        } else {
            val upperPosition = yPositionForValue(slider.value + slider.extent)
            thumbRect.x = trackRect.x
            thumbRect.y = upperPosition - thumbRect.height / 2
        }
        slider.repaint()
    }

    override fun calculateThumbSize() {
        super.calculateThumbSize()
        thumbRect.setSize(thumbRect.width, thumbRect.height)
    }

    override fun getThumbSize(): Dimension? {
        return thumbRect.size
    }

    private fun createThumbShape(width: Double, height: Double): Shape {
        val smallerSide = min(width, height)
        return RoundRectangle2D.Double(0.0, 0.0, width, height, smallerSide, smallerSide)
    }

    override fun paintThumb(g: Graphics) {
        val g2d = g.create() as Graphics2D
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

        g2d.translate(thumbRect.x, thumbRect.y)

        if (isThumbPaint) {
            val thumbShape: Shape = createThumbShape(thumbRect.width.toDouble(), thumbRect.height.toDouble())
            g2d.color = thumbColor
            g2d.fill(thumbShape)

            if (thumbIcon != null) {
                g2d.drawImage(
                    thumbIcon!!.image,
                    (thumbRect.width - thumbIcon!!.iconWidth) / 2,
                    (thumbRect.height - thumbIcon!!.iconHeight) / 2, null
                )
            }
        }

        g2d.dispose()
    }

    override fun paintTrack(g: Graphics) {
        val g2d = g.create() as Graphics2D
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

        val shift = thumbLength - thumbThickness

        g2d.translate(trackRect.x, trackRect.y)
        if (slider.orientation == JSlider.HORIZONTAL) {
            if (isBackgroundPaint) {
                g2d.color = sliderBackgroundLineColor
                g2d.fillRoundRect(
                    - (shift + backgroundThickness) / 2,
                    (trackRect.height - backgroundThickness) / 2,
                    trackRect.width + shift + backgroundThickness,
                    backgroundThickness,
                    backgroundThickness, backgroundThickness)
            }

            if (isForegroundPaint) {
                g2d.color = sliderForegroundLineColor
                g2d.fillRoundRect(
                    - (shift + foregroundThickness) / 2,
                    (trackRect.height - foregroundThickness) / 2,
                    thumbRect.x + shift + foregroundThickness,
                    foregroundThickness,
                    foregroundThickness, foregroundThickness
                )
            }

        } else {
            if (isBackgroundPaint) {
                g2d.color = sliderBackgroundLineColor
                g2d.fillRoundRect(
                    (trackRect.width - backgroundThickness) / 2,
                    - (shift + backgroundThickness) / 2,
                    backgroundThickness,
                    trackRect.height + shift + backgroundThickness,
                    backgroundThickness, backgroundThickness
                )
            }

            if (isForegroundPaint) {
                g2d.color = sliderForegroundLineColor
                g2d.fillRoundRect(
                    (trackRect.width - foregroundThickness) / 2,
                    thumbRect.y - (shift + foregroundThickness) / 2,
                    foregroundThickness,
                    trackRect.height - (thumbRect.y + shift + foregroundThickness),
                    foregroundThickness, foregroundThickness
                )
            }
        }
        g2d.translate(-trackRect.x, -trackRect.y)
        g2d.dispose()
    }

    private fun setDecor() {
        val c = this.slider!!
        val smallerSide = min(c.preferredSize.height, c.preferredSize.width)
        foregroundActualThickness = (smallerSide / foregroundRelativeThickness).toInt()
        backgroundActualThickness = (smallerSide / backgroundRelativeThickness).toInt()
        thumbActualThickness = (smallerSide / thumbRelativeThickness).toInt()
        thumbActualLength = (thumbActualThickness * thumbRelativeLength).toInt()

        val maxThickness = maxOf(foregroundThickness, backgroundThickness, thumbThickness)
        val maxShift = (maxThickness * thumbRelativeLength).toInt()

        if (c.orientation == JSlider.HORIZONTAL) {
            trackRect?.setSize(c.preferredSize.width - maxShift, maxThickness)
            thumbRect?.setSize(thumbLength, thumbThickness)
        }
        else {
            trackRect?.setSize(maxThickness, c.preferredSize.height - maxShift)
            thumbRect?.setSize(thumbThickness, thumbLength)
        }
    }

    init {
        if (c != null) {
            super.slider = c
            super.slider.addComponentListener(object : ComponentAdapter() {
                override fun componentResized(e: ComponentEvent?) {
                    super.componentResized(e)
                    setDecor()
                }
            })
            setDecor()
        }
    }
}