package co.aspirasoft.catalyst.adapters

import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import co.aspirasoft.adapter.ModelViewAdapter
import co.aspirasoft.catalyst.activities.abs.SecureActivity
import co.aspirasoft.catalyst.bo.TeamInviteBO
import co.aspirasoft.catalyst.models.TeamInvite
import co.aspirasoft.catalyst.views.IncomingInviteView
import kotlinx.coroutines.launch

class IncomingInviteAdapter(
    private val activity: SecureActivity,
    private val invites: List<TeamInvite>,
) : ModelViewAdapter<TeamInvite>(activity, invites, IncomingInviteView::class) {

    override fun notifyDataSetChanged() {
        invites.sortedBy { it.id }
        super.notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val v = super.getView(position, convertView, parent)
        val invite = invites[position]
        (v as IncomingInviteView).apply {
            setOnAcceptListener { activity.lifecycleScope.launch { TeamInviteBO.accept(invite) } }
            setOnRejectListener { activity.lifecycleScope.launch { TeamInviteBO.reject(invite) } }
        }
        return v
    }
}