package co.aspirasoft.catalyst.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import co.aspirasoft.catalyst.R
import co.aspirasoft.catalyst.models.Document
import co.aspirasoft.catalyst.models.DocumentType
import co.aspirasoft.catalyst.models.Project
import co.aspirasoft.catalyst.utils.FileUtils.openInExternalApp
import co.aspirasoft.catalyst.utils.storage.FileManager
import co.aspirasoft.catalyst.views.DocumentView
import co.aspirasoft.util.ViewUtils
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.snackbar.Snackbar
import kotlin.reflect.KClass

class DocumentAdapter(context: Context, private val project: Project, private val documents: Array<KClass<out DocumentType>>)
    : ArrayAdapter<KClass<out DocumentType>>(context, -1, documents) {

    private val fm = FileManager.projectDocsManager(context, project)

    private var onCreateListener: ((document: Document) -> Unit)? = null
    private var onDeleteListener: ((document: Document) -> Unit)? = null
    private var onEditListener: ((document: Document) -> Unit)? = null

    private val gradients = arrayOf(
            R.drawable.gradient_aqua_splash,
            R.drawable.gradient_eternal_constance,
            R.drawable.gradient_happy_fisher,
            R.drawable.gradient_plum_plate
    )

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.view_document, parent, false)

        val holder = DocumentView(view)
        val documentType = documents[position].constructors.elementAt(0).call()

        holder.itemCard.setBackgroundResource(gradients[position % gradients.size])
        holder.nameView.text = documentType.name

        val document = project.getDocument(documentType.name)
        if (document != null) {
            onDocumentExists(holder, document)
        } else {
            onDocumentNotFound(holder, documentType)
        }
        return view
    }

    fun setOnItemCreateListener(createListener: (document: Document) -> Unit) {
        onCreateListener = createListener
    }

    fun setOnItemDeleteListener(deleteListener: (document: Document) -> Unit) {
        onDeleteListener = deleteListener
    }

    fun setOnItemEditListener(editListener: (document: Document) -> Unit) {
        onEditListener = editListener
    }

    private fun onDocumentNotFound(holder: DocumentView, type: DocumentType) {
        holder.versionView.text = String.format("Version: N/A")
        holder.deleteButton.visibility = View.GONE
        holder.viewButton.visibility = View.GONE
        holder.editButton.setOnClickListener {
            onCreateListener?.invoke(Document.Builder()
                    .setProject(project.name)
                    .setType(type)
                    .setVersion("1.0.0")
                    .build())
        }
    }

    private fun onDocumentExists(holder: DocumentView, document: Document) {
        holder.versionView.text = String.format("Version: ${document.version}")

        holder.deleteButton.visibility = View.VISIBLE
        holder.viewButton.visibility = View.VISIBLE

        // Set up click actions
        holder.editButton.setOnClickListener {
            onEditListener?.invoke(document)
        }

        holder.deleteButton.setOnClickListener {
            onDeleteListener?.invoke(document)
        }

        holder.viewButton.setOnClickListener {
            val status = Snackbar.make(holder.itemCard, context.getString(R.string.status_opening), Snackbar.LENGTH_INDEFINITE)
            status.show()
            fm.downloadOnly(
                    String.format("%s (v%s).pdf", document.name, document.version),
                    OnSuccessListener { file ->
                        status.dismiss()
                        file.openInExternalApp(context)
                    },
                    OnFailureListener {
                        status.dismiss()
                        ViewUtils.showError(holder.itemCard, it.message
                                ?: context.getString(R.string.status_open_failed))
                    }
            )
        }
    }


}