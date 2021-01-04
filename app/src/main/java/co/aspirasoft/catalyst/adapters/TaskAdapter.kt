package co.aspirasoft.catalyst.adapters

import android.content.Intent
import android.view.View
import android.view.ViewGroup
import co.aspirasoft.adapter.ModelViewAdapter
import co.aspirasoft.catalyst.MyApplication
import co.aspirasoft.catalyst.activities.TaskActivity
import co.aspirasoft.catalyst.activities.abs.SecureActivity
import co.aspirasoft.catalyst.models.Project
import co.aspirasoft.catalyst.models.Task
import co.aspirasoft.catalyst.views.TaskView

class TaskAdapter(
    private val context: SecureActivity,
    private val tasks: List<Task>,
    private val project: Project,
) : ModelViewAdapter<Task>(context, tasks, TaskView::class) {

    override fun notifyDataSetChanged() {
        tasks.sortedByDescending { it.deadline }
        super.notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val v = super.getView(position, convertView, parent)
        v.setOnClickListener {
            context.startSecurely(TaskActivity::class.java, Intent().apply {
                putExtra(MyApplication.EXTRA_PROJECT, project)
                putExtra(MyApplication.EXTRA_TASK, tasks[position])
            })
        }
        return v
    }
}