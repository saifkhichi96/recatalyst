package co.aspirasoft.catalyst.dialogs

import android.app.Activity
import android.content.Intent
import co.aspirasoft.catalyst.R
import co.aspirasoft.catalyst.activities.SignInActivity
import co.aspirasoft.catalyst.bo.AuthBO
import com.google.android.material.dialog.MaterialAlertDialogBuilder

object LogoutConfirmationDialog {

    fun show(activity: Activity) {
        MaterialAlertDialogBuilder(activity)
                .setTitle(activity.getString(R.string.sign_out))
                .setMessage(activity.getString(R.string.sign_out_confirm))
                .setPositiveButton(android.R.string.yes) { _, _ ->
                    AuthBO.signOut()
                    activity.startActivity(Intent(activity, SignInActivity::class.java))
                    activity.finish()
                }
                .setNegativeButton(android.R.string.no) { dialog, _ ->
                    dialog.cancel()
                }
                .show()
    }

}