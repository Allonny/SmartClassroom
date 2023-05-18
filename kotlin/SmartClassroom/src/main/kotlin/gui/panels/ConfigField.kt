package gui.panels

import auxiliary.Fonts
import auxiliary.Labels
import auxiliary.Palette
import gui.GUIConstants
import gui.materialSwing.MaterialButton
import gui.materialSwing.MaterialTextArea
import gui.materialSwing.RoundedBorder
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.GridLayout
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*

class ConfigField(private val context: Context): FieldPanel() {
    override val titleText : String = Labels[Labels.SERIAL_LOG].title

    override fun setContent() {
        super.setContent()

        if(isExpanded) {
            val terminalView = JPanel()
            var logScroll = JScrollPane()
            val serialLogTextField = JTextField("", 1)
            val serialLogSendButton = MaterialButton(Labels[Labels.SEND].title)

            val lightPanel = JPanel(GridLayout())
            val windowPanel = JPanel(GridLayout())
            val powerPanel = JPanel(GridLayout())

            fun setLogPanel()  {
                logScroll.isVisible = false
                logScroll.removeAll()
                var logPanel = JPanel(BorderLayout())
                logScroll = JScrollPane(logPanel)

                logScroll.border = BorderFactory.createEmptyBorder()
                logScroll.background = Palette.BACKGROUND

                logPanel.background = logScroll.background

                context.serialManager!!.log.forEach {
                    if (it.second.isEmpty()) return@forEach
                    val linePanel = JPanel(GridBagLayout())
                    linePanel.background = Palette.BACKGROUND
                    val lineConstraints = GridBagConstraints()
                    lineConstraints.fill = GridBagConstraints.HORIZONTAL

                    lineConstraints.insets = GUIConstants.settingTerminalRecordInsets
                    lineConstraints.weightx = 0.0
                    lineConstraints.gridwidth = 1
                    lineConstraints.gridy = 0
                    val gridx = if (it.first == SerialManager.KEYWORD_INPUT) 0 else 1
                    lineConstraints.gridx = gridx
                    val color = if (it.first == SerialManager.KEYWORD_INPUT) Palette.ACCENT_LOW else Palette.DISABLE

                    val message = JPanel(BorderLayout())
                    message.background = logPanel.background
                    message.border = RoundedBorder(20, color)

                    val text = MaterialTextArea(it.second.replace(",", ",\n"))
                    text.background = color
                    text.foreground = Palette.FOREGROUND_ALT
                    text.border = BorderFactory.createEmptyBorder()
                    text.isEditable = false
                    text.lineWrap = true
                    text.columns = 40
                    text.font = Fonts.MONO.deriveFont(20f)

                    text.addMouseListener(object : MouseAdapter() {
                        override fun mouseReleased(e: MouseEvent?) {
                            serialLogTextField.text = text.text.replace("\n", "")
                        }
                    })

                    setSize(message, lineConstraints, message.minimumSize, changeableHeight = true)
                    message.add(text, BorderLayout.CENTER)
                    linePanel.add(message, lineConstraints)

                    lineConstraints.weightx = 1.0
                    lineConstraints.gridx = 1 - gridx

                    val space = JLabel()
                    setSize(space, lineConstraints, null)
                    linePanel.add(space, lineConstraints)

                    val newLogPanel = JPanel(BorderLayout())
                    newLogPanel.background = Palette.BACKGROUND
                    newLogPanel.add(linePanel, BorderLayout.NORTH)
                    logPanel.add(newLogPanel, BorderLayout.SOUTH)
                    logPanel = newLogPanel
                }

                logScroll.verticalScrollBar.unitIncrement = GUIConstants.scrollSpeed
                logScroll.verticalScrollBar.value = logScroll.verticalScrollBar.maximum

                logScroll.isVisible = true
            }

            fun sendData() {
                if (serialLogTextField.text.isEmpty()) return
                context.serialManager!!.send(serialLogTextField.text.trim())
                serialLogTextField.text = ""
            }

            val contentPanel = JPanel(GridBagLayout())
            contentPanel.background = Palette.BACKGROUND_ALT

            val constraints = GridBagConstraints()
            constraints.fill = GridBagConstraints.BOTH

            constraints.insets = GUIConstants.settingsFieldInsets

            constraints.gridx = 0
            constraints.gridy = 0
            constraints.gridwidth = 2
            constraints.weightx = 0.0

            terminalView.layout = BorderLayout()
            terminalView.background = Palette.BACKGROUND_ALT
            terminalView.border = RoundedBorder(20, Palette.BACKGROUND, Palette.ACCENT_NORMAL, 3)

            setLogPanel()

            terminalView.add(logScroll, BorderLayout.CENTER)
            terminalView.minimumSize = Dimension(0, 0)
            terminalView.maximumSize = terminalView.minimumSize
            terminalView.size = terminalView.maximumSize
            terminalView.preferredSize = terminalView.size

            setSize(terminalView, constraints, GUIConstants.settingTerminalSize, changeableWidth = true)
            contentPanel.add(terminalView, constraints)

            constraints.gridx = 0
            constraints.gridy = 1
            constraints.gridwidth = 1
            constraints.weightx = 1.0

            val backing = JPanel(BorderLayout())
            backing.background = Palette.BACKGROUND_ALT
            backing.border = RoundedBorder(20, Palette.BACKGROUND, Palette.ACCENT_NORMAL, 3)

            serialLogTextField.background = Palette.BACKGROUND
            serialLogTextField.foreground = Palette.FOREGROUND_ALT
            serialLogTextField.border = BorderFactory.createEmptyBorder()
            serialLogTextField.font = Fonts.MONO.deriveFont(20f)

            serialLogTextField.addKeyListener(object : KeyAdapter() {
                override fun keyReleased(e: KeyEvent?) {
                    if (e!!.keyCode == KeyEvent.VK_ENTER) sendData()
                    serialLogSendButton.isEnabled = serialLogTextField.text.isNotEmpty()
                }
            })

            backing.add(serialLogTextField, BorderLayout.CENTER)
            setSize(backing, constraints, null)
            contentPanel.add(backing, constraints)

            serialLogSendButton.cornerRadius = GUIConstants.buttonCornerRadius
            serialLogSendButton.backingColor = Palette.BACKGROUND_ALT
            serialLogSendButton.backgroundColor = Palette.ACCENT_NORMAL
            serialLogSendButton.disableBackgroundColor = Palette.DISABLE
            serialLogSendButton.foregroundColor = Palette.FOREGROUND
            serialLogSendButton.disableForegroundColor = Palette.FOREGROUND_ALT
            serialLogSendButton.font = Fonts.REGULAR.deriveFont(20f)

            serialLogSendButton.isEnabled = serialLogTextField.text.isNotEmpty()
            serialLogSendButton.addActionListener {
                sendData()
                serialLogSendButton.isEnabled = serialLogTextField.text.isNotEmpty()
            }

            constraints.gridx = 1
            setSize(serialLogSendButton, constraints, Dimension(250, GUIConstants.settingButtonSize.height))
            contentPanel.add(serialLogSendButton, constraints)
            add(contentPanel, BorderLayout.CENTER)
        }
    }
}