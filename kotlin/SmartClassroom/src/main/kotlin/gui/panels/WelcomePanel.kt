package gui.panels

import auxiliary.Fonts
import auxiliary.Labels
import auxiliary.Palette
import gui.GUIConstants
import gui.materialSwing.InscribedLabel
import gui.materialSwing.MaterialButton
import java.awt.*
import javax.swing.*

class WelcomePanel(private val context: Context): BasePanel(context) {
    override val titleText : String = Labels[Labels.WELCOME].description

    override fun setContent() {
        super.setContent()

        val text = Box(BoxLayout.Y_AXIS)
        Labels[Labels.MESSAGE].other.forEach {
            val minHeight = context.mainFrame!!.height - GUIConstants.topPanelSize.height -
                    (GUIConstants.loginButtonsSize.height + GUIConstants.loginButtonsInsets.top + GUIConstants.loginButtonsInsets.bottom)
            val minWidth = context.mainFrame!!.width - GUIConstants.sideFieldsWidth
            val maxSize = Integer.min(minWidth, minHeight)
            val line = InscribedLabel(it.value.toString(), Fonts.TITLE_ALT, maxSize, maxSize)
            line.foreground = Palette.DISABLE
            text.add(line)
        }
        add(text, BorderLayout.EAST)

        val buttonPanel = JPanel()
        val constraints = GridBagConstraints()
        constraints.fill = GridBagConstraints.HORIZONTAL
        buttonPanel.background = Palette.BACKGROUND

        val layout = GridBagLayout()
        buttonPanel.layout = layout

        constraints.weightx = 1.0
        constraints.gridwidth = 1
        for (j in arrayOf(0, 2)) {
            val space = JLabel()
            constraints.gridx = j
            buttonPanel.add(space, constraints)
        }

        val loginButton = MaterialButton(Labels[Labels.LOGIN].title, GUIConstants.loginButtonIcon[Labels.LOGIN])
        loginButton.cornerRadius = GUIConstants.buttonCornerRadius
        loginButton.backingColor = Palette.BACKGROUND
        loginButton.backgroundColor = Palette.ACCENT_LOW
        loginButton.foregroundColor = Palette.FOREGROUND_ALT
        loginButton.font = Fonts.REGULAR.deriveFont(20f)
        loginButton.iconPosition = MaterialButton.ICON_LEFT

        loginButton.addActionListener { this.listener.action(Labels.LOGIN) }

        constraints.ipadx = GUIConstants.loginButtonsSize.width - loginButton.minimumSize.width
        constraints.ipady = GUIConstants.loginButtonsSize.height - loginButton.minimumSize.height
        constraints.weightx = 0.0
        constraints.gridwidth = 1
        constraints.gridx = 1
        constraints.insets = GUIConstants.loginButtonsInsets
        buttonPanel.add(loginButton, constraints)

        layout.setConstraints(buttonPanel, constraints)
        add(buttonPanel, BorderLayout.SOUTH)
    }
}