package co.aspirasoft.catalyst.dao

import co.aspirasoft.catalyst.models.Message
import co.aspirasoft.catalyst.models.Project
import co.aspirasoft.catalyst.utils.db.RealtimeDatabase
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.tasks.await


/**
 * A data access class to read posts on notice boards.
 *
 * Purpose of this class is to provide methods for communicating with the
 * Firebase backend to access data related to notice boards.
 *
 * @author saifkhichi96
 * @since 1.0.0
 */
object ChatroomDao {

    /**
     * Sends a [Message] in a [Project]'s chatroom.
     *
     * @param project The project which owns the chatroom.
     * @param message The message to send.
     */
    suspend fun add(project: Project, message: Message) {
        RealtimeDatabase.chatroom(project.ownerId, project.name)
            .push().apply { message.id = this.key.toString() }

        val map = message.asMap().toMutableMap()
        map["timestamp"] = ServerValue.TIMESTAMP
        RealtimeDatabase.chatroom(project.ownerId, project.name).child(message.id)
            .setValue(map)
            .await()
    }

    /**
     * Observes messages in a [Project]'s chatroom.
     *
     * @param project The project which owns the chatroom.
     * @param observer Callback for receiving the results.
     */
    fun observeChatroom(project: Project, observer: (Message) -> Unit) {
        RealtimeDatabase.chatroom(project.ownerId, project.name)
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    snapshot.getValue<Message>()?.let { observer(it) }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onChildRemoved(snapshot: DataSnapshot) {}
                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onCancelled(error: DatabaseError) {}
            })
    }

}