package dev.aspirasoft.scribe

import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.pdf.PdfDocument
import dev.aspirasoft.scribe.page.PageOrientation
import dev.aspirasoft.scribe.page.PageSize
import dev.aspirasoft.scribe.text.Capitalization
import dev.aspirasoft.scribe.utils.mmToPixels
import dev.aspirasoft.scribe.utils.toTitleCase
import java.util.*
import kotlin.math.roundToInt

/**
 * @author saifkhichi96
 * @version 1.0.0
 */
class PDFWriter internal constructor(
    private val document: PDFDocument,
    private val documentTheme: DocumentTheme,
) {

    private val pen = Pen(document.marginLeft, document.marginTop)

    fun h1(text: String) = write(text, "h1")

    fun h2(text: String) = write(text, "h2")

    fun h3(text: String) = write(text, "h3")

    fun h4(text: String) = write(text, "h4")

    fun h5(text: String) = write(text, "h5")

    fun h6(text: String) = write(text, "h6")

    fun normal(text: String) = write(text, "p")

    fun title(text: String) = write(text, "title")

    fun subtitle(text: String) = write(text, "subtitle")

    fun image(bitmap: Bitmap) {
        // Get original bitmap size
        val width = bitmap.width
        val height = bitmap.height

        // Scale bitmap, preserving aspect ratio
        val finalWidth: Int = if (width > document.writeableWidth) document.writeableWidth.roundToInt() else width
        val finalHeight = (finalWidth * height / width.toFloat()).toInt()

        // Create a new page if this bitmap won't fit on current page
        val remainingPage = document.writeableHeight - pen.y
        if (remainingPage < finalHeight) {
            pageBreak()
        }

        // Determine final bitmap location of page
        val box = RectF(pen.x, pen.y, pen.x + finalWidth, pen.y + finalHeight)
        document.canvas?.drawBitmap(bitmap, null, box, pen)

        // Move cursor
        pen.move(0F, finalHeight.toFloat())
    }

    fun centerVertical() {
        pen.move(0F, document.writeableHeight / 2)
    }

    fun pageBreak() {
        document.startPage()
        pen.resetPosition()
    }

    fun paragraphBreak() {
        pen.moveToNextParagraph()
    }

    fun vspace(n: Int) {
        repeat(n) { paragraphBreak() }
    }

    fun finish(): PdfDocument {
        document.finishPage()
        return document
    }

    private fun writeLine(text: String) {
        val pageFinished = pen.y > document.writeableHeight
        if (pageFinished) {
            pageBreak()
        }

        document.canvas?.let {
            val textWidth = pen.measureText(text.trim())
            if (pen.currentStyle.alignment == Paint.Align.RIGHT) {
                pen.move(document.writeableWidth - textWidth, 0F)
            } else if (pen.currentStyle.alignment == Paint.Align.CENTER) {
                pen.move((document.writeableWidth - textWidth) / 2, 0F)
            }

            pen.writeOnCanvas(text.trim(), it)
            pen.moveToNextLine()
        }
    }

    private fun writeParagraph(text: String) {
        val breakpoint = pen.breakText(text, true, document.writeableWidth, null)
        when {
            breakpoint < text.toCharArray().size -> {
                var i = text.substring(0, breakpoint).lastIndexOf(' ')
                if (i == -1) i = breakpoint

                writeLine(text.substring(0, i))
                writeParagraph(text.substring(i))
            }
            else -> writeLine(text)
        }
    }

    private fun writeParagraphs(text: String) {
        if (text.contains("\n")) {
            text.split("\n").forEach { p ->
                if (p.isNotBlank()) {
                    writeParagraph(p.trim())
                    paragraphBreak()
                }
            }
        } else {
            writeParagraph(text.trim())
            paragraphBreak()
        }
    }

    private fun write(text: String, tag: String) {
        val paragraphStyle = when (tag.toLowerCase(Locale.getDefault())) {
            "title" -> documentTheme.title
            "subtitle" -> documentTheme.subtitle
            "h1" -> documentTheme.h1
            "h2" -> documentTheme.h2
            "h3" -> documentTheme.h3
            "h4" -> documentTheme.h4
            "h5" -> documentTheme.h5
            "h6" -> documentTheme.h6
            "p" -> documentTheme.body
            else -> documentTheme.body
        }

        pen.currentStyle = paragraphStyle
        val capitalizedText = when (paragraphStyle.capitalization) {
            Capitalization.NONE -> text
            Capitalization.ALL_CAPS -> text.toUpperCase(Locale.getDefault())
            Capitalization.TITLE_CASE -> text.toTitleCase()
        }
        writeParagraphs(capitalizedText)
    }

    class Builder {

        private var mPageOrientation = PageOrientation.PORTRAIT
        private var mPageSize = PageSize.A4

        private var mMarginBottom = 36.7F
        private var mMarginLeft = 19F
        private var mMarginRight = 13.2F
        private var mMarginTop = 19F

        private var mDocumentTheme = DocumentTheme()

        fun setPageSize(size: PageSize): Builder {
            this.mPageSize = size
            return this
        }

        fun setPageOrientation(orientation: PageOrientation): Builder {
            this.mPageOrientation = orientation
            return this
        }

        fun setVerticalMargin(top: Float, bottom: Float): Builder {
            mMarginTop = top
            mMarginBottom = bottom
            return this
        }

        fun setVerticalMargin(margin: Float): Builder {
            setVerticalMargin(margin, margin)
            return this
        }

        fun setHorizontalMargin(left: Float, right: Float): Builder {
            mMarginLeft = left
            mMarginRight = right
            return this
        }

        fun setHorizontalMargin(margin: Float): Builder {
            setHorizontalMargin(margin, margin)
            return this
        }

        fun setMargin(top: Float, left: Float, right: Float, bottom: Float): Builder {
            setVerticalMargin(top, bottom)
            setHorizontalMargin(left, right)
            return this
        }

        fun setMargin(margin: Float): Builder {
            setVerticalMargin(margin)
            setHorizontalMargin(margin)
            return this
        }

        fun setDocumentTheme(theme: DocumentTheme): Builder {
            mDocumentTheme = theme
            return this
        }

        fun build(): PDFWriter {
            val pdf = PDFDocument(
                marginBottom = mmToPixels(mMarginBottom),
                marginLeft = mmToPixels(mMarginLeft),
                marginRight = mmToPixels(mMarginRight),
                marginTop = mmToPixels(mMarginTop),
                pageSize = mPageSize,
                pageOrientation = mPageOrientation,
            ).apply { startPage() }
            return PDFWriter(pdf, mDocumentTheme)
        }

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
        private const val h1 = "<h1>"
        private const val h2 = "<h2>"
        private const val h3 = "<h3>"
        private const val h4 = "<h4>"
        private const val h5 = "<h5>"
        private const val h6 = "<h6>"
        private const val p = "<p>"
        private const val b = "<b>"
        private const val i = "<i>"
        private const val a = "<a>"
    }

}