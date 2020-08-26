package co.aspirasoft.catalyst.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import co.aspirasoft.catalyst.R
import co.aspirasoft.util.InputUtils.isEmail
import co.aspirasoft.util.InputUtils.isNotBlank
import co.aspirasoft.util.InputUtils.markRequired
import co.aspirasoft.util.InputUtils.showError
import co.aspirasoft.view.WizardViewStep
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

/**
 * First step of the sign up process.
 *
 * In this step, new users provide their email address. This fragment validates
 * the email address before letting the user navigate to the next step.
 *
 * @author saifkhichi96
 * @since 1.0.0
 */
class CreateAccountStep : WizardViewStep("") {

    private lateinit var emailField: TextInputEditText

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.signup_step_create_account, container, false)

        // Get UI references
        emailField = v.findViewById(R.id.emailField)

        // Mark required fields
        (emailField.parent.parent as TextInputLayout).markRequired()

        // Validate email as it is typed
        emailField.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                emailField.showError(null)
                if (!s.toString().trim().isEmail()) {
                    emailField.showError(getString(R.string.error_invalid_email))
                }
            }
        })

        return v
    }

    override fun isDataValid(): Boolean {
        return if (emailField.isNotBlank(true)) {
            val email = emailField.text.toString().trim()
            if (!email.isEmail()) {
                emailField.showError(getString(R.string.error_invalid_email))
                false
            } else {
                data.put(R.id.emailField, email)
                true
            }
        } else false
    }

}