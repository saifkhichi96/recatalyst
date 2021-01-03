package co.aspirasoft.catalyst.dao

import co.aspirasoft.catalyst.MyApplication
import co.aspirasoft.catalyst.utils.list


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
     * Gets list of a user's connections.
     *
     * @param uid The id of the user whose connections to get.
     * @param receiver Callback for receiving the result.
     */
    fun getUserConnections(uid: String, receiver: (List<String>) -> Unit) {
        MyApplication.refToUserConnections(uid).list(receiver)
    }

    /**
     * Gets list of all received connection requests.
     *
     * @param uid The id of the user whose received requests to get.
     * @param receiver Callback for receiving the result.
     */
    fun getReceivedRequests(uid: String, receiver: (List<String>) -> Unit) {
        MyApplication.refToUserConnectionsIncoming(uid).list(receiver)
    }

    /**
     * Gets list of all sent connection requests.
     *
     * @param uid The id of the user whose sent requests to get.
     * @param receiver Callback for receiving the result.
     */
    fun getSentRequests(uid: String, receiver: (List<String>) -> Unit) {
        MyApplication.refToUserConnectionsOutgoing(uid).list(receiver)
    }

}