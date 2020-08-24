package co.aspirasoft.catalyst.bo

import co.aspirasoft.catalyst.MyApplication
import co.aspirasoft.catalyst.dao.AccountsDao
import co.aspirasoft.catalyst.dao.ConnectionsDao
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

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
     * This operation happens asynchronously, and the result of the operation is a
     * an error message in case of failure, or null if operation successful. This
     * result is passed back through the callback method provided to the function.
     *
     * @param sender The user id of the request sender.
     * @param recipientEmail The email address of the request's recipient.
     * @param receiver A callback to receive the result of the operation.
     */
    fun send(sender: String, recipientEmail: String, receiver: (error: String?) -> Unit) {
        // Get reference to the invitee's account (need this to add the request in their account)
        AccountsDao.getByEmail(recipientEmail) { recipient ->
            // No user with this email? Oops! Sound the alarm bells!!!
            if (recipient == null) {
                receiver("Could not find the user $recipientEmail.")
                return@getByEmail
            }

            // Invitee is same as the sender? WHAT SORCERY IS THIS? WE AIN'T DOING INCEPTION HERE!!!
            if (recipient.id.equals(sender, true)) {
                receiver("Could not invite this user.")
                return@getByEmail
            }

            // Invitee's account located? Good! We can move forward now!!!
            ConnectionsDao.getSentRequests(sender) { sentRequests ->
                if (!sentRequests.none { it.equals(recipient.id, true) }) {
                    receiver("You have already invited this user to connect.")
                    return@getSentRequests
                }

                GlobalScope.launch {
                    try {
                        // Add outgoing request to the sender's account
                        MyApplication.refToUserConnectionsOutgoing(sender)
                                .child(recipient.id)
                                .setValue(recipient.id)
                                .await()

                        // Add incoming request to the recipient's account
                        MyApplication.refToUserConnectionsIncoming(recipient.id)
                                .child(sender)
                                .setValue(sender)
                                .await()

                        receiver(null)
                    } catch (ex: Exception) {
                        receiver("Could not send the connection request.")
                    }
                }
            }
        }
    }

    /**
     * Accepts a connection request.
     *
     * @param uid The id of the user who wants to accept a received request.
     * @param sender The user id of the request sender.
     */
    suspend fun accept(uid: String, sender: String) {
        // Remove request from incoming requests list
        MyApplication.refToUserConnectionsIncoming(uid)
                .child(sender)
                .removeValue()
                .await()

        // Add it to accepted requests list
        MyApplication.refToUserConnections(uid)
                .child(sender)
                .setValue(sender)
                .await()
    }

    /**
     * Rejects a connection request.
     *
     * @param uid The id of the user who wants to reject a received request.
     * @param sender The user id of the request sender.
     */
    suspend fun reject(uid: String, sender: String) {
        // Remove incoming request from the user's account
        MyApplication.refToUserConnectionsIncoming(uid)
                .child(sender)
                .removeValue()
                .await()

        // Remove outgoing request from the sender's account
        MyApplication.refToUserConnectionsIncoming(sender)
                .child(uid)
                .removeValue()
                .await()
    }

}