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
import co.aspirasoft.catalyst.databinding.ActivityListBinding
import co.aspirasoft.catalyst.models.Project
import co.aspirasoft.catalyst.models.Team
import co.aspirasoft.catalyst.models.UserAccount
import co.aspirasoft.catalyst.views.UserView
import co.aspirasoft.util.InputUtils.isEmail
import co.aspirasoft.util.ViewUtils
import co.aspirasoft.view.SingleInputForm
import kotlinx.coroutines.launch

class TeamActivity : DashboardChildActivity() {

    private lateinit var binding: ActivityListBinding

    private lateinit var team: Team

    private lateinit var memberAdapter: UserAdapter
    private val members = ArrayList<UserAccount>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val project = intent.getSerializableExtra(MyApplication.EXTRA_PROJECT) as Project? ?: return finish()
        team = Team(project.name, project.ownerId)

        memberAdapter = UserAdapter(this, members)
        binding.contentList.adapter = memberAdapter

        binding.addButton.setOnClickListener { onAddMemberClicked() }
    }

    override fun updateUI(currentUser: UserAccount) {
        this.members.clear()
        TeamDao.getTeamMembers(team) { members ->
            members.forEach { uid ->
                AccountsDao.getById(uid) { account ->
                    account?.let {
                        this.members.add(it)
                        memberAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    private fun onAddMemberClicked() {
        val dialog = SingleInputForm.newInstance(
                title = getString(R.string.invite_team_member),
                hint = getString(R.string.email_recipient),
                inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS,
                okButton = getString(R.string.invite)
        )
        dialog.setOnSubmitListener {
            // Validate input
            val email = it.trim()
            if (!email.isEmail()) {
                ViewUtils.showError(binding.addButton, getString(R.string.email_error))
                return@setOnSubmitListener
            }

            // Disable any further inputs
            dialog.setEnabled(false)

            // Look for an account with given email
            AccountsDao.getByEmail(email) { account ->
                // Show an error if no account with given email
                if (account == null) {
                    ViewUtils.showError(binding.addButton, getString(R.string.no_such_user))
                    dialog.setEnabled(true)
                    return@getByEmail
                }

                // Try sending the invite
                lifecycleScope.launch {
                    try {
                        // Send the invite
                        TeamInviteBO.send(account.id, team)

                        // Show success message and dismiss the dialog
                        ViewUtils.showMessage(binding.addButton, getString(R.string.invite_sent))
                        dialog.dismiss()
                    }

                    // If sending fails, show an error message and re-enable input (to allow retry)
                    catch (ex: Exception) {
                        ViewUtils.showError(
                            binding.addButton, when (ex) {
                                is IllegalArgumentException -> getString(R.string.invite_user_error)
                                is IllegalStateException -> getString(R.string.invite_conflict)
                                else -> ex.message ?: getString(R.string.invite_error)
                            }
                        )
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