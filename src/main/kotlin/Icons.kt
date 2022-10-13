import java.awt.Image
import javax.swing.ImageIcon

class Icons(size: Int, vararg iconsName: Pair<String, String>) {
    class Foreground {
        companion object {
            const val ADD_USER = """resources/images/png/foreground/add_user.png"""
            const val BACK = """resources/images/png/foreground/back.png"""
            const val LIGHT_OFF = """resources/images/png/foreground/light_off.png"""
            const val LIGHT_ON = """resources/images/png/foreground/light_on.png"""
            const val LOGIN = """resources/images/png/foreground/login.png"""
            const val POWER_MENU = """resources/images/png/foreground/power_menu.png"""
            const val POWER_SUPPLY = """resources/images/png/foreground/power_supply.png"""
            const val SETTINGS = """resources/images/png/foreground/settings.png"""
            const val WINDOW = """resources/images/png/foreground/window.png"""
            const val EXPAND = """resources/images/png/foreground/expand.png"""
            const val COLLAPSE = """resources/images/png/foreground/collapse.png"""
            const val SERIAL = """resources/images/png/foreground/serial.png"""
            const val CONSOLE = """resources/images/png/foreground/terminal.png"""
        }
    }

    class ForegroundAlt {
        companion object {
            const val ADD_USER = """resources/images/png/foreground_alt/add_user.png"""
            const val BACK = """resources/images/png/foreground_alt/back.png"""
            const val LIGHT_OFF = """resources/images/png/foreground_alt/light_off.png"""
            const val LIGHT_ON = """resources/images/png/foreground_alt/light_on.png"""
            const val LOGIN = """resources/images/png/foreground_alt/login.png"""
            const val POWER_MENU = """resources/images/png/foreground_alt/power_menu.png"""
            const val POWER_SUPPLY = """resources/images/png/foreground_alt/power_supply.png"""
            const val SETTINGS = """resources/images/png/foreground_alt/settings.png"""
            const val WINDOW = """resources/images/png/foreground_alt/window.png"""
            const val EXPAND = """resources/images/png/foreground_alt/expand.png"""
            const val COLLAPSE = """resources/images/png/foreground_alt/collapse.png"""
            const val SERIAL = """resources/images/png/foreground_alt/serial.png"""
            const val CONSOLE = """resources/images/png/foreground_alt/terminal.png"""
        }
    }

    companion object {
        const val LOGO = """resources/images/png/logo.png"""
    }

    private val icons: MutableMap<String, ImageIcon> = mutableMapOf<String, ImageIcon>().withDefault { ImageIcon() }
    init {
        iconsName.forEach {
            try { icons[it.first] = ImageIcon(ImageIcon(it.second).image.getScaledInstance(size / 2, size / 2, Image.SCALE_SMOOTH)) } finally { }
        }
    }

    operator fun get(name: String) = icons[name]
}
