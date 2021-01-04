package co.aspirasoft.catalyst.activities

import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.lifecycle.lifecycleScope
import co.aspirasoft.catalyst.MyApplication
import co.aspirasoft.catalyst.R
import co.aspirasoft.catalyst.activities.abs.DashboardChildActivity
import co.aspirasoft.catalyst.dao.TasksDao
import co.aspirasoft.catalyst.databinding.ActivityTaskDetailsBinding
import co.aspirasoft.catalyst.models.Project
import co.aspirasoft.catalyst.models.Task
import co.aspirasoft.catalyst.models.UserAccount
import co.aspirasoft.util.InputUtils.isNotBlank
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author saifkhichi96
 * @since 1.0.0
 */
class TaskActivity : DashboardChildActivity() {

    private lateinit var binding: ActivityTaskDetailsBinding

    private lateinit var project: Project

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Must know which subject's grades we are managing
        project = intent.getSerializableExtra(MyApplication.EXTRA_PROJECT) as Project? ?: return finish()
        val task = intent.getSerializableExtra(MyApplication.EXTRA_TASK) as Task?

        // if YES, then we are editing/viewing an existing test
        if (task != null) {
            // Disable edit options
            setEditEnabled(false)

            // Show test name in action bar
            binding.nameField.setText(task.name)
            binding.descriptionField.setText(task.description)
            binding.deadlineField.setText(task.deadline)
            binding.completeButton.isChecked = task.isCompleted
        }

        // if NO, then we are creating a new test
        else {
            // Enable edit options
            setEditEnabled(true)

            val formatter = SimpleDateFormat(Task.DATE_FORMAT, Locale.getDefault())
            binding.deadlineField.setText(formatter.format(System.currentTimeMillis()))
            binding.deadlineButton.setOnClickListener {
                val picker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText(getString(R.string.deadline))
                    .build()

                picker.addOnPositiveButtonClickListener {
                    binding.deadlineField.setText(formatter.format(Date(it)))
                }
                picker.show(supportFragmentManager, picker.toString())
            }
        }
    }

    override fun updateUI(currentUser: UserAccount) {

    }

    fun onSaveButtonClicked(v: View) {
        val name = binding.nameField.text.toString().trim()
        val description = binding.descriptionField.text.toString().trim()
        val deadline = binding.deadlineField.text.toString().trim()
        val isCompleted = binding.completeButton.isChecked

        if (binding.nameField.isNotBlank(true) && binding.descriptionField.isNotBlank(true)) {
            val task = Task(name, description)
            task.deadline = deadline
            task.isCompleted = isCompleted
            saveTask(task)
        }
    }

    private fun saveTask(task: Task) {
        val status = Snackbar.make(binding.nameField, getString(R.string.saving), Snackbar.LENGTH_INDEFINITE)
        status.show()
        try {
            lifecycleScope.launch { TasksDao.add(task, project) }
            status.setText(getString(R.string.save_changes_success))
            Handler().postDelayed({
                status.dismiss()
                finish()
            }, 1500L)
        } catch (ex: Exception) {

        }
    }

    private fun setEditEnabled(enabled: Boolean) {
        binding.nameField.isEnabled = enabled
        binding.descriptionField.isEnabled = enabled
        binding.deadlineButton.isEnabled = enabled
        binding.completeButton.isEnabled = enabled

        binding.completeButton.visibility = if (enabled) View.GONE else View.VISIBLE
        binding.saveButton.visibility = if (enabled) View.VISIBLE else View.GONE
    }

}