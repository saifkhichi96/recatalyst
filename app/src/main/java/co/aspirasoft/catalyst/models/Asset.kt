package co.aspirasoft.catalyst.models

import co.aspirasoft.model.BaseModel
import com.google.firebase.storage.StorageMetadata

class Asset(val name: String, val metadata: StorageMetadata) : BaseModel() {

    override fun toString(): String {
        return name
    }

}