package materialSwing

import java.awt.Color
import javax.swing.BoundedRangeModel
import javax.swing.ImageIcon
import javax.swing.JSlider

class MaterialSlider(brm: BoundedRangeModel) : JSlider() {
    var backgroundToForegroundThicknessRatio: Double = 5.0
        set(value) {
            if (backgroundToForegroundThicknessRatio > 0.0) {
                field = value
                materialUI.backgroundToForegroundThicknessRatio = this.backgroundToForegroundThicknessRatio
            }
        }

    var sliderBackgroundLineColor: Color = Color.LIGHT_GRAY
        set(value) {
            field = value
            materialUI.sliderBackgroundLineColor = this.sliderBackgroundLineColor
        }

    var sliderForegroundLineColor: Color = Color.BLUE
        set(value) {
            field = value
            materialUI.sliderForegroundLineColor = this.sliderForegroundLineColor
        }

    var thumbColor: Color = Color.CYAN
        set(value) {
            field = value
            materialUI.thumbColor = this.thumbColor
        }

    var thumbIcon: ImageIcon? = null
        set(value) {
            field = value
            materialUI.thumbIcon = this.thumbIcon
        }

    var isSliderBackgroundPaint = true
        set(value) {
            field = value
            materialUI.isSliderBackgroundPaint = isSliderBackgroundPaint
        }

    var isSliderForegroundPaint = true
        set(value) {
            field = value
            materialUI.isSliderForegroundPaint = isSliderForegroundPaint
        }

    var isThumbPaint = true
        set(value) {
            field = value
            materialUI.isThumbPaint = isThumbPaint
        }

    private val materialUI: MaterialSliderUI = MaterialSliderUI(this)

    init {
        super.setModel(brm)
        super.setUI(materialUI)
    }
}

