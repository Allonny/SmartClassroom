fun main() {
    System.setProperty("awt.useSystemAAFontSettings","on")
    System.setProperty("swing.aatext", "true")

    val dataBus: DataBus = DataBus()

    val dbManager: DBManager = DBManager(dataBus)

    val serialManager: SerialManager = SerialManager(dataBus)
    serialManager.serialSpeed = 19200
    serialManager.autoConnect = true

//    serialManager.addDataReceivedListener {
//        if(SerialManager.LABEL_UID in it.keys) {
//            val uid = serialManager.get(SerialManager.LABEL_UID)[SerialManager.LABEL_UID]
//            println(uid)
//            val user = dbManager.getUser(rfid = uid!!)
//            println(user)
//            if (user == null) {
//                var login: String
//                var password: String
//                var name: String
//                var privileges: Array<String>
//                var newUser: MutableDocument? = MutableDocument()
//                do {
//                    if (newUser == null) println("Логин уже знаят")
//                    print("Введите логин: ")
//                    login = readln()
//                    print("Введите пароль: ")
//                    password = readln()
//                    print("Введите имя: ")
//                    name = readln()
//                    print("Введите привилегии: ")
//                    privileges = readln().split(' ').toTypedArray()
//                    newUser = dbManager.addUser(
//                        login = login,
//                        password = password,
//                        rfid = uid,
//                        name = name,
//                        privileges = privileges)
//                } while (newUser == null)
//                println("Пользователь добавлен")
//            }
//        }
//    }


    val guiManager: GUIManager = GUIManager(dataBus)
}