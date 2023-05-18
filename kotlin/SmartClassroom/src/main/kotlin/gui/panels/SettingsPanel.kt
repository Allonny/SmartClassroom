package gui.panels

import auxiliary.Labels
import auxiliary.Palette
import gui.GUIConstants
import java.awt.BorderLayout
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.*

class SettingsPanel(private val context: Context): BasePanel(context) {
    override val titleText: String = Labels[Labels.SETTINGS].description

    val settingFields: ArrayList<FieldPanel> = arrayListOf()

    override fun setContent() {
        super.setContent()

        val settingsPanel = JPanel()
        val settingsScroll = JScrollPane(settingsPanel)
        val constraints = GridBagConstraints()
        constraints.fill = GridBagConstraints.BOTH
        settingsScroll.border = BorderFactory.createMatteBorder(0, 0, 0, 0, Palette.BACKGROUND)
        settingsScroll.background = Palette.BACKGROUND
        settingsPanel.background = Palette.BACKGROUND

        val layout = GridBagLayout()
        settingsPanel.layout = layout

        var counter = 0
        actionContent.forEach {
            constraints.gridy = counter
            constraints.weightx = 1.0
            constraints.gridwidth = 1
            for (j in arrayOf(0, 2)) {
                val space = JLabel()
                setSize(space, constraints, null)
                constraints.gridx = j
                settingsPanel.add(space, constraints)
            }
            constraints.weightx = 0.0
            constraints.gridwidth = 1
            constraints.gridx = 1
            constraints.gridy = counter
            constraints.insets = GUIConstants.settingsFieldInsets

            settingFields.add(when(it) {
                Labels.PORT_CONNECT -> ConnectionField(context)
                Labels.SERIAL_LOG -> SerialLogField(context)
                else -> FieldPanel()
            })

            val settingField = settingFields.last()

            settingField.setTitle()
            settingField.setContent()
            settingField.addActionListener { action ->
                settingField.isExpanded = when(action) {
                    Labels.COLLAPSE -> false
                    Labels.EXPAND -> true
                    else -> settingField.isExpanded
                }

//                settingField.setTitle()
//                settingField.setContent()
                settingField.update()
            }

            setSize(settingField, constraints, GUIConstants.settingsFieldSize, changeableHeight = true)
            settingsPanel.add(settingField, constraints)

            counter++
        }

        layout.setConstraints(settingsScroll, constraints)
        settingsScroll.verticalScrollBar.unitIncrement = GUIConstants.scrollSpeed
        //settingsScroll.verticalScrollBar.value = scrollPosition
        add(settingsScroll, BorderLayout.CENTER)
    }

    override fun update() {
        settingFields.forEach { settingField ->
            settingField.update()
        }

        super.update()
    }
}