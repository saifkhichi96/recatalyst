package co.aspirasoft.catalyst.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import co.aspirasoft.adapter.ModelViewAdapter
import co.aspirasoft.catalyst.R
import co.aspirasoft.catalyst.activities.abs.DashboardChildActivity
import co.aspirasoft.catalyst.bo.ConnectionRequestBO
import co.aspirasoft.catalyst.dao.AccountsDao
import co.aspirasoft.catalyst.dao.ConnectionsDao
import co.aspirasoft.catalyst.models.UserAccount
import co.aspirasoft.catalyst.views.UserView
import co.aspirasoft.util.InputUtils.isEmail
import co.aspirasoft.util.ViewUtils
import co.aspirasoft.view.SingleInputForm
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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
    private lateinit var requestsAdapter: ConnectionAdapter

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
        learnMoreButton.setOnClickListener {
            startActivity(Intent(applicationContext, IntroActivity::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        requestsAdapter = ConnectionAdapter(this, requests)
        requestsAdapter.onItemClickListener = { position ->
            val sender = requests[position]
            MaterialAlertDialogBuilder(this@ConnectionsActivity)
                    .setMessage("${sender.name} wants to add you as a connection.")
                    .setPositiveButton("Accept") { dialog, _ ->
                        dialog.dismiss()
                        lifecycleScope.launch {
                            ConnectionRequestBO.accept(
                                    uid = currentUser.id,
                                    sender = sender.id
                            )
                            requests.removeAt(position)
                            requestsAdapter.notifyDataSetChanged()
                            updateUI(currentUser)
                            if (requests.isEmpty()) runOnUiThread { requestsSection.visibility = View.GONE }
                        }
                    }
                    .setNegativeButton("Reject") { dialog, _ ->
                        dialog.dismiss()
                        lifecycleScope.launch {
                            ConnectionRequestBO.reject(
                                    uid = currentUser.id,
                                    sender = sender.id
                            )
                            requests.removeAt(position)
                            requestsAdapter.notifyDataSetChanged()
                            if (requests.isEmpty()) runOnUiThread { requestsSection.visibility = View.GONE }
                        }
                    }
                    .show()
        }
        requestsList.adapter = requestsAdapter

        connectionsAdapter = ConnectionAdapter(this, connections)
        connectionsList.adapter = connectionsAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_connections, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_invite -> {
                val dialog = SingleInputForm.newInstance(
                        title = "Send a Connection Request",
                        hint = "Recipient's Email",
                        inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS,
                        okButton = "Send")

                dialog.setOnSubmitListener { email ->
                    // Validate provided email address
                    if (!email.isEmail()) {
                        ViewUtils.showError(placeholder, getString(R.string.error_invalid_email))
                        return@setOnSubmitListener
                    }
                    dialog.dismiss()

                    // Send a connection request
                    ConnectionRequestBO.send(currentUser.id, email.toLowerCase(Locale.getDefault())) { error ->
                        if (error == null) {
                            ViewUtils.showMessage(placeholder, "Connection request sent.")
                        } else {
                            ViewUtils.showError(placeholder, error)
                        }
                    }
                }

                dialog.show(supportFragmentManager, dialog.toString())
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun updateUI(currentUser: UserAccount) {
        // Fetch and show the connection requests
        ConnectionsDao.getReceivedRequests(currentUser.id) {
            requests.clear()
            if (it.isNotEmpty()) {
                placeholder.visibility = View.GONE
                content.visibility = View.VISIBLE
                requestsSection.visibility = View.VISIBLE
            } else {
                requestsSection.visibility = View.GONE
            }

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
                content.visibility = View.VISIBLE
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

    private class ConnectionAdapter(context: Context, val users: List<UserAccount>)
        : ModelViewAdapter<UserAccount>(context, users, UserView::class) {

        var onItemClickListener: ((position: Int) -> Unit)? = null

        override fun notifyDataSetChanged() {
            users.sortedBy { it.name }
            super.notifyDataSetChanged()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val v = super.getView(position, convertView, parent)
            v.setOnClickListener {
                onItemClickListener?.let { it(position) }
            }
            return v
        }


    }

}