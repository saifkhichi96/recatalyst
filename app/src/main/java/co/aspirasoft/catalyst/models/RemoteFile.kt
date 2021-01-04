package co.aspirasoft.catalyst.models

import co.aspirasoft.model.BaseModel
import com.google.firebase.storage.StorageMetadata

/**
 * A file stored on the remote Firebase storage.
 *
 * @property name The name of the remote file.
 * @property metadata The [StorageMetadata] of the remote file.
 *
 * @author saifkhichi96
 * @since 1.0.0
 */
data class RemoteFile(val name: String, val metadata: StorageMetadata) : BaseModel()