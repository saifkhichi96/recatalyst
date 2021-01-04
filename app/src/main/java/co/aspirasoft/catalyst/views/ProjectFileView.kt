package co.aspirasoft.catalyst.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import co.aspirasoft.catalyst.R
import co.aspirasoft.catalyst.databinding.ViewProjectFileBinding
import co.aspirasoft.catalyst.models.RemoteFile
import co.aspirasoft.view.BaseView

class ProjectFileView : BaseView<RemoteFile> {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    private val binding = ViewProjectFileBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )

    override fun updateView(model: RemoteFile) {
        binding.fileView.text = model.name
    }

    override fun setOnClickListener(l: OnClickListener?) {
        binding.fileView.setOnClickListener(l)
    }

    fun setStatus(status: FileStatus) {
        binding.fileView.setIconResource(when (status) {
            FileStatus.Cloud -> R.drawable.ic_cloud
            FileStatus.Local -> R.drawable.ic_downloaded
            FileStatus.Downloading -> R.drawable.ic_download
        })
    }

    enum class FileStatus {
        Cloud,
        Local,
        Downloading
    }

}