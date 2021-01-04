package co.aspirasoft.catalyst.views

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.annotation.ColorInt
import co.aspirasoft.catalyst.R
import co.aspirasoft.catalyst.databinding.ViewTeamBinding
import co.aspirasoft.catalyst.models.Team
import co.aspirasoft.view.BaseView

class TeamView : BaseView<Team> {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    private val binding = ViewTeamBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )

    override fun updateView(model: Team) {
        binding.projectName.text = model.project
        binding.projectDescription.text = model.manager
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