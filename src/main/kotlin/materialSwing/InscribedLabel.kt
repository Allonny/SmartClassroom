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

    var maxIteration: Int = 0
        set(value) {
            field = value
            inscription()
        }

    var divideStep: Float = 250f
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
        var fontSize = 1f
        var iteration = 0
        do {
            this.font = this.font.deriveFont(fontSize)
            fontSize += (inscriptionWidth - maximumSize.width) / divideStep
        } while ((inscriptionWidth - maximumSize.width) > 10 && (inscriptionHeight - maximumSize.height) > 10 && (iteration++ < maxIteration || maxIteration <= 0))
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