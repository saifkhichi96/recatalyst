package co.aspirasoft.catalyst.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.lifecycle.lifecycleScope
import co.aspirasoft.catalyst.MyApplication
import co.aspirasoft.catalyst.R
import co.aspirasoft.catalyst.activities.EditorActivity
import co.aspirasoft.catalyst.activities.abs.SecureActivity
import co.aspirasoft.catalyst.dao.ProjectsDao
import co.aspirasoft.catalyst.databinding.ViewDocumentBinding
import co.aspirasoft.catalyst.models.Document
import co.aspirasoft.catalyst.models.DocumentType
import co.aspirasoft.catalyst.models.Project
import co.aspirasoft.catalyst.views.DocumentView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

class DocumentAdapter(
    private val activity: SecureActivity,
    private val project: Project,
    private val documents: Array<KClass<out DocumentType>>,
) : ArrayAdapter<KClass<out DocumentType>>(activity, -1, documents) {

    private val gradients = arrayOf(
        R.drawable.gradient_aqua_splash,
        R.drawable.gradient_eternal_constance,
        R.drawable.gradient_happy_fisher,
        R.drawable.gradient_plum_plate
    )

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        val documentView = when (view) {
            null -> {
                val binding = ViewDocumentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                val viewHolder = DocumentView(binding)
                view = binding.root
                view.tag = viewHolder
                viewHolder
            }
            else -> view.tag as DocumentView
        }

        val documentType = documents[position].constructors.elementAt(0).call()
        documentView.itemCard.setBackgroundResource(gradients[position % gradients.size])
        documentView.nameView.text = documentType.name
        when (val document = project.getDocument(documentType.name)) {
            null -> showBlankDocument(documentView, documentType)
            else -> showExistingDocument(documentView, document)
        }
        return view
    }

    private fun showBlankDocument(holder: DocumentView, type: DocumentType) {
        holder.versionView.text = String.format("Version: N/A")
        holder.itemCard.setOnClickListener { createNewDocument(type) }
    }

    private fun showExistingDocument(holder: DocumentView, document: Document) {
        holder.versionView.text = String.format("Version: ${document.version}")
        holder.itemCard.setOnClickListener { openDocumentInEditor(document) }
        holder.itemCard.setOnLongClickListener {
            confirmDocumentDeletion(document)
            true
        }
    }

    private fun createNewDocument(type: DocumentType) = activity.lifecycleScope.launch {
        try {
            // Create a new document
            val newDocument = Document.Builder()
                .setProject(project.name)
                .setType(type)
                .setVersion("1.0.0")
                .build()

            // Save document and upload it to database
            project.addDocument(newDocument)
            ProjectsDao.add(project)

            // Reflect changes in UI
            notifyDataSetChanged()

            // Open document in editor
            openDocumentInEditor(newDocument)
        } catch (ex: Exception) {
            // TODO: Handle errors during document creation
        }
    }

    /**
     * Opens a document in the [EditorActivity].
     *
     * @param document The [Document] to open.
     */
    private fun openDocumentInEditor(document: Document) {
        activity.startSecurely(EditorActivity::class.java, Intent().apply {
            putExtra(MyApplication.EXTRA_DOCUMENT, document)
            putExtra(MyApplication.EXTRA_PROJECT, project)
        })
    }

    /**
     * Asks for confirmation before deleting a document.
     *
     * @param document The [Document] to delete.
     */
    private fun confirmDocumentDeletion(document: Document) {
        MaterialAlertDialogBuilder(activity)
            .setTitle(activity.getString(R.string.delete_document))
            .setMessage(activity.getString(R.string.delete_document_confirm))
            .setPositiveButton(R.string.delete) { _, _ ->
                deleteDocument(document)
            }
            .setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.cancel() }
            .show()
    }

    /**
     * Deletes a project document.
     *
     * @param document The [Document] to delete.
     */
    private fun deleteDocument(document: Document) = activity.lifecycleScope.launch {
        try {
            // Delete the document
            val deleted = project.removeDocument(document.name) // Delete local document, and
            if (deleted) ProjectsDao.add(project)               // also delete from remote database

            // Reflect changes in the UI
            notifyDataSetChanged()
        } catch (ex: Exception) {
            // TODO: Show error message if document cannot be deleted
        }
    }

}