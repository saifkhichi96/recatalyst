package co.aspirasoft.catalyst.utils.storage

import android.os.Environment
import java.io.File

/**
 * @author saifkhichi96
 * @version 1.0.0
 * @since 1.0.0 26/10/2018 3:51 AM
 */
object LocalStorage {

    private val SAVE_PATH = Environment.getExternalStorageDirectory().toString() + "/Android/data/co.aspirasoft.sda/Projects/"

    private val EXPORT_PATH = Environment.getExternalStorageDirectory().toString() + "/Android/data/co.aspirasoft.sda/Exports/"

    val saveDir: File
    val exportDir: File

    init {
        saveDir = File(SAVE_PATH)
        if (!saveDir.exists()) {
            saveDir.mkdirs()
        }

        exportDir = File(EXPORT_PATH)
        if (!exportDir.exists()) {
            exportDir.mkdirs()
        }
    }

}