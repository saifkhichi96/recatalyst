package co.aspirasoft.catalyst.models

import co.aspirasoft.model.BaseModel

/**
 * A data class which represents an invitation to join a project team.
 *
 * @property id The id of the invitation.
 * @property project Name of the project to which this team belongs.
 * @property sender User id of the invitation sender. This is usually the team manager.
 * @property recipient User id of the invitation recipient.
 */
data class TeamInvite(
    val id: String,
    val project: String,
    val sender: String,
    val recipient: String,
) : BaseModel() {

    constructor() : this("", "", "", "")

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TeamInvite

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}