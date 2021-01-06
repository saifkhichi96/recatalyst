package co.aspirasoft.catalyst.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import co.aspirasoft.catalyst.R
import co.aspirasoft.catalyst.activities.abs.DashboardChildActivity
import co.aspirasoft.catalyst.databinding.ActivityProfileAvatarBinding
import co.aspirasoft.catalyst.databinding.ViewAvatarGalleryItemBinding
import co.aspirasoft.catalyst.models.UserAccount
import co.aspirasoft.catalyst.utils.storage.AccountStorage
import co.aspirasoft.catalyst.utils.storage.FileManager
import co.aspirasoft.catalyst.utils.storage.ImageLoader
import co.aspirasoft.catalyst.utils.storage.RemoteStorage
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.snapchat.kit.sdk.Bitmoji
import com.snapchat.kit.sdk.SnapLogin
import com.snapchat.kit.sdk.bitmoji.networking.FetchAvatarUrlCallback
import com.snapchat.kit.sdk.core.controller.LoginStateController
import kotlinx.coroutines.launch
import java.io.File

class ProfileAvatarActivity : DashboardChildActivity() {

    private lateinit var binding: ActivityProfileAvatarBinding
    private lateinit var accountStorage: AccountStorage

    private val avatarGallery = ArrayList<File>()
    private lateinit var adapter: AvatarGalleryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileAvatarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        accountStorage = AccountStorage(this, currentUser.id)
        ImageLoader.loadUserAvatar(
            this@ProfileAvatarActivity,
            currentUser.id,
            binding.accountAvatar
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            RESULT_ACTION_LOAD -> if (resultCode == Activity.RESULT_OK) {
                data?.data?.let { selectedAvatar -> setActiveAvatar(selectedAvatar) }
            }
        }
    }

    override fun updateUI(currentUser: UserAccount) {
        adapter = AvatarGalleryAdapter(this, avatarGallery)
        binding.avatarGallery.adapter = adapter
        avatarGallery.clear()

        getAvatarGallery()
    }

    /**
     * Gets the default avatars gallery.
     *
     * Downloaded images are sent to the [AvatarGalleryAdapter] for rendering.
     */
    private fun getAvatarGallery() {
        val fm = FileManager(this, RemoteStorage.defaultAvatars())
        lifecycleScope.launch {
            fm.listAll().forEach {
                avatarGallery.add(fm.download(it.name, preferCache = true))
                adapter.notifyDataSetChanged()
            }
        }
    }

    /**
     * Gets the Bitmoji avatar from Snapchat.
     */
    private fun getBitmojiAvatar() {
        Bitmoji.fetchAvatarUrl(this, object : FetchAvatarUrlCallback {
            override fun onSuccess(avatarUrl: String?) {
                avatarUrl?.let { setActiveAvatar(it) }
            }

            override fun onFailure(isNetworkError: Boolean, statusCode: Int) {
                // TODO: Handle Bitmoji failures
            }
        })
    }

    /**
     * Displays the image in the selected avatar view.
     *
     * @param bitmap The avatar image as a [Bitmap].
     */
    private fun setActiveAvatar(bitmap: Bitmap) = runOnUiThread {
        Glide.with(this)
            .load(bitmap)
            .into(binding.accountAvatar)
    }

    /**
     * Displays the image in the selected avatar view.
     *
     * @param data The image data as a [Uri].
     */
    private fun setActiveAvatar(data: Uri) = runOnUiThread {
        Glide.with(this)
            .load(data)
            .into(binding.accountAvatar)
    }

    /**
     * Displays the image in the selected avatar view.
     *
     * @param url The URL of the image.
     */
    private fun setActiveAvatar(url: String) {
        try {
            setActiveAvatar(Uri.parse(url))
        } catch (ex: Exception) {
            // TODO: Handle invalid urls
        }
    }

    /**
     * Crops out the center square from an image.
     *
     * @param bitmap The image to crop.
     */
    @Throws(IllegalArgumentException::class)
    private fun centerCrop(bitmap: Bitmap): Bitmap {
        return Bitmap.createScaledBitmap(
            if (bitmap.width >= bitmap.height) {
                Bitmap.createBitmap(
                    bitmap,
                    bitmap.width / 2 - bitmap.height / 2,
                    0,
                    bitmap.height,
                    bitmap.height
                )
            } else {
                Bitmap.createBitmap(
                    bitmap,
                    0,
                    bitmap.height / 2 - bitmap.width / 2,
                    bitmap.width,
                    bitmap.width
                )
            },
            256,
            256,
            false
        )
    }

    /**
     * Called when uploading new avatar fails.
     *
     * @param ex The [Exception] which caused the failure.
     */
    private fun onSaveError(ex: Exception) {
        binding.uploadPhotoButton.isEnabled = true
        Toast.makeText(this,
            ex.message ?: getString(R.string.upload_failed),
            Toast.LENGTH_LONG).show()
    }

    /**
     * Called when the new avatar is successfully uploaded.
     */
    private fun onSaveSuccess() {
        finish()
    }

    /**
     * Uploads the avatar to storage.
     *
     * @param bitmap The selected avatar as a [Bitmap].
     */
    private fun saveAvatar(bitmap: Bitmap) = lifecycleScope.launch {
        try {
            // Disable the upload button
            binding.uploadPhotoButton.isEnabled = false

            // Crop out the center square of the source image before uploading
            val cropped = centerCrop(bitmap)
            accountStorage.uploadAvatar(cropped) ?: throw RuntimeException()
            accountStorage.downloadAvatar(skipCache = false)
            onSaveSuccess()
        } catch (ex: Exception) {
            onSaveError(ex)
        }
    }

    /**
     * Starts the Snapchat connection sequence.
     */
    private fun signInWithSnapchat() {
        SnapLogin.getAuthTokenManager(this).startTokenGrant()
        SnapLogin.getLoginStateController(this)
            .addOnLoginStateChangedListener(object : LoginStateController.OnLoginStateChangedListener {
                override fun onLoginSucceeded() {
                    getBitmojiAvatar()
                }

                override fun onLoginFailed() {
                    // TODO: Handle Snapchat connecting errors
                }

                override fun onLogout() {

                }
            })
    }

    /**
     * Picks an image from the device.
     */
    fun importFromDevice(v: View) {
        val i = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(i, RESULT_ACTION_LOAD)
    }

    /**
     * Imports the Bitmoji avatar from Snapchat.
     *
     * Starts Snapchat connection sequence if user has not already linked
     * their account to Snapchat, then gets the Bitmoji avatar.
     */
    fun importFromBitmoji(v: View) {
        if (!SnapLogin.isUserLoggedIn(this)) {
            signInWithSnapchat()
        } else {
            getBitmojiAvatar()
        }
    }

    /**
     * Imports avatar from connected Github account.
     *
     * Starts the Github connection sequence if user has not already linked
     * their account to Github, then gets the avatar.
     */
    fun importFromGithub(v: View) {
        // TODO: Allow importing photo from Github
    }

    /**
     * Saves the selected avatar by uploading it to storage.
     */
    fun saveAvatar(v: View) {
        val bm = binding.accountAvatar.drawable as BitmapDrawable? ?: return
        val source = bm.bitmap ?: return
        saveAvatar(source)
    }

    private inner class AvatarGalleryAdapter(context: Context, private val avatars: List<File>) :
        ArrayAdapter<File>(context, -1, avatars) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val binding = ViewAvatarGalleryItemBinding.inflate(LayoutInflater.from(context))
            val view = binding.root

            Glide.with(view)
                .load(avatars[position])
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(binding.imageView)

            view.setOnClickListener {
                setActiveAvatar((binding.imageView.drawable as BitmapDrawable).bitmap)
            }
            return view
        }

    }

    companion object {
        private const val RESULT_ACTION_LOAD = 100
    }

}