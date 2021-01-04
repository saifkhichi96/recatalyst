package co.aspirasoft.catalyst.views.holders

import androidx.recyclerview.widget.RecyclerView
import co.aspirasoft.catalyst.databinding.ViewSectionBinding


class SectionViewHolder(binding: ViewSectionBinding) : RecyclerView.ViewHolder(binding.root) {

    var sectionHeader = binding.sectionHeader
    var sectionContents = binding.sectionContents
    var addImageButton = binding.addImageButton

}