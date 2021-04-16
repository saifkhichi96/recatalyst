package co.aspirasoft.catalyst.views

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import co.aspirasoft.catalyst.R
import co.aspirasoft.catalyst.databinding.ViewProjectBinding
import co.aspirasoft.catalyst.models.Project
import co.aspirasoft.catalyst.utils.ColorUtils
import co.aspirasoft.view.BaseView

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
        binding.projectDescription.text = model.description
        binding.projectColor.setCardBackgroundColor(ColorUtils.convertToColor(model))
    }

}