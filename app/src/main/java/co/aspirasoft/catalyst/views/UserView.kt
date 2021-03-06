package co.aspirasoft.catalyst.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import co.aspirasoft.catalyst.databinding.ViewUserBinding
import co.aspirasoft.catalyst.models.UserAccount
import co.aspirasoft.view.BaseView

class UserView : BaseView<UserAccount> {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    private val binding = ViewUserBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )

    private var onAvatarClickedListener: ((user: UserAccount) -> Unit)? = null

    fun setOnAvatarClickedListener(listener: (user: UserAccount) -> Unit) {
        this.onAvatarClickedListener = listener
    }

    override fun updateView(model: UserAccount) {
        binding.accountName.text = model.name
        binding.accountShortBio.text = model.headline
        binding.root.setOnClickListener { onAvatarClickedListener?.invoke(model) }
        binding.accountAvatar.updateView(model)
    }

}