package dev.aspirasoft.utils.firebase.database

import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow


@ExperimentalCoroutinesApi
suspend inline fun <reified T> DatabaseReference.observe(): Flow<DatabaseEvent<T>> = callbackFlow {
    val valueEventListener = object : ValueEventListener {
        override fun onDataChange(s: DataSnapshot) = sendBlocking(DatabaseEvent.Changed(s.getValue<T>()))
        override fun onCancelled(error: DatabaseError) = sendBlocking(DatabaseEvent.Cancelled<T>(error))
    }
    addValueEventListener(valueEventListener)
    awaitClose {
        removeEventListener(valueEventListener)
    }
}

@ExperimentalCoroutinesApi
suspend inline fun <reified T> DatabaseReference.observeChildren(): Flow<DatabaseEvent<T>> = callbackFlow {
    val childEventListener = object : ChildEventListener {
        override fun onChildAdded(s: DataSnapshot, p: String?) = sendBlocking(DatabaseEvent.Created(s.getValue<T>()))
        override fun onChildChanged(s: DataSnapshot, p: String?) = sendBlocking(DatabaseEvent.Changed(s.getValue<T>()))
        override fun onChildRemoved(s: DataSnapshot) = sendBlocking(DatabaseEvent.Deleted(s.getValue<T>()))
        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
        override fun onCancelled(error: DatabaseError) = sendBlocking(DatabaseEvent.Cancelled<T>(error))
    }
    addChildEventListener(childEventListener)
    awaitClose {
        removeEventListener(childEventListener)
    }
}