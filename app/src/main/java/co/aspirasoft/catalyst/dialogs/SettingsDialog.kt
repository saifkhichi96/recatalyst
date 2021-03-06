package co.aspirasoft.catalyst.dialogs

import android.app.Dialog
import android.os.Bundle
import co.aspirasoft.catalyst.activities.abs.SecureActivity
import co.aspirasoft.catalyst.databinding.DialogSettingsBinding

/**
 * AccountSwitcher allows switching between user accounts.
 *
 * This dialog box shows information of currently signed in user, and
 * allows users to sign out, along with some other high level controls.
 * It follows Google's material account switcher design.
 *
 * @author saifkhichi96
 * @since 1.0.0
 */
class SettingsDialog private constructor(private val activity: SecureActivity) : Dialog(activity) {

    private lateinit var binding: DialogSettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window?.setBackgroundDrawableResource(android.R.color.transparent)

        // Show sign out confirmation when sign out button is clicked
        binding.signOutButton.setOnClickListener {
            LogoutConfirmationDialog.show(activity)
            dismiss()
        }

        // TODO: Handle clicks on `Privacy` and `ToS` buttons.
        binding.privacyButton.setOnClickListener {

        }

        binding.tosButton.setOnClickListener {

        }
    }

    /**
     * Builder to create an [SettingsDialog] dialog box.
     *
     * @constructor Creates a new AccountSwitcher Builder.
     * @param context The activity where this will be used.
     *
     * @property dialog The AccountSwitcher instance being built.
     */
    class Builder(context: SecureActivity) {

        private val dialog = SettingsDialog(context)

        /**
         * Returns a new instance of [SettingsDialog] with defined properties.
         */
        fun create(): SettingsDialog {
            return dialog
        }

        /**
         * Shows the [SettingsDialog] dialog box.
         */
        fun show() {
            dialog.show()
        }

    }

}