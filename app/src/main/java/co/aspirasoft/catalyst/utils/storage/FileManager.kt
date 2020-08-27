package co.aspirasoft.catalyst.utils.storage

import android.content.Context
import android.net.Uri
import co.aspirasoft.catalyst.models.Document
import co.aspirasoft.catalyst.models.Project
import co.aspirasoft.catalyst.utils.FileUtils
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FileDownloadTask
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File

/**
 * FileManager class is used to access files from FirebaseStorage.
 *
 * This class provides methods to access files from cloud storage. Accessed
 * files are cached locally to improve latency on subsequent requests.
 *
 * @author saifkhichi96
 * @since 1.0.0
 */
class FileManager private constructor(context: Context, relativePath: String) {

    private val storage = Firebase.storage.getReference(relativePath)
    private val cache = context.getExternalFilesDir(null)?.let { File(it, relativePath) }

    init {
        if (cache != null && !cache.exists()) {
            cache.mkdirs()
        }
    }

    fun download(
            filename: String,
            successListener: OnSuccessListener<File>,
            failureListener: OnFailureListener,
            invalidate: Boolean = false,
    ) {
        when (val cachedFile = downloadFromCache(filename)) {
            null -> {
                val tempFile = FileUtils.createTempFile(filename, cache)
                downloadFromStorage(filename, tempFile)
                        .addOnSuccessListener { successListener.onSuccess(tempFile) }
                        .addOnFailureListener {
                            tempFile.delete()
                            failureListener.onFailure(it)
                        }
            }
            else -> {
                // Invalidate cache if flag set or more than a week since last update
                if (cachedFile.requiresInvalidation() || invalidate) {
                    downloadFromStorage(filename, cachedFile)
                            .addOnSuccessListener { successListener.onSuccess(cachedFile) }
                            .addOnFailureListener {
                                cachedFile.delete()
                                failureListener.onFailure(it)
                            }
                }
                successListener.onSuccess(cachedFile)
            }
        }
    }

    fun downloadOnly(
            filename: String,
            successListener: OnSuccessListener<File>,
            failureListener: OnFailureListener,
    ) {
        val file = downloadFromCache(filename) ?: FileUtils.createTempFile(filename, cache)
        downloadFromStorage(filename, file)
                .addOnSuccessListener { successListener.onSuccess(file) }
                .addOnFailureListener {
                    file.delete()
                    failureListener.onFailure(it)
                }
    }

    suspend fun upload(filename: String, uri: Uri): StorageMetadata? {
        return storage.child(filename).putFile(uri).await().metadata
    }

    suspend fun upload(filename: String, bytes: ByteArray) = withContext(Dispatchers.IO) {
        storage.child(filename).putBytes(bytes).await().metadata
    }

    suspend fun upload(document: Document): StorageMetadata? = withContext(Dispatchers.IO) {
        upload(document.toPdfName(), document.toByteArray())
    }

    fun hasInCache(filename: String): Boolean {
        return downloadFromCache(filename) != null
    }

    fun listAll(block: (list: List<StorageReference>) -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {
            block(storage.listAll().await().items)
        }
    }

    private fun downloadFromCache(filename: String): File? {
        val file = File(cache, filename)
        return if (file.exists()) file else null
    }

    private fun downloadFromStorage(filename: String, file: File): FileDownloadTask {
        return storage.child(filename).getFile(file)
    }

    private fun File.requiresInvalidation(): Boolean {
        val modified = this.lastModified()
        val now = System.currentTimeMillis()

        val days = (now - modified) / (1000 * 60 * 60 * 24L)
        return days > 7
    }

    companion object {
        fun projectDocsManager(context: Context, project: Project): FileManager {
            return newInstance(context, "${project.ownerId}/projects/${project.name}/docs/")
        }

        fun newInstance(context: Context, relativePath: String): FileManager {
            return FileManager(context, relativePath)
        }
    }

}