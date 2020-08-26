package co.aspirasoft.catalyst.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import co.aspirasoft.catalyst.R
import co.aspirasoft.util.InputUtils.isNotBlank
import co.aspirasoft.util.InputUtils.markRequired
import co.aspirasoft.view.WizardViewStep
import com.google.android.material.textfield.TextInputEditText
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

    private lateinit var nameField: TextInputEditText

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.signup_step_introduction, container, false)

        nameField = v.findViewById(R.id.nameField)
        (nameField.parent.parent as TextInputLayout).markRequired()

        return v
    }

    override fun isDataValid(): Boolean {
        return if (nameField.isNotBlank(true)) {
            val name = nameField.text.toString().trim()
            data.put(R.id.nameField, name)
            true
        } else false
    }

}