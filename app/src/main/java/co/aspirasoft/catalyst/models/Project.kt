package co.aspirasoft.catalyst.models

import co.aspirasoft.model.BaseModel
import java.util.*

/**
 * Project is a model class representing a user project.
 *
 * A project is owned by a user, and contains different project documents
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
data class Project(val name: String = "", val ownerId: String = "") : BaseModel() {

    var description: String? = null

    var team = Team(project = this.name, manager = this.ownerId)

    var deadline = 0L

    var finished = false

    fun getStatus(): TaskStatus {
        return if (this.finished) {
            TaskStatus.COMPLETED
        } else {
            try {
                val deadline = Date(this.deadline)
                val now = Calendar.getInstance().time
                when {
                    deadline.before(now) -> TaskStatus.OVERDUE
                    else -> TaskStatus.ONGOING
                }
            } catch (ex: Exception) {
                TaskStatus.ONGOING
            }
        }
    }

}