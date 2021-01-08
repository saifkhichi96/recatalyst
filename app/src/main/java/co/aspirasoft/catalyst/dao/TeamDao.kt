package co.aspirasoft.catalyst.dao

import co.aspirasoft.catalyst.models.Project
import co.aspirasoft.catalyst.models.Team
import co.aspirasoft.catalyst.models.TeamInvite
import co.aspirasoft.catalyst.utils.db.RealtimeDatabase
import co.aspirasoft.catalyst.utils.db.getOrNull
import co.aspirasoft.catalyst.utils.db.list
import kotlinx.coroutines.tasks.await

/**
 * A data access class to manage [Team]s.
 *
 * Purpose of this class is to provide an API for communicating with the
 * backend to access and manage project teams.
 *
 * @author saifkhichi96
 * @since 1.0.0
 */
object TeamDao {

    /**
     * Invites a [user] to a [team].
     *
     * @param user The id of the invite recipient.
     * @param team The team to which the recipient is invited.
     */
    suspend fun addInvite(user: String, team: Team) {
        // Create a new unique key for the invite
        val ref = RealtimeDatabase.teamInvitesReceived(user).push()
        val key = ref.key!!

        // Create a new invite
        val invite = TeamInvite(key)
        invite.project = team.project
        invite.sender = team.manager
        invite.recipient = user

        // Save invite in database
        ref.setValue(invite).await()
        RealtimeDatabase.teamInvitesSent(team.manager)
            .child(key)
            .setValue(invite)
            .await()

        RealtimeDatabase.team(team.manager, team.project)
            .child("invitedMembers/")
            .child(invite.recipient)
            .setValue(invite.recipient)
            .await()
    }

    /**
     * Adds a [user] to a [team].
     *
     * The function assumes that the [user] possess a valid [TeamInvite]
     * to the [team].
     *
     * @param user The id of the new member.
     * @param team The team in which to add the member.
     */
    suspend fun addTeamMember(user: String, team: Team) {
        RealtimeDatabase.team(team.manager, team.project)
            .child("members/")
            .child(user)
            .setValue(user)
            .await()

        RealtimeDatabase.user(user)
            .child("teams/")
            .push()
            .setValue(team)
    }

    /**
     * Deletes an incoming team invite.
     *
     * @param recipient The id of the request recipient.
     * @param inviteId The id of the team invite.
     */
    suspend fun deleteReceivedInvite(recipient: String, inviteId: String) {
        RealtimeDatabase.teamInvitesReceived(recipient)
            .child(inviteId)
            .removeValue()
            .await()
    }

    /**
     * Deletes an outgoing team invite.
     *
     * @param sender The id of the user who sent the invite.
     * @param inviteId The id of the team invite.
     */
    suspend fun deleteSentInvite(sender: String, inviteId: String) {
        RealtimeDatabase.teamInvitesSent(sender)
            .child(inviteId)
            .removeValue()
            .await()
    }

    /**
     * Gets a [Team] from the database.
     *
     * @param project The project to which this team belongs.
     * @param receiver Callback for receiving the results.
     */
    fun getProjectTeam(project: Project, receiver: (Team?) -> Unit) {
        RealtimeDatabase.team(project.ownerId, project.name)
            .getOrNull(receiver)
    }

    /**
     * Gets list of a user's [Team]s.
     *
     * @param uid The id of the user.
     * @param receiver Callback for receiving the result.
     */
    fun getUserTeams(uid: String, receiver: (List<Team>) -> Unit) {
        RealtimeDatabase.user(uid)
            .child("teams/")
            .list(receiver)
    }

    /**
     * Gets list of a [Team]'s members.
     *
     * @param team The team whose members to get.
     * @param receiver Callback for receiving the result.
     */
    fun getTeamMembers(team: Team, receiver: (List<String>) -> Unit) {
        RealtimeDatabase.team(team.manager, team.project)
            .child("members/")
            .list(receiver)
    }

    /**
     * Gets list of all received [TeamInvite]s.
     *
     * @param uid The id of the user whose invites to get.
     * @param receiver Callback for receiving the results.
     */
    fun getReceivedInvites(uid: String, receiver: (List<TeamInvite>) -> Unit) {
        RealtimeDatabase.teamInvitesReceived(uid).list(receiver)
    }

}