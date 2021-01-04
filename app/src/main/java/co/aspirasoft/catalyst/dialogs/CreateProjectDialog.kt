package co.aspirasoft.catalyst.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import co.aspirasoft.catalyst.R
import co.aspirasoft.catalyst.dao.ProjectsDao
import co.aspirasoft.catalyst.databinding.DialogCreateProjectBinding
import co.aspirasoft.catalyst.models.Project
import co.aspirasoft.util.InputUtils.isNotBlank
import co.aspirasoft.util.ViewUtils
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch


class CreateProjectDialog : BottomSheetDialogFragment() {

    private var _binding: DialogCreateProjectBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private lateinit var ownerId: String

    var onOkListener: ((project: Project) -> Unit)? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = DialogCreateProjectBinding.inflate(inflater, container, false)
        val view = binding.root

        try {
            val args = requireArguments()
            ownerId = args.getString(ARG_OWNER_ID)!!
        } catch (ex: Exception) {
            ex.message?.let { ViewUtils.showError(view, it) }
            dismiss()
            return null
        }

        binding.okButton.setOnClickListener { onOk() }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onOk() {
        if (binding.projectNameField.isNotBlank(true)) {
            isCancelable = false
            binding.okButton.isEnabled = false

            val projectName = binding.projectNameField.text.toString().trim()
            val project = Project(projectName, ownerId).apply {
                this.description = binding.descriptionField.text.toString().trim()
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
        ViewUtils.showError(binding.projectNameField, error ?: getString(R.string.create_project_error))
        isCancelable = true
        binding.okButton.isEnabled = true
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