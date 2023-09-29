package gui.panels

import auxiliary.Labels

class UserControlPanel(private val context: Context): BasePanel(context) {
    override val titleText : String = Labels[Labels.USER_CONTROL].description

}