package co.aspirasoft.catalyst.models

import android.content.Context
import co.aspirasoft.catalyst.R

enum class TaskStatus {

    COMPLETED,
    ONGOING,
    OVERDUE;

    fun toString(context: Context): String {
        return when (this) {
            COMPLETED -> context.getString(R.string.task_status_completed)
            ONGOING -> context.getString(R.string.task_status_ongoing)
            OVERDUE -> context.getString(R.string.task_status_overdue)
        }
    }

}