package co.aspirasoft.catalyst.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import co.aspirasoft.catalyst.R
import co.aspirasoft.catalyst.databinding.FragmentIntroBinding
import co.aspirasoft.view.WizardViewStep

class ConnectionIntro : WizardViewStep("") {

    private var _binding: FragmentIntroBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentIntroBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.icon.setImageResource(R.drawable.intro_connect)
        binding.headline.text = getString(R.string.connections)
        binding.body.text = getString(R.string.connections_explanation)

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun isDataValid(): Boolean {
        return true
    }

}