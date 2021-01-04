package co.aspirasoft.catalyst.activities

import android.content.Intent
import android.os.Bundle
import co.aspirasoft.catalyst.MyApplication
import co.aspirasoft.catalyst.activities.abs.DashboardChildActivity
import co.aspirasoft.catalyst.adapters.TaskAdapter
import co.aspirasoft.catalyst.dao.TasksDao
import co.aspirasoft.catalyst.databinding.ActivityListBinding
import co.aspirasoft.catalyst.models.Project
import co.aspirasoft.catalyst.models.Task
import co.aspirasoft.catalyst.models.UserAccount
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.getValue

/**
 * The tasks page of a [Project].
 *
 * Project members can see and manage project tasks here.
 *
 * @property binding The bindings to XML views.
 * @property project The [Project] whose tasks to show.
 * @property adapter A [TaskAdapter] to show the [tasks] list.
 * @property tasks List of project [Task]s.
 *
 * @author saifkhichi96
 * @since 1.0.0
 */
class TasksActivity : DashboardChildActivity() {

    private lateinit var binding: ActivityListBinding
    private lateinit var project: Project
    private lateinit var adapter: TaskAdapter
    private val tasks = ArrayList<Task>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        this.project = intent.getSerializableExtra(MyApplication.EXTRA_PROJECT) as Project? ?: return finish()
        binding.addButton.setOnClickListener { createNewTask() }
    }

    override fun updateUI(currentUser: UserAccount) {
        adapter = TaskAdapter(this, tasks, project)
        binding.contentList.adapter = adapter

        TasksDao.getProjectTasks(project, object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                snapshot.getValue<Task>()?.let { onTaskReceived(it) }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                snapshot.getValue<Task>()?.let { task ->
                    tasks.firstOrNull { it == task }?.let { adapter.remove(it) }
                    onTaskReceived(task)
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                snapshot.getValue<Task>()?.let { task ->
                    tasks.firstOrNull { it == task }?.let {
                        adapter.remove(it)
                        adapter.notifyDataSetChanged()
                    }
                }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    /**
     * Called when a new task is received from database.
     *
     * New task is added to the tasks list if it does not already exist.
     *
     * @param task: The new [Task] received.
     */
    private fun onTaskReceived(task: Task) {
        if (!tasks.contains(task)) {
            adapter.add(task)
            adapter.notifyDataSetChanged()
        }
    }

    private fun createNewTask() {
        startSecurely(TaskActivity::class.java, Intent().apply {
            putExtra(MyApplication.EXTRA_PROJECT, project)
        })
    }

}