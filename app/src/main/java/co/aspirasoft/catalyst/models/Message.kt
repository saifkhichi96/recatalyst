package co.aspirasoft.catalyst.models

import co.aspirasoft.model.BaseModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * A chat message sent by a user.
 *
 * @property id The unique id of the message.
 * @property content The body of the message.
 * @property sender The id of the message sender.
 * @property timestamp The local system time in milliseconds when message was sent.
 * @property datetime The [timestamp] as a formatted string.
 * @property isIncoming True if this is an incoming message, and False if outgoing.
 *
 * @author saifkhichi96
 * @since 1.0.0
 */
data class Message(var id: String = "") : BaseModel() {

    var content: String = ""
    var sender: String = ""
    var timestamp: Date = Date(System.currentTimeMillis())
    val datetime: String
        get() = SimpleDateFormat("hh:mm a, EE, MMM dd", Locale.getDefault()).format(timestamp)

    @Transient
    var isIncoming: Boolean = true

}