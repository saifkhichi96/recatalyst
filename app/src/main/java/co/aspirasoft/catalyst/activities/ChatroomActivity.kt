package co.aspirasoft.catalyst.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.lifecycleScope
import co.aspirasoft.catalyst.MyApplication
import co.aspirasoft.catalyst.R
import co.aspirasoft.catalyst.activities.abs.DashboardChildActivity
import co.aspirasoft.catalyst.adapters.MessageAdapter
import co.aspirasoft.catalyst.dao.ChatroomDao
import co.aspirasoft.catalyst.databinding.ActivityChatroomBinding
import co.aspirasoft.catalyst.models.Message
import co.aspirasoft.catalyst.models.Project
import co.aspirasoft.catalyst.models.UserAccount
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

/**
 * The chat page for a [Project] team.
 *
 * Project members who belong to the same team talk to each other on this screen.
 *
 * @property binding The bindings to XML views.
 * @property adapter A [MessageAdapter] for showing the messages.
 * @property project The [Project] to which this chatroom belongs.
 * @property messages List of all messages in the chatroom.
 *
 * @author saifkhichi96
 * @since 1.0.0
 */
class ChatroomActivity : DashboardChildActivity() {

    private lateinit var binding: ActivityChatroomBinding
    private lateinit var adapter: MessageAdapter
    private lateinit var project: Project
    private val messages = ArrayList<Message>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatroomBinding.inflate(layoutInflater)
        setContentView(binding.root)

        project = intent.getSerializableExtra(MyApplication.EXTRA_PROJECT) as Project? ?: return finish()
        adapter = MessageAdapter(this, messages, currentUser)
    }

    override fun updateUI(currentUser: UserAccount) {
        // Start observing chatroom for new messages
        ChatroomDao.observeChatroom(project) { onMessageReceived(it) }
        binding.messagesList.adapter = adapter

        // Move keyboard focus to input field
        binding.messageInput.requestFocus()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.chatroom_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> false
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun onMessageReceived(message: Message) {
        if (!messages.contains(message)) {
            messages.add(message)
            adapter.notifyDataSetChanged()
        }
    }

    fun sendMessage(v: View) {
        val messageContent = binding.messageInput.text.toString().trim()
        if (messageContent.isNotBlank()) {
            lifecycleScope.launch {
                try {
                    val message = Message(messageContent, currentUser.id)

                    ChatroomDao.add(project, message)
                    binding.messageInput.setText("")
                } catch (ex: Exception) {
                    Snackbar.make(
                        binding.messagesList,
                        ex.message ?: getString(R.string.send_message_failed),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

}