package co.aspirasoft.catalyst.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import co.aspirasoft.catalyst.dao.AccountsDao
import co.aspirasoft.catalyst.databinding.ViewMessageBinding
import co.aspirasoft.catalyst.databinding.ViewMessageOutgoingBinding
import co.aspirasoft.catalyst.models.Message
import co.aspirasoft.catalyst.utils.storage.ImageLoader
import co.aspirasoft.view.BaseView

class MessageView : BaseView<Message> {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    private var incomingBinding: ViewMessageBinding? = null
    private var outgoingBinding: ViewMessageOutgoingBinding? = null

    private val senderAvatar get() = incomingBinding?.senderAvatar ?: outgoingBinding?.senderAvatar
    private val senderName get() = incomingBinding?.messageSender ?: outgoingBinding?.messageSender
    private val body get() = incomingBinding?.messageBody ?: outgoingBinding?.messageBody
    private val timestamp get() = incomingBinding?.messageTimestamp ?: outgoingBinding?.messageTimestamp

    private fun createBindingsWith(message: Message) {
        this.removeAllViews()
        val inflater = LayoutInflater.from(context)
        if (message.isIncoming) {
            incomingBinding = ViewMessageBinding.inflate(inflater, this, true)
            outgoingBinding = null
        } else {
            incomingBinding = null
            outgoingBinding = ViewMessageOutgoingBinding.inflate(inflater, this, true)
        }
    }

    override fun updateView(model: Message) {
        createBindingsWith(model)

        body?.text = model.content
        timestamp?.text = model.datetime
        senderAvatar?.let {
            ImageLoader.loadUserAvatar(
                (context as AppCompatActivity),
                model.id,
                it
            )
        }

        senderName?.text = ""
        if (model.isIncoming) {
            AccountsDao.getById(model.sender) {
                senderName?.text = it?.name?.split(' ')?.get(0)
            }
        } else {
            senderAvatar?.visibility = View.GONE
            senderName?.visibility = View.GONE
        }
    }

    fun hideTimestamp() {
        timestamp?.visibility = View.GONE
    }

    fun hideSenderName() {
        senderName?.visibility = View.GONE
    }

    fun hideSenderAvatar() {
        senderAvatar?.visibility = View.INVISIBLE
    }

}