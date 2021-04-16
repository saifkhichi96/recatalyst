package co.aspirasoft.catalyst.adapters

import android.content.Intent
import android.view.View
import android.view.ViewGroup
import co.aspirasoft.adapter.ModelViewAdapter
import co.aspirasoft.catalyst.MyApplication
import co.aspirasoft.catalyst.activities.ProfileActivity
import co.aspirasoft.catalyst.activities.abs.SecureActivity
import co.aspirasoft.catalyst.models.UserAccount
import co.aspirasoft.catalyst.views.UserView

class UserAdapter(private val activity: SecureActivity, private val users: List<UserAccount>) :
    ModelViewAdapter<UserAccount>(activity, users, UserView::class) {

    override fun notifyDataSetChanged() {
        users.sortedBy { it.name }
        super.notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val v = super.getView(position, convertView, parent)
        (v as UserView).apply {
            setOnAvatarClickedListener {
                activity.startSecurely(ProfileActivity::class.java, Intent().apply {
                    putExtra(MyApplication.EXTRA_PROFILE_USER, it)
                })
            }
        }
        return v
    }

}