package co.aspirasoft.catalyst.models

import co.aspirasoft.model.BaseModel
import java.text.SimpleDateFormat
import java.util.*

data class Task(var id: String = "") : BaseModel() {

    var name: String = ""
    var description: String = ""
    var deadline: String = SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(System.currentTimeMillis())
    var isCompleted: Boolean = false

    companion object {
        const val DATE_FORMAT = "yyyy-MM-dd"
    }

}