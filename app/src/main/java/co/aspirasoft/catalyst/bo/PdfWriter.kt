package co.aspirasoft.catalyst.bo

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PointF
import android.graphics.RectF
import android.graphics.pdf.PdfDocument
import android.text.TextPaint

/**
 * @author saifkhichi96
 * @version 1.0.0
 * @since 1.0.0 26/10/2018 3:11 AM
 */
class PdfWriter internal constructor() {

    private var pdf = PdfDocument()
    private var currentPage: PdfDocument.Page? = null
    private var currentPageIndex = 0

    private val pen: TextPaint = TextPaint()
    private var cursor = PointF(PAGE_PADDING, PAGE_PADDING + TEXT_SIZE)

    init {
        pen.color = TEXT_COLOR
        pen.textSize = TEXT_SIZE.toFloat()
        pageBreak()
    }

    private fun write(text: String) {
        val sizeFactor = pen.textSize / TEXT_SIZE
        val lineWidth = (LINE_WIDTH / sizeFactor).toInt()
        when {
            text.length > lineWidth -> {
                val subtext = text.substring(0, lineWidth)
                val breakpoint = subtext.lastIndexOf(' ')
                write(text.substring(0, breakpoint))
                write(text.substring(breakpoint + 1))
            }
            text.contains("\n") -> {
                write(text.split("\n")[0])
                paragraphBreak()
            }
            else -> {
                currentPage?.canvas?.drawText(text, cursor.x, cursor.y, pen)
                moveCursor(cursor.x, cursor.y + pen.textSize + LINE_SPACING * sizeFactor)
            }
        }
    }

    fun title(text: String) {
        pen.textSize = TEXT_SIZE * 4f
        pen.isFakeBoldText = true
        write(text)
        pen.textSize = TEXT_SIZE
    }

    fun subtitle(text: String) {
        pen.textSize = TEXT_SIZE * 2.5f
        pen.isFakeBoldText = false
        write(text)
        pen.textSize = TEXT_SIZE
    }

    fun heading(text: String) {
        pen.textSize = TEXT_SIZE * 1.25f
        pen.isFakeBoldText = true
        write(text)
        pen.textSize = TEXT_SIZE
    }

    fun normal(text: String) {
        pen.textSize = TEXT_SIZE
        pen.isFakeBoldText = false
        write(text)
    }

    fun bold(text: String) {
        pen.textSize = TEXT_SIZE
        pen.isFakeBoldText = true
        write(text)
    }

    fun image(bitmap: Bitmap) {
        // Get original bitmap size
        val width = bitmap.width
        val height = bitmap.height

        // Scale down bitmap, preserving aspect ratio
        val finalWidth = IMAGE_WIDTH
        val finalHeight = (finalWidth * height / width.toFloat()).toInt()

        // Create a new page if this bitmap won't fit on current page
        val remainingPage = PAGE_HEIGHT - PAGE_PADDING - cursor.y
        if (remainingPage < finalHeight) {
            pageBreak()
        }

        // Determine final bitmap location of page
        val box = RectF(cursor.x, cursor.y, cursor.x + finalWidth, cursor.y + finalHeight)
        currentPage?.canvas?.drawBitmap(bitmap, null, box, pen)

        // Move cursor
        cursor.y += finalHeight
    }

    fun spacing(n: Int) {
        moveCursor(cursor.x, cursor.y + n * PARAGRAPH_SPACING)
    }

    fun paragraphBreak() {
        spacing(1)
    }

    fun sectionBreak() {
        spacing(2)
    }

    fun pageBreak() {
        // Finish current page, if opened
        finishPage()

        // Start a new page
        val meta = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, ++currentPageIndex).create()
        currentPage = pdf.startPage(meta)

        // Reset cursor position
        resetCursor()
    }

    fun finish(): PdfDocument {
        finishPage()
        return pdf
    }

    private fun finishPage() {
        currentPage?.let { pdf.finishPage(it) }
        currentPage = null
    }

    private fun moveCursor(x: Float, y: Float) {
        val pageFinished = y > PAGE_HEIGHT - PAGE_PADDING
        if (pageFinished) pageBreak() else {
            this.cursor.x = x
            this.cursor.y = y
        }
    }

    private fun resetCursor() {
        cursor.x = PAGE_PADDING
        cursor.y = PAGE_PADDING + TEXT_SIZE
    }

    companion object {
        /**
         * Unit Conversions
         *
         *
         * 1 pixel = 0.75 PS
         * 1 PS = 1/72 inch
         * 1 inch = 1/25.4 mm
         */
        // Page dimensions (A4)
        private const val PAGE_WIDTH = 595 // 72 * (210mm)/25.4
        private const val PAGE_HEIGHT = 842 // 72 * (297mm)/25.4

        // Whitespace and margins
        private const val PAGE_PADDING = 54F // 72 * (0.75in)
        private const val LINE_SPACING = 2F
        private const val PARAGRAPH_SPACING = 24F

        // Font styles
        private const val IMAGE_WIDTH = 192
        private const val TEXT_SIZE = 12F // 12px
        private const val TEXT_COLOR = Color.BLACK
        private const val LINE_WIDTH = 88
    }

}