package co.aspirasoft.catalyst.utils.db

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

object RealtimeDatabase {

    // the refTo* functions return a reference to resources
    // in the Firebase database
    private val db get() = Firebase.database

    fun users() = db.getReference("users/")
    fun user(uid: String) = users().child(uid)
    fun account(uid: String) = user(uid).child("account/")

    fun connections(uid: String) = user(uid).child("connections/")
    fun connectionRequestsReceived(uid: String) = user(uid).child("incoming_request/")
    fun connectionRequestsSent(uid: String) = user(uid).child("outgoing_request/")

    fun projects(uid: String) = user(uid).child("projects/")
    fun project(uid: String, pid: String) = projects(uid).child(pid)
    fun chatroom(uid: String, pid: String) = project(uid, pid).child("chats/")

    fun team(uid: String, pid: String) = project(uid, pid).child("team/")
    fun teamInvitesReceived(uid: String) = user(uid).child("incoming_invites/")
    fun teamInvitesSent(uid: String) = user(uid).child("outgoing_invites/")

}