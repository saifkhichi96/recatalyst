package co.aspirasoft.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.animation.DecelerateInterpolator
import android.widget.Scroller
import androidx.viewpager.widget.ViewPager

/**
 * A [ViewPager] which does not allow swiping gesture.
 *
 * @author saifkhichi96
 */
class NoSwipeViewPager(context: Context, attrs: AttributeSet?) : ViewPager(context, attrs) {

    constructor(context: Context) : this(context, null)

    init {
        NoSwipeScroller.init(context)
    }

    /**
     * Overrides touch gesture to disable swiping between pages.
     */
    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return false
    }

    /**
     * Overrides touch gesture to disable swiping between pages.
     */
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return false
    }

    /**
     * Custom [Scroller] which enables smooth manual scrolling between pages.
     */
    private class NoSwipeScroller(context: Context?) : Scroller(context, DecelerateInterpolator()) {
        override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int, duration: Int) {
            super.startScroll(startX, startY, dx, dy, 350 /*1 secs*/)
        }

        companion object {
            fun init(context: Context) {
                try {
                    val viewpager: Class<*> = ViewPager::class.java
                    val scroller = viewpager.getDeclaredField("mScroller")
                    scroller.isAccessible = true
                    scroller[this] = NoSwipeScroller(context)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

}