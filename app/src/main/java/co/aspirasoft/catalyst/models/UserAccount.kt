package co.aspirasoft.catalyst.models

import co.aspirasoft.model.BaseModel
import java.util.*
import kotlin.reflect.KClass

/**
 * A data class representing a single user of the app.
 *
 * @property id A unique id for this user.
 * @property name Name of the user.
 * @property email Email address of the user.
 * @property phone Phone number of the user.
 * @property password Password of the user's account.
 *
 * @author saifkhichi96
 * @since 1.0.0
 */
data class UserAccount(var id: String, var name: String, var email: String, @Transient var password: String) : BaseModel() {

    // no-arg constructor needed for Firebase
    constructor() : this("", "", "", "")

    val type: String get() = this::class.simpleName ?: "PersonalAccount"

    init {
        email = email.toLowerCase(Locale.getDefault())
    }

    var phone: String? = null
        set(value) {
            field = value
            setChanged()
        }

    companion object {
        @JvmStatic
        fun valueOf(s: String?): KClass<out UserAccount>? {
            return UserAccount::class
        }
    }

}

