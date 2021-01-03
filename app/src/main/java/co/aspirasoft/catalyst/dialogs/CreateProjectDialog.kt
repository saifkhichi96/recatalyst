package co.aspirasoft.catalyst.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.lifecycleScope
import co.aspirasoft.catalyst.R
import co.aspirasoft.catalyst.dao.ProjectsDao
import co.aspirasoft.catalyst.models.Project
import co.aspirasoft.util.InputUtils.isNotBlank
import co.aspirasoft.util.ViewUtils
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class CreateProjectDialog : BottomSheetDialogFragment() {

    private lateinit var ownerId: String

    private lateinit var projectNameField: TextInputEditText
    private lateinit var descriptionField: TextInputEditText
    private lateinit var okButton: Button

    var onOkListener: ((project: Project) -> Unit)? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.dialog_create_project, container, false)

        try {
            val args = requireArguments()
            ownerId = args.getString(ARG_OWNER_ID)!!
        } catch (ex: Exception) {
            ex.message?.let { ViewUtils.showError(v, it) }
            dismiss()
            return null
        }

        // Get UI references
        projectNameField = v.findViewById(R.id.projectNameField)
        descriptionField = v.findViewById(R.id.descriptionField)

        okButton = v.findViewById(R.id.okButton)
        okButton.setOnClickListener { onOk() }

        return v
    }

    private fun onOk() {
        if (projectNameField.isNotBlank(true)) {
            isCancelable = false
            okButton.isEnabled = false

            val projectName = projectNameField.text.toString().trim()
            val project = Project(projectName, ownerId).apply {
                this.description = descriptionField.text.toString().trim()
            }

            lifecycleScope.launch {
                try {
                    ProjectsDao.add(project)
                    onOkListener?.let { it(project) }
                    dismiss()
                } catch (ex: Exception) {
                    onError(ex.message)
                }
            }
        }
    }

    private fun onError(error: String? = null) {
        ViewUtils.showError(projectNameField, error ?: getString(R.string.create_project_error))
        isCancelable = true
        okButton.isEnabled = true
    }

    companion object {
        private const val ARG_OWNER_ID = "owner"

        @JvmStatic
        fun newInstance(ownerId: String): CreateProjectDialog {
            return CreateProjectDialog().also {
                it.arguments = Bundle().apply {
                    putString(ARG_OWNER_ID, ownerId)
                }
            }
        }
    }

}