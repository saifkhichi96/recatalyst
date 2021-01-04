package co.aspirasoft.catalyst.adapters

import android.content.Intent
import android.view.View
import android.view.ViewGroup
import co.aspirasoft.adapter.ModelViewAdapter
import co.aspirasoft.catalyst.MyApplication
import co.aspirasoft.catalyst.activities.ProfileActivity
import co.aspirasoft.catalyst.activities.abs.SecureActivity
import co.aspirasoft.catalyst.models.UserAccount
import co.aspirasoft.catalyst.views.IncomingRequestView

class IncomingRequestAdapter(
    private val activity: SecureActivity,
    private val users: List<UserAccount>,
) : ModelViewAdapter<UserAccount>(activity, users, IncomingRequestView::class) {

    private val MAX_COUNT = 2

    var onAcceptClickListener: ((position: Int) -> Unit)? = null
    var onRejectClickListener: ((position: Int) -> Unit)? = null

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val v = super.getView(position, convertView, parent)
        (v as IncomingRequestView).apply {
            setOnAcceptListener { onAcceptClickListener?.let { it(position) } }
            setOnRejectListener { onRejectClickListener?.let { it(position) } }
            setOnAvatarClickedListener {
                activity.startSecurely(ProfileActivity::class.java, Intent().apply {
                    putExtra(MyApplication.EXTRA_PROFILE_USER, users[position])
                })
            }
        }

        return v
    }

    override fun getCount(): Int {
        return if (super.getCount() > MAX_COUNT) MAX_COUNT else super.getCount()
    }
}