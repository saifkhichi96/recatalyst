package co.aspirasoft.catalyst.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import co.aspirasoft.adapter.ModelViewAdapter
import co.aspirasoft.catalyst.MyApplication
import co.aspirasoft.catalyst.R
import co.aspirasoft.catalyst.activities.abs.DashboardChildActivity
import co.aspirasoft.catalyst.bo.TeamInviteBO
import co.aspirasoft.catalyst.dao.AccountsDao
import co.aspirasoft.catalyst.dao.TeamDao
import co.aspirasoft.catalyst.models.Project
import co.aspirasoft.catalyst.models.Team
import co.aspirasoft.catalyst.models.UserAccount
import co.aspirasoft.catalyst.views.UserView
import co.aspirasoft.util.InputUtils.isEmail
import co.aspirasoft.util.ViewUtils
import co.aspirasoft.view.SingleInputForm
import kotlinx.android.synthetic.main.activity_list.*
import kotlinx.coroutines.launch

class TeamActivity : DashboardChildActivity() {

    private lateinit var project: Project

    private lateinit var memberAdapter: UserAdapter
    private val members = ArrayList<UserAccount>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        project = intent.getSerializableExtra(MyApplication.EXTRA_PROJECT) as Project? ?: return finish()

        memberAdapter = UserAdapter(this, members)
        contentList.adapter = memberAdapter

        addButton.setOnClickListener { onAddMemberClicked() }
    }

    override fun updateUI(currentUser: UserAccount) {
        this.members.clear()
        TeamDao.getMembers(project) {
            it.forEach { uid ->
                AccountsDao.getById(uid) { account ->
                    account?.let {
                        members.add(it)
                        memberAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    private fun onAddMemberClicked() {
        val dialog = SingleInputForm.newInstance(
                title = getString(R.string.invite_team_member),
                hint = "Recipient's Email Address",
                inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS,
                okButton = getString(R.string.label_invite)
        )
        dialog.setOnSubmitListener {
            // Validate input
            val email = it.trim()
            if (!email.isEmail()) {
                ViewUtils.showError(addButton, getString(R.string.error_invalid_email))
                return@setOnSubmitListener
            }

            // Disable any further inputs
            dialog.setEnabled(false)

            // Look for an account with given email
            AccountsDao.getByEmail(email) { account ->
                // Show an error if no account with given email
                if (account == null) {
                    ViewUtils.showError(addButton, getString(R.string.error_no_such_user))
                    dialog.setEnabled(true)
                    return@getByEmail
                }

                // Try sending the invite
                lifecycleScope.launch {
                    try {
                        // Send the invite
                        TeamInviteBO.send(account.id, Team(project.name, project.ownerId))

                        // Show success message and dismiss the dialog
                        ViewUtils.showMessage(addButton, getString(R.string.status_invitation_sent))
                        dialog.dismiss()
                    }

                    // If sending fails, show an error message and re-enable input (to allow retry)
                    catch (ex: Exception) {
                        ViewUtils.showError(addButton, when (ex) {
                            is IllegalArgumentException -> "Cannot invite this user."
                            is IllegalStateException -> "User already invited."
                            else -> ex.message ?: "Error sending the invite."
                        })
                        dialog.setEnabled(true)
                    }
                }
            }
        }
        dialog.onDismissListener = { memberAdapter.notifyDataSetChanged() }
        dialog.show(supportFragmentManager, dialog.toString())
    }

    private inner class UserAdapter(context: Context, val users: List<UserAccount>)
        : ModelViewAdapter<UserAccount>(context, users, UserView::class) {

        override fun notifyDataSetChanged() {
            users.sortedBy { it.name }
            super.notifyDataSetChanged()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val v = super.getView(position, convertView, parent)
            v.setOnClickListener {
                startSecurely(ProfileActivity::class.java, Intent().apply {
                    putExtra(MyApplication.EXTRA_PROFILE_USER, users[position])
                })
            }
            return v
        }

    }

}