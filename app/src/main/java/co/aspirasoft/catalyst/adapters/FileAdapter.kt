package co.aspirasoft.catalyst.adapters

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import co.aspirasoft.adapter.ModelViewAdapter
import co.aspirasoft.catalyst.R
import co.aspirasoft.catalyst.models.Asset
import co.aspirasoft.catalyst.utils.FileUtils.openInExternalApp
import co.aspirasoft.catalyst.utils.storage.FileManager
import co.aspirasoft.catalyst.views.ProjectFileView
import co.aspirasoft.util.ViewUtils
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import java.io.IOException

class FileAdapter(val context: Activity, val material: ArrayList<Asset>, private val fileManager: FileManager)
    : ModelViewAdapter<Asset>(context, material, ProjectFileView::class) {

    override fun notifyDataSetChanged() {
        material.sortBy { it.metadata.creationTimeMillis }
        super.notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val v = super.getView(position, convertView, parent) as ProjectFileView
        val item = material[position]
        if (fileManager.hasInCache(item.name)) {
            v.setStatus(ProjectFileView.FileStatus.Local)
        } else {
            v.setStatus(ProjectFileView.FileStatus.Cloud)
        }

        v.setOnClickListener {
            v.setStatus(ProjectFileView.FileStatus.Downloading)
            fileManager.download(
                    item.name,
                    OnSuccessListener { file ->
                        v.setStatus(ProjectFileView.FileStatus.Local)
                        try {
                            file.openInExternalApp(context)
                        } catch (ex: IOException) {
                            ViewUtils.showError(v, ex.message ?: context.getString(R.string.file_open_error))
                        }
                    },
                    OnFailureListener {
                        v.setStatus(ProjectFileView.FileStatus.Cloud)
                        ViewUtils.showError(v, it.message ?: context.getString(R.string.file_download_error))
                    }
            )
        }

        v.setOnLongClickListener {
            // TODO: Delete file in editable mode
            false
        }
        return v
    }

}