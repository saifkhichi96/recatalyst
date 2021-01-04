package co.aspirasoft.catalyst.activities.abs

import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import co.aspirasoft.catalyst.R

abstract class DashboardChildActivity : SecureActivity() {

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        findViewById<Toolbar?>(R.id.toolbar)?.let { setSupportActionBar(it) }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun setContentView(view: View?) {
        super.setContentView(view)
        findViewById<Toolbar?>(R.id.toolbar)?.let { setSupportActionBar(it) }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun setContentView(view: View?, params: ViewGroup.LayoutParams?) {
        super.setContentView(view, params)
        findViewById<Toolbar?>(R.id.toolbar)?.let { setSupportActionBar(it) }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            onBackPressed()
            true
        } else super.onOptionsItemSelected(item)
    }

}