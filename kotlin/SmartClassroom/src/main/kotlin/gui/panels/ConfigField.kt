package gui.panels

import SerialManager
import auxiliary.Fonts
import auxiliary.Labels
import auxiliary.Palette
import gui.materialSwing.MaterialButton
import java.awt.BorderLayout
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.GridLayout
import javax.swing.*

class ConfigField(private val context: Context): FieldPanel() {
    override val titleText : String = Labels[Labels.CONFIG].title
    override val titleIcon: JLabel = JLabel(GUIConstants.settingsIcons[Labels.CONFIG])

    private val pins: Map<Int, ArrayList<Int>> = mapOf<Int, ArrayList<Int>>(
        SerialManager.NONE_ID to arrayListOf(),
        SerialManager.LIGHT_ID to arrayListOf(),
        SerialManager.WINDOW_ID to arrayListOf(),
        SerialManager.POWER_ID to arrayListOf()).withDefault { arrayListOf() }
    private var reset = true

    private val contentPanel = Box(BoxLayout.Y_AXIS)
    private val lightPanel = JPanel()
    private val windowPanel = JPanel()
    private val powerPanel = JPanel()
    private val buttonsPanel = JPanel(GridLayout(1, 2, GUIConstants.menuButtonsInsets.top, GUIConstants.menuButtonsInsets.left))

    override fun update() {
        reset = true
        super.update()
    }

    override fun setContent() {
        super.setContent()

        contentPanel.removeAll()
        buttonsPanel.removeAll()

        if(isExpanded) {
            if (reset) {
                reset = false
                println(context.serialManager!!.pinmap)
                pins.forEach { it.value.clear() }
                context.serialManager!!.pinmap.forEachIndexed { index, pin ->
                    if(pin.type and SerialManager.ALLOW_PIN == SerialManager.ALLOW_PIN) {
                        pins[pin.id and SerialManager.ID_MASK]!!.add(index)
                        pins.forEach { it.value.sort() }
                    }
                }
            }

            println(pins)

            fun setPanel(panel: JPanel, id: Int) {
                panel.removeAll()
                panel.background = Palette.BACKGROUND_ALT

                val constraints = GridBagConstraints()
                constraints.fill = GridBagConstraints.HORIZONTAL

                val layout = GridBagLayout()
                panel.layout = layout

                pins[id]!!.forEachIndexed { index, pinNumber ->
                    val pinLabel = JLabel("${Labels[Labels.CONFIG].other[Labels.PIN] as String} $pinNumber")
                    pinLabel.font = Fonts.REGULAR_ALT.deriveFont(20f)
                    pinLabel.alignmentX = 1.0f

                    constraints.weightx = 1.0
                    constraints.gridwidth = 1
                    constraints.gridx = 0
                    constraints.gridy = index
                    constraints.insets = GUIConstants.menuButtonsInsets

                    panel.add(pinLabel, constraints)

                    val pinType = JLabel()
                    val type = context.serialManager!!.pinmap[pinNumber].type
                    if(type and SerialManager.PWM_PIN == SerialManager.PWM_PIN) pinType.text = Labels[Labels.CONFIG].other[Labels.PWM] as String
                    else if(type and SerialManager.ADC_PIN == SerialManager.ADC_PIN) pinType.text = Labels[Labels.CONFIG].other[Labels.ADC] as String
                    else pinType.text = Labels[Labels.CONFIG].other[Labels.DIGITAL] as String
                    pinType.font = Fonts.REGULAR_ALT.deriveFont(20f)
                    pinType.alignmentX = -1.0f

                    constraints.weightx = 1.0
                    constraints.gridwidth = 1
                    constraints.gridx = 1
                    constraints.gridy = index
                    constraints.insets = GUIConstants.menuButtonsInsets

                    panel.add(pinType, constraints)

                    val deleteButton = MaterialButton(Labels[Labels.CONFIG].other[Labels.DELETE] as String)
                    deleteButton.cornerRadius = GUIConstants.buttonCornerRadius
                    deleteButton.backingColor = Palette.BACKGROUND_ALT
                    deleteButton.backgroundColor = Palette.ACCENT_HIGH
                    deleteButton.foregroundColor = Palette.FOREGROUND
                    deleteButton.disableBackgroundColor = Palette.DISABLE
                    deleteButton.disableForegroundColor = Palette.FOREGROUND_ALT
                    deleteButton.font = Fonts.REGULAR.deriveFont(20f)

                    deleteButton.addActionListener {
                        println(id)
                        println(pinNumber)
                        pins[id]!!.remove(pinNumber)
                        pins[SerialManager.NONE_ID]!!.add(pinNumber)
                        pins[SerialManager.NONE_ID]!!.sort()
                        setContent()
                        super.update()
                    }

                    constraints.weightx = 1.0
                    constraints.gridwidth = 1
                    constraints.gridx = 2
                    constraints.gridy = index
                    constraints.insets = GUIConstants.menuButtonsInsets

                    panel.add(deleteButton, constraints)
                }

                constraints.weightx = 1.0
                constraints.gridwidth = 1
                constraints.gridx = 0
                constraints.gridy = pins[id]!!.size
                constraints.insets = GUIConstants.menuButtonsInsets

                panel.add(JLabel(), constraints)

                val newPinArray = arrayOf(Labels[Labels.CONFIG].other[Labels.ADD], *(pins[0]!!.map { v -> v.toString() }.toTypedArray()))
                val newPinCombo = JComboBox(newPinArray)
                newPinCombo.selectedIndex = 0
                newPinCombo.font = Fonts.MONO.deriveFont(20f)

                newPinCombo.addActionListener {
                    if (newPinCombo.selectedIndex == 0) return@addActionListener

                    pins[SerialManager.NONE_ID]!!.remove(newPinCombo.selectedItem!!.toString().toInt())
                    pins[id]!!.add(newPinCombo.selectedItem!!.toString().toInt())
                    pins[id]!!.sort()
                    setContent()
                    super.update()
                }

                constraints.weightx = 1.0
                constraints.gridwidth = 3
                constraints.gridx = 1
                constraints.gridy = pins[id]!!.size
                constraints.insets = GUIConstants.menuButtonsInsets

                if(pins[id]!!.size < SerialManager.GROUP_MAX_COUNT) panel.add(newPinCombo, constraints)
            }

            val lightTitle = JLabel(Labels[Labels.CONFIG].other[Labels.LIGHT_GROUP] as String)
            lightTitle.font = Fonts.SUBTITLE.deriveFont(30f)

            val windowTitle = JLabel(Labels[Labels.CONFIG].other[Labels.WINDOW_GROUP] as String)
            windowTitle.font = Fonts.SUBTITLE.deriveFont(30f)

            val powerTitle = JLabel(Labels[Labels.CONFIG].other[Labels.POWER_SUPPLY_GROUP] as String)
            powerTitle.font = Fonts.SUBTITLE.deriveFont(30f)

            setPanel(lightPanel, SerialManager.LIGHT_ID)
            setPanel(windowPanel, SerialManager.WINDOW_ID)
            setPanel(powerPanel, SerialManager.POWER_ID)

            val resetButton = MaterialButton(Labels[Labels.CONFIG].other[Labels.RESET] as String)
            resetButton.cornerRadius = GUIConstants.buttonCornerRadius
            resetButton.backingColor = Palette.BACKGROUND_ALT
            resetButton.backgroundColor = Palette.ACCENT_HIGH
            resetButton.foregroundColor = Palette.FOREGROUND
            resetButton.disableBackgroundColor = Palette.DISABLE
            resetButton.disableForegroundColor = Palette.FOREGROUND_ALT
            resetButton.font = Fonts.REGULAR.deriveFont(20f)
            resetButton.addActionListener{
                reset = true
                update()
            }

            buttonsPanel.add(resetButton)

            val acceptButton = MaterialButton(Labels[Labels.CONFIG].other[Labels.APPLY] as String)
            acceptButton.cornerRadius = GUIConstants.buttonCornerRadius
            acceptButton.backingColor = Palette.BACKGROUND_ALT
            acceptButton.backgroundColor = Palette.ACCENT_NORMAL
            acceptButton.foregroundColor = Palette.FOREGROUND
            acceptButton.disableBackgroundColor = Palette.DISABLE
            acceptButton.disableForegroundColor = Palette.FOREGROUND_ALT
            acceptButton.font = Fonts.REGULAR.deriveFont(20f)
            acceptButton.addActionListener {

                var configLine = ""
                for (pin in context.serialManager!!.pinmap.indices) {
                    configLine += when (pin) {
                        in pins[SerialManager.NONE_ID]!! -> "%02X".format(SerialManager.NONE_ID)
                        in pins[SerialManager.LIGHT_ID]!! -> "%02X".format(SerialManager.LIGHT_ID)
                        in pins[SerialManager.WINDOW_ID]!! -> "%02X".format(SerialManager.WINDOW_ID)
                        in pins[SerialManager.POWER_ID]!! -> "%02X".format(SerialManager.POWER_ID)
                        else -> "00"
                    }
                }

                context.serialManager!!.send(SerialManager.LABEL_CONFIG to configLine)
            }

            buttonsPanel.add(acceptButton)

            contentPanel.add(lightTitle)
            contentPanel.add(lightPanel)
            contentPanel.add(windowTitle)
            contentPanel.add(windowPanel)
            contentPanel.add(powerTitle)
            contentPanel.add(powerPanel)
            contentPanel.add(buttonsPanel)

            add(contentPanel, BorderLayout.CENTER)
        }
    }
}