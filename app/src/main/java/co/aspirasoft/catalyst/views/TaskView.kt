package co.aspirasoft.catalyst.views

import android.content.Context
import android.graphics.Paint
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.CheckBox
import android.widget.TextView
import co.aspirasoft.catalyst.R
import co.aspirasoft.catalyst.models.Task
import co.aspirasoft.view.BaseView

class TaskView : BaseView<Task> {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    private var nameView: TextView
    private var descriptionView: TextView
    private var completeButton: CheckBox

    init {
        LayoutInflater.from(context).inflate(R.layout.view_task, this)
        nameView = findViewById(R.id.nameField)
        descriptionView = findViewById(R.id.descriptionField)
        completeButton = findViewById(R.id.completeButton)
    }

    override fun updateView(model: Task) {
        nameView.text = model.name
        descriptionView.text = model.description
        completeButton.isChecked = model.isCompleted

        if (model.isCompleted) {
            nameView.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            descriptionView.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            completeButton.isEnabled = false
        }

        completeButton.setOnCheckedChangeListener { _, isChecked ->
            model.isCompleted = isChecked
        }
    }

}