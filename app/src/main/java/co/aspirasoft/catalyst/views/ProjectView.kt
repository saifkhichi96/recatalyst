package co.aspirasoft.catalyst.views

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.TextView
import androidx.annotation.ColorInt
import co.aspirasoft.catalyst.R
import co.aspirasoft.catalyst.models.Project
import co.aspirasoft.view.BaseView
import com.google.android.material.card.MaterialCardView

class ProjectView : BaseView<Project> {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    private var model: Project? = null

    private val projectColor: MaterialCardView
    private val projectName: TextView
    private val projectDescription: TextView

    init {
        LayoutInflater.from(context).inflate(R.layout.view_project, this)
        projectColor = findViewById(R.id.projectColor)
        projectName = findViewById(R.id.projectName)
        projectDescription = findViewById(R.id.projectDescription)
    }

    override fun updateView(model: Project) {
        this.model = model
        projectName.text = model.name
        projectDescription.text = model.description
        projectColor.setCardBackgroundColor(convertToColor(model))
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