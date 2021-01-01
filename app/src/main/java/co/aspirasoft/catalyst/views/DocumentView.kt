package co.aspirasoft.catalyst.views

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.aspirasoft.catalyst.R


class DocumentView(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var itemCard: View = itemView.findViewById(R.id.itemCard)
    var versionView: TextView = itemView.findViewById(R.id.idView)
    var nameView: TextView = itemView.findViewById(R.id.typeView)
}