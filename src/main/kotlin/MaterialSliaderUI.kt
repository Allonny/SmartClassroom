import java.awt.*
import java.awt.geom.Ellipse2D
import javax.swing.JComponent
import javax.swing.JSlider
import javax.swing.SwingConstants
import javax.swing.event.ChangeEvent
import javax.swing.event.ChangeListener
import javax.swing.plaf.ComponentUI
import javax.swing.plaf.basic.BasicSliderUI
import kotlin.math.min
import kotlin.math.roundToInt


class MaterialSliaderUI : BasicSliderUI {
    private val rangeColor: Color = Color.BLUE
    private val stroke = BasicStroke(2f)

    var backSliderThickness : Int = 5
    var faceSliderThickness : Int = 40

    @Transient
    private val upperDragging = false

//    fun createUI(c: JComponent?): ComponentUI? {
//        return MaterialSliaderUI(c as JSlider?)
//    }


    override fun calculateThumbSize() {
        super.calculateThumbSize()
        thumbRect.setSize(thumbRect.width, thumbRect.height)
    }

    /** Creates a listener to handle track events in the specified slider. */
    override fun createTrackListener(slider: JSlider?): TrackListener? {
        return super.createTrackListener(slider) //RangeTrackListener()
    }


    override fun calculateThumbLocation() {
        // Call superclass method for lower thumb location.
        super.calculateThumbLocation()

        // Adjust upper value to snap to ticks if necessary.
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
                // If it's not on a tick, change the value
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

        // Calculate upper thumb location.  The thumb is centered over its
        // value on the track.
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

    /** Returns the size of a thumb.
     * Parent method not use size from LaF
     * @return size of trumb
     */
    override fun getThumbSize(): Dimension? {
        return super.getThumbSize()
//        return org.gradle.language.nativeplatform.internal.Dimensions.getSliderThumbSize()
    }


    private fun createThumbShape(radius: Double): Shape {
        return Ellipse2D.Double(-(radius / 2.0), -(radius / 2.0), radius, radius)
    }

    override fun paintTrack(g: Graphics) {
        val g2d = g as Graphics2D
        g2d.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON
        )
        val old = g2d.stroke
        g2d.stroke = stroke
        g2d.color = Color.GRAY
        val oldColor: Color = Color.GRAY
        val trackBounds = trackRect
        if (slider.orientation == SwingConstants.HORIZONTAL) {
            faceSliderThickness = min(this.slider.preferredSize.height, this.slider.preferredSize.width)
            backSliderThickness = faceSliderThickness / 4
//            this.slider.preferredSize = Dimension(trackRect.width + 2 * max(backSliderThickness, faceSliderThickness), 2 * max(backSliderThickness, faceSliderThickness))
            //this.slider. = Insets(max(backSliderThickness, faceSliderThickness), max(backSliderThickness, faceSliderThickness), max(backSliderThickness, faceSliderThickness), max(backSliderThickness, faceSliderThickness))
//            trackRect.setSize(this.slider.preferredSize.width - 2 * max(backSliderThickness, faceSliderThickness), 2 * max(backSliderThickness, faceSliderThickness))

            val trackHalfHeight = trackRect.y + trackRect.height / 2

            g2d.fillRoundRect(trackRect.x, trackHalfHeight - backSliderThickness / 2, trackRect.width, backSliderThickness, backSliderThickness, backSliderThickness)
//            g2d.drawLine(
//                trackRect.x, trackRect.y + trackRect.height / 2,
//                trackRect.x + trackRect.width, trackRect.y + trackRect.height / 2
//            )

//            val lowerX = thumbRect.width / 2
//            val upperX = thumbRect.x + thumbRect.width / 2
//            val cy = trackBounds.height / 2 - 2
//            g2d.translate(trackBounds.x, trackBounds.y + cy)
            g2d.color = rangeColor
            g2d.fillRoundRect((thumbRect.minX - thumbRect.width / 2).toInt(), 0, (thumbRect.getX() + thumbRect.width / 2).toInt(), faceSliderThickness, faceSliderThickness, faceSliderThickness)
//            g2d.fillRoundRect(, trackHalfHeight - faceSliderThickness, , faceSliderThickness, faceSliderThickness, faceSliderThickness)
//            g2d.drawLine(lowerX - trackBounds.x, 2, upperX - trackBounds.x, 2)
//            g2d.translate(-trackBounds.x, -(trackBounds.y + cy))
            g2d.color = oldColor
        }
        g2d.stroke = old
    }


    /** Overrides superclass method to do nothing.  Thumb painting is handled
     * within the `paint()` method. */
    override fun paintThumb(g: Graphics) {
        val knobBounds = thumbRect
//        val w = knobBounds.width
//        val h = knobBounds.height
        val g2d = g.create() as Graphics2D
        thumbRect.setSize(faceSliderThickness, faceSliderThickness)
        val thumbShape: Shape = createThumbShape(faceSliderThickness.toDouble())
        g2d.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON
        )
        g2d.translate(thumbRect.x + faceSliderThickness / 4, faceSliderThickness / 2)
        g2d.color = Color.CYAN
        g2d.fill(thumbShape)
//        g2d.translate(knobBounds.x, knobBounds.y)
//        g2d.color = Color.WHITE
//        g2d.fill(thumbShape)
//        g2d.color = Color.BLUE
//        g2d.draw(thumbShape)
        g2d.dispose()
    }

    /** Listener to handle model change events.  This calculates the thumb
     * locations and repaints the slider if the value change is not caused by dragging a thumb. */
//    class ChangeHandler : ChangeListener {
//        override fun stateChanged(arg0: ChangeEvent) {
//            calculateThumbLocation()
//            slider.repaint()
//        }
//    }
    constructor()

    constructor(c: JSlider?) {
        super.slider = c

    }
    init {

    }
}