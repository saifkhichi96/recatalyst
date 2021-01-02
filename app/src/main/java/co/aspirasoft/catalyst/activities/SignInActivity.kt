package co.aspirasoft.catalyst.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import co.aspirasoft.catalyst.MyApplication
import co.aspirasoft.catalyst.R
import co.aspirasoft.catalyst.activities.abs.SilentSignInActivity
import co.aspirasoft.catalyst.bo.AuthBO
import co.aspirasoft.catalyst.models.UserAccount
import co.aspirasoft.util.InputUtils.isNotBlank
import com.google.firebase.auth.*
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

    private var signUpCredentials: AuthCredential? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        // Get intent extras
        signUpCredentials = intent.getParcelableExtra(MyApplication.EXTRA_NEW_SIGN_UP)

        signInButton.setOnClickListener { signIn(SignInMethod.DEFAULT) }
        githubButton.setOnClickListener { signIn(SignInMethod.GITHUB) }
        signUpButton.setOnClickListener { signUp() }
    }

    override fun onStart() {
        super.onStart()
        if (signUpCredentials != null) {
            signIn(SignInMethod.CREDENTIAL, signUpCredentials)
        } else {
            startSilentSignIn()
        }
    }

    /**
     * Explicitly starts the sign-in process with specified method.
     *
     * @param method The sign-in method to use.
     */
    private fun signIn(method: SignInMethod, args: Any? = null) {
        errorSection.visibility = View.INVISIBLE
        signInButton.isEnabled = false
        githubButton.isEnabled = false

        try {
            lifecycleScope.launch {
                when (method) {
                    SignInMethod.GITHUB -> signInWithGithub()
                    SignInMethod.CREDENTIAL -> signInWithCredential(args as AuthCredential)
                    SignInMethod.DEFAULT -> signIn()
                }
            }
        } catch (ex: Exception) {
            onFailure(ex)
        } finally {
            signInButton.isEnabled = true
            githubButton.isEnabled = true
        }
    }

    /**
     * Explicitly starts the sign-in process using user-provided email/password.
     *
     * Authentication is asynchronous, and happens only if all required fields
     * are provided. A blocking UI is displayed while the request is being
     * processed, disallowing all user actions.
     */
    @Throws(NullPointerException::class)
    private suspend fun signIn() {
        if (emailField.isNotBlank(true) && passwordField.isNotBlank(true)) {
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString().trim()

            val user = AuthBO.signInWithEmailPassword(email, password)
            onAuthenticated(user!!)
        }
    }

    /**
     * Completes authentication with sign-in credentials.
     */
    @Throws(NullPointerException::class)
    private suspend fun signInWithCredential(credential: AuthCredential) {
        val user = AuthBO.signInWithCredential(credential)
        onAuthenticated(user!!)
    }

    /**
     * Starts the authentication process with Github.
     *
     * Authentication is asynchronous, and a blocking UI is displayed while the
     * request is being processed, disallowing all user actions. If the connected
     * Github account is a new user, app redirects to sign-up page to complete
     * the registration process.
     */
    @Throws(NullPointerException::class)
    private suspend fun signInWithGithub() {
        val authResult = AuthBO.signInWithGithub(this@SignInActivity)
        val user = authResult.user!!
        val userInfo = authResult.additionalUserInfo!!
        if (userInfo.isNewUser) {
            signUpWithGithub(user.uid, userInfo.profile!!, authResult.credential!!)
        } else {
            onAuthenticated(user)
        }
    }

    /**
     * Opens the sign-up screen.
     */
    private fun signUp() {
        startActivity(Intent(this, SignUpActivity::class.java))
    }

    /**
     * Finishes the Github sign-up process.
     *
     * Opens the sign-up screen where any missing details are obtained from
     * the user before completing sign-up.
     */
    private fun signUpWithGithub(uid: String, userInfo: Map<String, Any>, credential: AuthCredential) {
        val newUser = UserAccount().apply {
            id = uid
            email = userInfo["email"].toString()
            name = userInfo["name"].toString()

            bio = userInfo.getOrDefault("bio", "").toString()
            blog = userInfo.getOrDefault("blog", "").toString()
            github = userInfo.getOrDefault("html_url", "").toString()
            location = userInfo.getOrDefault("location", "").toString()
        }
        startActivity(Intent(this, SignUpActivity::class.java).apply {
            putExtra(MyApplication.EXTRA_USER, newUser)
            putExtra(MyApplication.EXTRA_CREDENTIALS, credential)
        })
    }

    /**
     * Callback for when an error occurs during the sign-in process.
     *
     * This method is called if authentication fails or user's details
     * could not be fetched from the database.
     */
    private fun onFailure(ex: Exception) {
        errorMessage.text = when (ex) {
            is FirebaseAuthInvalidUserException,
            is FirebaseAuthInvalidCredentialsException,
            -> "Invalid email/password combination. Please check your credentials before retrying."
            is FirebaseAuthUserCollisionException ->
                "An account already exists with same email address but is not connected " +
                        "to GitHub. Sign in using email/password and connect GitHub in account settings."
            is NullPointerException -> getString(R.string.status_sign_in_failed)
            else -> "${ex.javaClass.simpleName}: ${ex.message}"
        }
        errorSection.visibility = View.VISIBLE
        auth.signOut()
    }

    /**
     * An enumeration of the supported sign-in methods.
     */
    private enum class SignInMethod {
        DEFAULT,
        CREDENTIAL,
        GITHUB,
    }

}