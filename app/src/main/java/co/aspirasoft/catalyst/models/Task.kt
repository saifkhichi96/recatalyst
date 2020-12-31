package co.aspirasoft.catalyst.models

import co.aspirasoft.model.BaseModel
import java.text.SimpleDateFormat
import java.util.*

class Task(var name: String, var description: String) : BaseModel() {

    constructor() : this("", "")

    lateinit var id: String

    var deadline: String = SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(System.currentTimeMillis())

    var isCompleted: Boolean = false

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Task

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    companion object {
        const val DATE_FORMAT = "yyyy-MM-dd"
    }

}