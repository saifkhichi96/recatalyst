package co.aspirasoft.catalyst.utils.storage

import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import co.aspirasoft.catalyst.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.coroutines.launch

object ImageLoader {

    /**
     * Shows a user's avatar in an [ImageView].
     *
     * @param context An instance of the activity where this view would be displayed.
     * @param uid The id of the user account.
     * @param view The [ImageView] where to show the avatar.
     */
    fun loadUserAvatar(context: AppCompatActivity, uid: String, view: ImageView) = context.lifecycleScope.launch {
        val storage = AccountStorage(context, uid)
        try {
            // Quickly load and display the cached avatar
            val cachedAvatar = storage.downloadAvatar(preferCache = true)
            Glide.with(context)
                .load(cachedAvatar)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .placeholder(R.drawable.placeholder_avatar)
                .apply { context.runOnUiThread { this.into(view) } }
        } catch (ex: Exception) {
            Glide.with(context)
                .load(R.drawable.placeholder_avatar)
                .apply { context.runOnUiThread { this.into(view) } }
        }
    }

}