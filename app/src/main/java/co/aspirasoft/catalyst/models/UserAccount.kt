package co.aspirasoft.catalyst.models

import co.aspirasoft.model.BaseModel
import java.util.*

/**
 * Account of an app user.
 *
 * @property id A unique id for this user.
 * @property name Name of the user.
 * @property email Email address of the user.
 * @property bio More info about the user.
 * @property blog Link to user's blog / website.
 * @property github Link to user's Github profile.
 * @property headline Headline of the user's profile.
 * @property location Location of the user.
 * @property phone Phone number of the user.
 * @property password Password of the user's account.
 *
 * @author saifkhichi96
 * @since 1.0.0
 */
data class UserAccount(var id: String = "") : BaseModel() {

    var name: String = ""
    var email: String = ""
        set(value) {
            field = value.toLowerCase(Locale.getDefault())
        }

    var bio: String? = null
    var blog: String? = null
    var github: String? = null
    var headline: String? = null
    var location: String? = null
    var phone: String? = null

    @Transient
    var password: String = ""

}