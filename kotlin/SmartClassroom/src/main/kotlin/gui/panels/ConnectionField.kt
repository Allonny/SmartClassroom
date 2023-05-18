package gui.panels

import auxiliary.Fonts
import auxiliary.Labels
import auxiliary.Palette
import gui.GUIConstants
import gui.materialSwing.MaterialButton
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.JLabel
import javax.swing.JPanel

class ConnectionField(private val context: Context): FieldPanel() {
    override val titleText : String = Labels[Labels.PORT_CONNECT].title
    override val titleIcon: JLabel = JLabel(GUIConstants.settingsIcons[Labels.PORT_CONNECT])

    override fun setContent() {
        super.setContent()

        if (isExpanded) {
            val contentPanel = JPanel(GridBagLayout())
            contentPanel.background = Palette.BACKGROUND_ALT

            val constraints = GridBagConstraints()
            constraints.fill = GridBagConstraints.HORIZONTAL
            constraints.insets = GUIConstants.settingsFieldInsets

            val title = JLabel()
            val status = context.serialManager!!.portName
            title.foreground = Palette.FOREGROUND
            title.font = Fonts.REGULAR_ALT
            title.font = title.font.deriveFont(20f)
            title.preferredSize = Dimension(0, GUIConstants.settingButtonSize.height)

            constraints.gridx = 0
            setSize(title, constraints, null)
            contentPanel.add(title, constraints)

            val reconnectButton = MaterialButton(Labels[Labels.RECONNECT].title)
            reconnectButton.cornerRadius = GUIConstants.buttonCornerRadius
            reconnectButton.backingColor = contentPanel.background
            reconnectButton.foregroundColor = Palette.FOREGROUND
            reconnectButton.disableBackgroundColor = Palette.DISABLE
            reconnectButton.disableForegroundColor = Palette.FOREGROUND_ALT

            if (context.serialManager!!.scanningPorts) {
                title.text = Labels[Labels.RECONNECT].other[Labels.SEARCH] as String
                reconnectButton.isEnabled = false
            } else {
                if (status == null) {
                    title.text = Labels[Labels.RECONNECT].other[Labels.NOT_FOUND] as String
                    reconnectButton.backgroundColor = Palette.ACCENT_HIGH
                } else {
                    title.text = Labels[Labels.RECONNECT].other[Labels.FOUND] as String + status
                    reconnectButton.backgroundColor = Palette.ACCENT_NORMAL
                }
            }

            reconnectButton.font = Fonts.REGULAR.deriveFont(20f)

            reconnectButton.addActionListener {
                context.serialManager!!.scan()
                setContent()
                //setSettingField(panel.parent)
            }

            constraints.gridx = 1
            setSize(reconnectButton, constraints, Dimension(250, GUIConstants.settingButtonSize.height))
            contentPanel.add(reconnectButton, constraints)

            add(contentPanel, BorderLayout.CENTER)
        }
    }
}