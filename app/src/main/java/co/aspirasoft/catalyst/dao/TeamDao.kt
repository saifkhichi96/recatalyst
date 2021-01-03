package co.aspirasoft.catalyst.dao

import co.aspirasoft.catalyst.MyApplication
import co.aspirasoft.catalyst.models.Project
import co.aspirasoft.catalyst.models.Team
import co.aspirasoft.catalyst.utils.getOrNull
import co.aspirasoft.catalyst.utils.list

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
     * Gets a [Team] from the database.
     *
     * @param project The project to which this team belongs.
     * @param receiver Callback for receiving the results.
     */
    fun getProjectTeam(project: Project, receiver: (Team?) -> Unit) {
        MyApplication.refToProjectTeam(project.ownerId, project.name)
                .getOrNull(receiver)
    }

    /**
     * Gets list of a user's [Team]s.
     *
     * @param uid The id of the user.
     * @param receiver Callback for receiving the result.
     */
    fun getUserTeams(uid: String, receiver: (List<Team>) -> Unit) {
        MyApplication.refToUser(uid)
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
        MyApplication.refToProjectTeam(team.manager, team.project)
                .child("members/")
                .list(receiver)
    }

}