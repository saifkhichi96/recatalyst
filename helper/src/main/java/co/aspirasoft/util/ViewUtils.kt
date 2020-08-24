package co.aspirasoft.util

import android.app.Activity
import android.graphics.Color
import android.view.View
import android.view.inputmethod.InputMethodManager
import co.aspirasoft.R
import com.google.android.material.snackbar.Snackbar


object ViewUtils {

    fun Activity.hideKeyboard() {
        val imm: InputMethodManager = this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        var view = this.currentFocus
        if (view == null) {
            view = View(this)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun showError(v: View, message: String) {
        Snackbar.make(v, message, Snackbar.LENGTH_LONG)
                .setBackgroundTint(v.context.getColor(R.color.colorWarning))
                .setTextColor(Color.WHITE)
                .show()
    }

    fun showMessage(v: View, message: String) {
        Snackbar.make(v, message, Snackbar.LENGTH_SHORT).show()
    }

}