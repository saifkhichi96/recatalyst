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
import co.aspirasoft.catalyst.fragments.CreateAccountStep
import co.aspirasoft.catalyst.fragments.CreatePasswordStep
import co.aspirasoft.catalyst.fragments.IntroductionStep
import co.aspirasoft.catalyst.models.UserAccount
import co.aspirasoft.util.ViewUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.coroutines.launch


/**
 * This activity allows a user to start or complete their registration.
 *
 * Purpose of this activity is to let users create new accounts.
 *
 * @author saifkhichi96
 * @since 1.0.0
 */
class SignUpActivity : AppCompatActivity() {


    /**
     * Overrides the onCreate activity lifecycle method.
     *
     * Sign up parameters are received and checked here. Account creation
     * only proceeds if all parameters are correct.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        // Set up views
        wizardView.setupWithWizardSteps(
                supportFragmentManager,
                listOf(
                        CreateAccountStep(),
                        IntroductionStep(),
                        CreatePasswordStep(),
                )
        )

        wizardView.setOnSubmitListener {
            findViewById<Button>(R.id.nextButton).text = getString(R.string.status_signing_up)
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
        val email = data[R.id.emailField, ""].toString()
        val name = data[R.id.nameField, ""].toString()
        val password = data[R.id.passwordField, ""].toString()

        UserAccount().apply {
            this.name = name
            this.email = email
            this.password = password

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
                AccountsBO.registerAccount(user)
                onSuccess()
            } catch (ex: Exception) {
                onFailure(ex.message ?: getString(R.string.status_sign_up_failed))
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
        findViewById<Button>(R.id.nextButton).text = getString(R.string.label_create_account)
        findViewById<Button>(R.id.nextButton).isEnabled = true
    }

    /**
     * Called when the sign up flow is completed successfully.
     *
     * Redirects to [SignInActivity].
     */
    private fun onSuccess() {
        val i = Intent(this, SignInActivity::class.java)
        i.putExtra(MyApplication.EXTRA_NEW_SIGN_UP, true)
        startActivity(i)
        finish()
    }

    /**
     * Overrides back button behaviour.
     *
     * Asks for a confirmation before closing the activity and canceling sign up.
     */
    override fun onBackPressed() {
        if (!wizardView.onBackPressed()) {
            MaterialAlertDialogBuilder(this)
                    .setMessage(getString(R.string.confirm_cancel_sign_up))
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

}