package co.aspirasoft.catalyst.bo

import android.app.Activity
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.OAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
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

    private val auth = Firebase.auth
    val currentUser get() = auth.currentUser

    /**
     * Signs a user into their account using email/password.
     *
     * @param email The email address associated with the user account.
     * @param password The password of the user account.
     * @return The signed in [FirebaseUser], or null on failure.
     */
    suspend fun signInWithEmailPassword(email: String, password: String): FirebaseUser? {
        return auth.signInWithEmailAndPassword(email, password)
                .await()
                ?.user
    }

    /**
     * Signs a user into their account using [AuthCredential].
     *
     * @param credential The credentials to use for signing in.
     * @return The signed in [FirebaseUser], or null on failure.
     */
    suspend fun signInWithCredential(credential: AuthCredential): FirebaseUser? {
        return auth.signInWithCredential(credential)
                .await()
                ?.user
    }

    /**
     * Signs a user into their account with Github.
     *
     * @return The result of the sign-in request.
     */
    @Throws(NullPointerException::class)
    suspend fun signInWithGithub(context: Activity): AuthResult {
        val provider = OAuthProvider.newBuilder("github.com").apply {
            scopes = arrayListOf("read:user", "user:email")
        }
        val pendingResult = auth.pendingAuthResult
        return when {
            // There's something already here! Finish the sign-in for your user.
            pendingResult != null -> pendingResult.await()!!

            // There's no pending result so you need to start the sign-in flow.
            else -> auth.startActivityForSignInWithProvider(context, provider.build()).await()!!
        }
    }

    /**
     * Signs a user out of their account.
     */
    fun signOut() {
        auth.signOut()
    }

}