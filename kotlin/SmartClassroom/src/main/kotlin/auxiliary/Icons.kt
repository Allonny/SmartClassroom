package auxiliary

import java.awt.Image
import javax.swing.ImageIcon

class Icons(size: Int, vararg iconsName: Pair<String, String>) {
    object Foreground {
        const val ADD_USER = """resources/images/png/foreground/add-user.png"""
        const val BACK = """resources/images/png/foreground/back.png"""
        const val LIGHT_OFF = """resources/images/png/foreground/light-off.png"""
        const val LIGHT_ON = """resources/images/png/foreground/light-on.png"""
        const val WINDOW_OPEN = """resources/images/png/foreground/window-open.png"""
        const val WINDOW_CLOSE = """resources/images/png/foreground/window-close.png"""
        const val TOGGLE_OFF = """resources/images/png/foreground/toggle-on.png"""
        const val TOGGLE_ON = """resources/images/png/foreground/toggle-off.png"""
        const val LOGIN = """resources/images/png/foreground/login.png"""
        const val POWER_MENU = """resources/images/png/foreground/power-menu.png"""
        const val POWER_SUPPLY = """resources/images/png/foreground/power-supply.png"""
        const val SETTINGS = """resources/images/png/foreground/settings.png"""
        const val WINDOW = """resources/images/png/foreground/window.png"""
        const val EXPAND = """resources/images/png/foreground/expand.png"""
        const val COLLAPSE = """resources/images/png/foreground/collapse.png"""
        const val SERIAL = """resources/images/png/foreground/serial.png"""
        const val CONSOLE = """resources/images/png/foreground/terminal.png"""
    }

    object ForegroundAlt {
        const val ADD_USER = """resources/images/png/foreground_alt/add_user.png"""
        const val BACK = """resources/images/png/foreground_alt/back.png"""
        const val LIGHT_OFF = """resources/images/png/foreground_alt/light-off.png"""
        const val LIGHT_ON = """resources/images/png/foreground_alt/light-on.png"""
        const val WINDOW_OPEN = """resources/images/png/foreground_alt/window-open.png"""
        const val WINDOW_CLOSE = """resources/images/png/foreground_alt/window-close.png"""
        const val TOGGLE_OFF = """resources/images/png/foreground_alt/toggle-on.png"""
        const val TOGGLE_ON = """resources/images/png/foreground_alt/toggle-off.png"""
        const val LOGIN = """resources/images/png/foreground_alt/login.png"""
        const val POWER_MENU = """resources/images/png/foreground_alt/power-menu.png"""
        const val POWER_SUPPLY = """resources/images/png/foreground_alt/power-supply.png"""
        const val SETTINGS = """resources/images/png/foreground_alt/settings.png"""
        const val WINDOW = """resources/images/png/foreground_alt/window.png"""
        const val EXPAND = """resources/images/png/foreground_alt/expand.png"""
        const val COLLAPSE = """resources/images/png/foreground_alt/collapse.png"""
        const val SERIAL = """resources/images/png/foreground_alt/serial.png"""
        const val CONSOLE = """resources/images/png/foreground_alt/terminal.png"""
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
