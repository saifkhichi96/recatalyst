package co.aspirasoft.catalyst.adapters

import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import co.aspirasoft.adapter.ModelViewAdapter
import co.aspirasoft.catalyst.R
import co.aspirasoft.catalyst.models.RemoteFile
import co.aspirasoft.catalyst.utils.FileUtils.openInExternalApp
import co.aspirasoft.catalyst.utils.storage.ProjectStorage
import co.aspirasoft.catalyst.views.ProjectFileView
import co.aspirasoft.util.ViewUtils
import kotlinx.coroutines.launch
import java.io.IOException

class FileAdapter(
    private val context: AppCompatActivity,
    private val files: ArrayList<RemoteFile>,
    private val storage: ProjectStorage,
) : ModelViewAdapter<RemoteFile>(context, files, ProjectFileView::class) {

    override fun notifyDataSetChanged() {
        files.sortBy { it.metadata.creationTimeMillis }
        super.notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val v = super.getView(position, convertView, parent) as ProjectFileView
        val item = files[position]
        v.setStatus(when (storage.hasFileInCache(item.name)) {
            true -> ProjectFileView.FileStatus.Local
            else -> ProjectFileView.FileStatus.Cloud
        })

        v.setOnClickListener { downloadFile(v, item.name) }
        v.setOnLongClickListener {
            // TODO: Delete file in editable mode
            false
        }
        return v
    }

    private fun downloadFile(v: ProjectFileView, path: String) = context.lifecycleScope.launch {
        v.setStatus(ProjectFileView.FileStatus.Downloading)
        try {
            val file = storage.getFile(path)
            v.setStatus(ProjectFileView.FileStatus.Local)
            context.runOnUiThread {
                try {
                    file.openInExternalApp(context)
                } catch (ex: IOException) {
                    ViewUtils.showError(v, ex.message ?: context.getString(R.string.file_open_error))
                }
            }
        } catch (ex: Exception) {
            v.setStatus(ProjectFileView.FileStatus.Cloud)
            ViewUtils.showError(v, ex.message ?: context.getString(R.string.file_download_error))
        }
    }

}