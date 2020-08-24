package co.aspirasoft.catalyst.bo

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

/**
 * Defines business-level logic for user authentication.
 *
 * Provides an API to sign users into their accounts, or sign them out.
 *
 * @author saifkhichi96
 * @since 1.0.0
 */
object AuthBO {

    private val auth = FirebaseAuth.getInstance()

    /**
     * Asynchronously signs a user into their account using their email/password.
     *
     * @param email The email address associated with the user account.
     * @param password The password of the user account.
     * @return A [FirebaseUser] if sign-in succeeds, else null.
     */
    suspend fun signInWithEmailPassword(email: String, password: String): FirebaseUser? {
        return auth.signInWithEmailAndPassword(email, password)
                .await()
                ?.user
    }

    /**
     * Signs a user out of their account.
     */
    fun signOut() {
        auth.signOut()
    }

}