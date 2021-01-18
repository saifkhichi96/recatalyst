package co.aspirasoft.catalyst.utils.db

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class CloudFirestore {

    suspend inline fun <reified T> get(query: CollectionReference): T? {
        return query.get().await().documents.firstOrNull()?.toObject()
    }

    suspend inline fun <reified T> get(query: DocumentReference): T? {
        return query.get().await().toObject()
    }

    companion object {
        private val db get() = Firebase.firestore
        fun users() = db.collection("users/")

        fun user(uid: String) = users().document(uid)
        fun account(uid: String) = user(uid)
        fun teams(uid: String) = user(uid).collection("teams/")

        fun connections(uid: String) = user(uid).collection("connections/")
        fun connectionRequestsReceived(uid: String) = user(uid).collection("incoming_request/")
        fun connectionRequestsSent(uid: String) = user(uid).collection("outgoing_request/")

        fun projects(uid: String) = user(uid).collection("projects/")
        fun project(uid: String, pid: String) = projects(uid).document(pid)
        fun chatroom(uid: String, pid: String) = project(uid, pid).collection("chats/")

        fun team(uid: String, pid: String) = project(uid, pid).collection("team/")
        fun teamInvitesReceived(uid: String) = user(uid).collection("incoming_invites/")
        fun teamInvitesSent(uid: String) = user(uid).collection("outgoing_invites/")
    }

}