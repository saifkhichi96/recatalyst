package co.aspirasoft.catalyst.views

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import co.aspirasoft.catalyst.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout


class DocumentSectionView(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var sectionHeader: TextInputLayout = itemView.findViewById(R.id.sectionHeader)
    var sectionContents: TextInputEditText = itemView.findViewById(R.id.sectionContents)
    var addImageButton: MaterialButton = itemView.findViewById(R.id.addImageButton)
}