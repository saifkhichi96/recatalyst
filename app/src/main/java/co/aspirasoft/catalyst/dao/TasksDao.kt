package co.aspirasoft.catalyst.dao

import co.aspirasoft.catalyst.MyApplication
import co.aspirasoft.catalyst.models.Project
import co.aspirasoft.catalyst.models.Task
import com.google.firebase.database.ChildEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

object TasksDao {

    suspend fun add(task: Task, project: Project): Void? = withContext(Dispatchers.Main) {
        val ref = MyApplication.refToProject(project.ownerId, project.name)
                .child("tasks/")
                .push()
        ref.key?.let {
            task.id = it
            ref.setValue(task).await()
        }
    }

    fun getTasksByProject(project: Project, listener: ChildEventListener) {
        MyApplication.refToProject(project.ownerId, project.name)
                .child("tasks/")
                .addChildEventListener(listener)
    }

}