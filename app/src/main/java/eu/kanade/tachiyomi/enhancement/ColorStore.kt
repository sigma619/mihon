package eu.kanade.tachiyomi.enhancement

import android.content.Context
import java.io.File
import java.security.MessageDigest

/**
 * Persistent on-device store for colorized pages.
 * Saves to <app-external-files>/colorized/<hash>.png so colored pages
 * survive app restarts (the demo branch only kept them in memory).
 */
class ColorStore(context: Context) {
    private val dir = File(context.getExternalFilesDir(null), "colorized").apply { mkdirs() }

    private fun keyFor(original: ByteArray, flags: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        md.update(flags.toByteArray())
        md.update(original)
        return md.digest().joinToString("") { "%02x".format(it) }
    }

    fun get(original: ByteArray, flags: String): ByteArray? {
        val f = File(dir, keyFor(original, flags) + ".png")
        return if (f.exists()) f.readBytes() else null
    }

    fun put(original: ByteArray, flags: String, colored: ByteArray) {
        File(dir, keyFor(original, flags) + ".png").outputStream().use { it.write(colored) }
    }
}
