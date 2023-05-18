package auxiliary

import java.security.MessageDigest

object Encrypt {
    fun sha256(input: String) = hashString("SHA-256", input)
    fun md5(input: String) = hashString("MD5", input)

    private fun hashString(type: String, input: String): String {
        val bytes = MessageDigest
            .getInstance(type)
            .digest(input.toByteArray())
        return bytes.toHex()
    }

    fun ByteArray.toHex(): String {
        return joinToString("") { "%02X".format(it) }
    }
}