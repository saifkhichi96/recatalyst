package co.aspirasoft.catalyst.utils.storage

import android.content.Context
import android.graphics.Bitmap
import co.aspirasoft.catalyst.models.UserAccount
import com.google.firebase.storage.StorageMetadata
import java.io.File

/**
 * Manages saved data of a [UserAccount].
 *
 * @param context The application [Context].
 * @param uid The user id of the account to manage.
 *
 * @author saifkhichi96
 * @since 1.0.0
 */
class AccountStorage(context: Context, uid: String) : FileManager(context, RemoteStorage.userData(uid)) {

    private val avatarFile = "photo.png"

    /**
     * Downloads user avatar.
     *
     * @param preferCache Set true to always return cached file, if it exists. Default is false.
     * @param skipCache Set true to download directly from remote storage. Default is false.
     *
     * @see FileManager.download
     */
    suspend fun downloadAvatar(preferCache: Boolean = false, skipCache: Boolean = false): File {
        return super.download(avatarFile, preferCache, skipCache)
    }

    /**
     * Uploads user avatar.
     *
     * @param bitmap User's avatar as a [Bitmap].
     * @return The metadata of uploaded file, or null if cannot be uploaded.
     */
    suspend fun uploadAvatar(bitmap: Bitmap): StorageMetadata? {
        return this.upload(avatarFile, bitmap)
    }

}