package gui.panels

import auxiliary.Fonts
import auxiliary.Labels
import auxiliary.Palette
import gui.materialSwing.InscribedLabel
import gui.materialSwing.MaterialButton
import gui.materialSwing.MaterialSlider
import gui.materialSwing.RoundedBorder
import java.awt.BorderLayout
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*

class WindowPanel(private val context: Context): BasePanel(context) {
    override val titleText : String = Labels[Labels.WINDOW].description

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

        actionContent.forEachIndexed { counter, pin ->
            constraints.insets = GUIConstants.menuButtonsInsets
            constraints.weightx = 0.0
            constraints.gridwidth = 1
            constraints.gridx = 1 + counter % context.buttonsInLine
            constraints.gridy = counter / context.buttonsInLine

            val groupPanel = JPanel(GridBagLayout())
            groupPanel.background = Palette.BACKGROUND
            groupPanel.border = RoundedBorder(50, Palette.BACKGROUND_ALT)

            val groupConstraints = GridBagConstraints()
            groupConstraints.fill = GridBagConstraints.VERTICAL
            groupConstraints.insets = GUIConstants.menuSwitchButtonInsets

            val title = InscribedLabel(
                Labels[Labels.GROUP].title + (counter + 1).toString(),
                Fonts.REGULAR_ALT,
                GUIConstants.menuFieldSize.width - 100,
                GUIConstants.menuFieldSize.height - 100
            )
            title.foreground = Palette.FOREGROUND_ALT

            pin as SerialManager.Pinstate
            val type = pin.type
            val value = pin.state
            var stateBool = value != 0

            val windowSlider = MaterialSlider(DefaultBoundedRangeModel(value, 0, 0, 255))
            windowSlider.sliderBackgroundLineColor = Palette.DISABLE
            windowSlider.sliderForegroundLineMinColor = Palette.ACCENT_NORMAL
            windowSlider.sliderForegroundLineMaxColor = Palette.ACCENT_HIGH
            windowSlider.thumbMinColor = Palette.ACCENT_NORMAL
            windowSlider.thumbMaxColor = Palette.ACCENT_HIGH
            windowSlider.thumbIcon = GUIConstants.menuLightIcons[Labels.SLIDER]
            windowSlider.preferredSize = GUIConstants.fieldSliderSize
            windowSlider.background = Palette.BACKGROUND_ALT

            val switchButton = MaterialButton()

            switchButton.cornerRadius = 150
            switchButton.backingColor = Palette.BACKGROUND_ALT
            switchButton.backgroundColor = Palette.ACCENT_NORMAL
            switchButton.foregroundColor = Palette.FOREGROUND

            fun stateButton(state: Boolean) {
                if (state) {
                    switchButton.backgroundColor = Palette.ACCENT_HIGH
                    switchButton.enableFaceTitle = Labels[Labels.WINDOW_GROUP].other[Labels.WINDOW_OFF] as String
                    switchButton.enableFaceIcon = GUIConstants.menuWindowIcons[Labels.WINDOW_ON]
                } else {
                    switchButton.backgroundColor = Palette.ACCENT_NORMAL
                    switchButton.enableFaceTitle = Labels[Labels.WINDOW_GROUP].other[Labels.WINDOW_ON] as String
                    switchButton.enableFaceIcon = GUIConstants.menuWindowIcons[Labels.WINDOW_OFF]
                }
            }

            stateButton(stateBool)

            switchButton.addActionListener {
                stateBool = !stateBool
                windowSlider.value = if (stateBool) 255 else 0

                listener.action(Labels.WINDOW_GROUP + ".%02X%02X".format(counter, windowSlider.value))
            }

            windowSlider.addMouseListener(object: MouseAdapter() {
                override fun mouseReleased(e: MouseEvent?) {
                    listener.action(Labels.WINDOW_GROUP + ".%02X%02X".format(counter, windowSlider.value))
                }
            } )

            windowSlider.addChangeListener {
                stateBool = windowSlider.value != 0
                stateButton(stateBool)
            }

            groupConstraints.gridy = 0
            setSize(title, groupConstraints, null)
            groupPanel.add(title, groupConstraints)

            groupConstraints.gridy = 1
            setSize(switchButton, groupConstraints, GUIConstants.menuSwitchButtonSize)
            groupPanel.add(switchButton, groupConstraints)

//            if (type and SerialManager.PWM_PIN == SerialManager.PWM_PIN) {
//                groupConstraints.gridy = 2
//                setSize(lightSlider, groupConstraints, null)
//                groupPanel.add(lightSlider, groupConstraints)
//            }

            setSize(groupPanel, constraints, GUIConstants.menuFieldSize)
            buttonPanel.add(groupPanel, constraints)
        }

        for (i in 0..(context.currentPanel!!.count - 1) / 2) {
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

        layout.setConstraints(buttonScroll, constraints)
        add(buttonScroll, BorderLayout.CENTER)
        buttonScroll.verticalScrollBar.unitIncrement = GUIConstants.scrollSpeed
    }
}