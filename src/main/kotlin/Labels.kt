class Labels {
    companion object {
        const val NAME = "name"
        const val TITLE = "title"
        const val ROOT = "root"
        const val WELCOME = "welcome"
        const val SETTINGS = "settings"
        const val POWER_MENU = "power_menu"
        const val LOGIN = "login"
        const val BASIC = "basic"
        const val EXTENDED = "extended"
        const val ADMIN = "admin"
        const val MENU = "menu"
        const val LIGHT = "light"
        const val WINDOW = "window"
        const val POWER_SUPPLY = "power_supply"
        const val ADD_USER = "addUser"
        const val SERIAL_PORT = "serial_port"
        const val BACK = "back"
        const val MESSAGE = "message"
        const val EXPAND = "expand"
        const val COLLAPSE = "collapse"
        const val SET = "setting_in_list"
        const val CONTENT = SET + "_content"
        const val PORT_CONNECT = SET + "_port_connect"
        const val SERIAL_LOG = SET + "_serial_log"
        const val SETTING_LABEL = SET + "_label"
        const val SETTING_BUTTON = SET + "_button"
        const val SETTING_TEXTFIELD = SET + "_textfield"

        private val fields : Map<String, TextField> = mapOf(
            NAME to TextField("SmartLab"),
            TITLE to TextField(),
            ROOT to TextField("Стартовая панель", "Стартовая панель", "Добро пожаловать"),
            WELCOME to TextField("Стартовая панель", "Стартовая панель", "Добро пожаловать"),
            SETTINGS to TextField("Настройки", "Настройки", "Настройки"),
            POWER_MENU to TextField("Питание", "Питание", "Управление питанием"),
            LOGIN to TextField("Вход при помощи логина и пароля", "Вход в систему", "Введите, пожалуйста, ваши учётные данные"),
            BASIC to TextField("Войти как студент", "Меню", "Что Вы желаете сделать?"),
            EXTENDED to TextField("Войти как преподаватель", "Меню", "Что Вы желаете сделать?"),
            ADMIN to TextField("Войти как администратор", "Меню", "Что Вы желаете сделать?"),
            MENU to TextField("Меню", "Меню", "Что Вы желаете сделать?"),
            LIGHT to TextField("Управление\nосвещением", "Освещение", "Да будет свет!"),
            WINDOW to TextField("Управление\nпроветриванием", "Проветривание", "Свежый воздух - прекрасная подпидка для мозга"),
            POWER_SUPPLY to TextField("Управление\nпитанием\nоборудования", "Питание оборудования", "Нечего энтропию за зря увеличивать"),
            ADD_USER to TextField("Добавление\nпользователя", "Добавление пользователя", "Укажите данные нового пользователя"),
            BACK to TextField("Назад"),
            MESSAGE to TextField(other = mapOf(1 to "Приложите,", 2 to "пожалуйста,", 3 to "свою карточку", 4 to "к сканнеру (=^ω^=)")),
            PORT_CONNECT to TextField("Порт подключения Arduino:", other = mapOf(SETTING_BUTTON to "Найти устройство")),
            SERIAL_LOG to TextField("История команд", other = mapOf(SETTING_TEXTFIELD to "Введите команду"))
        ).withDefault { TextField() }

        operator fun get (key: String): TextField = fields.getValue(key)
    }
}