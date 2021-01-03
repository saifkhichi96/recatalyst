package co.aspirasoft.catalyst.dao

import co.aspirasoft.catalyst.MyApplication
import co.aspirasoft.catalyst.models.UserAccount
import co.aspirasoft.catalyst.utils.get
import co.aspirasoft.catalyst.utils.getChildren
import com.google.firebase.database.DataSnapshot
import kotlinx.coroutines.tasks.await
import java.util.*


/**
 * A data access class to manage user accounts in the database.
 *
 * Purpose of this class is to provide an API for communicating with the
 * database to access data related to user accounts.
 *
 * @author saifkhichi96
 * @since 1.0.0
 */
object AccountsDao {

    /**
     * Adds a new [UserAccount] to the database.
     *
     * @param account The user account to add.
     */
    suspend fun add(account: UserAccount) {
        MyApplication.refToUserAccount(account.id)
                .setValue(account)
                .await()
    }

    /**
     * Gets user data by user's id.
     *
     * @param uid The id of the user to get.
     * @param receiver Callback for receiving the result.
     */
    fun getById(uid: String, receiver: (UserAccount?) -> Unit) {
        MyApplication.refToUserAccount(uid)
                .get { snapshot ->
                    receiver(try {
                        snapshot?.toUser()
                    } catch (ex: Exception) {
                        null
                    })
                }
    }

    /**
     * Gets user data by user's email address.
     *
     * @param email The email address of the user account to get.
     * @param receiver Callback for receiving the result.
     */
    fun getByEmail(email: String, receiver: (UserAccount?) -> Unit) {
        val accountPath = "account"
        val emailPath = "$accountPath/email"
        MyApplication.refToUsers()
                .orderByChild(emailPath)
                .equalTo(email.toLowerCase(Locale.getDefault()))
                .getChildren { snapshots ->
                    receiver(try {
                        snapshots!!.elementAt(0).child(accountPath).toUser()
                    } catch (ex: Exception) {
                        null
                    })
                }
    }

    /**
     * Deletes user data from database.
     *
     * @param uid The id of the user to delete.
     * @param receiver Callback for receiving the result.
     */
    fun delete(uid: String, receiver: () -> Unit) {
        // Remove connection links to other users
        ConnectionsDao.getUserConnections(uid) { connections ->
            connections.forEach { MyApplication.refToUserConnections(it).child(uid).setValue(null) }

            // Withdraw all sent connection requests
            ConnectionsDao.getSentRequests(uid) { sentRequests ->
                sentRequests.forEach { MyApplication.refToUserConnectionsIncoming(it).child(uid).setValue(null) }

                // Reject all received connection requests
                ConnectionsDao.getReceivedRequests(uid) { receivedRequests ->
                    receivedRequests.forEach { MyApplication.refToUserConnectionsOutgoing(it).child(uid).setValue(null) }

                    // TODO: Delete references to this user in other users' projects

                    // Delete all user data
                    MyApplication.refToUser(uid).setValue(null)

                    // TODO: Delete any files in Storage

                    // Callback method on completion
                    receiver()
                }
            }
        }
    }

    /**
     * Casts a [DataSnapshot] to a [UserAccount].
     *
     * @throws ClassCastException Exception thrown if casting fails.
     */
    @Throws(ClassCastException::class)
    private fun DataSnapshot.toUser(): UserAccount {
        return try {
            this.getValue(UserAccount::class.java)!!
        } catch (ex: Exception) {
            throw ClassCastException(ex.message)
        }
    }

}