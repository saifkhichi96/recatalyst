package co.aspirasoft.catalyst.views

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.annotation.ColorInt
import co.aspirasoft.catalyst.R
import co.aspirasoft.catalyst.databinding.ViewProjectBinding
import co.aspirasoft.catalyst.models.Project
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
        binding.projectColor.setCardBackgroundColor(convertToColor(model))
    }

    @ColorInt
    private fun convertToColor(o: Any): Int {
        return try {
            val i = o.hashCode()
            Color.parseColor("#FF" + Integer.toHexString(i shr 16 and 0xFF) +
                    Integer.toHexString(i shr 8 and 0xFF) +
                    Integer.toHexString(i and 0xFF))
        } catch (ignored: Exception) {
            context.getColor(R.color.colorAccent)
        }
    }

}