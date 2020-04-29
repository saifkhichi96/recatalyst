package co.aspirasoft.sams.model

import co.aspirasoft.model.BaseModel

class Credentials(var email: String, @Transient var password: String) : BaseModel() {

    // no-arg constructor required for Firebase
    constructor() : this("", "")

}