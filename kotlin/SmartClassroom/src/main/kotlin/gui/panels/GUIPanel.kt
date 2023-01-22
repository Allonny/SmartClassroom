package gui.panels

import javax.swing.JPanel

abstract class GUIPanel: JPanel {
    abstract  fun setTitle()

    abstract fun setContent()

    interface PanelActionListener {
        var action: (String?) -> Unit
            get() = action
            set(value) { action = value }
    }

    abstract fun addActionListener(action: (String?) -> Unit)

    constructor() {}
}