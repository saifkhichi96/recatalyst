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

    companion object {
        // the EXTRA_* strings are used as tags to pass
        // data between activities using Intents
        const val EXTRA_CREDENTIALS = "credentials"
        const val EXTRA_DOCUMENT = "document"
        const val EXTRA_NEW_SIGN_UP = "new_user"
        const val EXTRA_NOTICE_POSTS = "notice_posts"
        const val EXTRA_PROFILE_USER = "profile_user"
        const val EXTRA_PROJECT = "project"
        const val EXTRA_TASK = "task"
        const val EXTRA_USER = "user"

        // the PARAM_* strings are used to define parameters
        // used in dynamic links
        const val PARAM_LINK_TARGET = "continueUrl"

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

        fun refToReceivedInvites(uid: String) = refToUser(uid).child("incoming_invites/")
        fun refToSentInvites(uid: String) = refToUser(uid).child("outgoing_invites/")

        fun refToNews(uid: String, pid: String) = refToProject(uid, pid).child("news/")
    }

}