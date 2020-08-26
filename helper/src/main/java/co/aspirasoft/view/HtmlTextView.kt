package co.aspirasoft.view

import android.content.Context
import android.util.AttributeSet
import androidx.core.text.HtmlCompat
import com.google.android.material.textview.MaterialTextView

class HtmlTextView : MaterialTextView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    override fun setText(text: CharSequence?, type: BufferType?) {
        if (text != null) {
            super.setText(HtmlCompat.fromHtml(text.toString(), HtmlCompat.FROM_HTML_MODE_COMPACT), type)
        } else {
            super.setText(text, type)
        }
    }

}