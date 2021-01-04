package co.aspirasoft.catalyst.activities

import android.app.Dialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import co.aspirasoft.catalyst.R
import co.aspirasoft.catalyst.databinding.ActivityDrawingBinding
import co.aspirasoft.catalyst.utils.FileUtils.getBitmap
import co.aspirasoft.catalyst.utils.FileUtils.saveBitmap
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class DrawingActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityDrawingBinding

    private var selectedBrush: ImageButton? = null

    private var smallBrush = 0f
    private var mediumBrush = 0f
    private var largeBrush = 0f

    private var index = -1
    private lateinit var doorWindow: String
    private lateinit var project: String
    private lateinit var property: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDrawingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Get intent extras
        index = intent.getIntExtra("index", -1)
        if (index == -1) return finish()

        property = intent.getStringExtra("property") ?: return finish()
        doorWindow = intent.getStringExtra("doorWindow") ?: return finish()
        project = intent.getStringExtra("project") ?: return finish()

        this.title = property
        binding.drawingView.setBaseBitmap(getBitmap(index, doorWindow, project))

        // Getting the initial paint color.
        selectedBrush = binding.paintColors.getChildAt(1) as ImageButton
        selectedBrush!!.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.pallet_pressed))

        // Set initial brush size
        smallBrush = resources.getInteger(R.integer.small_size).toFloat()
        mediumBrush = resources.getInteger(R.integer.medium_size).toFloat()
        largeBrush = resources.getInteger(R.integer.large_size).toFloat()
        binding.drawingView.setBrushSize(smallBrush)

        // Set up click listeners
        binding.brushButton.setOnClickListener(this)
        binding.eraseButton.setOnClickListener(this)
        binding.newButton.setOnClickListener(this)
    }

    /**
     * Method is called when color is clicked from pallet.
     *
     * @param view ImageButton on which click took place.
     */
    fun paintClicked(view: View) {
        if (view !== selectedBrush) {
            // Update the color
            val imageButton = view as ImageButton
            val colorTag = imageButton.tag.toString()
            binding.drawingView.setColor(colorTag)

            // Swap the backgrounds for last active and currently active image button.
            imageButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.pallet_pressed))
            selectedBrush!!.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.pallet))
            selectedBrush = view
            binding.drawingView.setErase(false)
            binding.drawingView.setBrushSize(binding.drawingView.lastBrushSize)
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.brushButton -> showBrushSizeChooserDialog()
            R.id.eraseButton -> showEraserSizeChooserDialog()
            R.id.newButton -> showNewPaintingAlertDialog()
        }
    }

    private fun showBrushSizeChooserDialog() {
        val brushDialog = Dialog(this)
        brushDialog.setContentView(R.layout.dialog_drawing_brush)
        brushDialog.setTitle(getString(R.string.brush_size))
        val smallBtn = brushDialog.findViewById<ImageButton>(R.id.smallSize)
        smallBtn.setOnClickListener {
            binding.drawingView.setBrushSize(smallBrush)
            binding.drawingView.lastBrushSize = smallBrush
            brushDialog.dismiss()
        }
        val mediumBtn = brushDialog.findViewById<ImageButton>(R.id.mediumSize)
        mediumBtn.setOnClickListener {
            binding.drawingView.setBrushSize(mediumBrush)
            binding.drawingView.lastBrushSize = mediumBrush
            brushDialog.dismiss()
        }
        val largeBtn = brushDialog.findViewById<ImageButton>(R.id.largeSize)
        largeBtn.setOnClickListener {
            binding.drawingView.setBrushSize(largeBrush)
            binding.drawingView.lastBrushSize = largeBrush
            brushDialog.dismiss()
        }
        binding.drawingView.setErase(false)
        brushDialog.show()
    }

    private fun showEraserSizeChooserDialog() {
        val brushDialog = Dialog(this)
        brushDialog.setTitle(getString(R.string.eraser_size))
        brushDialog.setContentView(R.layout.dialog_drawing_brush)

        val smallButton = brushDialog.findViewById<ImageButton>(R.id.smallSize)
        smallButton.setOnClickListener {
            binding.drawingView.setErase(true)
            binding.drawingView.setBrushSize(smallBrush)
            brushDialog.dismiss()
        }

        val mediumButton = brushDialog.findViewById<ImageButton>(R.id.mediumSize)
        mediumButton.setOnClickListener {
            binding.drawingView.setErase(true)
            binding.drawingView.setBrushSize(mediumBrush)
            brushDialog.dismiss()
        }

        val largeButton = brushDialog.findViewById<ImageButton>(R.id.largeSize)
        largeButton.setOnClickListener {
            binding.drawingView.setErase(true)
            binding.drawingView.setBrushSize(largeBrush)
            brushDialog.dismiss()
        }
        brushDialog.show()
    }

    private fun showNewPaintingAlertDialog() {
        val newDialog = MaterialAlertDialogBuilder(this)
        newDialog.setTitle(getString(R.string.create_drawing))
        newDialog.setMessage(getString(R.string.create_drawing_confirm))
        newDialog.setPositiveButton(android.R.string.yes) { dialog, _ ->
            binding.drawingView.reset()
            dialog.dismiss()
        }
        newDialog.setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.cancel() }
        newDialog.show()
    }

    private fun showSavePaintingConfirmationDialog() {
        val saveDialog = MaterialAlertDialogBuilder(this)
        saveDialog.setTitle(getString(R.string.save))
        saveDialog.setMessage(getString(R.string.save_drawing, property))
        saveDialog.setPositiveButton(android.R.string.yes) { _, _ -> // Get canvas bitmap
            binding.drawingView.isDrawingCacheEnabled = true
            val bitmap = binding.drawingView.drawingCache

            // Save bitmap to file
            val saved = saveBitmap(bitmap, index, doorWindow, project)
            if (saved) {
                Toast.makeText(
                    this@DrawingActivity,
                    getString(R.string.save_drawing_success), Toast.LENGTH_SHORT
                )
                    .show()
                finish()
            } else {
                Toast.makeText(
                    this@DrawingActivity,
                        getString(R.string.save_error), Toast.LENGTH_SHORT)
                        .show()
            }
            // Destroy the current cache.
            binding.drawingView.destroyDrawingCache()
        }
        saveDialog.setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.cancel() }
        saveDialog.show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_drawing, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        // noinspection SimplifiableIfStatement
        if (id == R.id.action_save_drawing) {
            showSavePaintingConfirmationDialog()
            return true
        }
        if (id == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}