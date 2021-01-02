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
import co.aspirasoft.catalyst.dao.AccountsDao
import co.aspirasoft.catalyst.models.UserAccount
import co.aspirasoft.catalyst.utils.storage.FileManager
import co.aspirasoft.catalyst.utils.storage.ImageLoader
import co.aspirasoft.util.InputUtils.isNotBlank
import co.aspirasoft.util.PermissionUtils
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_profile.passwordField
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream


class ProfileActivity : DashboardChildActivity() {

    private lateinit var mFileManager: FileManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val user = intent.getSerializableExtra(MyApplication.EXTRA_PROFILE_USER) as UserAccount? ?: currentUser
        if (user.id != currentUser.id) {
            changeUserImageButton.visibility = View.GONE
            changePasswordButton.visibility = View.GONE
            deleteAccountButton.visibility = View.GONE
        }
        currentUser = user
        mFileManager = FileManager.newInstance(this, "users/${currentUser.id}/")

        changeUserImageButton.setOnClickListener {
            if (PermissionUtils.requestPermissionIfNeeded(
                            this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            getString(R.string.explanation_storage_permission),
                            RC_WRITE_PERMISSION
                    )) {
                pickImage()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            RESULT_ACTION_LOAD -> if (resultCode == Activity.RESULT_OK) {
                data?.data?.let { selectedImage ->
                    userImage.setImageURI(selectedImage)
                    val bm = userImage.drawable as BitmapDrawable? ?: return@let
                    val source = bm.bitmap ?: return@let

                    // Crop center region
                    val bitmap = Bitmap.createScaledBitmap(if (source.width >= source.height) {
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
                    }, 256, 256, false)

                    val outputStream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                    val bytes = outputStream.toByteArray()

                    val status = Snackbar.make(userImage, getString(R.string.status_uploading), Snackbar.LENGTH_INDEFINITE)
                    status.show()
                    lifecycleScope.launch {
                        try {
                            mFileManager.upload("photo.png", bytes)?.let {
                                status.setText(getString(R.string.status_uploaded))
                                Handler().postDelayed({ status.dismiss() }, 1500L)
                                showUserImage(true)
                            }
                        } catch (ex: Exception) {
                            userImage.setImageResource(R.drawable.placeholder_avatar)
                            status.setText(ex.message ?: getString(R.string.status_upload_failed))
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
        userNameLabel.text = currentUser.name
        userEmailLabel.text = currentUser.email

        // ACCOUNT SETTINGS
        // TODO: Allow password reset
        // Allow account deletion
        deleteAccountButton.setOnClickListener { deleteSection.visibility = View.VISIBLE }
        cancelDeleteButton.setOnClickListener { deleteSection.visibility = View.GONE }
        confirmDeleteButton.setOnClickListener {
            if (passwordField.isNotBlank(true)) {
                try {
                    confirmDeleteButton.isEnabled = false
                    // Delete user data from database
                    AccountsDao.delete(currentUser.id) {
                        lifecycleScope.launch {
                            val password = passwordField.text.toString().trim()
                            AccountsBO.deleteAccount(currentUser.email, password)
                            Firebase.auth.signOut()
                            finish()
                        }
                    }
                } catch (ex: Exception) {
                    passwordField.error = ex.message
                } finally {
                    confirmDeleteButton.isEnabled = true
                }
            }
        }
    }

    private fun showUserImage(invalidate: Boolean = false) {
        ImageLoader.with(this)
                .load(currentUser.id)
                .skipCache(invalidate)
                .into(userImage)
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