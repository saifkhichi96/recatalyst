package co.aspirasoft.catalyst.activities

import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.lifecycleScope
import co.aspirasoft.catalyst.MyApplication
import co.aspirasoft.catalyst.R
import co.aspirasoft.catalyst.activities.abs.DashboardChildActivity
import co.aspirasoft.catalyst.adapters.SectionAdapter
import co.aspirasoft.catalyst.dao.ProjectsDao
import co.aspirasoft.catalyst.models.Document
import co.aspirasoft.catalyst.models.Project
import co.aspirasoft.catalyst.models.UserAccount
import co.aspirasoft.catalyst.utils.storage.FileManager
import co.aspirasoft.util.ViewUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_editor.*
import kotlinx.coroutines.launch

class EditorActivity : DashboardChildActivity() {

    private lateinit var sectionAdapter: SectionAdapter
    private val sectionNumbers = ArrayList<String>()

    private lateinit var project: Project
    private lateinit var document: Document
    private lateinit var fm: FileManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)

        // Get intent extras
        val i = intent
        document = i.getSerializableExtra(MyApplication.EXTRA_DOCUMENT) as Document? ?: return finish()
        project = i.getSerializableExtra(MyApplication.EXTRA_PROJECT) as Project? ?: return finish()
        fm = FileManager.projectDocsManager(this, project)

        // Display door/window details
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = document.name
            subtitle = String.format("v%s", document.version)
        }

        // Display property fields
        sectionAdapter = SectionAdapter(this@EditorActivity, document, document.editableSections())
        sectionsList.adapter = sectionAdapter

        headingsView.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
            override fun onTabSelected(tab: TabLayout.Tab) {
                selectedSection.text = tab.text
            }
        })

        val sectionHeadings = document.headings()
        sectionHeadings.forEach { headingsView.addTab(headingsView.newTab().setText(it)) }

        backButton.isEnabled = false
        if (sectionHeadings.size <= 1) nextButton.isEnabled = false

        sectionNumbers.apply { sectionHeadings.forEach { this.add(it.split("[.:]".toRegex())[0]) } }
        sectionAdapter.filter.filter(sectionNumbers[0])

        nextButton.setOnClickListener { nextSection() }
        backButton.setOnClickListener { prevSection() }
    }

    private fun prevSection() {
        val selected = headingsView.selectedTabPosition
        navigateTo(selected - 1)

        backButton.isEnabled = selected - 1 != 0
        nextButton.isEnabled = true
    }

    private fun nextSection() {
        val selected = headingsView.selectedTabPosition
        navigateTo(selected + 1)

        nextButton.isEnabled = selected + 1 != headingsView.tabCount - 1
        backButton.isEnabled = true
    }

    private fun navigateTo(position: Int) {
        if (position >= 0 && position < headingsView.tabCount) {
            headingsView.getTabAt(position)?.select()
            sectionAdapter.filter.filter(sectionNumbers[position])
        }
    }

    override fun updateUI(currentUser: UserAccount) {

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_editor, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_save_file -> {
                saveProject()
                return true
            }
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun saveProject() {
        val status = Snackbar.make(sectionsList, getString(R.string.status_saving), Snackbar.LENGTH_INDEFINITE)
        status.show()

        project.addDocument(document)
        try {
            lifecycleScope.launch {
                ProjectsDao.add(project)
                fm.upload(document)
            }
            status.setText(getString(R.string.status_saved))
            Handler().postDelayed({ finish() }, 2500L)
        } catch (ex: Exception) {
            status.dismiss()
            val error = ex.message ?: getString(R.string.status_save_failed)
            ViewUtils.showError(sectionsList, error)
        }
    }

    override fun onBackPressed() {
        MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.close_editor))
                .setMessage(getString(R.string.unsaved_changes))
                .setPositiveButton(android.R.string.yes) { _, _ ->
                    super@EditorActivity.onBackPressed()
                }
                .setNeutralButton(R.string.label_save) { _, _ ->
                    saveProject()
                    super@EditorActivity.onBackPressed()
                }
                .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
    }

}