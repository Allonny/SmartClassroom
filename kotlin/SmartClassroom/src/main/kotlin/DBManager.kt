import auxiliary.Encrypt
import com.couchbase.lite.*
import com.couchbase.lite.Collection


class DBManager(val dataBus: DataBus) {
    var database: Database? = null
    var usersCollection: Collection? = null
    var serialCollection: Collection? = null
    var settingsCollection: Collection? = null

    private val statusNone: Int = 0
    private val statusInit: Int = 1
    private val statusDBCreated: Int = 2
    private val statusCollectionCreated: Int = 3
    private val statusDBGetted: Int = 4
    private val statusCollectinGetted: Int = 5
    private var status: Int = statusNone

    init {
        dataBus.dbManager = this

        try {
            status = statusInit
            CouchbaseLite.init()
            println("База данных инициализированна.")

            status = statusDBCreated
            database = Database("Terminal")
            println("База данных создана.")

            status = statusCollectionCreated
            arrayOf("users", "serial", "settings").forEach {
                if(database?.getCollection(it) == null) {
                    database?.createCollection(it)
                    println("Коллекция $it создана.")
                }
            }

            status = statusCollectinGetted
            usersCollection = database?.getCollection("users")
            serialCollection = database?.getCollection("serial")
            settingsCollection = database?.getCollection("settings")
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
                .where(Expression.property("login").equalTo(Expression.string(login)))
                .execute().next() != null ) return null

            val userDoc = MutableDocument()
                .setString("login", login)
                .setString("password", Encrypt.sha256(password))
                .setString("rfid", Encrypt.sha256(rfid))
                .setString("name", name)
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
                SelectResult.property("password"),
                SelectResult.property("rfid"))
            .from(DataSource.collection(usersCollection!!))
            .where(Expression.property("login").equalTo(Expression.string(login)))
        val result = query.execute().next() ?: return null
        return result.getString("id")?.let { id -> usersCollection?.getDocument(id) }
//        if (password.isNotEmpty() and result.getString("password").equals(Encrypt.sha256(password)))
//
//        else return null
    }

    fun getUser(rfid: String): Document? {
        val query = QueryBuilder
            .select(
                SelectResult.expression(Meta.id),
                SelectResult.property("rfid"))
            .from(DataSource.collection(usersCollection!!))
            .where(Expression.property("rfid").equalTo(Expression.string(Encrypt.sha256(rfid))))
        val result = query.execute().next() ?: return null
        return result.getString("id")?.let { id -> usersCollection?.getDocument(id) }
    }

    fun removeUser(login: String, password: String = "", rfid: String = "") {
        if(password.isEmpty() and rfid.isEmpty()) return
        val query = QueryBuilder
            .select(
                SelectResult.expression(Meta.id),
                SelectResult.property("password"),
                SelectResult.property("rfid"))
            .from(DataSource.collection(usersCollection!!))
            .where(Expression.property("login").equalTo(Expression.string(login)))
        val result = query.execute().next() ?: return
        if (password.isNotEmpty() and result.getString("password").equals(Encrypt.sha256(password))
            or rfid.isNotEmpty() and result.getString("rfid").equals(Encrypt.sha256(rfid)))
            result.getString("id")?.let { id -> usersCollection?.getDocument(id) }?.let { doc -> usersCollection?.delete(doc) }
    }
}