package co.aspirasoft.catalyst.models

import co.aspirasoft.model.BaseModel

/**
 * Team is a group of users who work together on a [Project].
 *
 * Every [project] has one team, and the project owner is the team [manager].
 *
 * @param project Name of the project this team is working on.
 * @param manager The id of the user who manages this team.
 *
 * @property members List of user IDs of joined team members.
 * @property invitedMembers List of user IDs of invited team members.
 *
 * @author saifkhichi96
 * @since 1.0.0
 */
data class Team(val project: String = "", val manager: String = "") : BaseModel() {

    private val members = HashMap<String, String>()

    private val invitedMembers = HashMap<String, String>()

    /**
     * Checks if a user has been invited to the team.
     *
     * @param uid The id of the user to check.
     * @return True if user is already invited, else False.
     */
    fun hasInvited(uid: String): Boolean {
        return invitedMembers.containsValue(uid)
    }

    /**
     * Checks if a user is member of the team.
     *
     * @param uid The id of the user to check.
     * @return True if user is a team member, else False.
     */
    fun hasMember(uid: String): Boolean {
        return members.containsValue(uid)
    }

    /**
     * Gets list of team members.
     *
     * @return List of team members.
     */
    fun members(): List<String> {
        return members.values.toList()
    }

}