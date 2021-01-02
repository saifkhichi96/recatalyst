package co.aspirasoft.catalyst.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import co.aspirasoft.adapter.ModelViewAdapter
import co.aspirasoft.catalyst.MyApplication
import co.aspirasoft.catalyst.R
import co.aspirasoft.catalyst.activities.abs.DashboardActivity
import co.aspirasoft.catalyst.dao.ProjectsDao
import co.aspirasoft.catalyst.dialogs.CreateProjectDialog
import co.aspirasoft.catalyst.dialogs.SettingsDialog
import co.aspirasoft.catalyst.models.Project
import co.aspirasoft.catalyst.models.UserAccount
import co.aspirasoft.catalyst.views.ProjectView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_dashboard.*

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

    /**
     * List of projects owned by the current user.
     */
    private val projects: ArrayList<Project> = ArrayList()

    /**
     * An adapter to populate a UI with user's [projects].
     */
    private lateinit var projectAdapter: ProjectAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        projectAdapter = ProjectAdapter(this, projects)
        projectsList.adapter = projectAdapter

        attendanceButton.setOnClickListener { startSecurely(ConnectionsActivity::class.java) }
        createProjectButton.setOnClickListener { onCreateProjectClicked() }

        settingsButton.setOnClickListener {
            SettingsDialog.Builder(this).show()
        }
    }

    override fun onResume() {
        super.onResume()
        if (Firebase.auth.currentUser == null) {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }
    }

    override fun updateUI(currentUser: UserAccount) {
        userSummaryView.apply {
            this.bindWithModel(currentUser)
            this.setOnProfileButtonClickedListener {
                startSecurely(ProfileActivity::class.java, Intent().apply {
                    putExtra(MyApplication.EXTRA_PROFILE_USER, currentUser)
                })
            }
        }

        getProjectsList(currentUser.id)
    }

    private fun createProject(okListener: ((project: Project) -> Unit)) {
        val dialog = CreateProjectDialog.newInstance(currentUser.id)
        dialog.onOkListener = okListener
        dialog.show(supportFragmentManager, dialog.toString())
    }

    private fun getProjectsList(uid: String) {
        ProjectsDao.getAll(uid) {
            projects.clear()
            projects.addAll(it)
            projects.sortBy { team -> team.name }
            projectAdapter.notifyDataSetChanged()
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

    private inner class ProjectAdapter(context: Context, val projects: List<Project>)
        : ModelViewAdapter<Project>(context, projects, ProjectView::class) {

        override fun notifyDataSetChanged() {
            super.notifyDataSetChanged()
            projectsSpace.visibility = if (projects.isNotEmpty()) View.GONE else View.VISIBLE
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

}