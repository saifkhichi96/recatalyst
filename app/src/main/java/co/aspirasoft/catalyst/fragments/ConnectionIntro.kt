package co.aspirasoft.catalyst.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import co.aspirasoft.catalyst.R
import co.aspirasoft.view.WizardViewStep

class ConnectionIntro : WizardViewStep("") {

    private lateinit var iconView: ImageView
    private lateinit var headlineView: TextView
    private lateinit var bodyView: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_intro, container, false)

        // Get UI references
        iconView = v.findViewById(R.id.icon)
        headlineView = v.findViewById(R.id.headline)
        bodyView = v.findViewById(R.id.body)

        iconView.setImageResource(R.drawable.intro_connect)
        headlineView.text = getString(R.string.title_connections)
        bodyView.text = getString(R.string.explain_connections)

        return v
    }

    override fun isDataValid(): Boolean {
        return true
    }

}