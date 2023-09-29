package auxiliary

import java.awt.Image
import javax.swing.ImageIcon

class Icons(size: Int, vararg iconsName: Pair<String, String>) {
    object Foreground {
        private const val DIRECTORY = """resources/images/png/foreground"""

        const val USER_ADD =        "$DIRECTORY/user-add.png"
        const val USER_CONTROL =    "$DIRECTORY/user-control.png"
        const val BACK =            "$DIRECTORY/back.png"
        const val LIGHT_OFF =       "$DIRECTORY/light-off.png"
        const val LIGHT_ON =        "$DIRECTORY/light-on.png"
        const val WINDOW_OPEN =     "$DIRECTORY/window-open.png"
        const val WINDOW_CLOSE =    "$DIRECTORY/window-close.png"
        const val TOGGLE_OFF =      "$DIRECTORY/toggle-on.png"
        const val TOGGLE_ON =       "$DIRECTORY/toggle-off.png"
        const val LOGIN =           "$DIRECTORY/login.png"
        const val POWER_MENU =      "$DIRECTORY/power-menu.png"
        const val POWER_SUPPLY =    "$DIRECTORY/power-supply.png"
        const val SETTINGS =        "$DIRECTORY/settings.png"
        const val WINDOW =          "$DIRECTORY/window.png"
        const val EXPAND =          "$DIRECTORY/expand.png"
        const val COLLAPSE =        "$DIRECTORY/collapse.png"
        const val SERIAL =          "$DIRECTORY/serial.png"
        const val CONSOLE =         "$DIRECTORY/terminal.png"
        const val CONFIG =          "$DIRECTORY/config.png"
        const val INFO =            "$DIRECTORY/info.png"
        const val DOT =             "$DIRECTORY/slider-dot.png"
        const val CIRCLE =          "$DIRECTORY/slider-circle.png"
    }

    object ForegroundAlt {
        private const val DIRECTORY = """resources/images/png/foreground-alt"""

        const val USER_ADD =        "$DIRECTORY/user-add.png"
        const val USER_CONTROL =    "$DIRECTORY/user-control.png"
        const val BACK =            "$DIRECTORY/back.png"
        const val LIGHT_OFF =       "$DIRECTORY/light-off.png"
        const val LIGHT_ON =        "$DIRECTORY/light-on.png"
        const val WINDOW_OPEN =     "$DIRECTORY/window-open.png"
        const val WINDOW_CLOSE =    "$DIRECTORY/window-close.png"
        const val TOGGLE_OFF =      "$DIRECTORY/toggle-on.png"
        const val TOGGLE_ON =       "$DIRECTORY/toggle-off.png"
        const val LOGIN =           "$DIRECTORY/login.png"
        const val POWER_MENU =      "$DIRECTORY/power-menu.png"
        const val POWER_SUPPLY =    "$DIRECTORY/power-supply.png"
        const val SETTINGS =        "$DIRECTORY/settings.png"
        const val WINDOW =          "$DIRECTORY/window.png"
        const val EXPAND =          "$DIRECTORY/expand.png"
        const val COLLAPSE =        "$DIRECTORY/collapse.png"
        const val SERIAL =          "$DIRECTORY/serial.png"
        const val CONSOLE =         "$DIRECTORY/terminal.png"
        const val CONFIG =          "$DIRECTORY/config.png"
        const val INFO =            "$DIRECTORY/info.png"
        const val DOT =             "$DIRECTORY/slider-dot.png"
        const val CIRCLE =          "$DIRECTORY/slider-circle.png"
    }

    companion object {
        const val LOGO = """resources/images/png/logo.png"""
    }

    private val icons: MutableMap<String, ImageIcon> = mutableMapOf<String, ImageIcon>().withDefault { ImageIcon() }

    init {
        iconsName.forEach {
            try {
                icons[it.first] =
                    ImageIcon(ImageIcon(it.second).image.getScaledInstance(size / 2, size / 2, Image.SCALE_SMOOTH))
            } finally {
            }
        }
    }

    operator fun get(name: String) = icons[name]
}
