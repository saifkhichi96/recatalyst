package co.aspirasoft.catalyst.dialogs

import android.app.Activity
import android.content.Intent
import co.aspirasoft.catalyst.activities.SignInActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth

object LogoutConfirmationDialog {

    fun show(activity: Activity) {
        MaterialAlertDialogBuilder(activity)
                .setTitle("Sign Out")
                .setMessage("You will be logged out. Continue?")
                .setPositiveButton(android.R.string.yes) { _, _ ->
                    FirebaseAuth.getInstance().signOut()
                    activity.startActivity(Intent(activity, SignInActivity::class.java))
                    activity.finish()
                }
                .setNegativeButton(android.R.string.no) { dialog, _ ->
                    dialog.cancel()
                }
                .show()
    }

}