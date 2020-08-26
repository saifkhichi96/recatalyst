package co.aspirasoft.catalyst.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import co.aspirasoft.catalyst.R
import co.aspirasoft.util.InputUtils.isNotBlank
import co.aspirasoft.util.InputUtils.markRequired
import co.aspirasoft.util.InputUtils.showError
import co.aspirasoft.view.WizardViewStep
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

/**
 * Last step of the sign up process.
 *
 * In this step, users create a password to secure their account. Input
 * is validated before submitting the sign up request.
 *
 * @author saifkhichi96
 * @since 1.0.0
 */
class CreatePasswordStep : WizardViewStep("") {

    private lateinit var passwordField: TextInputEditText
    private lateinit var passwordRepeatField: TextInputEditText

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.signup_step_create_password, container, false)

        // Get UI references
        passwordField = v.findViewById(R.id.passwordField)
        passwordRepeatField = v.findViewById(R.id.passwordRepeatField)

        // Mark required fields
        (passwordField.parent.parent as TextInputLayout).markRequired()
        (passwordRepeatField.parent.parent as TextInputLayout).markRequired()

        // Validate passwords as they are typed
        passwordField.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                passwordField.isNotBlank(true)
            }
        })
        passwordRepeatField.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                passwordRepeatField.isNotBlank(true)
                checkPasswordValid()
            }
        })

        return v
    }

    private fun checkPasswordValid(): Boolean {
        passwordField.showError(null)
        if (passwordField.text.isNullOrBlank() || passwordField.text.toString() != passwordRepeatField.text.toString()) {
            passwordField.showError(getString(R.string.error_password_mismatch))
            return false
        }

        return true
    }

    override fun isDataValid(): Boolean {
        return if (passwordField.isNotBlank(true) &&
                passwordRepeatField.isNotBlank(true) &&
                checkPasswordValid()) {
            val password = passwordField.text.toString().trim()
            data.put(R.id.passwordField, password)
            true
        } else false
    }

}