package co.aspirasoft.catalyst.activities.abs

import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import co.aspirasoft.catalyst.R
import co.aspirasoft.catalyst.dialogs.SettingsDialog

abstract class DashboardActivity : SecureActivity() {

    private var doubleBackToExitPressedOnce = false

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_action_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_open_account -> {
                SettingsDialog.Builder(this).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, getString(R.string.message_close_app), Toast.LENGTH_SHORT).show()
        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }

}