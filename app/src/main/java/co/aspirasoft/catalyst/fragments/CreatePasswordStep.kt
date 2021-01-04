package co.aspirasoft.catalyst.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import co.aspirasoft.catalyst.R
import co.aspirasoft.catalyst.databinding.SignupStepCreatePasswordBinding
import co.aspirasoft.util.InputUtils.isNotBlank
import co.aspirasoft.util.InputUtils.markRequired
import co.aspirasoft.view.WizardViewStep
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

    private var _binding: SignupStepCreatePasswordBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = SignupStepCreatePasswordBinding.inflate(inflater, container, false)
        val view = binding.root

        // Mark required fields
        (binding.passwordField.parent.parent as TextInputLayout).markRequired()
        (binding.passwordRepeatField.parent.parent as TextInputLayout).markRequired()

        // Validate passwords as they are typed
        binding.passwordField.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.passwordField.isNotBlank(true)
            }
        })
        binding.passwordRepeatField.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.passwordRepeatField.isNotBlank(true)
                checkPasswordValid()
            }
        })

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun checkPasswordValid(): Boolean {
        binding.passwordField.error = null
        if (binding.passwordField.text.isNullOrBlank() ||
            binding.passwordField.text.toString() != binding.passwordRepeatField.text.toString()
        ) {
            binding.passwordField.error = getString(R.string.password_mismatch_error)
            return false
        }

        return true
    }

    override fun isDataValid(): Boolean {
        return if (binding.passwordField.isNotBlank(true) &&
            binding.passwordRepeatField.isNotBlank(true) &&
            checkPasswordValid()
        ) {
            val password = binding.passwordField.text.toString().trim()
            data.put(R.id.passwordField, password)
            true
        } else false
    }

}