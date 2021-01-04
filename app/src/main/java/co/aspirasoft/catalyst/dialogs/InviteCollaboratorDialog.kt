package co.aspirasoft.catalyst.dialogs

import android.text.InputType
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import co.aspirasoft.catalyst.R
import co.aspirasoft.catalyst.activities.abs.SecureActivity
import co.aspirasoft.catalyst.adapters.UserAdapter
import co.aspirasoft.catalyst.bo.TeamInviteBO
import co.aspirasoft.catalyst.dao.AccountsDao
import co.aspirasoft.catalyst.models.Team
import co.aspirasoft.catalyst.models.UserAccount
import co.aspirasoft.util.InputUtils.isEmail
import co.aspirasoft.view.SingleInputForm
import kotlinx.coroutines.launch

class InviteCollaboratorDialog(
    private val activity: SecureActivity,
    private val fm: FragmentManager,
    private val team: Team,
    adapter: UserAdapter,
) {

    private val dialog = SingleInputForm.newInstance(
        title = activity.getString(R.string.invite_team_member),
        hint = activity.getString(R.string.email_recipient),
        inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS,
        okButton = activity.getString(R.string.invite)
    )

    var onInviteSuccess: ((UserAccount) -> Unit)? = null
    var onInviteFailed: ((Exception) -> Unit)? = null

    init {
        dialog.setOnSubmitListener {
            // Validate input
            val email = it.trim()
            if (!email.isEmail()) {
                onInviteFailed?.invoke(Exception(activity.getString(R.string.email_error)))
                return@setOnSubmitListener
            }

            // Disable any further inputs
            dialog.setEnabled(false)
            AccountsDao.getByEmail(email) { account -> inviteCollaborator(account) }
        }
        dialog.onDismissListener = { adapter.notifyDataSetChanged() }
    }

    /**
     * Invites a collaborator.
     *
     * @param account The [UserAccount] of the user to invite.
     */
    private fun inviteCollaborator(account: UserAccount?) = activity.lifecycleScope.launch {
        try {
            // Throw an error if no account with given email
            if (account == null) throw Exception(activity.getString(R.string.no_such_user))

            // Send the invite
            TeamInviteBO.send(account.id, team)
            onInviteSuccess?.invoke(account)
            dialog.dismiss()
        }

        // If sending fails, show an error message and re-enable input (to allow retry)
        catch (ex: Exception) {
            onInviteFailed?.invoke(ex)
            dialog.setEnabled(true)
        }
    }

    fun show() {
        dialog.show(fm, dialog.toString())
    }

}