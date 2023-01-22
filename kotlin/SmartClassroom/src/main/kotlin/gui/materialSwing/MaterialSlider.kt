package gui.materialSwing

import java.awt.Color
import javax.swing.BoundedRangeModel
import javax.swing.ImageIcon
import javax.swing.JSlider

class MaterialSlider(brm: BoundedRangeModel) : JSlider() {
    val foregroundThickness: Int get() = materialUI.foregroundThickness
    val backgroundThickness: Int get() = materialUI.backgroundThickness
    val thumbThickness: Int get() = materialUI.thumbThickness
    val thumbLength: Int get() = materialUI.thumbLength

    var foregroundRelativeThickness: Double
        get() = materialUI.foregroundRelativeThickness
        set(value) { materialUI.foregroundRelativeThickness = value }

    var backgroundRelativeThickness: Double
        get() = materialUI.backgroundRelativeThickness
        set(value) { materialUI.backgroundRelativeThickness = value }

    var thumbRelativeThickness: Double
        get() = materialUI.thumbRelativeThickness
        set(value) { materialUI.thumbRelativeThickness = value }

    var thumbRelativeLength: Double
        get() = materialUI.thumbRelativeLength
        set(value) { materialUI.thumbRelativeLength= value }

    var sliderForegroundLineColor: Color
        get() = materialUI.sliderForegroundLineColor
        set(value) { materialUI.sliderForegroundLineColor = value }

    var sliderBackgroundLineColor: Color
        get() = materialUI.sliderBackgroundLineColor
        set(value) { materialUI.sliderBackgroundLineColor = value }

    var thumbColor: Color
        get() = materialUI.thumbColor
        set(value) { materialUI.thumbColor = value }

    var thumbIcon: ImageIcon?
        get() = materialUI.thumbIcon
        set(value) { materialUI.thumbIcon = value }

    var isForegroundPaint: Boolean
        get() = materialUI.isForegroundPaint
        set(value) { materialUI.isForegroundPaint = value }

    var isBackgroundPaint: Boolean
        get() = materialUI.isBackgroundPaint
        set(value) { materialUI.isBackgroundPaint = value }

    var isThumbPaint: Boolean
        get() = materialUI.isThumbPaint
        set(value) { materialUI.isThumbPaint = value }

    private val materialUI: MaterialSliderUI = MaterialSliderUI(this)

    init {
        super.setModel(brm)
        super.setUI(materialUI)
    }
}

