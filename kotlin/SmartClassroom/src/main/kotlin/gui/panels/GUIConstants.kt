package gui.panels

import auxiliary.Icons
import auxiliary.Labels
import java.awt.Dimension
import java.awt.Insets

object GUIConstants {
    val topPanelSize = Dimension(0, 95)
    val topPanelButtonsSize = Dimension(75, 75)
    val topPanelButtonsInsets = Insets((topPanelSize.height - topPanelButtonsSize.height) / 2, 10, (topPanelSize.height - topPanelButtonsSize.height) / 2, 10)
    val loginButtonsSize = Dimension(450, 50)
    val loginButtonsInsets = Insets(10, 10, 10, 10)
    val menuButtonsSize = Dimension(400, 125)
    val menuButtonsInsets = Insets(10, 10, 10, 10)
    val menuFieldSize = Dimension(400, 400)
    val menuSwitchButtonSize = Dimension(150, 150)
    val menuSwitchButtonInsets = Insets(25, 10, 25, 10)
    val settingsFieldSize = Dimension(800, 100)
    val settingsFieldInsets = Insets(10, 10, 10, 10)
    val settingButtonSize = Dimension(50, 50)
    val settingLabelSize = Dimension(600, 50)
    val settingTerminalSize = Dimension(800, 500)
    val settingAboutSize = Dimension(100, 300)
    val settingTerminalRecordInsets = Insets(10, 10, 10, 10)
    val fieldSliderSize = Dimension(300, 50)
    const val buttonCornerRadius = 50
    const val scrollSpeed = 10
    const val sideFieldsWidth = 150
    const val maxTitleHeight = 100

    val topPanelIcons = Icons(
        Integer.min(topPanelButtonsSize.width, topPanelButtonsSize.height),
        Labels.POWER_MENU to Icons.Foreground.POWER_MENU,
        Labels.BACK to Icons.Foreground.BACK,
        Labels.SETTINGS to Icons.Foreground.SETTINGS,
        "${Labels.SETTINGS}_alt" to Icons.ForegroundAlt.SETTINGS
    )

    val menuButtonIcons = Icons(
        Integer.min(menuButtonsSize.width, menuButtonsSize.height),
        Labels.LIGHT to Icons.Foreground.LIGHT_ON,
        Labels.WINDOW to Icons.Foreground.WINDOW,
        Labels.POWER_SUPPLY to Icons.Foreground.POWER_SUPPLY,
        Labels.USER_CONTROL to Icons.Foreground.USER_CONTROL
    )

    val loginButtonIcon =
        Icons(Integer.min(loginButtonsSize.width, loginButtonsSize.height), Labels.LOGIN to Icons.ForegroundAlt.LOGIN)

    val settingsIcons = Icons(
        settingButtonSize.width,
        Labels.EXPAND to Icons.Foreground.EXPAND,
        Labels.COLLAPSE to Icons.Foreground.COLLAPSE,
        Labels.PORT_CONNECT to Icons.ForegroundAlt.SERIAL,
        Labels.SERIAL_LOG to Icons.ForegroundAlt.CONSOLE,
        Labels.CONFIG to Icons.ForegroundAlt.CONFIG,
        Labels.ABOUT to Icons.ForegroundAlt.INFO
    )

    val menuLightIcons = Icons(
        menuSwitchButtonSize.width,
        Labels.LIGHT_ON to Icons.Foreground.LIGHT_ON,
        Labels.LIGHT_OFF to Icons.Foreground.LIGHT_OFF,
        Labels.SLIDER to Icons.ForegroundAlt.CIRCLE
    )

    val menuWindowIcons = Icons(
        menuSwitchButtonSize.width,
        Labels.WINDOW_ON to Icons.Foreground.WINDOW_OPEN,
        Labels.WINDOW_OFF to Icons.Foreground.WINDOW_CLOSE
    )

    val menuPowerSupplyIcons = Icons(
        menuSwitchButtonSize.width,
        Labels.POWER_SUPPLY_ON to Icons.Foreground.TOGGLE_ON,
        Labels.POWER_SUPPLY_OFF to Icons.Foreground.TOGGLE_OFF
    )

    val menusOptions: MutableMap<String, Array<String>> = mutableMapOf(
        Labels.BASIC to arrayOf(Labels.LIGHT, Labels.WINDOW),
        Labels.EXTENDED to arrayOf(Labels.LIGHT, Labels.WINDOW, Labels.POWER_SUPPLY),
        Labels.ADMIN to arrayOf(Labels.LIGHT, Labels.WINDOW, Labels.POWER_SUPPLY, Labels.USER_CONTROL),
        Labels.FULL to arrayOf(Labels.LIGHT, Labels.WINDOW, Labels.POWER_SUPPLY, Labels.USER_CONTROL)
    ).withDefault { arrayOf() }
}