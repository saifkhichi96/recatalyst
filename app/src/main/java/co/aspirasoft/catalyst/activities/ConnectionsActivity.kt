package co.aspirasoft.catalyst.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.lifecycleScope
import co.aspirasoft.adapter.ModelViewAdapter
import co.aspirasoft.catalyst.MyApplication
import co.aspirasoft.catalyst.R
import co.aspirasoft.catalyst.activities.abs.DashboardChildActivity
import co.aspirasoft.catalyst.bo.ConnectionRequestBO
import co.aspirasoft.catalyst.dao.AccountsDao
import co.aspirasoft.catalyst.dao.ConnectionsDao
import co.aspirasoft.catalyst.models.UserAccount
import co.aspirasoft.catalyst.views.IncomingRequestView
import co.aspirasoft.catalyst.views.UserView
import co.aspirasoft.util.InputUtils.isEmail
import co.aspirasoft.util.ViewUtils
import kotlinx.android.synthetic.main.activity_connections.*
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

/**
 * Lists all connections of a user.
 *
 * A user can 'connect' with another user by sending a connection request
 * which the other user has to accept. Connected users can collaborate on
 * projects.
 *
 * @author saifkhichi96
 * @since 1.0.0
 */
class ConnectionsActivity : DashboardChildActivity() {

    /**
     * List of people current user has pending connection requests from.
     */
    private val requests = ArrayList<UserAccount>()

    /**
     * An adapter for showing the incoming requests list.
     */
    private lateinit var requestsAdapter: IncomingRequestAdapter

    /**
     * List of current user's connections.
     */
    private val connections = ArrayList<UserAccount>()

    /**
     * An adapter for showing the connections list.
     */
    private lateinit var connectionsAdapter: ConnectionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connections)

        placeholderText.text = "You don't have any Connections."
        learnMoreButton.setOnClickListener { onLearnMoreClicked() }

        requestsAdapter = IncomingRequestAdapter(this, requests)
        requestsAdapter.onAcceptClickListener = { position ->
            val sender = requests[position]
            lifecycleScope.launch {
                ConnectionRequestBO.accept(
                        uid = currentUser.id,
                        sender = sender.id
                )
                requests.removeAt(position)
                requestsAdapter.notifyDataSetChanged()
                updateUI(currentUser)
            }
        }
        requestsAdapter.onRejectClickListener = { position ->
            val sender = requests[position]
            lifecycleScope.launch {
                ConnectionRequestBO.reject(
                        uid = currentUser.id,
                        sender = sender.id
                )
                requests.removeAt(position)
                requestsAdapter.notifyDataSetChanged()
            }
        }
        requestsList.adapter = requestsAdapter

        connectionsAdapter = ConnectionAdapter(this, connections)
        connectionsList.adapter = connectionsAdapter

        inviteButton.setOnClickListener {
            content.post {
                content.smoothScrollTo(0, inviteeEmailInput.bottom)
                inviteeEmailInput.requestFocus()
                val im = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                im.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
            }
        }
        sendRequestButton.setOnClickListener { onSendRequestClicked() }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_connections, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_learn_more -> {
                onLearnMoreClicked()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun updateUI(currentUser: UserAccount) {
        // Fetch and show the connection requests
        ConnectionsDao.getReceivedRequests(currentUser.id) {
            requests.clear()
            it.forEach { uid ->
                AccountsDao.getById(uid) { request ->
                    request?.let { r ->
                        requests.add(r)
                        requestsAdapter.notifyDataSetChanged()
                    }
                }
            }
        }

        // Fetch and show the connections
        ConnectionsDao.getConnections(currentUser.id) {
            connections.clear()
            if (it.isNotEmpty()) {
                placeholder.visibility = View.GONE
            }

            it.forEach { uid ->
                AccountsDao.getById(uid) { connection ->
                    connection?.let { c ->
                        connections.add(c)
                        connectionsAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    private fun onLearnMoreClicked() {
        startActivity(Intent(applicationContext, IntroActivity::class.java))
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    private fun onSendRequestClicked() {
        val email = inviteeEmailInput.text.toString().trim()
        // Validate provided email address
        if (!email.isEmail()) {
            ViewUtils.showError(placeholder, getString(R.string.error_invalid_email))
            return
        }

        // Send a connection request
        ConnectionRequestBO.send(currentUser.id, email.toLowerCase(Locale.getDefault())) { error ->
            runOnUiThread { inviteeEmailInput.setText("") }
            if (error == null) {
                ViewUtils.showMessage(placeholder, "Connection request sent.")
            } else {
                ViewUtils.showError(placeholder, error)
            }
        }
    }

    inner class IncomingRequestAdapter(context: Context, val users: List<UserAccount>)
        : ModelViewAdapter<UserAccount>(context, users, IncomingRequestView::class) {

        private val MAX_COUNT = 2

        var onAcceptClickListener: ((position: Int) -> Unit)? = null
        var onRejectClickListener: ((position: Int) -> Unit)? = null

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val v = super.getView(position, convertView, parent)
            (v as IncomingRequestView).apply {
                setOnAcceptListener { onAcceptClickListener?.let { it(position) } }
                setOnRejectListener { onRejectClickListener?.let { it(position) } }
                setOnProfileButtonClickedListener {
                    startSecurely(ProfileActivity::class.java, Intent().apply {
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

    inner class ConnectionAdapter(context: Context, val users: List<UserAccount>)
        : ModelViewAdapter<UserAccount>(context, users, UserView::class) {

        override fun notifyDataSetChanged() {
            users.sortedBy { it.name }
            super.notifyDataSetChanged()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val v = super.getView(position, convertView, parent)
            (v as UserView).apply {
                setOnProfileButtonClickedListener {
                    startSecurely(ProfileActivity::class.java, Intent().apply {
                        putExtra(MyApplication.EXTRA_PROFILE_USER, users[position])
                    })
                }
            }

            return v
        }

    }

}