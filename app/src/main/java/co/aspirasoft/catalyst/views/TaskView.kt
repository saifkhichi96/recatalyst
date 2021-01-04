package co.aspirasoft.catalyst.views

import android.content.Context
import android.graphics.Paint
import android.util.AttributeSet
import android.view.LayoutInflater
import co.aspirasoft.catalyst.databinding.ViewTaskBinding
import co.aspirasoft.catalyst.models.Task
import co.aspirasoft.view.BaseView

class TaskView : BaseView<Task> {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    private val binding = ViewTaskBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )

    override fun updateView(model: Task) {
        binding.nameField.text = model.name
        binding.descriptionField.text = model.description
        binding.completeButton.isChecked = model.isCompleted

        if (model.isCompleted) {
            binding.nameField.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            binding.descriptionField.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            binding.completeButton.isEnabled = false
        }

        binding.completeButton.setOnCheckedChangeListener { _, isChecked ->
            model.isCompleted = isChecked
        }
    }

}