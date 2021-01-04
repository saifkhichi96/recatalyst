package co.aspirasoft.catalyst.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import co.aspirasoft.catalyst.R
import co.aspirasoft.catalyst.databinding.SignupStepCreateAccountBinding
import co.aspirasoft.util.InputUtils.isEmail
import co.aspirasoft.util.InputUtils.isNotBlank
import co.aspirasoft.util.InputUtils.markRequired
import co.aspirasoft.view.WizardViewStep
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

    private var _binding: SignupStepCreateAccountBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = SignupStepCreateAccountBinding.inflate(inflater, container, false)
        val view = binding.root

        // Mark required fields
        (binding.emailField.parent.parent as TextInputLayout).markRequired()

        // Validate email as it is typed
        binding.emailField.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.emailField.error = null
                if (!s.toString().trim().isEmail()) {
                    binding.emailField.error = getString(R.string.email_error)
                }
            }
        })

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun isDataValid(): Boolean {
        return if (binding.emailField.isNotBlank(true)) {
            val email = binding.emailField.text.toString().trim()
            if (!email.isEmail()) {
                binding.emailField.error = getString(R.string.email_error)
                false
            } else {
                data.put(R.id.emailField, email)
                true
            }
        } else false
    }

}