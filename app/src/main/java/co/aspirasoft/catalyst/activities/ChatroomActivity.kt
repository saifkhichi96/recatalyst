package co.aspirasoft.catalyst.activities

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import co.aspirasoft.adapter.ModelViewAdapter
import co.aspirasoft.catalyst.MyApplication
import co.aspirasoft.catalyst.R
import co.aspirasoft.catalyst.activities.abs.DashboardChildActivity
import co.aspirasoft.catalyst.dao.ChatroomDao
import co.aspirasoft.catalyst.models.Message
import co.aspirasoft.catalyst.models.Project
import co.aspirasoft.catalyst.models.UserAccount
import co.aspirasoft.catalyst.views.MessageView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_chatroom.*

class ChatroomActivity : DashboardChildActivity() {

    private val messages = ArrayList<Message>()
    private lateinit var messageAdapter: MessageAdapter

    private lateinit var project: Project

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatroom)
        messageInput.requestFocus()

        project = intent.getSerializableExtra(MyApplication.EXTRA_PROJECT) as Project? ?: return finish()
        messageAdapter = MessageAdapter(this, messages)
        ChatroomDao.getMessagesByProject(project) {
            if (!messages.contains(it)) {
                messages.add(it)
                messageAdapter.notifyDataSetChanged()
            }
        }

        sendMessage.setOnClickListener { onSendMessageClicked() }
    }

    override fun updateUI(currentUser: UserAccount) {
        messagesList.adapter = messageAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.chatroom_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun onSendMessageClicked() {
        val messageContent = messageInput.text.toString().trim()
        if (messageContent.isNotBlank()) {
            try {
                val message = Message(messageContent, currentUser.id)
                ChatroomDao.add(project, message) {
                    if (!it.isSuccessful) {
                        Snackbar.make(messagesList,
                                it.exception?.message ?: getString(R.string.status_message_not_sent),
                                Snackbar.LENGTH_LONG).show()
                    } else {
                        messageInput.setText("")
                    }
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

    }

    inner class MessageAdapter(context: Context, private val messages: MutableList<Message>)
        : ModelViewAdapter<Message>(context, messages, MessageView::class) {

        override fun notifyDataSetChanged() {
            messages.sortBy { it.timestamp }
            super.notifyDataSetChanged()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val v = super.getView(position, convertView, parent)

            val curr = messages[position]
            curr.incoming = curr.sender != currentUser.id
            curr.notifyObservers()

            if (position > 0) {
                val prev = messages[position - 1]
                if (curr.timestamp.time - prev.timestamp.time < 30 * 60 * 1000) {
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

}