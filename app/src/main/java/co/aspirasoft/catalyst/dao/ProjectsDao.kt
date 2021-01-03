package co.aspirasoft.catalyst.dao

import co.aspirasoft.catalyst.MyApplication
import co.aspirasoft.catalyst.models.Project
import co.aspirasoft.catalyst.utils.getOrNull
import co.aspirasoft.catalyst.utils.list
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
        MyApplication.refToProject(project.ownerId, project.name)
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
        MyApplication.refToProject(uid, project).getOrNull(receiver)
    }

    /**
     * Gets list of a user's [Project]s.
     *
     * @param uid The id of the user.
     * @param receiver Callback for receiving the result.
     */
    fun getUserProjects(uid: String, receiver: (List<Project>) -> Unit) {
        MyApplication.refToProjects(uid).list(receiver)
    }

}