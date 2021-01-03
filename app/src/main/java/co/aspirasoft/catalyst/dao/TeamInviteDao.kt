package co.aspirasoft.catalyst.dao

import co.aspirasoft.catalyst.MyApplication
import co.aspirasoft.catalyst.models.TeamInvite
import co.aspirasoft.catalyst.utils.list

/**
 * A data access class to manage [TeamInvite]s.
 *
 * Purpose of this class is to provide an API for communicating with the
 * backend to access and manage invites to teams.
 *
 * @author saifkhichi96
 * @since 1.0.0
 */
object TeamInviteDao {

    /**
     * Fetches a list of incoming invites.
     *
     * This involves a network request and happens asynchronously. A callback function
     * is used for handling the response of the request when it is available.
     *
     * @param uid The id of the user whose invites to get.
     * @param listener A listener for receiving response of the request.
     */
    fun getReceived(uid: String, listener: (team: List<TeamInvite>) -> Unit) {
        MyApplication.refToReceivedInvites(uid).list(listener)
    }

}