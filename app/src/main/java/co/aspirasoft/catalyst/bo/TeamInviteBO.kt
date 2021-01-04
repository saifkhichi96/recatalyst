package co.aspirasoft.catalyst.bo

import co.aspirasoft.catalyst.MyApplication
import co.aspirasoft.catalyst.models.Team
import co.aspirasoft.catalyst.models.TeamInvite
import kotlinx.coroutines.tasks.await

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
            else -> {
                // Create a new unique key for the invite
                val ref = MyApplication.refToReceivedInvites(uid).push()
                val key = ref.key!!

                // Create a new invite
                val invite = TeamInvite(
                    id = key,
                    project = team.project,
                    sender = team.manager,
                    recipient = uid
                )

                // Save invite in database
                ref.setValue(invite).await()
                MyApplication.refToSentInvites(team.manager)
                    .child(key)
                    .setValue(invite)
                    .await()

                MyApplication.refToProjectTeam(team.manager, team.project)
                    .child("invitedMembers/")
                    .child(invite.recipient)
                    .setValue(invite.recipient)
                    .await()
            }
        }
    }

    /**
     * Accepts a team invite.
     *
     * @param invite The invite to accept.
     */
    suspend fun accept(invite: TeamInvite) {
        // Add recipient to the invited team
        MyApplication.refToProjectTeam(invite.sender, invite.project)
            .child("members/")
            .child(invite.recipient)
            .setValue(invite.recipient)
            .await()

        MyApplication.refToUser(invite.recipient)
            .child("teams/")
            .push()
            .setValue(Team(
                project = invite.project,
                manager = invite.sender
            ))

        // Remove invitation logs
        reject(invite)
    }

    /**
     * Rejects a team invite.
     *
     * @param invite The invite to reject.
     */
    suspend fun reject(invite: TeamInvite) {
        // Remove invite from received invites list
        MyApplication.refToReceivedInvites(invite.recipient)
            .child(invite.id)
            .removeValue()
            .await()

        // Remove invite from sent invites list
        MyApplication.refToSentInvites(invite.sender)
            .child(invite.id)
            .removeValue()
            .await()
    }

}