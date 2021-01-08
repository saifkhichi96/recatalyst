package co.aspirasoft.catalyst.dao

import co.aspirasoft.catalyst.utils.db.RealtimeDatabase
import co.aspirasoft.catalyst.utils.db.list
import kotlinx.coroutines.tasks.await


/**
 * A data access class to manage user connections in the database.
 *
 * Purpose of this class is to provide an API for communicating with the
 * database to access data related to a user's connections.
 *
 * @author saifkhichi96
 * @since 1.0.0
 */
object ConnectionsDao {

    /**
     * Creates a new connection between two users.
     *
     * @param uid1 The id of the first user.
     * @param uid2 The id of the second user.
     */
    suspend fun addConnection(uid1: String, uid2: String) {
        RealtimeDatabase.connections(uid1)
            .child(uid2)
            .setValue(uid2)
            .await()

        RealtimeDatabase.connections(uid2)
            .child(uid1)
            .setValue(uid1)
            .await()
    }

    /**
     * Creates a new connection request between two users.
     *
     * @param sender The id of the request sender.
     * @param recipient The id of the second recipient.
     */
    suspend fun addConnectionRequest(sender: String, recipient: String) {
        // Add outgoing request to the sender's account
        RealtimeDatabase.connectionRequestsSent(sender)
            .child(recipient)
            .setValue(recipient)
            .await()

        // Add incoming request to the recipient's account
        RealtimeDatabase.connectionRequestsReceived(recipient)
            .child(sender)
            .setValue(sender)
            .await()
    }

    /**
     * Deletes an incoming connection request.
     *
     * @param sender The id of the user who sent the request.
     * @param recipient The id of the request recipient.
     */
    suspend fun deleteReceivedRequest(sender: String, recipient: String) {
        RealtimeDatabase.connectionRequestsReceived(recipient)
            .child(sender)
            .removeValue()
            .await()
    }

    /**
     * Deletes an outgoing connection request.
     *
     * @param sender The id of the user who sent the request.
     * @param recipient The id of the request recipient.
     */
    suspend fun deleteSentRequest(sender: String, recipient: String) {
        RealtimeDatabase.connectionRequestsSent(sender)
            .child(recipient)
            .removeValue()
            .await()
    }

    /**
     * Gets list of a user's connections.
     *
     * @param uid The id of the user whose connections to get.
     * @param receiver Callback for receiving the result.
     */
    fun getUserConnections(uid: String, receiver: (List<String>) -> Unit) {
        RealtimeDatabase.connections(uid).list(receiver)
    }

    /**
     * Gets list of all received connection requests.
     *
     * @param uid The id of the user whose received requests to get.
     * @param receiver Callback for receiving the result.
     */
    fun getReceivedRequests(uid: String, receiver: (List<String>) -> Unit) {
        RealtimeDatabase.connectionRequestsReceived(uid).list(receiver)
    }

    /**
     * Gets list of all sent connection requests.
     *
     * @param uid The id of the user whose sent requests to get.
     * @param receiver Callback for receiving the result.
     */
    fun getSentRequests(uid: String, receiver: (List<String>) -> Unit) {
        RealtimeDatabase.connectionRequestsSent(uid).list(receiver)
    }

}