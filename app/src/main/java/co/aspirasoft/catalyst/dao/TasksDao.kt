package co.aspirasoft.catalyst.dao

import co.aspirasoft.catalyst.models.Project
import co.aspirasoft.catalyst.models.Task
import co.aspirasoft.catalyst.utils.db.RealtimeDatabase
import com.google.firebase.database.ChildEventListener
import kotlinx.coroutines.tasks.await


object TasksDao {

    /**
     * Adds a new [Task] to the [Project].
     *
     * @param task The task to add.
     * @param project The project to add the task to.
     */
    suspend fun add(task: Task, project: Project) {
        val ref = RealtimeDatabase.project(project.ownerId, project.name)
            .child("tasks/")
            .push()
        ref.key?.let {
            task.id = it
            ref.setValue(task).await()
        }
    }

    /**
     * Gets list of a [Project]'s tasks.
     *
     * @param project The project whose tasks to get.
     * @param receiver Callback for receiving the result.
     */
    fun getProjectTasks(project: Project, receiver: ChildEventListener) {
        RealtimeDatabase.project(project.ownerId, project.name)
            .child("tasks/")
            .addChildEventListener(receiver)
    }

}