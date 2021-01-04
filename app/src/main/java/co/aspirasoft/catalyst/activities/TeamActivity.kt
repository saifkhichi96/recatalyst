package co.aspirasoft.catalyst.activities

import android.os.Bundle
import android.view.View
import co.aspirasoft.catalyst.MyApplication
import co.aspirasoft.catalyst.R
import co.aspirasoft.catalyst.activities.abs.DashboardChildActivity
import co.aspirasoft.catalyst.adapters.UserAdapter
import co.aspirasoft.catalyst.dao.AccountsDao
import co.aspirasoft.catalyst.dao.TeamDao
import co.aspirasoft.catalyst.databinding.ActivityListBinding
import co.aspirasoft.catalyst.dialogs.InviteCollaboratorDialog
import co.aspirasoft.catalyst.models.Project
import co.aspirasoft.catalyst.models.Team
import co.aspirasoft.catalyst.models.UserAccount
import co.aspirasoft.util.ViewUtils

/**
 * The [Team] page of a project.
 *
 * Team details, including a list of team members, is shown here.
 * The team admins can also manage the team here, which includes
 * adding/removing team members.
 *
 * @property binding The bindings to XML views.
 * @property team The team to display.
 * @property membersAdapter A [UserAdapter] to show details of team members.
 * @property members List of team members.
 * @property isEditable True if owner is viewing team, else false.
 *
 * @author saifkhichi96
 * @since 1.0.0
 */
class TeamActivity : DashboardChildActivity() {

    private lateinit var binding: ActivityListBinding
    private lateinit var team: Team
    private lateinit var membersAdapter: UserAdapter
    private val members = ArrayList<UserAccount>()
    private var isEditable: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val project = intent.getSerializableExtra(MyApplication.EXTRA_PROJECT) as Project? ?: return finish()
        team = Team(project.name, project.ownerId)
        isEditable = project.ownerId == currentUser.id
        if (isEditable) {
            binding.addButton.visibility = View.VISIBLE
        }

        membersAdapter = UserAdapter(this, members)
        binding.contentList.adapter = membersAdapter

        binding.addButton.setOnClickListener { inviteCollaborator() }
    }

    /**
     * Called on activity start.
     *
     * Requests a list of [team] members from server.
     *
     * @param currentUser The [UserAccount] of the signed-in user.
     */
    override fun updateUI(currentUser: UserAccount) {
        members.clear()
        TeamDao.getTeamMembers(team) { members ->
            members.forEach { uid -> onMemberIdReceived(uid) }
        }
    }

    /**
     * Called when a member id is received.
     *
     * Gets details of that member in background.
     *
     * @param uid The user id of the team member.
     */
    private fun onMemberIdReceived(uid: String) {
        AccountsDao.getById(uid) { account ->
            account?.let { onMemberDetailsReceived(it) }
        }
    }

    /**
     * Called when details of a team member are received.
     *
     * Shows the user in team members list on screen.
     *
     * @param member The [UserAccount] of the team member.
     */
    private fun onMemberDetailsReceived(member: UserAccount) {
        if (!members.contains(member)) {
            members.add(member)
            membersAdapter.notifyDataSetChanged()
        }
    }

    /**
     * Invites a collaborator.
     */
    private fun inviteCollaborator() {
        InviteCollaboratorDialog(this, supportFragmentManager, team, membersAdapter).apply {
            this.onInviteSuccess = { onInviteSuccess(it) }
            this.onInviteFailed = { onInviteFailed(it) }
        }.show()
    }

    /**
     * Called when invitation fails to send.
     *
     * @param ex: The error which caused the failure.
     */
    private fun onInviteFailed(ex: Exception) {
        ViewUtils.showError(
            binding.addButton, when (ex) {
                is IllegalArgumentException -> getString(R.string.invite_user_error)
                is IllegalStateException -> getString(R.string.invite_conflict)
                else -> ex.message ?: getString(R.string.invite_error)
            }
        )
    }

    /**
     * Called when invitation is successful.
     *
     * @param account The details of the invited user.
     */
    private fun onInviteSuccess(account: UserAccount) {
        ViewUtils.showMessage(binding.addButton, getString(R.string.invite_sent))
        // TODO: Show user in sent invitations list
    }

}