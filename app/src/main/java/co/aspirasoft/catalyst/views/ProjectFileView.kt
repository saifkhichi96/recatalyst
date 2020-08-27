package co.aspirasoft.catalyst.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import co.aspirasoft.catalyst.R
import co.aspirasoft.catalyst.models.Asset
import co.aspirasoft.view.BaseView
import com.google.android.material.button.MaterialButton

class ProjectFileView : BaseView<Asset> {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    private val mFileView: MaterialButton

    init {
        LayoutInflater.from(context).inflate(R.layout.view_project_file, this)
        mFileView = findViewById(R.id.fileView)
    }

    override fun updateView(model: Asset) {
        mFileView.text = model.name
    }

    override fun setOnClickListener(l: OnClickListener?) {
        mFileView.setOnClickListener(l)
    }

    fun setStatus(status: FileStatus) {
        mFileView.setIconResource(when (status) {
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