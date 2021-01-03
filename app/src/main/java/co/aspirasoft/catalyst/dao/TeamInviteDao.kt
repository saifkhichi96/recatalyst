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
     * Gets list of all received [TeamInvite]s.
     *
     * @param uid The id of the user whose invites to get.
     * @param receiver Callback for receiving the results.
     */
    fun getReceivedInvites(uid: String, receiver: (List<TeamInvite>) -> Unit) {
        MyApplication.refToReceivedInvites(uid).list(receiver)
    }

}