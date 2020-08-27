package co.aspirasoft.catalyst.activities.abs

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import co.aspirasoft.catalyst.MyApplication
import co.aspirasoft.catalyst.models.UserAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

/**
 * SecureActivity is an abstract activity which restricts usage to signed in users.
 *
 * Extend this class to make an activity secure. A `secured` activity will only be
 * allowed to open if a [FirebaseUser] is authenticated and the [UserAccount] instance of
 * signed in user is passed to the activity intent with [MyApplication.EXTRA_USER] tag.
 *
 * @author saifkhichi96
 * @since 1.0.0
 */
abstract class SecureActivity : AppCompatActivity() {

    protected lateinit var currentUser: UserAccount

    /**
     * Overrides the onCreate activity lifecycle method.
     *
     * All authentication checks are performed here, and activity is terminated
     * if the checks fail.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val signedInUser = FirebaseAuth.getInstance().currentUser       // firebase auth -> (1)
        val user = intent.getSerializableExtra(MyApplication.EXTRA_USER) as UserAccount? // account details -> (2)
        currentUser = when {
            signedInUser == null || user == null -> return finish() // both (1) and (2) must exist
            user.id == signedInUser.uid -> user                     // and both must belong to same user
            else -> return finish()                                 // else finish activity
        }
    }

    /**
     * Overrides the onStart activity lifecycle method.
     *
     * This is only called if all authentication checks passed. We can safely
     * use our [currentUser] object here.
     */
    override fun onStart() {
        super.onStart()
        updateUI(currentUser)
    }

    /**
     * Overrides the back navigation button in action bar.
     *
     * Hooks the action bar back button to default back action.
     */
    override fun onNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    /**
     * Ensures required arguments are forwarded when opening another secure activity.
     */
    fun startSecurely(target: Class<out SecureActivity>, src: Intent? = null) {
        startActivity(Intent(this, target).apply {
            this.putExtra(MyApplication.EXTRA_USER, currentUser)
            src?.let { this.putExtras(it) }
        })
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    /**
     * Implement this method to update UI for signed in user, if needed.
     */
    abstract fun updateUI(currentUser: UserAccount)

}