package co.aspirasoft.catalyst.activities

import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.lifecycle.lifecycleScope
import co.aspirasoft.catalyst.MyApplication
import co.aspirasoft.catalyst.R
import co.aspirasoft.catalyst.activities.abs.DashboardChildActivity
import co.aspirasoft.catalyst.dao.TasksDao
import co.aspirasoft.catalyst.models.Project
import co.aspirasoft.catalyst.models.Task
import co.aspirasoft.catalyst.models.UserAccount
import co.aspirasoft.util.InputUtils.isNotBlank
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_task_details.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author saifkhichi96
 * @since 1.0.0
 */
class TaskActivity : DashboardChildActivity() {

    private lateinit var project: Project

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_details)

        // Must know which subject's grades we are managing
        project = intent.getSerializableExtra(MyApplication.EXTRA_PROJECT) as Project? ?: return finish()
        val task = intent.getSerializableExtra(MyApplication.EXTRA_TASK) as Task?

        // if YES, then we are editing/viewing an existing test
        if (task != null) {
            // Disable edit options
            setEditEnabled(false)

            // Show test name in action bar
            nameField.setText(task.name)
            descriptionField.setText(task.description)
            deadlineField.setText(task.deadline)
            completeButton.isChecked = task.isCompleted
        }

        // if NO, then we are creating a new test
        else {
            // Enable edit options
            setEditEnabled(true)

            val formatter = SimpleDateFormat(Task.DATE_FORMAT, Locale.getDefault())
            deadlineField.setText(formatter.format(System.currentTimeMillis()))
            deadlineButton.setOnClickListener {
                val picker = MaterialDatePicker.Builder.datePicker()
                        .setTitleText(getString(R.string.hint_due_date))
                        .build()

                picker.addOnPositiveButtonClickListener {
                    deadlineField.setText(formatter.format(Date(it)))
                }
                picker.show(supportFragmentManager, picker.toString())
            }
        }
    }

    override fun updateUI(currentUser: UserAccount) {

    }

    fun onSaveButtonClicked(v: View) {
        val name = nameField.text.toString().trim()
        val description = descriptionField.text.toString().trim()
        val deadline = deadlineField.text.toString().trim()
        val isCompleted = completeButton.isChecked

        if (nameField.isNotBlank(true) && descriptionField.isNotBlank(true)) {
            val task = Task(name, description)
            task.deadline = deadline
            task.isCompleted = isCompleted
            saveTask(task)
        }
    }

    private fun saveTask(task: Task) {
        val status = Snackbar.make(nameField, getString(R.string.status_saving), Snackbar.LENGTH_INDEFINITE)
        status.show()
        try {
            lifecycleScope.launch { TasksDao.add(task, project) }
            status.setText(getString(R.string.status_saved))
            Handler().postDelayed({
                status.dismiss()
                finish()
            }, 1500L)
        } catch (ex: Exception) {

        }
    }

    private fun setEditEnabled(enabled: Boolean) {
        nameField.isEnabled = enabled
        descriptionField.isEnabled = enabled
        deadlineButton.isEnabled = enabled
        completeButton.isEnabled = enabled

        completeButton.visibility = if (enabled) View.GONE else View.VISIBLE
        saveButton.visibility = if (enabled) View.VISIBLE else View.GONE
    }

}