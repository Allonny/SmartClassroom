package auxiliary

class Labels {
    companion object {
        const val NAME = "name"
        const val TITLE = "title"
        const val ROOT = "root"
        const val WELCOME = "welcome"
        const val SETTINGS = "settings"
        const val POWER_MENU = "power_menu"
        const val LOGIN = "login"
        const val USER = "user"
        const val BASIC = "basic"
        const val EXTENDED = "extended"
        const val ADMIN = "admin"
        const val FULL = "full"
        const val MENU = "menu"
        const val LIGHT = "light"
        const val WINDOW = "window"
        const val POWER_SUPPLY = "power_supply"
        const val ADD_USER = "addUser"
        const val SUPER_USER = "superuser"
        const val ENTER = "enter"
        const val SERIAL_PORT = "serial_port"
        const val BACK = "back"
        const val MESSAGE = "message"
        const val EXPAND = "expand"
        const val COLLAPSE = "collapse"
        const val UPDATE = "update"
        const val SET = "setting_in_list"
        const val CONTENT = SET + "_content"
        const val PORT_CONNECT = SET + "_port_connect"
        const val SERIAL_LOG = SET + "_serial_log"
        const val CONFIG = SET + "_config"
        const val SETTING_LABEL = SET + "_label"
        const val SETTING_BUTTON = SET + "_button"
        const val SETTING_TEXTFIELD = SET + "_textfield"
        const val RECONNECT = "reconnect"
        const val SEND = "send"
        const val RESET = "reset"
        const val APPLY = "apply"
        const val ADD = "add"
        const val DELETE = "delete"
        const val PWM = "pwm"
        const val ADC = "adc"
        const val DIGITAL = "digital"
        const val PIN = "pin"
        const val ABOUT = "about"
        const val LINK = "link"
        const val TEXT = "text"
        const val SEARCH = "search"
        const val FOUND = "found"
        const val NOT_FOUND = "not_found"
        const val OFF = "_off"
        const val ON = "_on"
        const val SWITCH = "switch"
        const val SWITCH_OFF = SWITCH + OFF
        const val SWITCH_ON = SWITCH + ON
        const val LIGHT_OFF = LIGHT + OFF
        const val LIGHT_ON = LIGHT + ON
        const val WINDOW_OFF = WINDOW + OFF
        const val WINDOW_ON = WINDOW + ON
        const val POWER_SUPPLY_OFF = POWER_SUPPLY + OFF
        const val POWER_SUPPLY_ON = POWER_SUPPLY + ON
        const val COUNT = "_count"
        const val GROUP = "_group"
        const val LIGHT_GROUP = LIGHT + GROUP
        const val WINDOW_GROUP = WINDOW + GROUP
        const val POWER_SUPPLY_GROUP = POWER_SUPPLY + GROUP

        private val fields : Map<String, TextField> = mapOf(
            NAME to TextField("SmartLab"),
            TITLE to TextField(),
            ROOT to TextField("Стартовая панель", "Стартовая панель", "Добро пожаловать"),
            WELCOME to TextField("Стартовая панель", "Стартовая панель", "Добро пожаловать"),
            SETTINGS to TextField("Настройки", "Настройки", "Настройки"),
            POWER_MENU to TextField("Питание", "Питание", "Управление питанием"),
            LOGIN to TextField(
                "Вход при помощи логина и пароля",
                "Вход в систему",
                "Введите, пожалуйста, ваши учётные данные"
            ),
            BASIC to TextField("Войти как студент", "Меню", "Что Вы желаете сделать?"),
            EXTENDED to TextField("Войти как преподаватель", "Меню", "Что Вы желаете сделать?"),
            ADMIN to TextField("Войти как администратор", "Меню", "Что Вы желаете сделать?"),
            MENU to TextField("Меню", "Меню", "Что Вы желаете сделать?"),
            LIGHT to TextField("Управление\nосвещением", "Освещение", "Да будет свет!"),
            WINDOW to TextField(
                "Управление\nпроветриванием",
                "Проветривание",
                "Свежый воздух - прекрасная подпидка для мозга"
            ),
            POWER_SUPPLY to TextField(
                "Управление\nпитанием\nоборудования",
                "Питание оборудования",
                "Нечего энтропию за зря увеличивать"
            ),
            ADD_USER to TextField(
                "Добавление\nпользователя",
                "Добавление пользователя",
                "Укажите данные нового пользователя"
            ),
            BACK to TextField("Назад"),
            MESSAGE to TextField(
                other = mapOf(
                    1 to "Приложите,",
                    2 to "пожалуйста,",
                    3 to "свою карточку",
                    4 to "к сканнеру (=^ω^=)"
                )
            ),
            EXPAND to TextField("Раскрыть"),
            COLLAPSE to TextField("Скрыть"),
            RECONNECT to TextField(
                "Переподключить",
                other = mapOf(
                    SEARCH to "Происходит поиск устройств",
                    NOT_FOUND to "Устройство не подключено!",
                    FOUND to "Подключено к порту: "
                )
            ),
            PORT_CONNECT to TextField("Порт подключения Arduino:", other = mapOf(SETTING_BUTTON to "Найти устройство")),
            SERIAL_LOG to TextField("История команд", other = mapOf(SETTING_TEXTFIELD to "Введите команду")),
            SEND to TextField("Отправить"),
            CONFIG to TextField("Настроить функциональные группы", other = mapOf(
                LIGHT_GROUP to "Контроль освещения",
                WINDOW_GROUP to "Проветривание",
                POWER_SUPPLY_GROUP to "Питание оборудования",
                APPLY to "Принять",
                RESET to "Сброисть",
                ADD to "Добавить вывод",
                DELETE to "Удалить",
                PWM to "ШИМ",
                ADC to "Аналоговый",
                DIGITAL to "Цифровой",
                PIN to "Выход"
            )),
            ABOUT to TextField("О программе", other = mapOf(
                TEXT + 1 to "Данная программа является результатом дипломного проекта, все исходные коды которого (если я не забуду их выложить, разумеется) можно найти в данном репозитории:",
                LINK to "https://github.com/Allonny/SmartClassroom",
                TEXT + 2 to "Надеюсь, я не заброшу проект после получения диплома и начала работы...",
                TEXT + 3 to "Во всяком случае надеюсь, с вами всё хорошо, а главное, не забывайте, что вы самое невероятное чудо, вот!",
                TEXT + 4 to "Ваша ~Allonny~"
            )),
            SWITCH to TextField(other = mapOf(SWITCH_ON to "Включить", SWITCH_OFF to "Выключить")),
            LIGHT_GROUP to TextField(other = mapOf(LIGHT_ON to "Включить", LIGHT_OFF to "Выключить")),
            WINDOW_GROUP to TextField(other = mapOf(WINDOW_ON to "Открыть", WINDOW_OFF to "Закрыть")),
            POWER_SUPPLY_GROUP to TextField(other = mapOf(POWER_SUPPLY_ON to "Включить", POWER_SUPPLY_OFF to "Выключить")),
            GROUP to TextField("Группа №")
        ).withDefault { TextField() }

        operator fun get (key: String): TextField = fields.getValue(key)
    }
}