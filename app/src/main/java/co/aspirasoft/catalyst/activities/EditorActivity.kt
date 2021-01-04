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
import co.aspirasoft.catalyst.databinding.ActivityEditorBinding
import co.aspirasoft.catalyst.models.Document
import co.aspirasoft.catalyst.models.Project
import co.aspirasoft.catalyst.models.UserAccount
import co.aspirasoft.catalyst.utils.FileUtils
import co.aspirasoft.catalyst.utils.FileUtils.openInExternalApp
import co.aspirasoft.catalyst.utils.storage.FileManager
import co.aspirasoft.util.ViewUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.launch
import java.io.FileOutputStream

class EditorActivity : DashboardChildActivity() {

    private lateinit var binding: ActivityEditorBinding

    private lateinit var sectionAdapter: SectionAdapter
    private val sectionNumbers = ArrayList<String>()

    private lateinit var project: Project
    private lateinit var document: Document
    private lateinit var fm: FileManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get intent extras
        val i = intent
        document = i.getSerializableExtra(MyApplication.EXTRA_DOCUMENT) as Document? ?: return finish()
        project = i.getSerializableExtra(MyApplication.EXTRA_PROJECT) as Project? ?: return finish()
        fm = FileManager.projectDocsManager(this, project)

        // Display door/window details
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            title = document.name
            subtitle = String.format("v%s", document.version)
        }

        // Display property fields
        sectionAdapter = SectionAdapter(this@EditorActivity, document, document.editableSections())
        binding.sectionsList.adapter = sectionAdapter

        binding.headingsView.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
            override fun onTabSelected(tab: TabLayout.Tab) {
                binding.selectedSection.text = tab.text
            }
        })

        val sectionHeadings = document.headings()
        sectionHeadings.forEach { binding.headingsView.addTab(binding.headingsView.newTab().setText(it)) }

        binding.backButton.isEnabled = false
        if (sectionHeadings.size <= 1) binding.nextButton.isEnabled = false

        sectionNumbers.apply { sectionHeadings.forEach { this.add(it.split("[.:]".toRegex())[0]) } }
        sectionAdapter.filter.filter(sectionNumbers[0])

        binding.nextButton.setOnClickListener { nextSection() }
        binding.backButton.setOnClickListener { prevSection() }
    }

    private fun prevSection() {
        val selected = binding.headingsView.selectedTabPosition
        navigateTo(selected - 1)

        binding.backButton.isEnabled = selected - 1 != 0
        binding.nextButton.isEnabled = true
    }

    private fun nextSection() {
        val selected = binding.headingsView.selectedTabPosition
        navigateTo(selected + 1)

        binding.nextButton.isEnabled = selected + 1 != binding.headingsView.tabCount - 1
        binding.backButton.isEnabled = true
    }

    private fun navigateTo(position: Int) {
        if (position >= 0 && position < binding.headingsView.tabCount) {
            binding.headingsView.getTabAt(position)?.select()
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
            R.id.action_share_document -> {
                shareProject()
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
        // FIXME: Chats, members, and tasks are deleted when saving project!!!
        val status = Snackbar.make(binding.sectionsList, getString(R.string.saving), Snackbar.LENGTH_INDEFINITE)
        status.show()

        project.addDocument(document)
        try {
            lifecycleScope.launch {
                ProjectsDao.add(project)
                fm.upload(document)
            }
            status.setText(getString(R.string.save_changes_success))
            Handler().postDelayed({ finish() }, 2500L)
        } catch (ex: Exception) {
            status.dismiss()
            val error = ex.message ?: getString(R.string.save_changes_error)
            ViewUtils.showError(binding.sectionsList, error)
        }
    }

    private fun shareProject() {
        // Save document as a PDF
        val name = String.format("%s (v%s).pdf", document.name, document.version)
        val file = FileUtils.createTempFile(name, fm.cache)
        FileOutputStream(file).use { fos ->
            fos.write(document.toByteArray())
        }

        // Open PDF in external app
        file.openInExternalApp(this)
    }

    override fun onBackPressed() {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.close_editor))
            .setMessage(getString(R.string.ignore_unsaved_changes))
            .setPositiveButton(android.R.string.yes) { _, _ ->
                super@EditorActivity.onBackPressed()
            }
            .setNeutralButton(R.string.save) { _, _ ->
                saveProject()
                super@EditorActivity.onBackPressed()
            }
            .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

}