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
 * @property members List of team members.
 *
 * @author saifkhichi96
 * @since 1.0.0
 */
class Team(var project: String, var manager: String) : BaseModel() {

    /**
     * Default public constructor.
     *
     * A no-argument constructor is required for serialization.
     */
    constructor() : this("", "")

    private var members = ArrayList<String>()

    private var invitedMembers = ArrayList<String>()

    /**
     * Invites a user to join the team.
     *
     * An invitation is successfully sent only if the user is not already
     * invited, team member or team manager.
     *
     * @param uid The id of the user to invite.
     * @return True if invitation successfully sent, else False.
     */
    fun inviteMember(uid: String): Boolean {
        if (uid == manager || invitedMembers.contains(uid) || members.contains(uid)) {
            return false
        }

        invitedMembers.add(uid)

        // TODO: Send an invitation
        return true
    }

    /**
     * Allows a user to accept invitation to join the team.
     *
     * A user who holds a valid invitation can call this method to accept and join
     * the team. If no valid invitation corresponding to the requesting user can
     * be found, joining fails.
     *
     * @param uid The id of the user who wants to join the team.
     * @return True if user successfully joined the team, else False.
     */
    fun acceptInvitation(uid: String): Boolean {
        if (invitedMembers.contains(uid)) {
            invitedMembers.remove(uid)
            members.add(uid)

            // TODO: Update team information in the database
            return true
        }

        return false
    }

    /**
     * Checks if a user is member of the team.
     *
     * @param uid The if of the user to check.
     * @return True if user is a team member, else False.
     */
    fun hasMember(uid: String): Boolean {
        return members.contains(uid)
    }

    /**
     * Gets list of team members.
     *
     * @return List of team members.
     */
    fun members(): ArrayList<String> {
        return members
    }

}