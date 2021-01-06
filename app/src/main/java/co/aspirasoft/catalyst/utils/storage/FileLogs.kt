package co.aspirasoft.catalyst.utils.storage

import java.io.File
import java.util.*

/**
 * Manages the logs of cached files.
 *
 * Provides an interface to import/export key-value pairs to a
 * logs file, which can be used to store metadata of the cached
 * files.
 *
 * For example, hash codes of cached files can be saved here, and then
 * compared with corresponding remote files to determine if they have
 * changed and should be re-downloaded.
 *
 * @property cache The cache folder on local storage.
 *
 * @author saifkhichi96
 * @since 1.0.0
 */
class FileLogs(private val cache: File) {

    private var data: MutableMap<String, String>
    private val fn = "logs.txt"

    init {
        data = import().toMutableMap()
    }

    /**
     * Imports logs from cache.
     *
     * @return A map of saved key-value pairs, or an empty map.
     */
    private fun import(): Map<String, String> {
        return try {
            val props = Properties()
            val logs = File(cache, fn)
            logs.inputStream().use { stream ->
                props.load(stream)
            }

            props.map { (key, value) -> key.toString() to value.toString() }.toMap()
        } catch (ex: Exception) {
            emptyMap()
        }
    }

    /**
     * Saves the logs data to cache.
     *
     * Existing logs are overwritten when exporting new logs.
     *
     * @param data A map of key-value pairs to save.
     */
    private fun export(data: Map<String, String>) {
        val props = Properties()
        for ((key, value) in data) {
            props[key] = value
        }

        val logs = File(cache, fn)
        logs.outputStream().use { stream ->
            props.store(stream, null)
        }
    }

    /**
     * Saves a property to logs.
     *
     * Any existing value with same key is overwritten.
     *
     * @param key The unique key of the property.
     * @param value The value of the property.
     */
    operator fun set(key: String, value: String) {
        data[key] = value
        export(data)
    }

    /**
     * Gets value of a saved property.
     *
     * @param key The unique key of the property.
     * @return The saved value, or null if nothing saved.
     */
    operator fun get(key: String): String? {
        return data[key]
    }

}