package co.aspirasoft.catalyst.utils.storage

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import co.aspirasoft.catalyst.R
import co.aspirasoft.catalyst.utils.FileUtils
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException

/**
 * FileManager class is used to access files from FirebaseStorage.
 *
 * This class provides methods to access files from cloud storage. Accessed
 * files are cached locally to improve latency on subsequent requests.
 *
 * @property remote Reference to a remote folder.
 * @property cache A folder on local storage for caching purposes.
 * @property logs A logs file to list all files in [cache], including their metadata.
 *
 * @author saifkhichi96
 * @since 1.0.0
 */
open class FileManager @Throws(IOException::class) internal constructor(
    private val context: Context,
    private val remote: StorageReference,
) {
    @Throws(IOException::class)
    internal constructor(context: Context, path: String)
            : this(context, Firebase.storage.getReference(path))

    val cache: File
        @Throws(IOException::class)
        get() {
            val root = context.getExternalFilesDir(null)
                ?: throw IOException(context.getString(R.string.storage_access_denied))

            val cache = File(root, remote.path)
            if (!cache.exists()) {
                cache.mkdirs()
            }
            return cache
        }

    private val logs = FileLogs(cache)

    /**
     * Downloads a file from storage.
     *
     * By default, if the file is cached, the cached copy is returned if the remote
     * file has not changed. You can change this behavior in one of the following
     * ways.
     *
     * You can force-invalidate by setting the [skipCache] parameter to true. This
     * causes the file to be downloaded from remote storage regardless of whether
     * we already had the latest copy in cache.
     *
     * You can set [preferCache] to true to return a cached copy of the file, if
     * one exists, even when the remote file may have changed. If [preferCache] is
     * set to true, the value of [skipCache] is ignored.
     *
     * @param path The path of the file.
     * @param preferCache Set true to always return cached file, if it exists. Default is false.
     * @param skipCache Set true to download directly from remote storage. Default is false.
     */
    @Throws(Exception::class)
    suspend fun download(path: String, preferCache: Boolean = false, skipCache: Boolean = false): File {
        // case: file in cache and we WANT the cached file (even if remote changed)
        val cachedFile = getFileFromCache(path)
        if (cachedFile != null && (preferCache || checkFileExpired(cachedFile))) {
            return cachedFile
        }

        // case: we WANT a fresh copy (even if it is already cached)
        // case: file not in cache
        // case: file in cache but remote file has changed
        val metadata = getMetadata(path)
        if (skipCache || cachedFile == null || checkFileChanged(path, metadata)) {
            val remoteFile = getFileFromStorage(path)
            cacheHashCode(metadata)
            return remoteFile
        }

        // case: file in cache, remote hasn't changed either
        return cachedFile
    }

    /**
     * Checks if a file is cached.
     *
     * @param path Location of the file.
     * @return True if the file is in cache, else false.
     */
    fun hasInCache(path: String): Boolean {
        return getFileFromCache(path) != null
    }

    /**
     * Lists the files in the [remote] directory, or one of its sub-directories.
     *
     * @param path An optional path to a sub-directory. Default is null.
     */
    suspend fun listAll(path: String? = null): List<StorageReference> {
        val ref = if (path != null) remote.child(path) else remote
        return ref.listAll().await().items
    }

    /**
     * Uploads a file to [remote] storage.
     *
     * @param path Location of the file.
     * @param data Contents of the file.
     * @return The metadata of uploaded file, or null if cannot be uploaded.
     */
    open suspend fun upload(path: String, data: Uri): StorageMetadata? {
        return remote.child(path).putFile(data).await().metadata
    }

    /**
     * Uploads a file to [remote] storage.
     *
     * @param path Location of the file.
     * @param data Contents of the file as a byte array.
     * @return The metadata of uploaded file, or null if cannot be uploaded.
     */
    open suspend fun upload(path: String, data: ByteArray): StorageMetadata? {
        return remote.child(path).putBytes(data).await().metadata
    }

    /**
     * Uploads an image to [remote] storage.
     *
     * @param path Name of the image file.
     * @param bitmap The image as a [Bitmap].
     * @return The metadata of uploaded file, or null if cannot be uploaded.
     */
    open suspend fun upload(path: String, bitmap: Bitmap): StorageMetadata? {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)

        return upload(path, outputStream.toByteArray())
    }

    /**
     * Saves hash code of a remote file in cache.
     *
     * @param metadata The [StorageMetadata] of a remote file.
     */
    private fun cacheHashCode(metadata: StorageMetadata) {
        metadata.md5Hash?.let { logs["${metadata.path}-md5"] = it }
    }

    /**
     * Checks if a file needs invalidation.
     *
     * @param path Location of the file to check.
     * @param metadata The metadata of the remote file.
     * @return True if remote file has changed, else false.
     */
    private fun checkFileChanged(path: String, metadata: StorageMetadata): Boolean {
        val cachedChecksum = logs["${path}-md5"]
        val remoteChecksum = runCatching { metadata.md5Hash }.getOrNull()
        return cachedChecksum != remoteChecksum
    }

    /**
     * Checks if a cached file has expired.
     *
     * Cache has a default timeout of one day (24 hours), and any file which
     * was stored in cache before that is expired and needs to be updated.
     * Default timeout can be changed by passing another value in the [timeout]
     * parameter.
     *
     * @param file The file to check.
     * @param timeout Cache timeout in hours. Default is 24.
     * @return True if file not in cache or expired, else false.
     */
    private fun checkFileExpired(file: File, timeout: Int = 24): Boolean {
        val modified = file.lastModified()
        val now = System.currentTimeMillis()
        val days = (now - modified) / (1000 * 60 * 60)
        return days > timeout
    }

    /**
     * Gets a cached file from device memory.
     *
     * @param path Location of the file.
     * @return The cached file, ot null if file is not cached.
     */
    private fun getFileFromCache(path: String): File? {
        val file = File(cache, path)
        return if (file.exists()) file else null
    }

    /**
     * Downloads a file from remote storage.
     *
     * This method throws an exception if the file could not be downloaded.
     * Some the reasons why this might happen are, that the file may not be
     * at the specified path on remote storage, or the user might not have
     * access to this file.
     *
     * @param path Location of the file.
     * @return The downloaded file.
     * @throws Exception An exception raised if downloading fails for some reason.
     */
    @Throws(Exception::class)
    private suspend fun getFileFromStorage(path: String): File {
        val file = FileUtils.createTempFile(path, cache)
        try {
            remote.child(path).getFile(file).await()
            return file
        } catch (ex: Exception) {
            file.delete()
            throw ex
        }
    }

    /**
     * Gets metadata of a remote file.
     *
     * @param path Location of the file.
     */
    private suspend fun getMetadata(path: String): StorageMetadata {
        return remote.child(path).metadata.await()
    }

}