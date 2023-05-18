package gui.panels

import auxiliary.Fonts
import auxiliary.Labels
import auxiliary.Palette
import gui.GUIConstants
import gui.materialSwing.MaterialButton
import java.awt.BorderLayout
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.*

class MenuPanel(private val context: Context): BasePanel(context) {
    override val titleText: String = Labels[Labels.MENU].description
    override fun setContent() {
        super.setContent()

        val buttonPanel = JPanel()
        val buttonScroll = JScrollPane(buttonPanel)
        val constraints = GridBagConstraints()
        constraints.fill = GridBagConstraints.BOTH
        buttonScroll.border = BorderFactory.createMatteBorder(0, 0, 0, 0, Palette.BACKGROUND)
        buttonScroll.background = Palette.BACKGROUND
        buttonPanel.background = Palette.BACKGROUND

        val layout = GridBagLayout()
        buttonPanel.layout = layout

        var counter = 0
        actionContent.forEach {
            constraints.insets = GUIConstants.menuButtonsInsets
            constraints.weightx = 0.0
            constraints.gridwidth = 1
            constraints.gridx = 1 + counter % context.buttonsInLine
            constraints.gridy = counter / context.buttonsInLine
            counter++

            val button = menuButton(it as String)
            setSize(button, constraints, GUIConstants.menuButtonsSize)
            buttonPanel.add(button, constraints)
        }

        for (i in 0..(actionContent.count() - 1) / 2) {
            constraints.gridy = i
            constraints.weightx = 1.0
            constraints.gridwidth = 1
            for (j in arrayOf(0, context.buttonsInLine + 1)) {
                val space = JLabel()
                setSize(space, constraints, null)
                constraints.gridx = j
                buttonPanel.add(space, constraints)
            }
        }
        add(buttonPanel, BorderLayout.CENTER)
    }

    private fun menuButton(name: String): JButton {
        var icon: ImageIcon? = null
        when (name) {
            Labels.LIGHT -> icon = GUIConstants.menuButtonIcons[Labels.LIGHT]
            Labels.WINDOW -> icon = GUIConstants.menuButtonIcons[Labels.WINDOW]
            Labels.POWER_SUPPLY -> icon = GUIConstants.menuButtonIcons[Labels.POWER_SUPPLY]
            Labels.ADD_USER -> icon = GUIConstants.menuButtonIcons[Labels.ADD_USER]
        }

        val button = MaterialButton(Labels[name].title, icon)
        button.cornerRadius = GUIConstants.buttonCornerRadius
        button.backingColor = Palette.BACKGROUND
        button.backgroundColor = Palette.ACCENT_NORMAL
        button.foregroundColor = Palette.FOREGROUND
        button.iconPosition = MaterialButton.ICON_LEFT
        button.font = Fonts.REGULAR.deriveFont(30f)

        button.addActionListener { this.listener.action(name) }
        return button
    }
}