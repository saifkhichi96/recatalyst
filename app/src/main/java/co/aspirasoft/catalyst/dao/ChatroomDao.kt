package co.aspirasoft.catalyst.dao

import co.aspirasoft.catalyst.MyApplication
import co.aspirasoft.catalyst.models.Message
import co.aspirasoft.catalyst.models.Project
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.getValue

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
     * Adds a new post to a class notice board.
     *
     * @param classId The id of the class.
     * @param post The post to sendTeamInvitation.
     * @param listener A listener for receiving response of the request.
     */
    fun add(classId: String, post: Message, listener: OnCompleteListener<Void?>) {
        MyApplication.refToNews(post.sender, classId)
                .push()
                .setValue(post)
                .addOnCompleteListener(listener)
    }

    /**
     * Adds a new post to a subject notice board.
     *
     * @param message The post to sendTeamInvitation.
     * @param listener A listener for receiving response of the request.
     */
    fun add(project: Project, message: Message, listener: OnCompleteListener<Void?>): Message {
        MyApplication.refToProjectMessages(project.ownerId, project.name)
                .push().apply { message.id = this.key.toString() }
                .setValue(message)
                .addOnCompleteListener(listener)
        return message
    }

    /**
     * Retrieves list of all messages in a project's chatroom.
     *
     * @param project The project to which this chatroom belongs.
     * @param listener A listener for receiving response of the request.
     */
    fun getMessagesByProject(project: Project, listener: (list: Message) -> Unit) {
        MyApplication.refToProjectMessages(project.ownerId, project.name)
                .addChildEventListener(object : ChildEventListener {
                    override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                        snapshot.getValue<Message>()?.let { listener(it) }
                    }

                    override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
                    override fun onChildRemoved(snapshot: DataSnapshot) {}
                    override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                    override fun onCancelled(error: DatabaseError) {}
                })
    }

}