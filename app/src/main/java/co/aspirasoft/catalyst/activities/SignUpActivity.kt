package co.aspirasoft.catalyst.activities

import android.content.Intent
import android.os.Bundle
import android.util.SparseArray
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import co.aspirasoft.catalyst.MyApplication
import co.aspirasoft.catalyst.R
import co.aspirasoft.catalyst.bo.AccountsBO
import co.aspirasoft.catalyst.bo.AuthBO
import co.aspirasoft.catalyst.fragments.CreateAccountStep
import co.aspirasoft.catalyst.fragments.CreatePasswordStep
import co.aspirasoft.catalyst.fragments.IntroductionStep
import co.aspirasoft.catalyst.models.UserAccount
import co.aspirasoft.util.ViewUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.coroutines.launch


/**
 * This activity allows a user to start or complete their registration.
 *
 * Purpose of this activity is to let users create new accounts.
 *
 * @property signUpCompleted Says whether the sign-up process was completed successfully or not.
 * @property linkUser An (optionally) existing user to link this new account with. Default is null.
 * @property linkCredential (Optional) login credentials of the existing user. Default is null.
 *
 * @author saifkhichi96
 * @since 1.0.0
 */
class SignUpActivity : AppCompatActivity() {

    private var signUpCompleted = false
    private var linkUser: UserAccount? = null
    private var linkCredential: AuthCredential? = null

    /**
     * Overrides the onCreate activity lifecycle method.
     *
     * Sign up parameters are received and checked here. Account creation
     * only proceeds if all parameters are correct.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        // Get intent extras
        val i = intent
        linkUser = i.getSerializableExtra(MyApplication.EXTRA_USER) as UserAccount?
        linkCredential = i.getParcelableExtra(MyApplication.EXTRA_CREDENTIALS)

        // Set up views
        wizardView.setupWithWizardSteps(
                supportFragmentManager,
                when {
                    // case: new sign up
                    linkUser == null || linkCredential == null -> {
                        linkUser = null
                        linkCredential = null
                        listOf(
                                CreateAccountStep(),
                                IntroductionStep(),
                                CreatePasswordStep()
                        )
                    }
                    // case: continue sign up with Github
                    else -> listOf(CreatePasswordStep())
                }
        )

        wizardView.setOnSubmitListener {
            findViewById<Button>(R.id.nextButton).text = getString(R.string.signing_in)
            findViewById<Button>(R.id.nextButton).isEnabled = false
            onSubmit(it)
        }
    }

    /**
     * Callback for when the sign up form is submitted.
     *
     * This method retrieves form data and creates a valid instance of
     * a [UserAccount] subclass representing the current user.
     */
    private fun onSubmit(data: SparseArray<Any>) {
        val newUser = if (linkUser != null) linkUser!! else UserAccount()
        newUser.apply {
            this.name = data[R.id.nameField, linkUser?.name ?: ""].toString()
            this.email = data[R.id.emailField, linkUser?.email ?: ""].toString()
            this.password = data[R.id.passwordField, ""].toString()

            onUserCreated(this)
        }
    }

    /**
     * Callback for when a valid user has been created.
     *
     * This method performs the actual sign up task.
     */
    private fun onUserCreated(user: UserAccount) {
        lifecycleScope.launch {
            try {
                val credential = EmailAuthProvider.getCredential(user.email, user.password)
                AccountsBO.registerAccount(user, AuthBO.currentUser)
                signUpCompleted = true
                onSuccess(credential)
            } catch (ex: Exception) {
                signUpCompleted = false
                onFailure(ex.message ?: getString(R.string.sign_up_failed))
            }
        }
    }

    /**
     * Called when registration cannot be completed.
     *
     * An error message is displayed.
     *
     * @param error An (optional) description of cause of failure.
     */
    private fun onFailure(error: String) {
        ViewUtils.showError(wizardView, error)
        findViewById<Button>(R.id.nextButton).text = getString(R.string.create_account)
        findViewById<Button>(R.id.nextButton).isEnabled = true
    }

    /**
     * Called when the sign up flow is completed successfully.
     *
     * Redirects to [SignInActivity], which automatically signs in the
     * new user.
     *
     * @param credential The sign-in credentials of the new account.
     */
    private fun onSuccess(credential: AuthCredential) {
        val i = Intent(this, SignInActivity::class.java)
        i.putExtra(MyApplication.EXTRA_NEW_SIGN_UP, credential)
        startActivity(i)
        finish()
    }

    /**
     * Overrides back button behavior.
     *
     * Asks for a confirmation before closing the activity and canceling sign up.
     */
    override fun onBackPressed() {
        if (!wizardView.onBackPressed()) {
            MaterialAlertDialogBuilder(this)
                    .setMessage(getString(R.string.sign_up_cancel_confirm))
                    .setPositiveButton(android.R.string.yes) { dialog, _ ->
                        super.onBackPressed()
                        dialog.dismiss()
                    }
                    .setNegativeButton(android.R.string.no) { dialog, _ ->
                        dialog.cancel()
                    }
                    .show()
        }
    }

    /**
     * Overrides activity destroy behavior.
     *
     * Before destroying the activity, any partially-created accounts are
     * deleted if the sign-up process was not completed successfully.
     */
    override fun onDestroy() {
        if (!signUpCompleted && linkCredential != null) {
            lifecycleScope.launch {
                AccountsBO.deleteAccount(linkCredential!!)
            }
        }
        super.onDestroy()
    }

}