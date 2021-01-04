package co.aspirasoft.catalyst.models

import co.aspirasoft.model.BaseModel

/**
 * An invitation to collaborate on a [Project].
 *
 * @property id The id of the invitation.
 * @property project Name of the project.
 * @property sender User id of the invitation sender. This is usually the team manager.
 * @property recipient User id of the invitation recipient.
 */
data class TeamInvite(val id: String = "") : BaseModel() {

    var project: String = ""
    var sender: String = ""
    var recipient: String = ""

}