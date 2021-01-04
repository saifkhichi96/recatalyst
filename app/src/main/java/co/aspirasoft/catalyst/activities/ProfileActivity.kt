package co.aspirasoft.catalyst.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.view.View
import androidx.lifecycle.lifecycleScope
import co.aspirasoft.catalyst.MyApplication
import co.aspirasoft.catalyst.R
import co.aspirasoft.catalyst.activities.abs.DashboardChildActivity
import co.aspirasoft.catalyst.bo.AccountsBO
import co.aspirasoft.catalyst.bo.AuthBO
import co.aspirasoft.catalyst.dao.AccountsDao
import co.aspirasoft.catalyst.databinding.ActivityProfileBinding
import co.aspirasoft.catalyst.models.UserAccount
import co.aspirasoft.catalyst.utils.storage.FileManager
import co.aspirasoft.catalyst.utils.storage.ImageLoader
import co.aspirasoft.util.InputUtils.isNotBlank
import co.aspirasoft.util.PermissionUtils
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream


/**
 * Shows details of a [UserAccount].
 *
 * This is a profile page, where a user can see their own or someone
 * else's details.
 */
class ProfileActivity : DashboardChildActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var mFileManager: FileManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val user = intent.getSerializableExtra(MyApplication.EXTRA_PROFILE_USER) as UserAccount? ?: currentUser
        if (user.id != currentUser.id) {
            binding.changeUserImageButton.visibility = View.GONE
            binding.changePasswordButton.visibility = View.GONE
            binding.deleteAccountButton.visibility = View.GONE
            binding.connectButton.visibility = View.VISIBLE // FIXME: Show Connect button only if user not a Connection
        }
        currentUser = user
        mFileManager = FileManager.newInstance(this, "users/${currentUser.id}/")

        binding.changeUserImageButton.setOnClickListener {
            if (PermissionUtils.requestPermissionIfNeeded(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    getString(R.string.permission_storage),
                    RC_WRITE_PERMISSION
                )
            ) {
                pickImage()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            RESULT_ACTION_LOAD -> if (resultCode == Activity.RESULT_OK) {
                data?.data?.let { selectedImage ->
                    binding.accountAvatar.setImageURI(selectedImage)
                    val bm = binding.accountAvatar.drawable as BitmapDrawable? ?: return@let
                    val source = bm.bitmap ?: return@let

                    // Crop center region
                    val bitmap = Bitmap.createScaledBitmap(
                        if (source.width >= source.height) {
                            Bitmap.createBitmap(
                                source,
                                source.width / 2 - source.height / 2,
                                0,
                                source.height,
                                source.height
                            )
                        } else {
                            Bitmap.createBitmap(
                                source,
                                0,
                                source.height / 2 - source.width / 2,
                                source.width,
                                source.width
                            )
                        }, 256, 256, false
                    )

                    val outputStream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                    val bytes = outputStream.toByteArray()

                    val status =
                        Snackbar.make(binding.accountAvatar, getString(R.string.uploading), Snackbar.LENGTH_INDEFINITE)
                    status.show()
                    lifecycleScope.launch {
                        try {
                            mFileManager.upload("photo.png", bytes)?.let {
                                status.setText(getString(R.string.uploaded))
                                Handler().postDelayed({ status.dismiss() }, 1500L)
                                showUserImage(true)
                            }
                        } catch (ex: Exception) {
                            binding.accountAvatar.setImageResource(R.drawable.placeholder_avatar)
                            status.setText(ex.message ?: getString(R.string.upload_failed))
                            Handler().postDelayed({ status.dismiss() }, 1500L)
                        }
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == RC_WRITE_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImage()
            }
        }
    }

    override fun updateUI(currentUser: UserAccount) {
        showUserImage()

        // HEADLINE
        binding.accountName.text = currentUser.name
        if (!currentUser.headline.isNullOrBlank()) {
            binding.accountShortBio.text = currentUser.headline
            binding.accountShortBio.visibility = View.VISIBLE
        }
        if (!currentUser.location.isNullOrBlank()) {
            binding.accountLocation.text = currentUser.location
            binding.accountLocation.visibility = View.VISIBLE
        }

        // STATS SECTION
        binding.accountConnectionCount.text = "0" // FIXME: Show actual # of connections
        binding.accountProjectCount.text = "0" // FIXME: Show actual # of projects

        // ABOUT SECTION
        if (!currentUser.bio.isNullOrBlank()) {
            binding.accountLongBio.text = currentUser.bio
            binding.aboutSection.visibility = View.VISIBLE
        }

        // CONTACT SECTION
        binding.accountEmail.text = currentUser.email
        if (!currentUser.phone.isNullOrBlank()) {
            binding.accountPhone.text = currentUser.phone
            binding.accountPhone.visibility = View.VISIBLE
        }
        if (!currentUser.blog.isNullOrBlank()) {
            binding.accountBlog.text = currentUser.blog
            binding.accountBlog.visibility = View.VISIBLE
        }

        // ACCOUNT SETTINGS
        // TODO: Allow password reset
        // Allow account deletion
        binding.deleteAccountButton.setOnClickListener { binding.deleteSection.visibility = View.VISIBLE }
        binding.cancelDeleteButton.setOnClickListener { binding.deleteSection.visibility = View.GONE }
        binding.confirmDeleteButton.setOnClickListener { onDeleteAccountClicked() }
    }

    private fun onDeleteAccountClicked() {
        if (binding.passwordField.isNotBlank(true)) {
            try {
                binding.confirmDeleteButton.isEnabled = false
                // Delete user data from database
                AccountsDao.delete(currentUser.id) {
                    lifecycleScope.launch {
                        val password = binding.passwordField.text.toString().trim()
                        AccountsBO.deleteAccount(currentUser.email, password)
                        AuthBO.signOut()
                        finish()
                    }
                }
            } catch (ex: Exception) {
                binding.passwordField.error = ex.message
            } finally {
                binding.confirmDeleteButton.isEnabled = true
            }
        }
    }

    private fun showUserImage(invalidate: Boolean = false) {
        ImageLoader.with(this)
            .load(currentUser.id)
            .skipCache(invalidate)
            .into(binding.accountAvatar)
    }

    private fun pickImage() {
        val i = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(i, RESULT_ACTION_LOAD)
    }

    companion object {
        private const val RESULT_ACTION_LOAD = 100
        private const val RC_WRITE_PERMISSION = 200
    }

}