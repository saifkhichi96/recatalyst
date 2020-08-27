package co.aspirasoft.catalyst.models

import android.os.Parcelable
import co.aspirasoft.model.BaseModel
import kotlinx.android.parcel.Parcelize
import java.text.SimpleDateFormat
import java.util.*

@Parcelize
class Message(
        var postDate: Date = Date(System.currentTimeMillis()),
        var postContent: String = "",
        var postAuthor: String = "",
) : BaseModel(), Parcelable {

    val postDateAsString: String
        get() {
            val formatter = SimpleDateFormat("hh:mm a, EE dd MMM, yyyy", Locale.getDefault())
            return formatter.format(postDate)
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Message

        if (postDate != other.postDate) return false
        if (postContent != other.postContent) return false

        return true
    }

    override fun hashCode(): Int {
        return postDate.hashCode()
    }

}