package co.aspirasoft.catalyst.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.view.View
import androidx.lifecycle.lifecycleScope
import co.aspirasoft.catalyst.MyApplication
import co.aspirasoft.catalyst.R
import co.aspirasoft.catalyst.activities.abs.DashboardChildActivity
import co.aspirasoft.catalyst.models.UserAccount
import co.aspirasoft.catalyst.utils.storage.FileManager
import co.aspirasoft.catalyst.utils.storage.ImageLoader
import co.aspirasoft.util.PermissionUtils
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_profile.*
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
                    val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
                    contentResolver.query(
                            selectedImage,
                            filePathColumn,
                            null,
                            null,
                            null
                    )?.let { cursor ->
                        cursor.moveToFirst()

                        val columnIndex: Int = cursor.getColumnIndex(filePathColumn[0])
                        val picturePath: String = cursor.getString(columnIndex)
                        cursor.close()

                        // Crop center region
                        val source = BitmapFactory.decodeFile(picturePath)
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
                                status.setText(ex.message ?: getString(R.string.status_upload_failed))
                                Handler().postDelayed({ status.dismiss() }, 1500L)
                            }
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
        // TODO: Allow account deletion
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