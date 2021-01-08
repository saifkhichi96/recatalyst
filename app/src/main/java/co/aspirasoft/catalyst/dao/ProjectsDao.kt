package co.aspirasoft.catalyst.dao

import co.aspirasoft.catalyst.models.Project
import co.aspirasoft.catalyst.utils.db.RealtimeDatabase
import co.aspirasoft.catalyst.utils.db.getOrNull
import co.aspirasoft.catalyst.utils.db.list
import kotlinx.coroutines.tasks.await


/**
 * A data access class to read details of subjects.
 *
 * Purpose of this class is to provide methods for communicating with the
 * Firebase backend to access data related to subjects.
 *
 * @author saifkhichi96
 * @since 1.0.0
 */
object ProjectsDao {

    /**
     * Adds a new [Project] to the database.
     *
     * @param project The project to add.
     */
    suspend fun add(project: Project) {
        RealtimeDatabase.project(project.ownerId, project.name)
            .setValue(project)
            .await()
    }

    /**
     * Gets a [Project] from the database.
     *
     * @param uid The id of the project owner.
     * @param project The name of the project.
     * @param receiver Callback for receiving the result.
     */
    fun get(uid: String, project: String, receiver: (Project?) -> Unit) {
        RealtimeDatabase.project(uid, project).getOrNull(receiver)
    }

    /**
     * Gets list of a user's [Project]s.
     *
     * @param uid The id of the user.
     * @param receiver Callback for receiving the result.
     */
    fun getUserProjects(uid: String, receiver: (List<Project>) -> Unit) {
        RealtimeDatabase.projects(uid).list(receiver)
    }

}