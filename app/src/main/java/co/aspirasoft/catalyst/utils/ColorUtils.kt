package co.aspirasoft.catalyst.utils

import android.content.Context
import android.graphics.Color
import androidx.annotation.ColorInt
import co.aspirasoft.catalyst.R


object ColorUtils {

    @ColorInt
    fun convertToColor(context: Context, o: Any): Int {
        return try {
            val i = o.hashCode()
            Color.parseColor("#FF" + Integer.toHexString(i shr 16 and 0xFF) +
                    Integer.toHexString(i shr 8 and 0xFF) +
                    Integer.toHexString(i and 0xFF))
        } catch (ignored: Exception) {
            context.getColor(R.color.colorAccent)
        }
    }

    @ColorInt
    fun setAlpha(@ColorInt color: Int, alpha: Float): Int {
        val c = Color.valueOf(color)
        return Color.valueOf(c.red(), c.green(), c.blue(), alpha).toArgb()
    }

}