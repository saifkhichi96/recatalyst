package co.aspirasoft.catalyst.models

import java.io.Serializable

/**
 * A single segment of the [Document].
 *
 * This class represents a segment of a document comprising of a heading, and content
 * which may include text and pictures.
 */
class DocumentSection internal constructor(val name: String, var body: String) : Serializable {

    constructor() : this("", "")

    var isSubsection: Boolean = name.split(".").dropLastWhile { it.isEmpty() }.size > 2

}