package dev.aspirasoft.scribe

import android.graphics.Typeface
import dev.aspirasoft.scribe.text.Capitalization
import dev.aspirasoft.scribe.text.FontStyle
import dev.aspirasoft.scribe.text.ParagraphStyle

class DocumentTheme {
    val title = ParagraphStyle().apply {
        this.fontSize = 30F
        this.fontStyle = FontStyle.BOLD
        this.capitalization = Capitalization.TITLE_CASE
    }

    val subtitle = ParagraphStyle().apply {
        this.fontSize = 20F
        this.fontStyle = FontStyle.REGULAR
        this.capitalization = Capitalization.TITLE_CASE
    }

    val body = ParagraphStyle().apply {
        this.fontSize = 11F
        this.fontStyle = FontStyle.REGULAR
        this.capitalization = Capitalization.NONE
    }

    val caption = ParagraphStyle().apply {
        this.fontSize = 11F
        this.fontStyle = FontStyle.BOLD
        this.capitalization = Capitalization.ALL_CAPS
    }

    val h1 = ParagraphStyle().apply {
        this.fontSize = 18F
        this.fontStyle = FontStyle.BOLD
        this.capitalization = Capitalization.TITLE_CASE
    }

    val h2 = ParagraphStyle().apply {
        this.fontSize = 16F
        this.fontStyle = FontStyle.BOLD
        this.capitalization = Capitalization.TITLE_CASE
    }

    val h3 = ParagraphStyle().apply {
        this.fontSize = 14F
        this.fontStyle = FontStyle.BOLD
        this.capitalization = Capitalization.TITLE_CASE
    }

    val h4 = ParagraphStyle().apply {
        this.fontSize = 13F
        this.fontStyle = FontStyle.BOLD_ITALIC
        this.capitalization = Capitalization.TITLE_CASE
    }

    val h5 = ParagraphStyle().apply {
        this.fontSize = 12F
        this.fontStyle = FontStyle.REGULAR
        this.capitalization = Capitalization.ALL_CAPS
    }

    val h6 = ParagraphStyle().apply {
        this.fontSize = 11F
        this.fontStyle = FontStyle.ITALIC
        this.capitalization = Capitalization.NONE
    }

    fun setFont(font: Typeface) {
        title.font = font
        subtitle.font = font
        body.font = font
        caption.font = font
        h1.font = font
        h2.font = font
        h3.font = font
        h4.font = font
        h5.font = font
        h6.font = font
    }
}