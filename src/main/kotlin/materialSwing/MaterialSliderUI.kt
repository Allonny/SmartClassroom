package materialSwing

import java.awt.*
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.awt.geom.Ellipse2D
import java.lang.Integer.max
import javax.swing.ImageIcon
import javax.swing.JSlider
import javax.swing.plaf.basic.BasicSliderUI
import kotlin.math.min
import kotlin.math.roundToInt

class MaterialSliderUI(c: JSlider?) : BasicSliderUI() {
    private var sliderBackgroundLineThickness: Int = 1
    private var sliderForegroundLineThickness: Int = 1

    var backgroundToForegroundThicknessRatio: Double = 5.0
        set(value) {
            field = value
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

    var isSliderBackgroundPaint = true
        set(value) {
            field = value
            setDecor()
        }

    var isSliderForegroundPaint = true
        set(value) {
            field = value
            setDecor()
        }

    var isThumbPaint = true
        set(value) {
            field = value
            setDecor()
        }

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

    private fun createThumbShape(radius: Double): Shape {
        return Ellipse2D.Double(0.0, 0.0, radius, radius)
    }

    override fun paintThumb(g: Graphics) {
        val g2d = g.create() as Graphics2D
        g2d.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON
        )

        g2d.translate(thumbRect.x, thumbRect.y)

        if (isThumbPaint) {
            val thumbShape: Shape = createThumbShape(sliderForegroundLineThickness.toDouble())
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

        g2d.translate(trackRect.x, trackRect.y)
        if (slider.orientation == JSlider.HORIZONTAL) {
            if (isSliderBackgroundPaint) {
                g2d.color = sliderBackgroundLineColor
                g2d.fillRoundRect(
                    -sliderBackgroundLineThickness / 2,
                    (trackRect.height - sliderBackgroundLineThickness) / 2,
                    trackRect.width,
                    sliderBackgroundLineThickness,
                    sliderBackgroundLineThickness, sliderBackgroundLineThickness
                )
            }

            if (isSliderForegroundPaint) {
                g2d.color = sliderForegroundLineColor
                g2d.fillRoundRect(
                    -thumbRect.width / 2,
                    (trackRect.height - sliderForegroundLineThickness) / 2,
                    thumbRect.x + thumbRect.width,
                    sliderForegroundLineThickness,
                    sliderForegroundLineThickness, sliderForegroundLineThickness
                )
            }

        } else {
            if (isSliderBackgroundPaint) {
                g2d.color = sliderBackgroundLineColor
                g2d.fillRoundRect(
                    (trackRect.width - sliderBackgroundLineThickness) / 2,
                    -sliderBackgroundLineThickness / 2,
                    sliderBackgroundLineThickness,
                    trackRect.height + sliderBackgroundLineThickness,
                    sliderBackgroundLineThickness, sliderBackgroundLineThickness
                )
            }

            if (isSliderForegroundPaint) {
                g2d.color = sliderForegroundLineColor
                g2d.fillRoundRect(
                    (trackRect.width - sliderForegroundLineThickness) / 2,
                    thumbRect.y - thumbRect.height / 2,
                    sliderForegroundLineThickness,
                    trackRect.height - thumbRect.y + thumbRect.height,
                    sliderForegroundLineThickness, sliderForegroundLineThickness
                )
            }
        }
        g2d.translate(-trackRect.x, -trackRect.y)
        g2d.dispose()
    }

    private fun setDecor() {
        val c = this.slider!!
        sliderForegroundLineThickness = min(c.preferredSize.height, c.preferredSize.width)
        sliderBackgroundLineThickness = (sliderForegroundLineThickness / backgroundToForegroundThicknessRatio).toInt()

        val maxThickness = max(sliderForegroundLineThickness, sliderBackgroundLineThickness)

        if (slider.orientation == JSlider.HORIZONTAL)
            trackRect?.setSize(c.preferredSize.width - maxThickness, maxThickness)
        else
            trackRect?.setSize(maxThickness, c.preferredSize.height - maxThickness)

        thumbRect?.setSize(sliderForegroundLineThickness, sliderForegroundLineThickness)
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