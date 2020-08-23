package co.aspirasoft.view

import android.content.Context
import android.text.InputType
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import co.aspirasoft.R
import kotlinx.android.synthetic.main.input_field.view.*


class TextInputField(context: Context, attrs: AttributeSet?, defStyleInt: Int)
    : LinearLayout(context, attrs, defStyleInt) {

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    var hint: String? = ""
        set(value) {
            field = value
            if (value.isNullOrBlank()) {
                inputFieldHint.visibility = View.GONE
            }

            inputFieldHint.visibility = View.VISIBLE
            inputFieldHint.text = value
        }

    var text: String? = ""
        get() = inputFieldText.text?.toString()
        set(value) {
            field = value
            inputFieldText.setText(value ?: "")
        }

    var inputType: Int = InputType.TYPE_CLASS_TEXT
        set(value) {
            field = value
            inputFieldText.inputType = value
        }

    init {
        View.inflate(context, R.layout.input_field, this@TextInputField)
        context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.TextInputField,
                0, 0).apply {

            try {
                hint = getString(R.styleable.TextInputField_hint)
                text = getString(R.styleable.TextInputField_text)
                inputType = getInt(R.styleable.TextInputField_inputType, InputType.TYPE_CLASS_TEXT)
            } finally {
                recycle()
            }
        }
    }

}