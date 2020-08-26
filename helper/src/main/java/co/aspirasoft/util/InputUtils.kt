package co.aspirasoft.util

import androidx.core.text.HtmlCompat
import co.aspirasoft.R
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.util.regex.Matcher
import java.util.regex.Pattern

object InputUtils {

    /**
     * Returns `true` if this [TextInputEditText] contains some valid non-whitespace characters.
     *
     * If [isErrorEnabled] is `true`, an appropriate error message is generated.
     */
    fun TextInputEditText.isNotBlank(isErrorEnabled: Boolean = false): Boolean {
        this.showError(null)
        return if (this.text.isNullOrBlank()) {
            if (isErrorEnabled) this.showError("This field is required.")
            false
        } else true
    }

    /**
     * Shows the [error] message in an appropriate format.
     */
    fun TextInputEditText.showError(error: String?) {
        try {
            val wrapper = this.parent.parent as TextInputLayout
            wrapper.isErrorEnabled = error != null
            wrapper.error = error
        } catch (ignored: Exception) {
            error?.let { Snackbar.make(this, it, Snackbar.LENGTH_LONG).show() }
        }
    }

    /**
     * Method is used for checking valid email address format.
     *
     * @return boolean true for valid false for invalid
     */
    fun String.isEmail(): Boolean {
        val expression = "^[\\w.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"
        val pattern: Pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
        val matcher: Matcher = pattern.matcher(this)
        return matcher.matches()
    }

    /**
     * Appends a red asterisk to the [TextInputLayout]'s hint to mark it as required.
     */
    fun TextInputLayout.markRequired() {
        this.hint = HtmlCompat.fromHtml(
                "${this.hint} ${context.getString(R.string.required_asterisk)}",
                HtmlCompat.FROM_HTML_MODE_COMPACT
        )
    }

}