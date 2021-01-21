package co.aspirasoft.catalyst.dao

import android.util.Log
import co.aspirasoft.catalyst.models.Document
import co.aspirasoft.catalyst.models.Project
import co.aspirasoft.catalyst.utils.db.RealtimeDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import dev.aspirasoft.utils.firebase.database.DatabaseEvent
import dev.aspirasoft.utils.firebase.database.observeChildren
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.tasks.await


object DocumentsDao {

    private val Project.documents: DatabaseReference
        get() = RealtimeDatabase.project(ownerId, name).child("documents/")

    @ExperimentalCoroutinesApi
    suspend fun observeDocuments(project: Project, callback: (DatabaseEvent<Document>) -> Unit) {
        project.documents
            .observeChildren<Document>()
            .collect { callback(it) }
    }

    /**
     * Adds a new [Document] to a project.
     *
     * Each document has a unique code name. Trying to add multiple documents
     * with same name will fail. In that case, a new version for the existing
     * document with that name must be created.
     *
     * @param document The document to add.
     * @param project The project in which to add.
     * @see addVersion
     */
    suspend fun add(document: Document, project: Project) {
        try {
            project.documents
                .push()
                .setValue(document)
                .await()
        } catch (ex: Exception) {
            Log.e("RECatalyst", ex.message ?: "" + ex.stackTrace)
        }
    }

    /**
     * Gets the latest version of a project document.
     *
     * @param id The id of the document to get
     */
    suspend fun get(id: String, project: Project): Document? = runCatching {
        project.documents
            .orderByChild(Document::id.name)
            .equalTo(id)
            .limitToFirst(1)
            .get().await()
            .getValue<Document>()
    }.getOrNull()

    suspend fun update(document: Document, project: Project) {
        project.documents.orderByChild("name")
            .equalTo(document.name)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach { child ->
                        child.key?.let { name ->
                            project.documents
                                .child(name)
                                .setValue(document)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }

    suspend fun delete(name: String, project: Project) {
        project.documents
            .get().await()
            .children
            .filter { it.getValue<Document>()?.id == name }
            .forEach {
                it.key?.let { key -> project.documents.child(key).removeValue() }
            }
    }

    suspend fun addVersion(document: Document, project: Project) {

    }

}