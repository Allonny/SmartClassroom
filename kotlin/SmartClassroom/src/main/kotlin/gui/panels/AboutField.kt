package gui.panels

import auxiliary.Fonts
import auxiliary.Labels
import java.awt.BorderLayout
import java.awt.Desktop
import java.awt.GridLayout
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.net.URI
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.JLabel
import javax.swing.JPanel

class AboutField(private val context: Context): FieldPanel() {
    override val titleText : String = Labels[Labels.ABOUT].title
    override val titleIcon: JLabel = JLabel(GUIConstants.settingsIcons[Labels.ABOUT])

    override fun setContent() {
        super.setContent()

        if (isExpanded) {
            maximumSize = GUIConstants.settingAboutSize

            val label1 = JLabel("<html>${Labels[Labels.ABOUT].other[Labels.TEXT + 1] as String}</html>")
            label1.font = Fonts.REGULAR_ALT.deriveFont(20f)

            val link = JLabel("<html>${Labels[Labels.ABOUT].other[Labels.LINK] as String}</html>")
            link.font = Fonts.MONO.deriveFont(20f)

            link.addMouseListener(object : MouseAdapter() {
                override fun mouseClicked(e: MouseEvent?) {
                    try {
                        Desktop.getDesktop().browse(URI(Labels[Labels.ABOUT].other[Labels.LINK] as String))
                    } catch (e: Exception) {
                        Runtime.getRuntime().exec(arrayOf<String>("xdg-open", Labels[Labels.ABOUT].other[Labels.LINK] as String))
                    }
                }

                override fun mouseExited(e: MouseEvent?) {
                    link.text = "<html>${Labels[Labels.ABOUT].other[Labels.LINK] as String}</html>"
                    link.font = Fonts.MONO.deriveFont(20f)
                }

                override fun mouseEntered(e: MouseEvent?) {
                    link.text = "<html><u>${Labels[Labels.ABOUT].other[Labels.LINK] as String}</u></html>"
                    link.font = Fonts.MONO.deriveFont(20f)
                }
            })

            val label2 = JLabel("<html>${Labels[Labels.ABOUT].other[Labels.TEXT + 2] as String}</html>")
            label2.font = Fonts.REGULAR_ALT.deriveFont(20f)

            val label3 = JLabel("<html>${Labels[Labels.ABOUT].other[Labels.TEXT + 3] as String}</html>")
            label3.font = Fonts.REGULAR_ALT.deriveFont(20f)

            val label4 = JLabel("<html>${Labels[Labels.ABOUT].other[Labels.TEXT + 4] as String}</html>")
            label4.font = Fonts.REGULAR_ALT.deriveFont(20f)

            val text = Box(BoxLayout.Y_AXIS)
            text.maximumSize = GUIConstants.settingAboutSize
            text.size = text.maximumSize
            text.preferredSize = text.size

            text.add(label1)
            text.add(link)
            text.add(Box.createVerticalStrut(20))
            text.add(label2)
            text.add(Box.createVerticalStrut(20))
            text.add(label3)
            text.add(Box.createVerticalStrut(20))
            text.add(label4)

            add(text, BorderLayout.CENTER)
            alignmentX = CENTER_ALIGNMENT
        }
    }
}