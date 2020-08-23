package co.aspirasoft.catalyst.utils

import com.google.firebase.database.*
import kotlin.reflect.KClass


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

fun <T : Any> Query.getOrNull(type: KClass<T>, listener: (result: T?) -> Unit) {
    get { snapshot ->
        listener(snapshot.getOrNull(type))
    }
}

fun <T : Any> Query.observeOrNull(type: KClass<T>, listener: (result: T?) -> Unit) {
    observe { snapshot ->
        listener(snapshot.getOrNull(type))
    }
}

fun <T : Any> Query.getOrNull(type: GenericTypeIndicator<T>, listener: (result: T?) -> Unit) {
    get { snapshot ->
        listener(snapshot.getOrNull(type))
    }
}

fun <T : Any> Query.observeOrNull(type: GenericTypeIndicator<T>, listener: (result: T?) -> Unit) {
    observe { snapshot ->
        listener(snapshot.getOrNull(type))
    }
}

fun <T : Any> Query.list(listener: (list: List<T>) -> Unit) {
    val t = object : GenericTypeIndicator<ArrayList<T>>() {}
    getOrNull(t) { listener(it.orEmpty()) }
}

fun <V : Any> Query.map(listener: (map: HashMap<String, V>?) -> Unit) {
    val t = object : GenericTypeIndicator<HashMap<String, V>>() {}
    getOrNull(t) { listener(it) }
}

fun <T : Any> Query.mappedList(listener: (list: List<T>) -> Unit) {
    map<T> { listener(it?.values?.toList().orEmpty()) }
}

fun <T : Any> DataSnapshot?.getOrNull(type: KClass<T>): T? {
    return kotlin.runCatching { this?.getValue(type.java) }.getOrNull()
}

fun <T : Any> DataSnapshot?.getOrNull(type: GenericTypeIndicator<T>): T? {
    return kotlin.runCatching { this?.getValue(type) }.getOrNull()
}

fun <T : Any> DataSnapshot?.list(listener: (list: List<T>) -> Unit) {
    list<T, Unit>(listener)
}

fun <T : Any, R> DataSnapshot?.list(listener: (list: List<T>) -> R): R {
    val t = object : GenericTypeIndicator<ArrayList<T>>() {}
    return listener(getOrNull(t).orEmpty())
}

fun <V : Any> DataSnapshot?.map(listener: (map: HashMap<String, V>?) -> Unit) {
    map<V, Unit>(listener)
}

fun <V : Any, R> DataSnapshot?.map(listener: (map: HashMap<String, V>?) -> R): R {
    val t = object : GenericTypeIndicator<HashMap<String, V>>() {}
    return listener(getOrNull(t))
}

fun <T : Any> DataSnapshot?.mappedList(listener: (list: List<T>) -> Unit) {
    mappedList<T, Unit>(listener)
}

fun <T : Any, R> DataSnapshot?.mappedList(listener: (list: List<T>) -> R): R {
    return map<T, R> { listener(it?.values?.toList().orEmpty()) }
}