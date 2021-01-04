package co.aspirasoft.catalyst.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import co.aspirasoft.catalyst.activities.DrawingActivity
import co.aspirasoft.catalyst.databinding.ViewSectionBinding
import co.aspirasoft.catalyst.models.Document
import co.aspirasoft.catalyst.models.DocumentSection
import co.aspirasoft.catalyst.views.holders.SectionViewHolder
import java.util.*

class SectionAdapter(
    private val context: Context,
    private val document: Document,
    private val properties: List<DocumentSection>,
) : BaseAdapter(), Filterable {

    private var filteredProperties: List<DocumentSection>

    init {
        filteredProperties = properties
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = ViewSectionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val viewHolder = SectionViewHolder(binding)
        val view = binding.root

        val section = filteredProperties[position]
        viewHolder.sectionContents.setText(section.body)
        viewHolder.sectionHeader.hint = section.name

        // Bind property field with property object
        viewHolder.sectionContents.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                filteredProperties[position].body = charSequence.toString()
            }

            override fun afterTextChanged(editable: Editable) {}
        })

        // Header and footer properties have no sketches
        if (position < 4 || position == filteredProperties.size - 1) viewHolder.addImageButton.visibility = View.GONE
        viewHolder.addImageButton.setOnClickListener {
            val intent = Intent(context, DrawingActivity::class.java)
            intent.putExtra("index", position)
            intent.putExtra("section", section.name)
            intent.putExtra("version", document.version)
            intent.putExtra("project", document.projectId)
            context.startActivity(intent)
        }
        return view
    }

    override fun getCount(): Int {
        return filteredProperties.size
    }

    override fun getItem(position: Int): DocumentSection {
        return filteredProperties[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getFilter(): Filter {
        return ValueFilter()
    }

    private inner class ValueFilter : Filter() {

        override fun performFiltering(constraint: CharSequence): FilterResults {
            val results = FilterResults()
            if (constraint.isNotEmpty()) {
                val filteredProperties: MutableList<DocumentSection> = ArrayList()
                for (section in properties) {
                    if (section.name.startsWith(constraint.toString())) {
                        filteredProperties.add(section)
                    }
                }
                results.count = filteredProperties.size
                results.values = filteredProperties
            } else {
                results.count = properties.size
                results.values = properties
            }
            return results
        }

        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            filteredProperties = results.values as ArrayList<DocumentSection>
            notifyDataSetChanged()
        }

    }

}