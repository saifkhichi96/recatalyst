package co.aspirasoft.catalyst.bo

import co.aspirasoft.catalyst.dao.AccountsDao
import co.aspirasoft.catalyst.models.UserAccount
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

/**
 * Defines business-level logic for user account management.
 *
 * Purpose of this class is to provide an API for creating and managing
 * user accounts.
 *
 * @author saifkhichi96
 * @since 1.0.0
 */
object AccountsBO {

    private val auth = Firebase.auth

    /**
     * Registers a new user account.
     *
     * Registration involves creating a new user account with provided credentials, and
     * write user details to the database.
     *
     * @param account The account to register.
     * @param existingUser (Optional) Existing user to link the new account with.
     */
    @Throws(Exception::class)
    suspend fun registerAccount(account: UserAccount, existingUser: FirebaseUser? = null) {
        // Sign up for a new account
        val email = account.email
        val password = account.password

        val result = when (existingUser) {
            null -> auth.createUserWithEmailAndPassword(email, password).await()
            else -> {
                val credential = EmailAuthProvider.getCredential(email, password)
                existingUser.linkWithCredential(credential).await()
            }
        }

        // Save user's details in the database
        try {
            val newUser = result.user!!
            account.id = newUser.uid
            account.password = "" // we don't want to save passwords
            AccountsDao.add(account)
        }

        // Delete account if error saving details
        catch (ex: Exception) {
            deleteAccount(email, password)
            throw ex
        }
    }

    /**
     * Deletes a user account.
     *
     * @param credential The sign-in credentials of the account to delete.
     */
    @Throws(RuntimeException::class)
    suspend fun deleteAccount(credential: AuthCredential) {
        try {
            // Delete user account
            auth.currentUser!!.reauthenticate(credential).await()
            auth.currentUser!!.delete().await()
        } catch (ex: Exception) {
            throw RuntimeException("${ex.javaClass.simpleName}: ${ex.message}")
        }
    }

    /**
     * Deletes a user account.
     *
     * @param email The email address associated with the account to delete.
     * @param password The password of the user account.
     */
    @Throws(RuntimeException::class)
    suspend fun deleteAccount(email: String, password: String) {
        val credential = EmailAuthProvider.getCredential(email, password)
        deleteAccount(credential)
    }

}