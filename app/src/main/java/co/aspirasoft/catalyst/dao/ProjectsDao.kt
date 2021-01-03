package co.aspirasoft.catalyst.dao

import co.aspirasoft.catalyst.MyApplication
import co.aspirasoft.catalyst.models.Project
import co.aspirasoft.catalyst.utils.list
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

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
     * Adds a new subject to database.
     *
     * @param project The subject to sendTeamInvitation.
     */
    suspend fun add(project: Project): Void? = withContext(Dispatchers.Main) {
        MyApplication.refToProject(project.ownerId, project.name)
                .setValue(project)
                .await()
    }

    /**
     * Retrieves a list of projects owned by a user.
     *
     * @param uid The id of the user.
     * @param listener A listener for receiving response of the request.
     */
    fun getAll(uid: String, listener: (projects: List<Project>) -> Unit) {
        MyApplication.refToProjects(uid).list(listener)
    }

}