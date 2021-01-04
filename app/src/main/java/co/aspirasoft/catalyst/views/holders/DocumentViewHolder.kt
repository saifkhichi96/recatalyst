package co.aspirasoft.catalyst.views.holders

import androidx.recyclerview.widget.RecyclerView
import co.aspirasoft.catalyst.databinding.ViewDocumentBinding

class DocumentViewHolder(binding: ViewDocumentBinding) : RecyclerView.ViewHolder(binding.root) {

    val itemCard = binding.itemCard
    val versionView = binding.idView
    val nameView = binding.typeView

}