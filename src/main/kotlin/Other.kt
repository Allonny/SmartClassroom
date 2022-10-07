import java.awt.*
import java.io.File
import javax.swing.border.Border

class TreeNode<T> (val name: String, val value: T) {
    var parent: TreeNode<T> = this
    private val nodes: MutableMap<String, TreeNode<T>> = mutableMapOf<String, TreeNode<T>>().withDefault { parent }

    val count: Int get() = nodes.size
    val names: Set<String> = nodes.keys
    val children: Collection<TreeNode<T>> = nodes.values

    fun addChild(node: TreeNode<T>) {
        nodes[node.name] = node
        node.parent = this
    }

    fun addChild(name: String, value: T) = addChild(TreeNode(name, value))

    fun addChild(pair: Pair<String, T>) = addChild(pair.first, pair.second)

    fun addChildren(nodes: Map<String, T>) = nodes.forEach { (name, value) -> addChild(name, value) }

    fun addChildren(vararg nodes: Pair<String, T>) = addChildren(nodes.toMap())

    operator fun plusAssign(node: TreeNode<T>) = addChild(node)

    operator fun plusAssign(nodes: Map<String, T>) = nodes.forEach{ addChild(it.toPair()) }

    operator fun contains(node: TreeNode<T>): Boolean = node in nodes.values

    fun clearChildren() = nodes.clear()

    fun removeChild(name: String) = nodes.remove(name)

    operator fun get(name: String): TreeNode<T> = nodes.getValue(name)

    operator fun set(name: String, node: TreeNode<T>?) {
        nodes.remove(name)
        if (node != null) {
            nodes[name] = node
            node.parent = this
        }
    }

    operator fun set(name: String, value: T) = set(name, TreeNode(name, value))

    inline fun forEach(it: (TreeNode<T>) -> Unit) = children.forEach { node -> it(node) }

    override fun toString(): String {
        var output = name
        if (count != 0)
        {
            output += " -> "
            val temp = arrayListOf<String>()
            forEach { temp.add(it.toString()) }
            output += "[ ${temp.joinToString(", ")} ]"
        }
        return output
    }
}

class RoundedBorder (private val radius: Int, private val background: Color) : Border {
    override fun getBorderInsets(c: Component?): Insets = Insets(radius / 5, radius / 5, radius / 5, radius / 5)

    override fun isBorderOpaque(): Boolean = true

    override fun paintBorder(c: Component?, g: Graphics, x: Int, y: Int, width: Int, height: Int) {
        g.color = background
        g.fillRoundRect(x, y, width - 1, height - 1, radius, radius)
        g.drawRoundRect(x, y, width - 1, height - 1, radius, radius)

    }
}

class Palette () {
    companion object {
        val BACKGROUND =        Color(0xF2F1F6)
        val BACKGROUND_ALT =    Color(0xDEE9E7)
        val FOREGROUND =        Color(0x3F3F3F)
        val FOREGROUND_ALT =    Color(0x62717E)
        val ACCENT_LOW =        Color(0xE3F2F7)
        val ACCENT_NORMAL =     Color(0xB5CBCC)
        val ACCENT_HIGH =       Color(0xFFCAAF)
        val DISABLE =           Color(0xD1D1CF)
        val TRANSPARENT =       Color(0x00000000, true)
    }
    // Палитра - https://www.pinterest.com/pin/801781539899322636/
}

class TextField (
    val title: String = "",
    val titleAlt: String = "",
    val description: String = "",
    val descriptionAlt: String = "",
    val other: Map<String, String> = mapOf()
)

class Labels {
    companion object {
        val TITLE = "title"
        val ROOT = "root"
        val WELCOME = "welcome"
        val SETTINGS = "settings"
        val POWER_MENU = "powerMenu"
        val LOGIN = "login"
        val BASIC = "basic"
        val EXTENDED = "extended"
        val ADMIN = "admin"
        val MENU = "menu"
        val LIGHT = "light"
        val WINDOW = "window"
        val POWER_SUPPLY = "powerSupply"
        val ADD_USER = "addUser"
        val SERIAL_PORT = "serialPort"

        val fields : Map<String, TextField> = mapOf(
            TITLE to TextField("SmartLab"),
            ROOT to TextField("Стартовая панель", "Стартовая панель", "Добро пожаловать"),
            WELCOME to TextField("Стартовая панель", "Стартовая панель", "Добро пожаловать"),
            SETTINGS to TextField("Настройки", "Настройки", "Настройки"),
            POWER_MENU to TextField("Питание", "Питание", "Управление питанием"),
            LOGIN to TextField("Вход через логин и пароль", "Вход в систему", "Введите, пожалуйста, ваши учётные данные"),
            BASIC to TextField("Войти как студент", "Меню", "Что Вы желаете сделать?"),
            EXTENDED to TextField("Войти как преподаватель", "Меню", "Что Вы желаете сделать?"),
            ADMIN to TextField("Войти как администратор", "Меню", "Что Вы желаете сделать?"),
            MENU to TextField("Меню", "Меню", "Что Вы желаете сделать?"),
            LIGHT to TextField("Управление освещением", "Освещение"),
            WINDOW to TextField("Управление проветриванием", "Проветривание"),
            POWER_SUPPLY to TextField("Управление питанием оборудования", "Питание оборудования"),
            ADD_USER to TextField("Добавление пользователя", "Добавление пользователя", "Укажите данные нового пользователя"),
        ).withDefault { TextField() }

        operator fun get (key: String): TextField = fields.getValue(key)
    }
}

class Fonts {
    companion object {
        val TITLE_FONT = Font.createFont(Font.TRUETYPE_FONT, File("""resources/fonts/Comfortaa/static/Comfortaa-Bold.ttf"""))
        val REGULAR_FONT = Font.createFont(Font.TRUETYPE_FONT, File("""resources/fonts/Montserrat/static/Montserrat-Medium.ttf"""))
    }
}