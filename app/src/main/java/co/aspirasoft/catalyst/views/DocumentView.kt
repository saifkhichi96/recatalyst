package co.aspirasoft.catalyst.views

import androidx.recyclerview.widget.RecyclerView
import co.aspirasoft.catalyst.databinding.ViewDocumentBinding

class DocumentView(binding: ViewDocumentBinding) : RecyclerView.ViewHolder(binding.root) {

    val itemCard = binding.itemCard
    val versionView = binding.idView
    val nameView = binding.typeView

}