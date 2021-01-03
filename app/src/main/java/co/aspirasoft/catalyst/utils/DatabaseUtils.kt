package co.aspirasoft.catalyst.utils

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue


fun Query.get(listener: (snapshot: DataSnapshot?) -> Unit) {
    getOrError { snapshot, _ ->
        listener(snapshot)
    }
}

fun Query.observe(observer: (snapshot: DataSnapshot?) -> Unit) {
    observeOrError { snapshot, _ ->
        observer(snapshot)
    }
}

fun Query.getOrError(listener: (snapshot: DataSnapshot?, ex: Exception?) -> Unit) {
    addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            listener(snapshot, null)
        }

        override fun onCancelled(error: DatabaseError) {
            listener(null, error.toException())
        }
    })
}

fun Query.observeOrError(observer: (snapshot: DataSnapshot?, ex: Exception?) -> Unit) {
    addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            observer(snapshot, null)
        }

        override fun onCancelled(error: DatabaseError) {
            observer(null, error.toException())
        }
    })
}

fun Query.getChildren(listener: (snapshots: Iterable<DataSnapshot>?) -> Unit) {
    get {
        listener(it?.children)
    }
}

fun Query.observeChildren(observer: (snapshots: Iterable<DataSnapshot>?) -> Unit) {
    observe {
        observer(it?.children)
    }
}

inline fun <reified T> Query.getOrNull(crossinline listener: (result: T?) -> Unit) {
    get { snapshot ->
        listener(snapshot.getOrNull())
    }
}

inline fun <reified T> Query.observeOrNull(crossinline listener: (result: T?) -> Unit) {
    observe { snapshot ->
        listener(snapshot.getOrNull())
    }
}

inline fun <reified T> Query.list(crossinline listener: (list: List<T>) -> Unit) {
    get { listener(it.list()) }
}

inline fun <reified T> DataSnapshot?.getOrNull(): T? {
    return kotlin.runCatching { this?.getValue<T>() }.getOrNull()
}

inline fun <reified T> DataSnapshot?.list(): List<T> {
    val list = mutableListOf<T>()
    if (this != null) {
        for (childSnapshot in this.children) {
            childSnapshot.getOrNull<T>()?.let { list.add(it) }
        }
    }
    return list
}