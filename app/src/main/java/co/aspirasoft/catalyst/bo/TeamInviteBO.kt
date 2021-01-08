package co.aspirasoft.catalyst.bo

import co.aspirasoft.catalyst.dao.TeamDao
import co.aspirasoft.catalyst.models.Team
import co.aspirasoft.catalyst.models.TeamInvite

/**
 * Defines business-level logic for managing connection requests.
 *
 * Purpose of this class is to provide an API for sending, accepting and rejecting
 * connection requests.
 *
 * @author saifkhichi96
 * @since 1.0.0
 */
object TeamInviteBO {

    /**
     * Sends a [TeamInvite] to a user.
     *
     * @param uid The user id of the recipient.
     * @param team The team for which to send the invite.
     * @throws IllegalArgumentException Exception thrown if recipient already member of team.
     * @throws IllegalStateException Exception thrown if recipient has already been invited.
     */
    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    suspend fun send(uid: String, team: Team) {
        when {
            // FIXME: case: user already in team (as member or manager)
            team.hasMember(uid) -> throw IllegalArgumentException()
            team.manager == uid -> throw IllegalArgumentException()

            // FIXME: case: user invited before
            team.hasInvited(uid) -> throw IllegalStateException()

            // case: all okay, send the invitation
            else -> TeamDao.addInvite(uid, team)
        }
    }

    /**
     * Accepts a team invite.
     *
     * @param invite The invite to accept.
     */
    suspend fun accept(invite: TeamInvite) {
        TeamDao.addTeamMember(invite.recipient, Team(project = invite.project, manager = invite.sender))
        reject(invite)
    }

    /**
     * Rejects a team invite.
     *
     * @param invite The invite to reject.
     */
    suspend fun reject(invite: TeamInvite) {
        TeamDao.deleteReceivedInvite(invite.recipient, invite.id)
        TeamDao.deleteSentInvite(invite.sender, invite.id)
    }

}