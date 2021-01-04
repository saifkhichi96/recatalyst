package co.aspirasoft.catalyst.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import co.aspirasoft.catalyst.databinding.ActivityIntroBinding
import co.aspirasoft.catalyst.fragments.ConnectionIntro
import co.aspirasoft.catalyst.fragments.ConnectionRequestIntro


/**
 * @author saifkhichi96
 * @since 1.0.0
 */
class IntroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityIntroBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.wizardView.apply {
            setupWithWizardSteps(
                supportFragmentManager, listOf(
                    ConnectionIntro(),
                    ConnectionRequestIntro()
                )
            )
            setOnSubmitListener { finish() }
        }

        binding.closeButton.setOnClickListener {
            finish()
        }
    }

    override fun onBackPressed() {
        if (!binding.wizardView.onBackPressed()) {
            super.onBackPressed()
        }
    }

}