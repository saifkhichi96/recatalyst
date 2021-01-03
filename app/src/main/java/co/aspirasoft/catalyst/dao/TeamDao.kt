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
     * Fetches a team from database.
     *
     * This involves a network request and happens asynchronously. A callback function
     * is used for handling the response of the request when it is available.
     *
     * @param project The project to which this team belongs.
     * @param listener A listener for receiving response of the request.
     */
    fun get(project: Project, listener: (team: Team?) -> Unit) {
        MyApplication.refToProjectTeam(project.ownerId, project.name)
                .getOrNull<Team>(listener)
    }

    fun getAll(uid: String, listener: (teams: List<Team>) -> Unit) {
        MyApplication.refToUser(uid)
                .child("teams/")
                .list(listener)
    }

    fun getMembers(project: Project, listener: (team: List<String>) -> Unit) {
        MyApplication.refToProjectTeam(project.ownerId, project.name)
                .child("members/")
                .list(listener)
    }

}