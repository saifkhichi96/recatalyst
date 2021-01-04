package co.aspirasoft.catalyst.models

import android.graphics.pdf.PdfDocument
import co.aspirasoft.catalyst.bo.PdfWriter
import co.aspirasoft.catalyst.utils.FileUtils
import java.io.ByteArrayOutputStream
import java.io.Serializable

/**
 * @author saifkhichi96
 * @version 1.0.0
 * @since 1.0.0 26/10/2018 3:00 AM
 */
class Document : Serializable {

    lateinit var name: String

    lateinit var projectId: String

    lateinit var version: String

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

    fun toByteArray(): ByteArray {
        val stream = ByteArrayOutputStream()
        val pdf = toPdf()
        pdf.writeTo(stream)
        pdf.close()
        return stream.toByteArray()
    }

    fun toPdfName(): String {
        return String.format("%s (v%s).pdf", this.name, this.version)
    }

    private fun toPdf(): PdfDocument {
        val writer = PdfWriter()

        // Create cover page
        writer.spacing(10)
        writer.title(this.projectId)
        writer.subtitle(this.name)
        writer.heading("Version: ${this.version}")
        writer.pageBreak()

        // Write document contents
        this.sections.forEachIndexed { index, section ->
            if (section.isSubsection) {
                writer.sectionBreak()
                writer.bold(section.name)
            } else {
                if (index > 0) writer.pageBreak()
                writer.heading(section.name)
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