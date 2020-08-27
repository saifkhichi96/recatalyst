package co.aspirasoft.catalyst

import android.app.Application
import com.google.firebase.database.FirebaseDatabase

/**
 * An [Application] subclass represents this application.
 *
 * Purpose of this class is to define default behaviours, perform
 * SDK initializations and declare any shared data.
 */
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Enabling persistence speeds up app by caching data locally
        // db.setPersistenceEnabled(true)

        STATUS_INVITE_PENDING = getString(R.string.pending)
        STATUS_INVITE_ACCEPTED = getString(R.string.accepted)
    }

    companion object {
        // the STATUS_* strings are used to represent status of
        // different tasks
        lateinit var STATUS_INVITE_PENDING: String private set
        lateinit var STATUS_INVITE_ACCEPTED: String private set

        // the EXTRA_* strings are used as tags to pass
        // data between activities using Intents
        const val EXTRA_ACCOUNT_TYPE = "account_type"
        const val EXTRA_EDITABLE_MODE = "editable"
        const val EXTRA_INVITE_STATUS = "invite_status"
        const val EXTRA_INVITES = "invites"
        const val EXTRA_NEW_SIGN_UP = "new_user"
        const val EXTRA_NOTICE_POSTS = "notice_posts"
        const val EXTRA_PROFILE_USER = "profile_user"
        const val EXTRA_PROJECT = "project"
        const val EXTRA_REFERRAL_CODE = "referral_code"
        const val EXTRA_SCHOOL = "school"
        const val EXTRA_SCHOOL_SUBJECT = "subject"
        const val EXTRA_STUDENT_ROLL_NO = "roll_no"
        const val EXTRA_STUDENT_CLASS_ID = "class_id"
        const val EXTRA_TEST_NAME = "test_name"
        const val EXTRA_USER = "user"

        // the PARAM_* strings are used to define parameters
        // used in dynamic links
        const val PARAM_ACCOUNT_TYPE = "type"
        const val PARAM_LINK_TARGET = "continueUrl"
        const val PARAM_REFERRAL_CODE = "referral"
        const val PARAM_STUDENT_ROLL_NO = "roll_no"
        const val PARAM_STUDENT_CLASS_ID = "class_id"

        // the refTo* functions return a reference to resources
        // in the Firebase database
        private val db get() = FirebaseDatabase.getInstance()

        fun refToUsers() = db.getReference("users/")
        fun refToUser(uid: String) = refToUsers().child(uid)
        fun refToUserAccount(uid: String) = refToUser(uid).child("account/")
        fun refToUserConnections(uid: String) = refToUser(uid).child("connections/")
        fun refToUserConnectionsIncoming(uid: String) = refToUser(uid).child("incoming_request/")
        fun refToUserConnectionsOutgoing(uid: String) = refToUser(uid).child("outgoing_request/")

        fun refToProjects(uid: String) = refToUser(uid).child("projects/")
        fun refToProject(uid: String, pid: String) = refToProjects(uid).child(pid)
        fun refToProjectMessages(uid: String, pid: String) = refToProject(uid, pid).child("chats/")
        fun refToProjectTeam(uid: String, pid: String) = refToProject(uid, pid).child("team/")

        fun refToNews(uid: String, pid: String) = refToProject(uid, pid).child("news/")
    }

}