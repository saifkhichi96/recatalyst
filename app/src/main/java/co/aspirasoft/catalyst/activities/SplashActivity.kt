package co.aspirasoft.catalyst.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import androidx.lifecycle.lifecycleScope
import co.aspirasoft.catalyst.MyApplication
import co.aspirasoft.catalyst.R
import co.aspirasoft.catalyst.activities.abs.SilentSignInActivity
import co.aspirasoft.catalyst.utils.DynamicLinksUtils
import co.aspirasoft.util.ViewUtils
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_splash.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.ParseException

/**
 * SplashActivity is the entry point of the app.
 *
 * Purpose of this activity is to provide a splash screen which displays app
 * branding and performs all necessary initializations in background before
 * actually starting the app.
 *
 * @author saifkhichi96
 * @since 1.0.0
 */
class SplashActivity : SilentSignInActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
    }

    override fun onStart() {
        super.onStart()
        lifecycleScope.launch {
            val parsingLink = checkForDynamicLinks()
            val userAuthenticated = startSilentSignIn()
            if (!parsingLink && !userAuthenticated) {
                startAppDelayed()
            }
        }
    }

    /**
     * Redirects to the start page of the app after a short delay.
     *
     * @param delay The duration of the delay in milliseconds. Default is 1500 ms.
     */
    private fun startAppDelayed(delay: Long = 1500L) {
        Handler().postDelayed({ startApp() }, delay)
    }

    /**
     * Redirects to the start page of the app.
     */
    private fun startApp() {
        startActivity(Intent(applicationContext, SignInActivity::class.java))
        finish()
    }

    /**
     * Checks for incoming [FirebaseDynamicLinks] to allow deep linking.
     *
     * Appropriate action is taken if a parseable link can be found,
     * otherwise the app proceeds with normal startup.
     *
     * @return True if a link was found, else False.
     */
    private suspend fun checkForDynamicLinks(): Boolean {
        val link = Firebase.dynamicLinks.getDynamicLink(intent).await()?.link
        if (link != null) {
            parseDeepLink(link)
            return true
        }

        return false
    }

    /**
     * Parses a deep link to ascertain which action to take.
     *
     * @param link The link to parse.
     * @return Data of the action to be taken, or null if no action found.
     * @throws ParseException An exception is raised if the link cannot be parsed.
     */
    private fun getLinkAction(link: Uri): Uri? {
        try {
            return Uri.parse(link.getQueryParameter(MyApplication.PARAM_LINK_TARGET))
        } catch (ex: Exception) {
            throw ParseException(getString(R.string.url_error), 0)
        }
    }

    /**
     * Parses a deep link to take appropriate action.
     *
     * @param link The link to parse.
     */
    private fun parseDeepLink(link: Uri) {
        try {
            val actionData = getLinkAction(link)
            when (actionData!!.path) {
                // TODO: Perform deep-linked actions here.
                // case: Complete sign up process
                DynamicLinksUtils.ACTION_REGISTRATION -> {
                    // actionCompleteSignUp(link, actionData)
                }
            }
        } catch (ex: Exception) {
            ViewUtils.showError(splashScreen, ex.message ?: getString(R.string.url_error))
        }
    }

    // private fun actionCompleteSignUp(link: Uri, data: Uri) {
    //     val i = Intent(this, SignUpActivity::class.java)
    //     i.putExtra(MyApplication.EXTRA_REFERRAL_CODE, data.getQueryParameter(MyApplication.PARAM_REFERRAL_CODE))
    //     i.putExtra(MyApplication.EXTRA_ACCOUNT_TYPE, data.getQueryParameter(MyApplication.PARAM_ACCOUNT_TYPE))
    //     i.data = link
    //
    //     startActivity(i)
    //     finish()
    // }

}