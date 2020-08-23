package co.aspirasoft.view

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import co.aspirasoft.R
import co.aspirasoft.util.ViewUtils
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class SingleInputForm : BottomSheetDialogFragment() {

    private var onOk: ((v: String) -> Unit)? = null

    private lateinit var inputField: TextInputField
    private lateinit var okButton: Button

    var onDismissListener: ((dialog: DialogInterface) -> Unit)? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.single_input_form, container, false)

        inputField = v.findViewById(R.id.inputField)

        okButton = v.findViewById(R.id.okButton)
        okButton.setOnClickListener { onOk() }

        try {
            val args = requireArguments()

            val titleView: TextView = v.findViewById(R.id.title)
            titleView.text = args.getString(ARG_TITLE)

            inputField.hint = args.getString(ARG_INPUT_HINT)
            inputField.inputType = args.getInt(ARG_INPUT_TYPE)

            okButton.text = args.getString(ARG_OK_BUTTON)
        } catch (ex: Exception) {
            ex.message?.let { ViewUtils.showError(v, it) }
            dismiss()
            return null
        }

        return v
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismissListener?.let { it(dialog) }
    }

    private fun onOk() {
        onOk?.let { callback ->
            callback(inputField.text ?: "")
        }
    }

    fun setEnabled(enabled: Boolean) {
        this.isCancelable = enabled
        this.okButton.isEnabled = enabled
    }

    fun setOnSubmitListener(listener: (v: String) -> Unit) {
        this.onOk = listener
    }

    companion object {
        private const val ARG_TITLE = "title"
        private const val ARG_INPUT_HINT = "hint"
        private const val ARG_INPUT_TYPE = "inputType"
        private const val ARG_OK_BUTTON = "okButton"

        @JvmStatic
        fun newInstance(
                title: String,
                hint: String,
                inputType: Int,
                okButton: String
        ): SingleInputForm {
            return SingleInputForm().also {
                it.arguments = Bundle().apply {
                    putString(ARG_TITLE, title)
                    putString(ARG_INPUT_HINT, hint)
                    putInt(ARG_INPUT_TYPE, inputType)
                    putString(ARG_OK_BUTTON, okButton)
                }
            }
        }
    }

}