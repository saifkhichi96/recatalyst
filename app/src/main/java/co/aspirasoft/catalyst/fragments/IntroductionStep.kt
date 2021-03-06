package co.aspirasoft.catalyst.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import co.aspirasoft.catalyst.R
import co.aspirasoft.catalyst.databinding.SignupStepIntroductionBinding
import co.aspirasoft.util.InputUtils.isNotBlank
import co.aspirasoft.util.InputUtils.markRequired
import co.aspirasoft.view.WizardViewStep
import com.google.android.material.textfield.TextInputLayout

/**
 * Second step of the sign up process.
 *
 * In this step, users introduce themselves by providing their personal details.
 * Input is validated before letting the user navigate to the next step.
 *
 * @author saifkhichi96
 * @since 1.0.0
 */
class IntroductionStep : WizardViewStep("") {

    private var _binding: SignupStepIntroductionBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = SignupStepIntroductionBinding.inflate(inflater, container, false)
        val view = binding.root

        (binding.nameField.parent.parent as TextInputLayout).markRequired()

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun isDataValid(): Boolean {
        return if (binding.nameField.isNotBlank(true)) {
            val name = binding.nameField.text.toString().trim()
            data.put(R.id.nameField, name)
            true
        } else false
    }

}