package co.aspirasoft.catalyst.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import co.aspirasoft.catalyst.R

class DrawingView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    // To hold the path that will be drawn.
    private var drawPath: Path? = null

    // Paint object to draw drawPath and drawCanvas.
    private var drawPaint: Paint? = null
    private var canvasPaint: Paint? = null

    // initial color
    private var paintColor = -0x1000000
    private var previousColor = paintColor

    // canvas on which drawing takes place.
    private var drawCanvas: Canvas? = null

    // canvas bitmap
    private var canvasBitmap: Bitmap? = null

    // Brush stroke width
    private var brushSize = 0f
    var lastBrushSize = 0f

    // To enable and disable erasing mode.
    private var erase = false
    private var baseBitmap: Bitmap? = null

    /**
     * Initialize all objects required for drawing here.
     * One time initialization reduces resource consumption.
     */
    init {
        drawPath = Path()
        drawPaint = Paint()
        drawPaint!!.color = paintColor

        // Making drawing smooth.
        drawPaint!!.isAntiAlias = true
        drawPaint!!.style = Paint.Style.STROKE
        drawPaint!!.strokeJoin = Paint.Join.ROUND
        drawPaint!!.strokeCap = Paint.Cap.ROUND
        canvasPaint = Paint(Paint.DITHER_FLAG)

        // Initial brush size is small.
        brushSize = resources.getInteger(R.integer.small_size).toFloat()
        lastBrushSize = brushSize
        drawPaint!!.strokeWidth = brushSize
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        canvasBitmap = when (baseBitmap) {
            null -> Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            else -> baseBitmap!!.copy(baseBitmap!!.config, true)
        }
        drawCanvas = Canvas(canvasBitmap!!)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(canvasBitmap!!, 0f, 0f, canvasPaint)
        canvas.drawPath(drawPath!!, drawPaint!!)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        // X and Y position of user touch.
        val touchX = event.x
        val touchY = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> drawPath!!.moveTo(touchX, touchY)
            MotionEvent.ACTION_MOVE -> drawPath!!.lineTo(touchX, touchY)
            MotionEvent.ACTION_UP -> {
                if (erase) {
                    drawPaint!!.color = Color.WHITE
                }
                drawCanvas!!.drawPath(drawPath!!, drawPaint!!)
                drawPath!!.reset()
                drawPaint!!.xfermode = null
            }
            else -> return false
        }

        // invalidate the view so that canvas is redrawn.
        invalidate()
        return true
    }

    fun setColor(newColor: String?) {
        // invalidate the view
        invalidate()
        paintColor = Color.parseColor(newColor)
        drawPaint!!.color = paintColor
        previousColor = paintColor
    }

    fun setBrushSize(newSize: Float) {
        brushSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, newSize, resources.displayMetrics)
        drawPaint!!.strokeWidth = brushSize
    }

    fun setErase(isErase: Boolean) {
        //set erase true or false
        erase = isErase
        if (erase) {
            drawPaint!!.color = Color.WHITE
            //drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        } else {
            drawPaint!!.color = previousColor
            drawPaint!!.xfermode = null
        }
    }

    fun reset() {
        // drawCanvas.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR);
        canvasBitmap = Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888)
        drawCanvas = Canvas(canvasBitmap!!)
        drawCanvas!!.drawColor(0, PorterDuff.Mode.CLEAR)
        invalidate()
    }

    fun setBaseBitmap(baseBitmap: Bitmap?) {
        this.baseBitmap = baseBitmap
    }

}