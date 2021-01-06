package co.aspirasoft.catalyst.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import co.aspirasoft.catalyst.databinding.ViewRequestIncomingBinding
import co.aspirasoft.catalyst.models.UserAccount
import co.aspirasoft.catalyst.utils.storage.ImageLoader
import co.aspirasoft.view.BaseView

class IncomingRequestView : BaseView<UserAccount> {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    private val binding = ViewRequestIncomingBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )

    private var onAvatarClickedListener: ((user: UserAccount) -> Unit)? = null

    fun setOnAvatarClickedListener(listener: (user: UserAccount) -> Unit) {
        this.onAvatarClickedListener = listener
    }

    fun setOnAcceptListener(l: OnClickListener) {
        binding.acceptButton.setOnClickListener(l)
    }

    fun setOnRejectListener(l: OnClickListener) {
        binding.rejectButton.setOnClickListener(l)
    }

    override fun updateView(model: UserAccount) {
        binding.root.setOnClickListener { onAvatarClickedListener?.let { it(model) } }
        binding.accountName.text = model.name
        binding.accountShortBio.text = model.headline
        ImageLoader.loadUserAvatar(
            (context as AppCompatActivity),
            model.id,
            binding.accountAvatar
        )
    }

}