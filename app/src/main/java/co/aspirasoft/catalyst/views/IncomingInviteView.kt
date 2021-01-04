package co.aspirasoft.catalyst.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import co.aspirasoft.catalyst.R
import co.aspirasoft.catalyst.databinding.ViewInviteIncomingBinding
import co.aspirasoft.catalyst.models.TeamInvite
import co.aspirasoft.view.BaseView

class IncomingInviteView : BaseView<TeamInvite> {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    private val binding = ViewInviteIncomingBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )

    fun setOnAcceptListener(l: OnClickListener) {
        binding.acceptButton.setOnClickListener(l)
    }

    fun setOnRejectListener(l: OnClickListener) {
        binding.rejectButton.setOnClickListener(l)
    }

    override fun updateView(model: TeamInvite) {
        binding.inviteMessage.text = context.getString(R.string.project_team_invite, model.sender, model.project)
    }

}