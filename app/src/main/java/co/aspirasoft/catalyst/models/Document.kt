package co.aspirasoft.catalyst.models

import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import co.aspirasoft.catalyst.utils.FileUtils
import dev.aspirasoft.scribe.DocumentTheme
import dev.aspirasoft.scribe.PDFWriter
import java.io.ByteArrayOutputStream
import java.io.Serializable

/**
 * @author saifkhichi96
 * @version 1.0.0
 * @since 1.0.0 26/10/2018 3:00 AM
 */
class Document : Serializable {

    var name: String = ""

    var projectId: String = ""

    var version: String = ""

    val sections = ArrayList<DocumentSection>()

    fun editableSections() = sections.filterIndexed { i, section ->
        section.isSubsection || !(sections.getOrNull(i + 1)?.isSubsection ?: false)
    }

    fun headings() = ArrayList<String>().apply {
        sections.filterNot { it.isSubsection }.mapTo(this) { it.name }
    }

    fun updateWith(other: Document) {
        this.name = other.name
        this.projectId = other.projectId
        this.version = other.version
        this.sections.apply {
            clear()
            addAll(other.sections)
        }
    }

    fun toByteArray(context: Context): ByteArray {
        val stream = ByteArrayOutputStream()
        val pdf = toPdf(context)
        pdf.writeTo(stream)
        pdf.close()
        return stream.toByteArray()
    }

    fun toPdfName(): String {
        return String.format("%s (v%s).pdf", this.name, this.version)
    }

    private fun toPdf(context: Context): PdfDocument {
        val writer = PDFWriter.Builder()
            .setDocumentTheme(DocumentTheme().apply {
                title.alignment = Paint.Align.RIGHT
                subtitle.alignment = Paint.Align.RIGHT
                setFont(Typeface.createFromAsset(context.resources.assets, "fonts/OpenSans-Regular.ttf"))
            })
            .setMargin(19F)
            .build()

        // Create cover page
        writer.centerVertical()
        writer.title(this.projectId)
        writer.subtitle(this.name)
        writer.subtitle("Version: ${this.version}")
        writer.pageBreak()

        // Write document contents
        this.sections.forEachIndexed { index, section ->
            if (section.isSubsection) {
                writer.vspace(2)
                writer.h2(section.name)
            } else {
                if (index > 0) writer.pageBreak()
                writer.h1(section.name)
            }

            if (section.body.isNotEmpty()) writer.normal(section.body)
            FileUtils.getBitmap(index, version, projectId)?.let { writer.image(it) }
        }

        return writer.finish()
    }

    class Builder {

        private val document = Document()

        fun setProject(project: String): Builder {
            document.projectId = project
            return this
        }

        fun setType(type: DocumentType): Builder {
            document.name = type.name
            document.sections.addAll(createSections(type))
            return this
        }

        fun setVersion(version: String): Builder {
            document.version = version
            return this
        }

        fun build(): Document {
            return document
        }

        private fun createSections(type: DocumentType): List<DocumentSection> {
            val sections = ArrayList<DocumentSection>()
            type.headings.mapTo(sections) { DocumentSection(it) }
            return sections
        }

    }

}