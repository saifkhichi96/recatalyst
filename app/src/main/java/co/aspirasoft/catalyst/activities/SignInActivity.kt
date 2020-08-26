package co.aspirasoft.catalyst.activities

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import co.aspirasoft.catalyst.MyApplication
import co.aspirasoft.catalyst.R
import co.aspirasoft.catalyst.activities.abs.SilentSignInActivity
import co.aspirasoft.catalyst.bo.AuthBO
import co.aspirasoft.util.InputUtils.isNotBlank
import co.aspirasoft.util.ViewUtils
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.coroutines.launch

/**
 * SignInActivity is the start page for all unauthenticated users.
 *
 * Purpose of this activity is to let users sign into their accounts, or in case
 * of new users, provide a link to the page where they can create new accounts.
 *
 * @author saifkhichi96
 * @since 1.0.0
 */
class SignInActivity : SilentSignInActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        signInButton.setOnClickListener { signIn() }
        signUpButton.setOnClickListener { signUp() }
    }

    override fun onStart() {
        super.onStart()
        if (isNewSignUp()) {
            onNewSignUp()
        } else {
            startSilentSignIn()
        }
    }

    /**
     * Explicitly starts the sign-in process using user-provided credentials.
     *
     * Authentication is asynchronous, and happens only if all required fields
     * are provided. A blocking UI is displayed while the request is being
     * processed, disallowing all user actions.
     */
    private fun signIn() {
        if (emailField.isNotBlank(true) && passwordField.isNotBlank(true)) {
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString().trim()
            signInButton.isEnabled = false

            lifecycleScope.launch {
                try {
                    val user = AuthBO.signInWithEmailPassword(email, password)
                    onAuthenticated(user!!)
                } catch (ex: NullPointerException) {
                    onFailure()
                } finally {
                    signInButton.isEnabled = true
                }
            }
        }
    }

    /**
     * Opens the sign-up screen.
     */
    private fun signUp() {
        startActivity(Intent(this, SignUpActivity::class.java))
    }

    /**
     * Callback for when an error occurs during the sign-in process.
     *
     * This method is called if authentication fails or user's details
     * could not be fetched from the database.
     */
    private fun onFailure() {
        ViewUtils.showError(signInButton, getString(R.string.status_sign_in_failed))
        auth.signOut()
    }

    /**
     * Checks if a new user has signed up.
     */
    private fun isNewSignUp(): Boolean {
        return intent.getBooleanExtra(MyApplication.EXTRA_NEW_SIGN_UP, false)
    }

    /**
     * Callback for when a new user has signed up.
     *
     * Removes any saved authentications and displays a welcome message to the new user.
     */
    private fun onNewSignUp() {
        auth.signOut()
        Snackbar.make(
                signInButton,
                getString(R.string.status_signed_up),
                Snackbar.LENGTH_LONG
        ).show()
    }

}