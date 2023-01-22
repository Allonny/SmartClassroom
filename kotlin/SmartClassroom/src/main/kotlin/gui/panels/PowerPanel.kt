package gui.panels

import auxiliary.Labels
import javax.swing.JFrame

class PowerPanel(private val mainFrame: JFrame): BasePanel(mainFrame) {
    override val titleText : String = Labels[Labels.POWER_MENU].description
}