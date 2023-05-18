package gui.materialSwing

import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.RenderingHints
import javax.swing.JTextArea
import javax.swing.text.Document

class MaterialTextArea : JTextArea {
    override fun paint(g: Graphics?) {
        val g2d = g as Graphics2D
        g2d.setRenderingHints(mapOf(
            RenderingHints.KEY_ANTIALIASING to RenderingHints.VALUE_ANTIALIAS_ON,
            RenderingHints.KEY_TEXT_ANTIALIASING to RenderingHints.VALUE_TEXT_ANTIALIAS_ON))
        super.paint(g2d)
    }
    constructor() : super()
    constructor(text: String) : super(text)
    constructor(rows: Int, columns: Int) : super(rows, columns)
    constructor(text: String, rows: Int, columns: Int) : super(text, rows, columns)
    constructor(doc: Document) : super(doc)
    constructor(doc: Document, text: String, rows: Int, columns: Int) : super(doc, text, rows, columns)

    init {}

}
