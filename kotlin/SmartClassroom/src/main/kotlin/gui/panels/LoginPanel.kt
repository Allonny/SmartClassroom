package gui.panels

import auxiliary.Fonts
import auxiliary.Labels
import auxiliary.Palette
import gui.GUIConstants
import gui.materialSwing.MaterialButton
import java.awt.BorderLayout
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.lang.Exception
import javax.swing.BorderFactory
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JScrollPane

class LoginPanel(private val mainFrame: JFrame): BasePanel(mainFrame) {
    override val titleText : String = Labels[Labels.LOGIN].description

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
            constraints.gridy = counter
            constraints.weightx = 1.0
            constraints.gridwidth = 1
            for (j in arrayOf(0, 2)) {
                val space = JLabel()
                setSize(space, constraints, null)
                constraints.gridx = j
                buttonPanel.add(space, constraints)
            }

            constraints.weightx = 0.0
            constraints.gridwidth = 1
            constraints.gridx = 1
            constraints.gridy = counter
            counter++
            constraints.insets = GUIConstants.loginButtonsInsets

            val button = MaterialButton(Labels[it].title)
            button.cornerRadius = GUIConstants.buttonCornerRadius
            button.backingColor = Palette.BACKGROUND
            button.backgroundColor = Palette.ACCENT_NORMAL
            button.foregroundColor = Palette.FOREGROUND
            button.font = Fonts.REGULAR.deriveFont(20f)

            button.addActionListener { _ -> this.listener.action(it) }
            setSize(button, constraints, GUIConstants.loginButtonsSize)
            buttonPanel.add(button, constraints)
        }

        buttonScroll.verticalScrollBar.unitIncrement = GUIConstants.scrollSpeed
        layout.setConstraints(buttonScroll, constraints)
        add(buttonScroll, BorderLayout.CENTER)
    }
}