package co.aspirasoft.catalyst.activities

import android.os.Bundle
import co.aspirasoft.catalyst.activities.abs.DashboardChildActivity
import co.aspirasoft.catalyst.adapters.IncomingInviteAdapter
import co.aspirasoft.catalyst.dao.TeamDao
import co.aspirasoft.catalyst.databinding.ActivityListBinding
import co.aspirasoft.catalyst.models.TeamInvite
import co.aspirasoft.catalyst.models.UserAccount

/**
 *
 */
class TeamInvitesActivity : DashboardChildActivity() {

    private lateinit var binding: ActivityListBinding

    private lateinit var inviteAdapter: IncomingInviteAdapter
    private val invites = ArrayList<TeamInvite>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        inviteAdapter = IncomingInviteAdapter(this, invites)
        binding.contentList.adapter = inviteAdapter
    }

    override fun updateUI(currentUser: UserAccount) {
        TeamDao.getReceivedInvites(currentUser.id) {
            invites.clear()
            invites.addAll(it)
            inviteAdapter.notifyDataSetChanged()
        }
    }

}