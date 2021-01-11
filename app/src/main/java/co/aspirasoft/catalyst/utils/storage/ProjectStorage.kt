package co.aspirasoft.catalyst.utils.storage

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import co.aspirasoft.catalyst.models.Document
import co.aspirasoft.catalyst.models.Project
import co.aspirasoft.catalyst.utils.FileUtils
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference
import java.io.File
import java.io.FileOutputStream

/**
 * Manages saved data of a [Project].
 *
 * @param context The application [Context].
 * @param project The project to manage.
 *
 * @author saifkhichi96
 * @since 1.0.0
 */
class ProjectStorage(context: Context, project: Project) {

    private val docsStorage: FileManager
    private val filesStorage: FileManager
    private val imagesStorage: FileManager

    init {
        val projectReference = RemoteStorage.projectData(project)
        docsStorage = FileManager(context, projectReference.child("docs"))
        filesStorage = FileManager(context, projectReference.child("files"))
        imagesStorage = FileManager(context, projectReference.child("images"))
    }

    /**
     * Saves a [Document] as a PDF on device storage.
     *
     * @param document The document to save.
     * @return The newly created document file on device.
     */
    fun cache(document: Document, context: Context): File {
        val file = FileUtils.createTempFile(document.toPdfName(), docsStorage.cache)
        FileOutputStream(file).use { fos ->
            fos.write(document.toByteArray(context))
        }
        return file
    }

    /**
     * Gets a project document file.
     *
     * @param document The document to get.
     */
    @Throws(Exception::class)
    suspend fun getDocument(document: Document): File {
        return docsStorage.download(document.toPdfName())
    }

    /**
     * Gets a project file.
     *
     * @param name Name of the file.
     */
    @Throws(Exception::class)
    suspend fun getFile(name: String): File {
        return filesStorage.download(name)
    }

    /**
     * Gets a project image.
     *
     * @param name Name of the image.
     */
    @Throws(Exception::class)
    suspend fun getImage(name: String): File {
        return imagesStorage.download(name)
    }

    /**
     * Gets a list of all project documents on remote storage.
     *
     * @return A list of [StorageReference]s pointing to remote project documents.
     */
    suspend fun getAllDocuments(): List<StorageReference> {
        return docsStorage.listAll()
    }

    /**
     * Gets a list of all project files on remote storage.
     *
     * @return A list of [StorageReference]s pointing to remote project files.
     */
    suspend fun getAllFiles(): List<StorageReference> {
        return filesStorage.listAll()
    }

    /**
     * Gets a list of all project images on remote storage.
     *
     * @return A list of [StorageReference]s pointing to remote project images.
     */
    suspend fun getAllImages(): List<StorageReference> {
        return imagesStorage.listAll()
    }

    /**
     * Checks if a document is cached.
     *
     * @param name Name of the document
     * @return True if the document is in cache, else false.
     */
    fun hasDocumentInCache(name: String): Boolean {
        return docsStorage.hasInCache(name)
    }

    /**
     * Checks if a file is cached.
     *
     * @param name Name of the file
     * @return True if the file is in cache, else false.
     */
    fun hasFileInCache(name: String): Boolean {
        return filesStorage.hasInCache(name)
    }

    /**
     * Checks if a image is cached.
     *
     * @param name Name of the image
     * @return True if the image is in cache, else false.
     */
    fun hasImageInCache(name: String): Boolean {
        return imagesStorage.hasInCache(name)
    }

    /**
     * Uploads a project document PDF to remote storage.
     *
     * @param document The document to upload.
     * @return The metadata of uploaded file, or null if cannot be uploaded.
     */
    suspend fun upload(document: Document, context: Context): StorageMetadata? {
        return docsStorage.upload(document.toPdfName(), document.toByteArray(context))
    }

    /**
     * Uploads a project file to remote storage.
     *
     * @param path Name of the file.
     * @param data Contents of the file.
     * @return The metadata of uploaded file, or null if cannot be uploaded.
     */
    suspend fun upload(path: String, data: Uri): StorageMetadata? {
        return filesStorage.upload(path, data)
    }

    /**
     * Uploads a project image to remote storage.
     *
     * @param path Name of the image file.
     * @param bitmap Contents of the image file.
     * @return The metadata of uploaded file, or null if cannot be uploaded.
     */
    suspend fun upload(path: String, bitmap: Bitmap): StorageMetadata? {
        return imagesStorage.upload(path, bitmap)
    }

}