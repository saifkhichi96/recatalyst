package co.aspirasoft.catalyst.bo

import co.aspirasoft.catalyst.dao.AccountsDao
import co.aspirasoft.catalyst.models.UserAccount
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth
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

    private val auth = FirebaseAuth.getInstance()

    /**
     * Registers a new user account.
     *
     * Registration involves creating a new user account with provided credentials, and
     * write user details to the database.
     *
     * @param account The account to register.
     * @throws Throwable An exception is raised if registration fails.
     */
    suspend fun registerAccount(account: UserAccount) {
        // Sign up for a new account
        val email = account.email
        val password = account.password
        val result = auth.createUserWithEmailAndPassword(email, password).await()

        // Save user's details in the database
        try {
            account.id = result.user!!.uid
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
     * @param email The email address associated with the account to delete.
     * @param password The password of the user account.
     * @throws RuntimeException An error is raised if account deletion fails.
     */
    suspend fun deleteAccount(email: String, password: String) {
        try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            result.user!!.reauthenticate(result.credential!!).await()
            result.user!!.delete().await()
        } catch (ex: Exception) {
            throw RuntimeException(ex.message)
        }
    }

    suspend fun sendSignUpInvitation(senderId: String, inviteeEmail: String, actionCode: ActionCodeSettings) {
        auth.sendSignInLinkToEmail(inviteeEmail, actionCode).await()
        // TODO: InvitesDao.sendTeamInvitation(inviteeEmail, senderId, OnCompleteListener { })
    }

}