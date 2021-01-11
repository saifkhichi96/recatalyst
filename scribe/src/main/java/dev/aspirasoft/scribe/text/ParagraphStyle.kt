package dev.aspirasoft.scribe.text

import android.graphics.Paint.Align
import android.graphics.Typeface

/**
 * @author saifkhichi96
 * @since 1.0.0
 */
class ParagraphStyle {

    var font: Typeface = Typeface.DEFAULT
    var fontSize: Float = 11F
    var fontStyle: FontStyle = FontStyle.REGULAR

    var alignment: Align = Align.LEFT
    var capitalization: Capitalization = Capitalization.NONE

    var indent: Int = 0
    var lineSpacing: Int = 1
    var paragraphSpacing: Float = 6F

}