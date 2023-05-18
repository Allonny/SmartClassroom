package gui.panels

import auxiliary.Labels

class PowerPanel(private val context: Context): BasePanel(context) {
    override val titleText : String = Labels[Labels.POWER_MENU].description
}