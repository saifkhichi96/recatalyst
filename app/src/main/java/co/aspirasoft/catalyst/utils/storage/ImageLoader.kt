package co.aspirasoft.catalyst.utils.storage

import android.content.Context
import android.widget.ImageView
import co.aspirasoft.catalyst.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

object ImageLoader {

    fun with(context: Context): ImageLoadTask {
        return ImageLoadTask(context)
    }

    class ImageLoadTask(val context: Context) {
        fun load(uid: String): UserImageTask {
            return UserImageTask(context, uid)
        }
    }

    class UserImageTask(val context: Context, val uid: String) {
        private var skip: Boolean = false
        private val filename = "photo.png"

        fun skipCache(skip: Boolean): UserImageTask {
            this.skip = skip
            return this
        }

        fun into(target: ImageView) {
            FileManager.newInstance(context, "users/${uid}/").download(
                filename,
                {
                    try {
                        Glide.with(context)
                            .load(it)
                            .skipMemoryCache(true)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .placeholder(R.drawable.placeholder_avatar)
                            .into(target)
                    } catch (ignored: Exception) {

                    }
                },
                {
                    try {
                        Glide.with(context).load(R.drawable.placeholder_avatar).into(target)
                    } catch (ignored: Exception) {

                    }
                },
                skip
            )

        }
    }

}