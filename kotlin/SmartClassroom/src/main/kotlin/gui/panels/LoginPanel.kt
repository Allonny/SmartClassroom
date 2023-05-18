package gui.panels

import auxiliary.Fonts
import auxiliary.Labels
import auxiliary.Palette
import gui.materialSwing.MaterialButton
import gui.materialSwing.RoundedBorder
import java.awt.*
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import javax.swing.*

class LoginPanel(private val context: Context): BasePanel(context) {
    override val titleText : String = Labels[Labels.LOGIN].description

    private val loginTextField = JTextField("", 1)
    private val passwordTextField = JPasswordField("", 1)
    private val loginButton = MaterialButton(Labels[Labels.LOGIN].titleAlt)

    public val login: String get() = loginTextField.text
    public val password: String get() = passwordTextField.text

    override fun setContent() {
        super.setContent()

        val buttonPanel = JPanel()
        val buttonScroll = JScrollPane(buttonPanel)
        val constraints = GridBagConstraints()
        constraints.fill = GridBagConstraints.BOTH
        buttonScroll.border = BorderFactory.createMatteBorder(0, 0, 0, 0, Palette.BACKGROUND)
        buttonScroll.background = Palette.BACKGROUND
        buttonPanel.background = Palette.BACKGROUND

        val loginLabel = JLabel("Логин")
        val passwordLabel = JLabel("Пароль")
        val labels = arrayOf(loginLabel, passwordLabel)

        val textFields = arrayOf(loginTextField, passwordTextField)

        val layout = GridBagLayout()
        buttonPanel.layout = layout

        var counter = 0
        for (counter in 0..1) {
            constraints.gridy = counter
            constraints.weightx = 1.0
            constraints.gridwidth = 1
            for (j in arrayOf(0, 7)) {
                val space = JLabel()
                setSize(space, constraints, null)
                constraints.gridx = j
                buttonPanel.add(space, constraints)
            }

            constraints.weightx = 0.0
            constraints.gridwidth = 1
            constraints.gridx = 1
            constraints.gridy = counter
            constraints.insets = GUIConstants.loginButtonsInsets

            labels[counter].font = Fonts.REGULAR.deriveFont(20f)
            buttonPanel.add(labels[counter], constraints)

            constraints.weightx = 5.0
            constraints.gridwidth = 5
            constraints.gridx = 2
            constraints.gridy = counter
            constraints.insets = GUIConstants.loginButtonsInsets

            val backing = JPanel(BorderLayout())
            backing.background = Palette.BACKGROUND
            backing.border = RoundedBorder(20, Palette.BACKGROUND, Palette.ACCENT_NORMAL, 3)

            textFields[counter]
            textFields[counter].background = Palette.BACKGROUND
            textFields[counter].foreground = Palette.FOREGROUND_ALT
            textFields[counter].border = BorderFactory.createEmptyBorder()
            textFields[counter].font = Fonts.MONO.deriveFont(20f)

            textFields[counter].addKeyListener(object : KeyAdapter() {
                override fun keyReleased(e: KeyEvent?) {
                    if ((e!!.keyCode == KeyEvent.VK_ENTER)) enter()
                    loginButton.isEnabled = login.isNotEmpty() and password.isNotEmpty()
                }
            })

            backing.add(textFields[counter], BorderLayout.CENTER)
            buttonPanel.add(backing, constraints)
        }

        constraints.gridy = 2
        constraints.weightx = 1.0
        constraints.gridwidth = 1
        for (j in arrayOf(0, 7)) {
            val space = JLabel()
            setSize(space, constraints, null)
            constraints.gridx = j
            buttonPanel.add(space, constraints)
        }

        constraints.weightx = 5.0
        constraints.gridwidth = 6
        constraints.gridx = 1
        constraints.gridy = 2
        constraints.insets = GUIConstants.loginButtonsInsets

        loginButton.cornerRadius = GUIConstants.buttonCornerRadius
        loginButton.backingColor = Palette.BACKGROUND
        loginButton.backgroundColor = Palette.ACCENT_NORMAL
        loginButton.disableBackgroundColor = Palette.DISABLE
        loginButton.foregroundColor = Palette.FOREGROUND
        loginButton.disableForegroundColor = Palette.FOREGROUND_ALT
        loginButton.font = Fonts.REGULAR.deriveFont(20f)
        loginButton.isEnabled = login.isNotEmpty() and password.isNotEmpty()

        loginButton.addActionListener {
            enter()
        }

        setSize(loginButton, constraints, GUIConstants.loginButtonsSize)
        buttonPanel.add(loginButton, constraints)

        buttonScroll.verticalScrollBar.unitIncrement = GUIConstants.scrollSpeed
        layout.setConstraints(buttonScroll, constraints)
        add(buttonScroll, BorderLayout.CENTER)
    }

    private fun enter()
    {
        if (login.isEmpty() or password.isEmpty()) return
        this.listener.action(Labels.ENTER)
        loginTextField.text = ""
        passwordTextField.text = ""
        loginButton.isEnabled = false
    }
}