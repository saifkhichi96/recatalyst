package co.aspirasoft.catalyst.utils.db

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObject

inline fun <reified T> DocumentSnapshot?.getOrNull(): T? {
    return kotlin.runCatching { this?.toObject<T>() }.getOrNull()
}

//inline fun <reified T> DocumentSnapshot?.list(): List<T> {
//    val list = mutableListOf<T>()
//    if (this != null) {
//        for (document in this.) {
//            childSnapshot.getOrNull<T>()?.let { list.add(it) }
//        }
//    }
//    return list
//}