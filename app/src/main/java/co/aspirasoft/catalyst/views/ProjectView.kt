package co.aspirasoft.catalyst.views

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import co.aspirasoft.catalyst.R
import co.aspirasoft.catalyst.databinding.ViewProjectBinding
import co.aspirasoft.catalyst.models.Project
import co.aspirasoft.catalyst.models.TaskStatus
import co.aspirasoft.catalyst.utils.ColorUtils
import co.aspirasoft.view.BaseView
import java.text.DateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class ProjectView : BaseView<Project> {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    private val binding = ViewProjectBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )

    override fun updateView(model: Project) {
        binding.projectName.text = model.name
        binding.projectStatus.text = context.getString(
            R.string.project_due_date,
            model.getStatus().toString(context),
            when (model.getStatus()) {
                TaskStatus.OVERDUE -> {
                    // Show status description in WARNING color
                    binding.projectStatus.setTextColor(context.getColor(R.color.colorWarning))

                    // Figure out how much the project is overdue by
                    val deadline = Date(model.deadline).time
                    val now = Calendar.getInstance().time.time
                    var diff = TimeUnit.DAYS.convert(now - deadline, TimeUnit.MILLISECONDS).toInt()
                    when {
                        diff <= 30 -> context.resources.getQuantityString(R.plurals.days, diff, diff)
                        else -> {
                            diff /= 30
                            when {
                                diff <= 12 -> context.resources.getQuantityString(R.plurals.months, diff, diff)
                                else -> {
                                    diff /= 12
                                    context.resources.getQuantityString(R.plurals.years, diff, diff)
                                }
                            }
                        }
                    }
                }
                // CASE: project not overdue (on-track or completed)
                else -> {
                    DateFormat.getDateInstance(
                        DateFormat.MEDIUM,
                        Locale.getDefault()
                    ).format(Date(model.deadline))
                }
            })

        val iconText = "${model.name.first().toUpperCase()}${model.name.last().toLowerCase()}"
        binding.projectIcon.text = iconText

        updateColorsWith(model)
        showProgress(model.getProgress())
    }

    private fun updateColorsWith(project: Project) {
        val colorAccent = ColorUtils.convertToColor(context, project)
        binding.projectIconBg.setCardBackgroundColor(colorAccent)
        binding.projectProgressBar.setIndicatorColor(colorAccent)
        binding.projectProgressText.setTextColor(colorAccent)
        binding.projectProgressBar.trackColor = Color.valueOf(colorAccent).let {
            Color.valueOf(it.red(), it.green(), it.blue(), 0.25f).toArgb()
        }
    }

    private fun showProgress(progress: Int) {
        binding.projectProgressBar.progress = progress
        binding.projectProgressText.text = if (progress == 100) "✓" else "${progress}%"
    }

}