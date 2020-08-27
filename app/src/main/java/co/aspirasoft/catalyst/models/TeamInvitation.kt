package co.aspirasoft.catalyst.models

import android.os.Parcelable
import co.aspirasoft.model.BaseModel
import kotlinx.android.parcel.Parcelize

/**
 * A data class which represents an invitation to join a project team.
 *
 * @property id The id of the invitation.
 * @property project Name of the project to which this team belongs.
 * @property team Name of the project team for which this invitation is created.
 * @property inviter User id of the invitation sender. This is usually the team manager.
 * @property invitee Email address of the invitation recipient.
 * @property status The [InvitationStatus].
 */
@Parcelize
data class TeamInvitation(
        val id: String,
        val project: String,
        val team: String,
        val inviter: String,
        val invitee: String,
        val status: InvitationStatus = InvitationStatus.Pending,
) : Parcelable, BaseModel() {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TeamInvitation

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

}