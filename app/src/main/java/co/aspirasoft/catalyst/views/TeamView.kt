package co.aspirasoft.catalyst.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import co.aspirasoft.catalyst.databinding.ViewTeamBinding
import co.aspirasoft.catalyst.models.Team
import co.aspirasoft.catalyst.utils.ColorUtils
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
        binding.projectColor.setCardBackgroundColor(ColorUtils.convertToColor(context, model))
    }

}