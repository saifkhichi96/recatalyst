package co.aspirasoft.catalyst.models

import co.aspirasoft.model.BaseModel

/**
 * Project is a model class representing a user project.
 *
 * A project is owned by a user, and contains different project [documents]
 * and assets. It may also have a short [description] describing the purpose
 * of the project.
 *
 *
 * @constructor Creates a new project.
 * @param name Name of the project.
 * @param ownerId The id of the project owner.
 *
 * @author saifkhichi96
 * @since 1.0.0
 */
class Project(var name: String, var ownerId: String) : BaseModel() {

    // no-arg constructor required for Firebase
    constructor() : this("", "")

    var description: String? = null

    var team = Team(project = this.name, manager = this.ownerId)

    var documents = ArrayList<Document>()
        private set

    private fun containsDocument(name: String) = getDocument(name) != null

    fun addDocument(document: Document) {
        if (containsDocument(document.name)) {
            updateDocument(document.name, document)
        } else {
            documents.add(document)
        }
    }

    fun getDocument(name: String): Document? {
        return documents.find { it.name.equals(name, false) }
    }

    fun removeDocument(name: String) = getDocument(name)?.let { documents.remove(it) } != null

    private fun updateDocument(name: String, updatedDocument: Document) {
        getDocument(name)?.updateWith(updatedDocument)
    }

    override fun toString(): String {
        return name
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Project

        if (name != other.name) return false
        if (ownerId != other.ownerId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + ownerId.hashCode()
        return result
    }

}