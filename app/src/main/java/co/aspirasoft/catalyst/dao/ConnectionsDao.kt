package co.aspirasoft.catalyst.dao

import co.aspirasoft.catalyst.MyApplication
import co.aspirasoft.catalyst.utils.mappedList

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
     * Fetches list of a user's connections.
     *
     * This operation happens asynchronously, and the result of the operation is a
     * list of user ids of all the connections, or an empty list. This result is
     * passed back through the callback method provided to the function.
     *
     * @param uid The id of the user whose connections to fetch.
     * @param receiver A callback to receive the result of the operation.
     */
    fun getConnections(uid: String, receiver: (list: List<String>) -> Unit) {
        MyApplication.refToUserConnections(uid)
                .mappedList<String> { connections ->
                    receiver(connections)
                }
    }

    /**
     * Fetches list of all connection requests received by a user.
     *
     * This operation happens asynchronously, and the result of the operation is a
     * list of user ids of all the senders, or an empty list. This result is passed
     * back through the callback method provided to the function.
     *
     * @param uid The id of the user whose incoming requests to fetch.
     * @param receiver A callback to receive the result of the operation.
     */
    fun getReceivedRequests(uid: String, receiver: (list: List<String>) -> Unit) {
        MyApplication.refToUserConnectionsIncoming(uid)
                .mappedList<String> { connections ->
                    receiver(connections)
                }
    }

    /**
     * Fetches list of all connection requests sent by a user.
     *
     * This operation happens asynchronously, and the result of the operation is a
     * list of user ids of all the recipients, or an empty list. This result is passed
     * back through the callback method provided to the function.
     *
     * @param uid The id of the user whose outgoing requests to fetch.
     * @param receiver A callback to receive the result of the operation.
     */
    fun getSentRequests(uid: String, receiver: (list: List<String>) -> Unit) {
        MyApplication.refToUserConnectionsOutgoing(uid)
                .mappedList<String> { connections ->
                    receiver(connections)
                }
    }

}