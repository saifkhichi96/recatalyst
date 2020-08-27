package co.aspirasoft.catalyst.utils

import com.google.firebase.auth.ActionCodeSettings
import java.net.URLEncoder

/**
 * A singleton class for creating deep links.
 *
 * @author saifkhichi96
 * @since 1.0.0
 */
object DynamicLinksUtils {

    private const val domain = "https://aspirasoft.page.link"
    private const val androidPackageName = "co.aspirasoft.catalyst"
    private const val iOSBundleId = "co.aspirasoft.catalyst"

    const val ACTION_REGISTRATION = "/finishSignUp"

    /**
     * Encodes a string as a URL with UTF-8 encoding.
     *
     * @param param The string to encode.
     * @return The input string as a url-encoded text.
     */
    private fun encode(param: String): String {
        return URLEncoder.encode(param, "utf-8")
    }

    /**
     * Constructs an [ActionCodeSettings] object for email link authentication.
     *
     * @return The [ActionCodeSettings] with instructions on how to construct the email link.
     */
    fun createSignUpAction(): ActionCodeSettings {
        val redirectUrl = "$domain$ACTION_REGISTRATION"
        return ActionCodeSettings.newBuilder()
                // URL you want to redirect back to.
                .setUrl(redirectUrl)
                .setHandleCodeInApp(true)   // This must be true
                .setIOSBundleId(iOSBundleId)
                .setAndroidPackageName(
                        androidPackageName,
                        true,   // installIfNotAvailable
                        "1"     // minimumVersion
                )
                .build()
    }

}