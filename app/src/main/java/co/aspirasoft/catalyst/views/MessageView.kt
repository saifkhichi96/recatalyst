package co.aspirasoft.catalyst.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import co.aspirasoft.catalyst.R
import co.aspirasoft.catalyst.dao.AccountsDao
import co.aspirasoft.catalyst.models.Message
import co.aspirasoft.catalyst.utils.storage.ImageLoader
import co.aspirasoft.view.BaseView

class MessageView : BaseView<Message> {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    private lateinit var senderAvatar: ImageView
    private lateinit var senderName: TextView
    private lateinit var body: TextView
    private lateinit var timestamp: TextView

    override fun updateView(model: Message) {
        this.removeAllViews()
        LayoutInflater.from(context).inflate(
                if (model.incoming) R.layout.view_message else R.layout.view_message_outgoing,
                this
        )
        senderAvatar = findViewById(R.id.userImage)
        senderName = findViewById(R.id.messageSender)
        body = findViewById(R.id.messageBody)
        timestamp = findViewById(R.id.messageTimestamp)

        body.text = model.content
        timestamp.text = model.datetime
        ImageLoader.with(context)
                .load(model.sender)
                .into(senderAvatar)

        senderName.text = ""
        if (model.incoming) {
            AccountsDao.getById(model.sender) {
                senderName.text = it?.name?.split(' ')?.get(0)
            }
        } else {
            findViewById<View>(R.id.senderAvatar).visibility = View.GONE
            senderName.visibility = View.GONE
        }
    }

    fun hideTimestamp() {
        timestamp.visibility = View.GONE
    }

    fun hideSenderName() {
        senderName.visibility = View.GONE
    }

    fun hideSenderAvatar() {
        senderAvatar.visibility = View.INVISIBLE
    }

}