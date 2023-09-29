package gui.panels

import auxiliary.Labels

class UserAddPanel(private val context: Context): BasePanel(context) {
    override val titleText : String = Labels[Labels.USER_ADD].description

}