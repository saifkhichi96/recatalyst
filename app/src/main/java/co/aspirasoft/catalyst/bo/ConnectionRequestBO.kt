package co.aspirasoft.catalyst.bo

import co.aspirasoft.catalyst.dao.AccountsDao
import co.aspirasoft.catalyst.dao.ConnectionsDao
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * Defines business-level logic for managing connection requests.
 *
 * Purpose of this class is to provide an API for sending, accepting and rejecting
 * connection requests.
 *
 * @author saifkhichi96
 * @since 1.0.0
 */
object ConnectionRequestBO {

    /**
     * Sends a new connection request.
     *
     * @param sender The user id of the sender.
     * @param recipientEmail The email address of the recipient.
     * @param receiver Callback for receiving the result.
     */
    fun send(sender: String, recipientEmail: String, receiver: (ex: Exception?) -> Unit) {
        // Get reference to the invitee's account (need this to add the request in their account)
        AccountsDao.getByEmail(recipientEmail) { recipient ->
            // No user with this email? Oops! Sound the alarm bells!!!
            if (recipient == null) {
                receiver(NoSuchElementException())
                return@getByEmail
            }

            // Invitee is same as the sender? WHAT SORCERY IS THIS? WE AIN'T DOING INCEPTION HERE!!!
            if (recipient.id.equals(sender, true)) {
                receiver(IllegalArgumentException())
                return@getByEmail
            }

            // Invitee's account located? Good! We can move forward now!!!
            ConnectionsDao.getSentRequests(sender) { sentRequests ->
                if (!sentRequests.none { it.equals(recipient.id, true) }) {
                    receiver(IllegalStateException())
                    return@getSentRequests
                }

                GlobalScope.launch {
                    try {
                        ConnectionsDao.addConnectionRequest(sender, recipient.id)
                        receiver(null)
                    } catch (ex: Exception) {
                        receiver(ex)
                    }
                }
            }
        }
    }

    /**
     * Accepts a connection request.
     *
     * @param recipient The id of the user who wants to accept a received request.
     * @param sender The user id of the request sender.
     */
    suspend fun accept(recipient: String, sender: String) {
        ConnectionsDao.addConnection(recipient, sender)
        reject(recipient, sender)
    }

    /**
     * Rejects a connection request.
     *
     * @param recipient The id of the user who wants to reject a received request.
     * @param sender The user id of the request sender.
     */
    suspend fun reject(recipient: String, sender: String) {
        ConnectionsDao.deleteSentRequest(sender, recipient)
        ConnectionsDao.deleteReceivedRequest(sender, recipient)
    }

}