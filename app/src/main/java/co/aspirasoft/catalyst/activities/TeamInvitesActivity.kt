package co.aspirasoft.catalyst.activities

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import co.aspirasoft.adapter.ModelViewAdapter
import co.aspirasoft.catalyst.R
import co.aspirasoft.catalyst.activities.abs.DashboardChildActivity
import co.aspirasoft.catalyst.bo.TeamInviteBO
import co.aspirasoft.catalyst.dao.TeamInviteDao
import co.aspirasoft.catalyst.models.TeamInvite
import co.aspirasoft.catalyst.models.UserAccount
import co.aspirasoft.catalyst.views.IncomingInviteView
import kotlinx.android.synthetic.main.activity_list.*
import kotlinx.coroutines.launch

class TeamInvitesActivity : DashboardChildActivity() {

    private lateinit var inviteAdapter: IncomingInviteAdapter
    private val invites = ArrayList<TeamInvite>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        inviteAdapter = IncomingInviteAdapter(this, invites)
        contentList.adapter = inviteAdapter
    }

    override fun updateUI(currentUser: UserAccount) {
        TeamInviteDao.getReceivedInvites(currentUser.id) {
            invites.clear()
            invites.addAll(it)
            inviteAdapter.notifyDataSetChanged()
        }
    }

    private inner class IncomingInviteAdapter(context: Context, val invites: List<TeamInvite>)
        : ModelViewAdapter<TeamInvite>(context, invites, IncomingInviteView::class) {

        override fun notifyDataSetChanged() {
            invites.sortedBy { it.id }
            super.notifyDataSetChanged()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val v = super.getView(position, convertView, parent)
            val invite = invites[position]
            (v as IncomingInviteView).apply {
                setOnAcceptListener {
                    lifecycleScope.launch { TeamInviteBO.accept(invite) }
                }
                setOnRejectListener {
                    lifecycleScope.launch { TeamInviteBO.reject(invite) }
                }
            }
            return v
        }
    }

}