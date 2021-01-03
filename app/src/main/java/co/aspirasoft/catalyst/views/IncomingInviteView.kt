package co.aspirasoft.catalyst.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import co.aspirasoft.catalyst.R
import co.aspirasoft.catalyst.models.TeamInvite
import co.aspirasoft.view.BaseView

class IncomingInviteView : BaseView<TeamInvite> {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    private var inviteMessage: TextView
    private var acceptButton: View
    private var rejectButton: View

    init {
        LayoutInflater.from(context).inflate(R.layout.view_invite_incoming, this)
        inviteMessage = findViewById(R.id.inviteMessage)
        acceptButton = findViewById(R.id.acceptButton)
        rejectButton = findViewById(R.id.rejectButton)
    }

    fun setOnAcceptListener(l: OnClickListener) {
        acceptButton.setOnClickListener(l)
    }

    fun setOnRejectListener(l: OnClickListener) {
        rejectButton.setOnClickListener(l)
    }

    override fun updateView(model: TeamInvite) {
        inviteMessage.text = context.getString(R.string.project_team_invite, model.sender, model.project)
    }

}