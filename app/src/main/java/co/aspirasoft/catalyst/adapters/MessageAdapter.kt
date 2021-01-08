package co.aspirasoft.catalyst.adapters

import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import co.aspirasoft.adapter.ModelViewAdapter
import co.aspirasoft.catalyst.models.Message
import co.aspirasoft.catalyst.models.UserAccount
import co.aspirasoft.catalyst.views.MessageView

class MessageAdapter(
    context: AppCompatActivity,
    private val messages: MutableList<Message>,
    private val currentUser: UserAccount,
) : ModelViewAdapter<Message>(context, messages, MessageView::class) {

    override fun notifyDataSetChanged() {
        messages.sortBy { it.timestamp }
        super.notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val v = super.getView(position, convertView, parent)

        val curr = messages[position]
        curr.isIncoming = curr.sender != currentUser.id
        curr.notifyObservers()

        if (position > 0) {
            val prev = messages[position - 1]
            if (curr.timestamp - prev.timestamp < 30 * 60 * 1000) {
                (v as MessageView).hideTimestamp()
                if (prev.sender == curr.sender) {
                    v.hideSenderName()
                    if (curr.sender != currentUser.id) {
                        v.hideSenderAvatar()
                    }
                }
            }
        }

        return v
    }

    override fun isEnabled(position: Int): Boolean {
        return false
    }
}