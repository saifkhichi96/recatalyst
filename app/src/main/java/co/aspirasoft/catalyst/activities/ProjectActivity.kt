package co.aspirasoft.catalyst.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.lifecycleScope
import co.aspirasoft.catalyst.MyApplication
import co.aspirasoft.catalyst.R
import co.aspirasoft.catalyst.activities.abs.DashboardChildActivity
import co.aspirasoft.catalyst.adapters.DocumentAdapter
import co.aspirasoft.catalyst.adapters.FileAdapter
import co.aspirasoft.catalyst.databinding.ActivityProjectBinding
import co.aspirasoft.catalyst.models.DocumentType
import co.aspirasoft.catalyst.models.Project
import co.aspirasoft.catalyst.models.RemoteFile
import co.aspirasoft.catalyst.models.UserAccount
import co.aspirasoft.catalyst.utils.FileUtils.getLastPathSegmentOnly
import co.aspirasoft.catalyst.utils.storage.FileManager
import co.aspirasoft.util.PermissionUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * The homepage of a [Project].
 *
 * Shows details of a project. This page is where users see projects, and
 * perform and allowed actions on them. If the project owner is viewing
 * their own project, they can edit the project. Other users only see what
 * they are allowed to see, depending on their relationship with the
 * project.
 *
 * @property binding The bindings to XML views.
 * @property fm The [FileManager] for managing project files.
 * @property project The project to display.
 * @property projectDocumentsAdapter A [DocumentAdapter] for showing project documents.
 * @property projectFilesAdapter A [FileAdapter] for showing project files.
 * @property projectFiles List of project files.
 * @property isEditable True if owner is viewing project, else false.
 *
 * @author saifkhichi96
 * @since 1.0.0
 */
class ProjectActivity : DashboardChildActivity() {

    private lateinit var binding: ActivityProjectBinding
    private lateinit var fm: FileManager
    private lateinit var project: Project
    private lateinit var projectDocumentsAdapter: DocumentAdapter
    private lateinit var projectFilesAdapter: FileAdapter
    private val projectFiles = ArrayList<RemoteFile>()
    private var isEditable: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProjectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        project = intent.getSerializableExtra(MyApplication.EXTRA_PROJECT) as Project? ?: return finish()
        isEditable = project.ownerId == currentUser.id
        if (isEditable) {
            binding.addAssetButton.visibility = View.VISIBLE
        }

        fm = FileManager.newInstance(this, "${project.ownerId}/projects/${project.name}/assets/")
        projectFilesAdapter = FileAdapter(this, projectFiles, fm)
        binding.contentList.adapter = projectFilesAdapter

        projectDocumentsAdapter = DocumentAdapter(this, project, DocumentType.values())
        binding.documentsList.adapter = projectDocumentsAdapter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                RC_PICK_FILE -> data?.data?.getLastPathSegmentOnly(this)?.let {
                    confirmFileUpload(it, data.data!!)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == RC_WRITE_PERMISSION) {
            val allowed = grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
            if (allowed) pickFile()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_project, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_chatroom -> {
                openChatroom()
                true
            }
            R.id.action_tasks -> {
                showTask()
                true
            }
            R.id.action_team -> {
                showProjectTeam()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun updateUI(currentUser: UserAccount) {
        supportActionBar?.title = project.name
        binding.clientName.text = "" // FIXME: project.clientName
        binding.clientContact.text = "" // FIXME: project.clientContact
        binding.comments.text = when {
            project.description.isNullOrBlank() -> getString(R.string.notes_none)
            else -> project.description
        }

        projectFiles.clear()
        fm.listAll { items -> items.forEach { onProjectFileReceived(it) } }
    }

    /**
     * Called when a project file is received.
     *
     * Displays the file details in project files list.
     *
     * @param reference A [StorageReference] to the file.
     */
    private fun onProjectFileReceived(reference: StorageReference) = lifecycleScope.launch {
        try {
            // Read file metadata
            val metadata = reference.metadata.await()
            val filename = reference.name

            projectFiles.add(RemoteFile(filename, metadata))
            projectFilesAdapter.notifyDataSetChanged()

            binding.assetsSpace.visibility = View.GONE
        } catch (ex: Exception) {
            Log.d(getString(R.string.app_name), ex.message ?: ex.javaClass.simpleName)
        }
    }

    /**
     * Opens the project chatroom.
     */
    private fun openChatroom() {
        startSecurely(ChatroomActivity::class.java, Intent().apply {
            putExtra(MyApplication.EXTRA_PROJECT, project)
        })
    }

    /**
     * Opens the project tasks page.
     */
    private fun showTask() {
        startSecurely(TasksActivity::class.java, Intent().apply {
            putExtra(MyApplication.EXTRA_PROJECT, project)
        })
    }

    /**
     * Opens the project team page.
     */
    private fun showProjectTeam() {
        startSecurely(TeamActivity::class.java, Intent().apply {
            putExtra(MyApplication.EXTRA_PROJECT, project)
        })
    }

    /**
     * Asks permission (if not already granted) before picking a file from device storage.
     *
     * When asking permission, retries picking the file in [onRequestPermissionsResult]
     * if user grants permission.
     */
    fun pickFileIfAllowed(v: View) {
        if (PermissionUtils.requestPermissionIfNeeded(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                getString(R.string.permission_storage),
                RC_WRITE_PERMISSION
            )
        ) pickFile()
    }

    /**
     * Picks a file from device storage.
     */
    private fun pickFile() {
        val i = Intent(Intent.ACTION_GET_CONTENT)
        i.type = "application/*"
        i.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(i, RC_PICK_FILE)
    }

    /**
     * Asks for confirmation before uploading a file.
     *
     * @param filename The name of the file to upload.
     * @param data The contents ot the file.
     */
    private fun confirmFileUpload(filename: String, data: Uri) {
        MaterialAlertDialogBuilder(this)
            .setTitle(String.format(getString(R.string.file_upload), project.name, project.ownerId))
            .setMessage(String.format(getString(R.string.upload_confirm), filename))
            .setPositiveButton(android.R.string.yes) { dialog, _ ->
                dialog.dismiss()
                uploadFile(filename, data)
            }
            .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                dialog.cancel()
            }
            .show()
    }

    /**
     * Uploads a file to server.
     *
     * @param filename The name of the file to upload.
     * @param data The contents ot the file.
     */
    private fun uploadFile(filename: String, data: Uri) = lifecycleScope.launch {
        val status = Snackbar.make(binding.contentList, getString(R.string.uploading), Snackbar.LENGTH_INDEFINITE)
        status.show()
        try {
            fm.upload(filename, data)?.let { metadata ->
                projectFilesAdapter.add(RemoteFile(filename, metadata))
                projectFilesAdapter.notifyDataSetChanged()

                runOnUiThread {
                    status.setText(getString(R.string.uploaded))
                    Handler().postDelayed({ status.dismiss() }, 2500L)
                }
            }
        } catch (ex: Exception) {
            runOnUiThread {
                status.setText(ex.message ?: getString(R.string.upload_failed))
                Handler().postDelayed({ status.dismiss() }, 2500L)
            }
        }
    }

    companion object {
        private const val RC_PICK_FILE = 100
        private const val RC_WRITE_PERMISSION = 200
    }

}