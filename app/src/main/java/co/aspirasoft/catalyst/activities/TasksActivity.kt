package co.aspirasoft.catalyst.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import co.aspirasoft.adapter.ModelViewAdapter
import co.aspirasoft.catalyst.MyApplication
import co.aspirasoft.catalyst.R
import co.aspirasoft.catalyst.activities.abs.DashboardChildActivity
import co.aspirasoft.catalyst.dao.TasksDao
import co.aspirasoft.catalyst.models.Project
import co.aspirasoft.catalyst.models.Task
import co.aspirasoft.catalyst.models.UserAccount
import co.aspirasoft.catalyst.views.TaskView
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.getValue
import kotlinx.android.synthetic.main.activity_list.*

/**
 * TestsActivity displays names of all tests of a subject
 *
 * Purpose of this activity is to show a mappedList of all graded tests
 * in a subject, and allow teachers to create and update test
 * grades.
 *
 * @author saifkhichi96
 * @since 1.0.0
 */
class TasksActivity : DashboardChildActivity() {

    private lateinit var adapter: TaskAdapter
    private lateinit var project: Project
    private val tasks = ArrayList<Task>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        this.project = intent.getSerializableExtra(MyApplication.EXTRA_PROJECT) as Project? ?: return finish()
        addButton.setOnClickListener { onAddTaskClicked() }
    }

    override fun updateUI(currentUser: UserAccount) {
        adapter = TaskAdapter(this, tasks)
        contentList.adapter = adapter
        TasksDao.getTasksByProject(project, object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                snapshot.getValue<Task>()?.let { task ->
                    if (!tasks.contains(task)) {
                        adapter.add(task)
                        adapter.notifyDataSetChanged()
                    }
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                snapshot.getValue<Task>()?.let { task ->
                    if (tasks.contains(task)) {
                        var oldTask: Task? = null
                        for (t in tasks) {
                            if (t == task) {
                                oldTask = t
                                break
                            }
                        }

                        oldTask?.let {
                            adapter.remove(oldTask)
                            adapter.add(task)
                            adapter.notifyDataSetChanged()
                        }
                    }
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                snapshot.getValue<Task>()?.let { task ->
                    if (tasks.contains(task)) {
                        var oldTask: Task? = null
                        for (t in tasks) {
                            if (t == task) {
                                oldTask = t
                                break
                            }
                        }

                        oldTask?.let {
                            adapter.remove(oldTask)
                            adapter.notifyDataSetChanged()
                        }
                    }
                }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun onAddTaskClicked() {
        startSecurely(TaskActivity::class.java, Intent().apply {
            putExtra(MyApplication.EXTRA_PROJECT, project)
        })
    }

    private inner class TaskAdapter(context: Context, val tasks: List<Task>)
        : ModelViewAdapter<Task>(context, tasks, TaskView::class) {

        override fun notifyDataSetChanged() {
            tasks.sortedByDescending { it.deadline }
            super.notifyDataSetChanged()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val v = super.getView(position, convertView, parent)
            v.setOnClickListener {
                startSecurely(TaskActivity::class.java, Intent().apply {
                    putExtra(MyApplication.EXTRA_PROJECT, project)
                    putExtra(MyApplication.EXTRA_TASK, tasks[position])
                })
            }
            return v
        }

    }

}