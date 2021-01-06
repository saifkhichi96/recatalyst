package co.aspirasoft.catalyst.utils.storage

import co.aspirasoft.catalyst.models.Project
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage

object RemoteStorage {

    fun userData(uid: String): StorageReference {
        return Firebase.storage.getReference("users/$uid/")
    }

    fun projectData(project: Project): StorageReference {
        return userData(project.ownerId).child("projects/${project.name}/")
    }

}