package co.aspirasoft.catalyst

import android.app.Application
import com.orhanobut.hawk.Hawk
import dagger.hilt.android.HiltAndroidApp

/**
 * An [Application] subclass represents this application.
 *
 * Purpose of this class is to define default behaviours, perform
 * SDK initializations and declare any shared data.
 */
@HiltAndroidApp
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Init dependencies
        Hawk.init(this).build()
    }

    companion object {
        // the EXTRA_* strings are used as tags to pass
        // data between activities using Intents
        const val EXTRA_CREDENTIALS = "credentials"
        const val EXTRA_DOCUMENT = "document"
        const val EXTRA_NEW_SIGN_UP = "new_user"
        const val EXTRA_PROFILE_USER = "profile_user"
        const val EXTRA_PROJECT = "project"
        const val EXTRA_TASK = "task"
        const val EXTRA_USER = "user"

        // the PARAM_* strings are used to define parameters
        // used in dynamic links
        const val PARAM_LINK_TARGET = "continueUrl"
    }

}