package co.aspirasoft.catalyst.activities.abs

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import co.aspirasoft.catalyst.MyApplication
import co.aspirasoft.catalyst.activities.DashboardActivity
import co.aspirasoft.catalyst.dao.AccountsDao
import co.aspirasoft.catalyst.models.UserAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

/**
 * Abstract activity which silently signs in a user if already authenticated.
 *
 * Purpose of this activity is to provide an interface for all activities which intend
 * to automatically complete the sign in task in background, if a user had previously
 * signed in and saved their authentication credentials.
 *
 * @author saifkhichi96
 * @since 1.0.0
 */
abstract class SilentSignInActivity : AppCompatActivity() {

    protected val auth = FirebaseAuth.getInstance()

    /**
     * Starts the silent sign-in process.
     *
     * It check if a user is already authenticated, and if yes, begins the
     * sign-in process.
     *
     * @return True if the sign-in process was started, else False.
     */
    protected fun startSilentSignIn(): Boolean {
        auth.currentUser?.let {
            onAuthenticated(it)
            return true
        }

        return false
    }

    /**
     * Callback for when user authentication has succeeded.
     *
     * Fetches data of the authenticated user from database to complete the
     * sign-in process.
     *
     * @param user The user who has authenticated.
     */
    protected fun onAuthenticated(user: FirebaseUser) {
        AccountsDao.getById(user.uid) { account ->
            account?.let { onSignedIn(account) }
        }
    }

    /**
     * Callback for when the sign-in process completes.
     *
     * User is automatically redirected to the appropriate screen in the app.
     *
     * @param account The signed-in user's account data.
     */
    private fun onSignedIn(account: UserAccount) {
        val i = Intent(applicationContext, DashboardActivity::class.java).apply {
            putExtra(MyApplication.EXTRA_USER, account)
        }

        startActivity(i)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }

}