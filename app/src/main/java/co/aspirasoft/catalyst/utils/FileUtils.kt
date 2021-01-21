package co.aspirasoft.catalyst.utils

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import co.aspirasoft.catalyst.utils.storage.LocalStorage
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.charset.Charset

object FileUtils {

    fun createTempFile(filename: String, parent: File? = null): File {
        val file = parent?.let { File(it, filename) } ?: File(filename)
        if (file.exists()) file.delete()

        file.parent?.let { File(it).mkdirs() }
        file.createNewFile()
        return file
    }

    @Throws(IOException::class)
    fun File.openInExternalApp(context: Context) {
        try {
            val extension = this.extension
            val mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension) ?: "*/*"
            val uri = FileProvider.getUriForFile(
                context,
                context.applicationContext.packageName + ".fileprovider",
                this
            )

            val i = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, mime)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(i)
        } catch (ex: Exception) {
            throw IOException(ex.message)
        }
    }

    fun getBitmap(property: Int, itemId: String, projectName: String): Bitmap? {
        val projectDir = File(LocalStorage.saveDir, projectName)
        val propertyBitmapFile = File(projectDir, itemId + "_" + property.toString() + ".jpg")
        if (!propertyBitmapFile.exists()) {
            return null
        }

        val filePath = propertyBitmapFile.absolutePath
        return BitmapFactory.decodeFile(filePath)
    }

    fun saveBitmap(bitmap: Bitmap, property: Int, itemId: String, projectName: String): Boolean {
        val projectDir = File(LocalStorage.saveDir, projectName)
        val propertyBitmapFile = File(projectDir, itemId + "_" + property.toString() + ".jpg")
        if (propertyBitmapFile.exists()) {
            propertyBitmapFile.delete()
        }

        return try {
            propertyBitmapFile.createNewFile()
            val out = FileOutputStream(propertyBitmapFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            true

        } catch (e: IOException) {
            false
        }

    }

    fun Uri.getLastPathSegmentOnly(context: Context): String? {
        var name: String? = null
        when {
            ContentResolver.SCHEME_FILE == this.scheme -> name = this.lastPathSegment
            ContentResolver.SCHEME_CONTENT == this.scheme -> {
                val returnCursor: Cursor? = context.contentResolver.query(this, null, null, null, null)
                if (returnCursor != null && returnCursor.moveToFirst()) {
                    val nameIndex: Int = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    name = returnCursor.getString(nameIndex)
                    returnCursor.close()
                }
            }
        }
        return name
    }

    fun getJsonFromAssets(context: Context, filename: String): String? {
        return try {
            val stream = context.assets.open(filename)
            val size: Int = stream.available()
            val buffer = ByteArray(size)
            stream.read(buffer)
            stream.close()
            String(buffer, Charset.defaultCharset())
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

}