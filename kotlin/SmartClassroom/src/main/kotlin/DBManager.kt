import auxiliary.Encrypt
import com.couchbase.lite.*
import com.couchbase.lite.Collection


class DBManager(val dataBus: DataBus) {
    companion object {
        const val dbLabel = "Terminal"
        const val usersLabel = "Users"
        const val serialLabel = "Serial"
        const val settingsLabel = "Settings"

        const val idLabel = "id"
        const val loginLabel = "login"
        const val passwordLabel = "password"
        const val rfidLabel = "rfid"
        const val nameLabel = "name"

        const val superuserKeyword = "superuser"

        private const val defaultSuperuserLogin = "_su_"
        private const val defaultSuperuserPassword = "superuser"

        private const val statusNone: Int = 0
        private const val statusInit: Int = 1
        private const val statusDBCreated: Int = 2
        private const val statusCollectionCreated: Int = 3
        private const val statusDBGetted: Int = 4
        private const val statusCollectinGetted: Int = 5
    }

    var database: Database? = null
    val usersCollection: Collection?
        get() = database?.getCollection(usersLabel)
    val serialCollection: Collection?
        get() = database?.getCollection(serialLabel)
    val settingsCollection: Collection?
        get() = database?.getCollection(settingsLabel)

    private var status: Int = statusNone

    init {
        dataBus.dbManager = this

        try {
            status = statusInit
            CouchbaseLite.init()
            println("База данных инициализированна.")

            status = statusDBCreated
            database = Database(dbLabel)
            println("База данных создана.")

            status = statusCollectionCreated
            arrayOf(usersLabel, serialLabel, settingsLabel).forEach {
                if(database?.getCollection(it) == null) {
                    database?.createCollection(it)
                    println("Коллекция $it создана.")
                    if(it == usersLabel) {
                        addUser(defaultSuperuserLogin, defaultSuperuserPassword, privileges = arrayOf(superuserKeyword))
                    }
                }
            }

            status = statusCollectinGetted
            println("Все коллекции получены.")

        } catch (e: CouchbaseLiteException) {
            when(status) {
                statusNone -> println("Неизвестная ошибка")
                statusInit -> println("СУБД не инициализирована")
                statusDBCreated -> println("База данных не удалось создать")
                statusCollectionCreated -> println("Колекцию не удалось создать")
                statusDBGetted -> println("Базу данных не удалось получить")
                statusCollectinGetted -> println("Коллекцию не удалось получить")
            }

        }
    }

    fun addUser(login: String, password: String, rfid: String = "", name: String = "", privileges: Array<String> = arrayOf()): MutableDocument? {
        if(usersCollection == null) return null

        try {
            if( QueryBuilder
                .select(SelectResult.expression(Meta.id))
                .from(DataSource.collection(usersCollection!!))
                .where(Expression.property(loginLabel).equalTo(Expression.string(login)))
                .execute().next() != null ) return null

            val userDoc = MutableDocument()
                .setString(loginLabel, login)
                .setString(passwordLabel, Encrypt.sha256(password))
                .setString(rfidLabel, Encrypt.sha256(rfid))
                .setString(nameLabel, name)
            privileges.forEach { userDoc.setBoolean(it, true) }
            usersCollection?.save(userDoc)
            return userDoc
        } catch (e: Exception) {
            return null
        }
    }

    fun getUser(login: String, password: String): Document? {
        val query = QueryBuilder
            .select(
                SelectResult.expression(Meta.id),
                SelectResult.property(passwordLabel),
                SelectResult.property(rfidLabel))
            .from(DataSource.collection(usersCollection!!))
            .where(Expression.property(loginLabel).equalTo(Expression.string(login)))
        val result = query.execute().next() ?: return null
        if (password.isEmpty() or ! result.getString(passwordLabel).equals(Encrypt.sha256(password))) return null
        return result.getString(idLabel)?.let { id -> usersCollection?.getDocument(id) }
    }

    fun getUser(rfid: String): Document? {
        val query = QueryBuilder
            .select(
                SelectResult.expression(Meta.id),
                SelectResult.property(rfidLabel))
            .from(DataSource.collection(usersCollection!!))
            .where(Expression.property(rfidLabel).equalTo(Expression.string(Encrypt.sha256(rfid))))
        val result = query.execute().next() ?: return null
        return result.getString(idLabel)?.let { id -> usersCollection?.getDocument(id) }
    }

    fun removeUser(login: String, password: String = "", rfid: String = "") {
        if(password.isEmpty() and rfid.isEmpty()) return
        val query = QueryBuilder
            .select(
                SelectResult.expression(Meta.id),
                SelectResult.property(passwordLabel),
                SelectResult.property(rfidLabel))
            .from(DataSource.collection(usersCollection!!))
            .where(Expression.property(loginLabel).equalTo(Expression.string(login)))
        val result = query.execute().next() ?: return
        if (password.isNotEmpty() and result.getString(passwordLabel).equals(Encrypt.sha256(password))
            or rfid.isNotEmpty() and result.getString(rfidLabel).equals(Encrypt.sha256(rfid)))
            result.getString(idLabel)?.let { id -> usersCollection?.getDocument(id) }?.let { doc -> usersCollection?.delete(doc) }
    }

    fun getAllUsers() : List<Document> {
        val userList = ArrayList<Document>()

        val query = QueryBuilder
            .select(SelectResult.expression(Meta.id))
            .from(DataSource.collection(usersCollection!!))
        val result = query.execute().allResults()
        result.forEach {
            userList.add(it.getString(idLabel)?.
                let { id -> usersCollection?.getDocument(id) }?.
                let { doc -> doc.toMutable().remove(passwordLabel).remove(rfidLabel) } as Document)
        }

        return userList.toList()
    }
}