package co.aspirasoft.catalyst.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import co.aspirasoft.adapter.ModelViewAdapter
import co.aspirasoft.catalyst.MyApplication
import co.aspirasoft.catalyst.R
import co.aspirasoft.catalyst.activities.abs.DashboardActivity
import co.aspirasoft.catalyst.activities.abs.SecureActivity
import co.aspirasoft.catalyst.bo.AuthBO
import co.aspirasoft.catalyst.dao.ProjectsDao
import co.aspirasoft.catalyst.dao.TeamDao
import co.aspirasoft.catalyst.databinding.ActivityDashboardBinding
import co.aspirasoft.catalyst.dialogs.CreateProjectDialog
import co.aspirasoft.catalyst.dialogs.SettingsDialog
import co.aspirasoft.catalyst.models.Project
import co.aspirasoft.catalyst.models.Team
import co.aspirasoft.catalyst.models.UserAccount
import co.aspirasoft.catalyst.views.ProjectView
import co.aspirasoft.catalyst.views.TeamView
import java.text.DateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * Dashboard is the homepage of a user.
 *
 * Purpose of this activity is to provide a homepage for all users which shows
 * their content and allows them to navigate to other parts of the app.
 *
 * @author saifkhichi96
 * @since 1.0.0
 */
class DashboardActivity : DashboardActivity() {

    private lateinit var binding: ActivityDashboardBinding

    /**
     * List of projects owned by the current user.
     */
    private val projects: ArrayList<Project> = ArrayList()

    /**
     * An adapter to populate a UI with user's [projects].
     */
    private lateinit var projectAdapter: ProjectAdapter

    /**
     * List of teams the current user has joined.
     */
    private val teams: ArrayList<Team> = ArrayList()

    /**
     * An adapter to populate a UI with user's [teams].
     */
    private lateinit var teamAdapter: TeamAdapter

    private val mGreetingPeriod = 10 * 60 * 1000L // every 10 minutes
    private val mGreetingTask = object : TimerTask() {
        override fun run() {
            val now = Calendar.getInstance()
            val formatter = DateFormat.getDateInstance(DateFormat.DEFAULT, Locale.getDefault())
            binding.date.text = formatter.format(now.time)

            val hour = now.get(Calendar.HOUR_OF_DAY)
            binding.greeting.text = when (hour) {
                in 4..11 -> getString(R.string.greeting_morning)
                in 12..17 -> getString(R.string.greeting_afternoon)
                in 18..22 -> getString(R.string.greeting_evening)
                else -> getString(R.string.greeting_night)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Timer(true).scheduleAtFixedRate(mGreetingTask, 0, mGreetingPeriod)

        projectAdapter = ProjectAdapter(this, projects)
        binding.projectsList.adapter = projectAdapter

        teamAdapter = TeamAdapter(this, teams)
        binding.teamsList.adapter = teamAdapter

        // Set click listeners for sidebar buttons
        binding.connectionsButton.setOnClickListener { startSecurely(ConnectionsActivity::class.java) }
        binding.invitesButton.setOnClickListener { startSecurely(TeamInvitesActivity::class.java) }
        binding.notificationsButton.setOnClickListener { }
        binding.settingsButton.setOnClickListener { SettingsDialog.Builder(this).show() }

        // Set other click listeners
        binding.createProjectButton.setOnClickListener { onCreateProjectClicked() }

        binding.teamsButton.setOnClickListener { showTeamsFragment() }
        binding.projectsButton.setOnClickListener { showProjectsFragment() }
    }

    override fun onResume() {
        super.onResume()
        if (AuthBO.currentUser == null) {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }
    }

    override fun updateUI(currentUser: UserAccount) {
        val firstName = currentUser.name.split(' ').first()
        binding.username.text = String.format(getString(R.string.greeting), firstName)

        binding.userSummaryView.apply {
            this.bindWithModel(currentUser)
            this.setOnAvatarClickedListener {
                startSecurely(ProfileActivity::class.java, Intent().apply {
                    putExtra(MyApplication.EXTRA_PROFILE_USER, currentUser)
                })
            }
        }

        getProjectsList(currentUser.id)
        getTeamsList(currentUser.id)
    }

    private fun createProject(okListener: ((project: Project) -> Unit)) {
        val dialog = CreateProjectDialog.newInstance(currentUser.id)
        dialog.onOkListener = okListener
        dialog.show(supportFragmentManager, dialog.toString())
    }

    private fun getProjectsList(uid: String) {
        ProjectsDao.getUserProjects(uid) {
            projects.clear()
            projects.addAll(it)
            projects.sortBy { team -> team.name }
            projectAdapter.notifyDataSetChanged()
        }
    }

    private fun getTeamsList(uid: String) {
        TeamDao.getUserTeams(uid) {
            teams.clear()
            teams.addAll(it)
            teams.sortBy { team -> team.project }
            teamAdapter.notifyDataSetChanged()
        }
    }

    private fun onCreateProjectClicked() {
        createProject { project ->
            projects.add(project)
            projectAdapter.notifyDataSetChanged()

            startSecurely(ProjectActivity::class.java, Intent().apply {
                putExtra(MyApplication.EXTRA_PROJECT, project)
            })
        }
    }

    /**
     * Brings the fragment with projects list to front.
     */
    private fun showProjectsFragment() {
        binding.teamsButton.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
        binding.teamsSection.visibility = View.GONE

        binding.projectsButton.setTextColor(Color.BLACK)
        binding.projectsSection.visibility = View.VISIBLE
    }

    /**
     * Brings the fragment with list of joined teams to front.
     */
    private fun showTeamsFragment() {
        binding.projectsButton.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
        binding.projectsSection.visibility = View.GONE

        binding.teamsButton.setTextColor(Color.BLACK)
        binding.teamsSection.visibility = View.VISIBLE
    }

    private inner class ProjectAdapter(context: SecureActivity, val projects: List<Project>)
        : ModelViewAdapter<Project>(context, projects, ProjectView::class) {

        override fun notifyDataSetChanged() {
            super.notifyDataSetChanged()
            binding.projectsSpace.visibility = if (projects.isNotEmpty()) View.GONE else View.VISIBLE
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val v = super.getView(position, convertView, parent)
            v.setOnClickListener {
                startSecurely(ProjectActivity::class.java, Intent().apply {
                    putExtra(MyApplication.EXTRA_PROJECT, projects[position])
                })
            }
            return v
        }

    }

    private inner class TeamAdapter(context: SecureActivity, val teams: List<Team>)
        : ModelViewAdapter<Team>(context, teams, TeamView::class) {

        override fun notifyDataSetChanged() {
            super.notifyDataSetChanged()
            binding.teamsSpace.visibility = if (teams.isNotEmpty()) View.GONE else View.VISIBLE
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val v = super.getView(position, convertView, parent)
            v.setOnClickListener {
                // TODO: Open the project which owns this team
            }
            return v
        }

    }

}