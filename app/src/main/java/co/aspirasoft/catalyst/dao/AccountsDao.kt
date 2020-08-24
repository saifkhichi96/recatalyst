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
     * Adds a new user account to the database.
     *
     * @param account The account to add.
     */
    suspend fun add(account: UserAccount) {
        MyApplication.refToUserAccount(account.id)
                .setValue(account)
                .await()
    }

    /**
     * Fetches a user's account details by their user id.
     *
     * This operation happens asynchronously, and the result of the operation, which
     * can either be a [UserAccount] or null in case of a mismatch or error, is
     * passed back through the callback method provided to the function.
     *
     * @param uid The id of the user to fetch.
     * @param receiver A callback to receive the result of the operation.
     */
    fun getById(uid: String, receiver: (user: UserAccount?) -> Unit) {
        MyApplication.refToUserAccount(uid)
                .get { snapshot ->
                    receiver(snapshot?.toUser())
                }
    }

    /**
     * Fetches a user's account details by their user id.
     *
     * This operation happens asynchronously, and the result of the operation, which
     * can either be a [UserAccount] or null in case of a mismatch or error, is
     * passed back through the callback method provided to the function.
     *
     * @param email The email address associated with account of the user to fetch.
     * @param receiver A callback to receive the result of the operation.
     */
    fun getByEmail(email: String, receiver: (user: UserAccount?) -> Unit) {
        val accountPath = "account"
        val emailPath = "$accountPath/email"
        MyApplication.refToUsers()
                .orderByChild(emailPath)
                .equalTo(email.toLowerCase(Locale.getDefault()))
                .getChildren { snapshots ->
                    receiver(if (snapshots == null) null else runCatching {
                        val snapshot = snapshots.elementAt(0).child(accountPath)
                        snapshot.toUser()
                    }.getOrNull())
                }
    }

    /**
     * Casts a [DataSnapshot] to a [UserAccount].
     *
     * @return The user account contained in the snapshot.
     * @throws ClassCastException An error is raised if casting fails.
     */
    private fun DataSnapshot.toUser(): UserAccount {
        return try {
            this.getValue(UserAccount::class.java)!!
        } catch (ex: Exception) {
            throw ClassCastException(ex.message)
        }
    }

}