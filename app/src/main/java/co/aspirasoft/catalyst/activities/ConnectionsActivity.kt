package co.aspirasoft.catalyst.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.lifecycleScope
import co.aspirasoft.catalyst.R
import co.aspirasoft.catalyst.activities.abs.DashboardChildActivity
import co.aspirasoft.catalyst.adapters.IncomingRequestAdapter
import co.aspirasoft.catalyst.adapters.UserAdapter
import co.aspirasoft.catalyst.bo.ConnectionRequestBO
import co.aspirasoft.catalyst.dao.AccountsDao
import co.aspirasoft.catalyst.dao.ConnectionsDao
import co.aspirasoft.catalyst.databinding.ActivityConnectionsBinding
import co.aspirasoft.catalyst.models.UserAccount
import co.aspirasoft.util.InputUtils.isEmail
import co.aspirasoft.util.ViewUtils
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

/**
 * The Connections page of a [UserAccount].
 *
 * Shows the Connections of a user, allows them to Connect to other users,
 * and respond to any Connection Requests from other users.
 *
 * @property binding The bindings to XML views.
 * @property requests List of accounts from whom the user has pending Connection Requests.
 * @property requestsAdapter An [IncomingRequestAdapter] for showing the [requests].
 * @property connections List of accounts who are connected with the user.
 * @property connectionsAdapter A [UserAdapter] for showing the [connections].
 *
 * @author saifkhichi96
 * @since 1.0.0
 */
class ConnectionsActivity : DashboardChildActivity() {

    private lateinit var binding: ActivityConnectionsBinding

    private val requests = ArrayList<UserAccount>()
    private lateinit var requestsAdapter: IncomingRequestAdapter

    private val connections = ArrayList<UserAccount>()
    private lateinit var connectionsAdapter: UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConnectionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.placeholderText.text = getString(R.string.connections_none)
        binding.learnMoreButton.setOnClickListener { openExplanationScreen() }

        requestsAdapter = IncomingRequestAdapter(this, requests)
        requestsAdapter.onAcceptClickListener = { acceptConnectionRequest(it) }
        requestsAdapter.onRejectClickListener = { rejectConnectionRequest(it) }
        binding.requestsList.adapter = requestsAdapter

        connectionsAdapter = UserAdapter(this, connections)
        binding.connectionsList.adapter = connectionsAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_connections, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_learn_more -> {
                openExplanationScreen()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun updateUI(currentUser: UserAccount) {
        // Get pending requests list from database
        ConnectionsDao.getReceivedRequests(currentUser.id) { onRequestsReceived(it) }

        // Get connections list from database
        ConnectionsDao.getUserConnections(currentUser.id) { onConnectionsReceived(it) }
    }

    fun onInviteClicked(v: View) {
        binding.content.post {
            binding.content.smoothScrollTo(0, binding.inviteeEmailInput.bottom)
            binding.inviteeEmailInput.requestFocus()
            val im = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            im.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        }
    }

    fun onSendRequestClicked(v: View) {
        val email = binding.inviteeEmailInput.text.toString().trim()
        // Validate provided email address
        if (!email.isEmail()) {
            ViewUtils.showError(binding.placeholder, getString(R.string.email_error))
            return
        }

        // Send a connection request
        ConnectionRequestBO.send(currentUser.id, email.toLowerCase(Locale.getDefault())) { ex ->
            runOnUiThread { binding.inviteeEmailInput.setText("") }
            if (ex == null) {
                ViewUtils.showMessage(binding.placeholder, getString(R.string.connection_requested))
            } else {
                ViewUtils.showError(
                    binding.placeholder, when (ex) {
                        is IllegalArgumentException -> getString(R.string.invite_user_error)
                        is IllegalStateException -> getString(R.string.invite_conflict)
                        is NoSuchElementException -> getString(R.string.no_such_user)
                        else -> ex.message ?: getString(R.string.invite_error)
                    }
                )
            }
        }
    }

    /**
     * Called when ids of the [connections] are received.
     *
     * Gets details of each connection from the database.
     *
     * @param ids The user ids of the connections.
     */
    private fun onConnectionsReceived(ids: List<String>) {
        // Show placeholder view when connections list empty
        binding.placeholder.visibility = if (ids.isEmpty()) View.VISIBLE else View.GONE

        ids.forEach { uid ->
            AccountsDao.getById(uid) { connection ->
                connection?.let { onNewConnection(it) }
            }
        }
    }

    /**
     * Called when a new connection is received.
     *
     * Displays the connection details using the [connectionsAdapter].
     *
     * @param connection The [UserAccount] of the new connection.
     */
    private fun onNewConnection(connection: UserAccount) {
        if (!connections.contains(connection)) {
            connections.add(connection)
            connectionsAdapter.notifyDataSetChanged()
        }
    }

    /**
     * Called when ids of the request senders are received.
     *
     * Gets details of each sender from the database and displays them.
     *
     * @param ids The user ids of the senders.
     */
    private fun onRequestsReceived(ids: List<String>) {
        requests.clear()
        ids.forEach { uid ->
            AccountsDao.getById(uid) { sender ->
                sender?.let { onRequestReceived(it) }
            }
        }
    }

    /**
     * Called when a new request is received.
     *
     * @param sender The [UserAccount] of the request sender.
     */
    private fun onRequestReceived(sender: UserAccount) {
        if (!requests.contains(sender)) {
            requests.add(sender)
            requestsAdapter.notifyDataSetChanged()
        }
    }

    private fun acceptConnectionRequest(id: Int) = lifecycleScope.launch {
        // Accept the incoming request
        val sender = requests[id]
        ConnectionRequestBO.accept(currentUser.id, sender.id)
        onNewConnection(sender)

        // Remove it from pending requests list
        requests.removeAt(id)
        requestsAdapter.notifyDataSetChanged()
    }

    private fun rejectConnectionRequest(id: Int) = lifecycleScope.launch {
        // Reject the incoming request
        val sender = requests[id]
        ConnectionRequestBO.reject(currentUser.id, sender.id)

        // Remove it from pending requests list
        requests.removeAt(id)
        requestsAdapter.notifyDataSetChanged()
    }

    private fun openExplanationScreen() {
        startActivity(Intent(applicationContext, IntroActivity::class.java))
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

}