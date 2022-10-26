package materialSwing

import java.awt.Font
import javax.swing.JLabel

class InscribedLabel : JLabel {
    var inscriptionWidth: Int = 100
        set(value) {
            if (value > 0) {
                field = value
                inscription()
            }
        }

    var inscriptionHeight: Int = 100
        set(value) {
            if (value > 0) {
                field = value
                inscription()
            }
        }


    override fun setFont(font: Font?) {
        super.setFont(font)
        if (this.font.name != font!!.name) inscription()
    }

    private fun inscription() {
        val widthRatio = inscriptionWidth.toFloat() / maximumSize.width.toFloat()
        val heightRatio = inscriptionHeight.toFloat() / maximumSize.height.toFloat()

        var fontSize = this.font.size2D * if (heightRatio > widthRatio) widthRatio else heightRatio
        this.font = this.font.deriveFont(fontSize)
    }

    constructor()

    constructor(text: String) {
        this.text = text
    }

    constructor(text: String, font: Font) {
        this.text = text
        this.font = font
    }

    constructor(text: String, font: Font, inscriptionWidth: Int, inscriptionHeight: Int) {
        this.text = text
        this.font = font
        this.inscriptionWidth = inscriptionWidth
        this.inscriptionHeight = inscriptionHeight
    }
}