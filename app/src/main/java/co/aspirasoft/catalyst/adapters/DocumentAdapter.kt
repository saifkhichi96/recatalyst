package co.aspirasoft.catalyst.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.children
import androidx.lifecycle.lifecycleScope
import co.aspirasoft.catalyst.MyApplication
import co.aspirasoft.catalyst.R
import co.aspirasoft.catalyst.activities.EditorActivity
import co.aspirasoft.catalyst.activities.abs.SecureActivity
import co.aspirasoft.catalyst.dao.DocumentsDao
import co.aspirasoft.catalyst.databinding.ViewDocumentBinding
import co.aspirasoft.catalyst.databinding.ViewDocumentCreateBinding
import co.aspirasoft.catalyst.models.Document
import co.aspirasoft.catalyst.models.DocumentType
import co.aspirasoft.catalyst.models.Project
import co.aspirasoft.catalyst.utils.FileUtils.getJsonFromAssets
import co.aspirasoft.catalyst.views.holders.DocumentViewHolder
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.util.*

class DocumentAdapter(
    private val activity: SecureActivity,
    private val documents: ArrayList<Document?>,
    private val project: Project,
) : ArrayAdapter<Document?>(activity, -1, documents) {

    private val gradients = arrayOf(
        R.drawable.gradient_aqua_splash,
        R.drawable.gradient_eternal_constance,
        R.drawable.gradient_happy_fisher,
        R.drawable.gradient_plum_plate
    )

    private var documentPickerDialog: AlertDialog? = null

    init {
        context.assets.list("templates/en/")?.let { files ->
            val standards = files.map {
                it.toUpperCase(Locale.getDefault())
                    .replace('-', ' ')
                    .replace(".JSON", "")
            }.toTypedArray()
            documentPickerDialog = MaterialAlertDialogBuilder(context)
                .setTitle(context.getString(R.string.documentation_choose_template))
                .setItems(standards) { _, i ->
                    createNewDocument("templates/en/${files[i]}")
                }
                .create()
        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        val document = documents[position]
        if (document == null) {
            if (view == null) {
                val binding = ViewDocumentCreateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                view = binding.root
                val l = View.OnClickListener { documentPickerDialog?.show() }
                view.apply {
                    setOnClickListener(l)
                    (children.elementAt(0) as ViewGroup).children.forEach {
                        it.setOnClickListener(l)
                    }
                }
            }
        } else {
            val viewHolder: DocumentViewHolder = when {
                view == null || view.tag == null -> {
                    val binding = ViewDocumentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                    val holder = DocumentViewHolder(binding)
                    view = binding.root
                    view.tag = holder
                    holder
                }
                else -> view.tag as DocumentViewHolder
            }
            viewHolder.itemCard.setBackgroundResource(gradients[position % gradients.size])
            viewHolder.nameView.text = document.name
            showExistingDocument(viewHolder, document)
        }
        return view
    }

    private fun showExistingDocument(holder: DocumentViewHolder, document: Document) {
        holder.versionView.text = String.format(context.getString(R.string.documentation_version), document.version)
        holder.itemCard.setOnClickListener { openDocumentInEditor(document) }
        holder.itemCard.setOnLongClickListener {
            confirmDocumentDeletion(document)
            true
        }
    }

    private fun createNewDocument(type: DocumentType) = activity.lifecycleScope.launch {
        try {
            // Create a new document
            val newDocument = Document.Builder(type)
                .setProject(project.name)
                .setVersion("1.0.0")
                .build()

            // Save document and upload it to database
            DocumentsDao.add(newDocument, project)

            // Open document in editor
            openDocumentInEditor(newDocument)
        } catch (ex: Exception) {
            Toast.makeText(
                context,
                ex.message ?: ex::class.java.simpleName,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun createNewDocument(assetsFile: String) {
        getJsonFromAssets(context, assetsFile)?.let { json ->
            val template = Gson().fromJson(json, DocumentType::class.java)
            createNewDocument(template)
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
            DocumentsDao.delete(document.id, project)
        } catch (ex: Exception) {
            Toast.makeText(
                context,
                ex.message ?: ex::class.java.simpleName,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

}