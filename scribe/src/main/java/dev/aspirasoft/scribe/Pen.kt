package dev.aspirasoft.scribe

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PointF
import android.text.TextPaint
import dev.aspirasoft.scribe.text.FontStyle
import dev.aspirasoft.scribe.text.ParagraphStyle

/**
 * @author saifkhichi96
 * @since 1.0.0
 */
internal class Pen(private val ix: Float, private val iy: Float) : TextPaint() {

    private var breakDirty = false

    private var cursor = PointF(ix, iy)

    val x get() = cursor.x
    val y get() = cursor.y

    var currentStyle: ParagraphStyle = ParagraphStyle()
        set(value) {
            field = value
            typeface = value.font
            textSize = value.fontSize
            isFakeBoldText = value.fontStyle == FontStyle.BOLD || value.fontStyle == FontStyle.BOLD_ITALIC
            if (value.fontStyle == FontStyle.ITALIC || value.fontStyle == FontStyle.BOLD_ITALIC) {
                textSkewX = -0.25f
            } else {
                textSkewX = 0f
            }
        }

    init {
        this.color = Color.BLACK
    }

    fun writeOnCanvas(text: String, canvas: Canvas) {
        canvas.drawText(text, cursor.x, cursor.y, this)
    }

    fun moveToNextLine() {
        moveTo(ix, y + (textSize + 2f) * currentStyle.lineSpacing)
        breakDirty = true
    }

    fun moveToNextParagraph() {
        if (breakDirty) {
            moveTo(ix, y + currentStyle.paragraphSpacing)
        } else {
            moveTo(ix, y + textSize + currentStyle.paragraphSpacing)
        }
        breakDirty = false
    }

    fun move(dx: Float, dy: Float) {
        cursor.x += dx
        cursor.y += dy
    }

    fun resetPosition() {
        moveTo(ix, iy)
    }

    private fun moveTo(x: Float, y: Float) {
        cursor.x = x
        cursor.y = y
    }

}