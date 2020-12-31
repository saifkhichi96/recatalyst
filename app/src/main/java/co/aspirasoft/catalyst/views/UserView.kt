package co.aspirasoft.catalyst.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import co.aspirasoft.catalyst.R
import co.aspirasoft.catalyst.models.UserAccount
import co.aspirasoft.catalyst.utils.storage.ImageLoader
import co.aspirasoft.view.BaseView

class UserView : BaseView<UserAccount> {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    private var userImage: ImageView
    private var userNameLabel: TextView
    private var userEmailLabel: TextView

    private var onProfileButtonClickedListener: ((user: UserAccount) -> Unit)? = null

    fun setOnProfileButtonClickedListener(onProfileButtonClickedListener: (user: UserAccount) -> Unit) {
        this.onProfileButtonClickedListener = onProfileButtonClickedListener
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.view_user, this)
        userImage = findViewById(R.id.userImage)
        userNameLabel = findViewById(R.id.userNameLabel)
        userEmailLabel = findViewById(R.id.userEmailLabel)
    }

    override fun updateView(model: UserAccount) {
        userNameLabel.text = model.name
        userEmailLabel.text = model.email
        userImage.setOnClickListener {
            onProfileButtonClickedListener?.let { it(model) }
        }

        ImageLoader.with(context)
                .load(model.id)
                .into(userImage)
    }

}