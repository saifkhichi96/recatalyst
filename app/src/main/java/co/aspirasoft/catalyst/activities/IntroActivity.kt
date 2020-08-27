package co.aspirasoft.catalyst.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import co.aspirasoft.catalyst.R
import co.aspirasoft.catalyst.fragments.ConnectionIntro
import co.aspirasoft.catalyst.fragments.ConnectionRequestIntro
import kotlinx.android.synthetic.main.activity_intro.*


/**
 * @author saifkhichi96
 * @since 1.0.0
 */
class IntroActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        wizardView.apply {
            setupWithWizardSteps(supportFragmentManager, listOf(
                    ConnectionIntro(),
                    ConnectionRequestIntro()
            ))
            setOnSubmitListener { finish() }
        }

        closeButton.setOnClickListener {
            finish()
        }
    }

    override fun onBackPressed() {
        if (!wizardView.onBackPressed()) {
            super.onBackPressed()
        }
    }


}