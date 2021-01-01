package co.aspirasoft.catalyst.models

import android.os.Parcelable
import co.aspirasoft.model.BaseModel
import kotlinx.android.parcel.Parcelize
import java.text.SimpleDateFormat
import java.util.*

@Parcelize
class Message(var content: String, var sender: String, var timestamp: Date = Date(System.currentTimeMillis()))
    : BaseModel(), Parcelable {

    constructor() : this("", "", Date(System.currentTimeMillis()))

    var id: String = ""

    val datetime: String
        get() {
            val formatter = SimpleDateFormat("hh:mm a, EE dd MMM, yyyy", Locale.getDefault())
            return formatter.format(timestamp)
        }

    @Transient
    var incoming: Boolean = true

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Message

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

}