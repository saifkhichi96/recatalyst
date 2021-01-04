package co.aspirasoft.catalyst.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.lifecycleScope
import co.aspirasoft.catalyst.MyApplication
import co.aspirasoft.catalyst.R
import co.aspirasoft.catalyst.activities.abs.DashboardChildActivity
import co.aspirasoft.catalyst.adapters.DocumentAdapter
import co.aspirasoft.catalyst.adapters.FileAdapter
import co.aspirasoft.catalyst.dao.ProjectsDao
import co.aspirasoft.catalyst.databinding.ActivityProjectBinding
import co.aspirasoft.catalyst.models.Asset
import co.aspirasoft.catalyst.models.DocumentType
import co.aspirasoft.catalyst.models.Project
import co.aspirasoft.catalyst.models.UserAccount
import co.aspirasoft.catalyst.utils.FileUtils.getLastPathSegmentOnly
import co.aspirasoft.catalyst.utils.storage.FileManager
import co.aspirasoft.util.PermissionUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


/**
 * SubjectActivity shows details of a [Project].
 *
 * @author saifkhichi96
 * @since 1.0.0
 */
class ProjectActivity : DashboardChildActivity() {

    private lateinit var binding: ActivityProjectBinding

    private lateinit var project: Project
    private var editable: Boolean = false

    private lateinit var mAssetManager: FileManager
    private var mAssetAdapter: FileAdapter? = null
    private val mAssets = ArrayList<Asset>()

    private var mDocAdapter: DocumentAdapter? = null

    private var pickRequestCode = RESULT_ACTION_PICK_ASSETS

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProjectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        project = intent.getSerializableExtra(MyApplication.EXTRA_PROJECT) as Project? ?: return finish()
        editable = project.ownerId == currentUser.id
        if (editable) {
            binding.addAssetButton.visibility = View.VISIBLE
        }

        mAssetManager = FileManager.newInstance(this, "${project.ownerId}/projects/${project.name}/assets/")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                RESULT_ACTION_PICK_ASSETS -> {
                    data?.data?.getLastPathSegmentOnly(this)?.let { filename ->
                        uploadFile(mAssetManager, filename, data.data!!, mAssetAdapter)
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == RC_WRITE_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickFile(pickRequestCode)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_project, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_chatroom -> {
                onChatroomClicked()
                true
            }
            R.id.action_tasks -> {
                onTasksClicked()
                true
            }
            R.id.action_team -> {
                onTeamClicked()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun updateUI(currentUser: UserAccount) {
        // Show subject details
        supportActionBar?.title = project.name
        binding.clientName.text = "" // FIXME: project.clientName
        binding.clientContact.text = "" // FIXME: project.clientContact
        binding.comments.text = when {
            project.description.isNullOrBlank() -> getString(R.string.notes_none)
            else -> project.description
        }

        // Show project assets
        showProjectContents()
    }

    fun onAddAssetsClicked(v: View) {
        if (PermissionUtils.requestPermissionIfNeeded(
                        this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        getString(R.string.permission_storage),
                        RC_WRITE_PERMISSION
                )) {
            pickFile(RESULT_ACTION_PICK_ASSETS)
        }
    }

    private fun onChatroomClicked() {
        startSecurely(ChatroomActivity::class.java, Intent().apply {
            putExtra(MyApplication.EXTRA_PROJECT, project)
        })
    }

    private fun onTasksClicked() {
        startSecurely(TasksActivity::class.java, Intent().apply {
            putExtra(MyApplication.EXTRA_PROJECT, project)
        })
    }

    private fun onTeamClicked() {
        startSecurely(TeamActivity::class.java, Intent().apply {
            putExtra(MyApplication.EXTRA_PROJECT, project)
        })
    }

    private fun showProjectContents() {
        showProjectAssets()
        showProjectDocs()
    }

    private fun showProjectAssets() {
        if (mAssetAdapter == null) {
            mAssetAdapter = FileAdapter(this, mAssets, mAssetManager)
            binding.contentList.adapter = mAssetAdapter
        }

        mAssetManager.listAll { items ->
            mAssets.clear()
            items.forEach { reference ->
                lifecycleScope.launch {
                    try {
                        val metadata = reference.metadata.await()
                        mAssets.add(Asset(reference.name, metadata))
                        mAssetAdapter?.notifyDataSetChanged()
                    } catch (ex: Exception) {
                        // TODO: Handle failure
                    }
                }
            }

            runOnUiThread {
                binding.assetsSpace.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
            }
        }
    }

    private fun showProjectDocs() {
        if (mDocAdapter == null) {
            mDocAdapter = DocumentAdapter(this, project, DocumentType.values()).apply {
                setOnItemCreateListener {
                    project.addDocument(it)
                    try {
                        lifecycleScope.launch { ProjectsDao.add(project) }
                        notifyDataSetChanged()
                    } catch (ex: Exception) {
                        // TODO
                    }
                }

                setOnItemEditListener {
                    startSecurely(EditorActivity::class.java, Intent().apply {
                        putExtra(MyApplication.EXTRA_DOCUMENT, it)
                        putExtra(MyApplication.EXTRA_PROJECT, project)
                    })
                }

                setOnItemDeleteListener { document ->
                    MaterialAlertDialogBuilder(context)
                            .setTitle(getString(R.string.delete_document))
                            .setMessage(getString(R.string.delete_document_confirm))
                            .setPositiveButton(R.string.delete) { _, _ ->
                                if (project.removeDocument(document.name)) {
                                    try {
                                        lifecycleScope.launch { ProjectsDao.add(project) }
                                        notifyDataSetChanged()
                                    } catch (ex: Exception) {
                                        // TODO:
                                    }
                                }
                            }
                        .setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.cancel() }
                        .show()
                }
            }
            binding.documentsList.adapter = mDocAdapter
        }
    }

    private fun pickFile(requestCode: Int) {
        this.pickRequestCode = requestCode

        val i = Intent(Intent.ACTION_GET_CONTENT)
        i.type = "application/*"
        i.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(i, requestCode)
    }

    private fun uploadFile(fm: FileManager, filename: String, data: Uri, adapter: FileAdapter?) {
        MaterialAlertDialogBuilder(this)
                .setTitle(String.format(getString(R.string.file_upload), project.name, project.ownerId))
                .setMessage(String.format(getString(R.string.upload_confirm), filename))
                .setPositiveButton(android.R.string.yes) { dialog, _ ->
                    dialog.dismiss()
                    val status =
                        Snackbar.make(binding.contentList, getString(R.string.uploading), Snackbar.LENGTH_INDEFINITE)
                    status.show()
                    GlobalScope.launch(Dispatchers.Main) {
                        try {
                            fm.upload(filename, data)?.let { metadata ->
                                adapter?.add(Asset(filename, metadata))
                                adapter?.notifyDataSetChanged()

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
                }
                .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                    dialog.cancel()
                }
                .show()
    }

    companion object {
        private const val RESULT_ACTION_PICK_ASSETS = 100
        private const val RC_WRITE_PERMISSION = 200
    }

}