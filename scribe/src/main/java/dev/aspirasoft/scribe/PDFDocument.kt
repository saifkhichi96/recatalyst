package dev.aspirasoft.scribe

import android.graphics.pdf.PdfDocument
import dev.aspirasoft.scribe.page.PageOrientation
import dev.aspirasoft.scribe.page.PageSize
import kotlin.math.roundToInt

/**
 * @author saifkhichi96
 * @since 1.0.0
 */
internal class PDFDocument(
    val marginBottom: Float,
    val marginLeft: Float,
    val marginRight: Float,
    val marginTop: Float,
    pageSize: PageSize,
    pageOrientation: PageOrientation,
) : PdfDocument() {

    private var currentPage: Page? = null

    val width = when (pageOrientation) {
        PageOrientation.PORTRAIT -> pageSize.width
        PageOrientation.LANDSCAPE -> pageSize.height
    }

    val height = when (pageOrientation) {
        PageOrientation.PORTRAIT -> pageSize.height
        PageOrientation.LANDSCAPE -> pageSize.width
    }

    val writeableWidth = width - marginLeft - marginRight

    val writeableHeight = height - marginBottom

    val canvas get() = currentPage?.canvas

    /**
     * Starts a new page.
     */
    fun startPage() {
        finishPage()

        // Start a new page
        val page = PageInfo.Builder(
            width.roundToInt(),
            height.roundToInt(),
            pages.size + 1
        ).create()
        currentPage = super.startPage(page)
    }

    /**
     * Finishes the currently open page, if any.
     */
    fun finishPage() {
        currentPage?.let { super.finishPage(it) }
        currentPage = null
    }

}