package co.aspirasoft.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.SparseArray
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import co.aspirasoft.R
import params.com.stepview.StatusViewScroller

/**
 * WizardView allows creation of a UI with multiple steps.
 *
 * This view can be used to break down complex tasks into simpler steps and
 * present them to the user one after the other. It can be used to create
 * intros, sign up forms, etc.
 *
 * @author saifkhichi96
 * @version 1.1.0
 */
class WizardView(context: Context, attrs: AttributeSet?, defStyleInt: Int)
    : LinearLayout(context, attrs, defStyleInt) {

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    private val backButton: Button get() = findViewById(R.id.backButton)
    private val contentView: ViewPager get() = findViewById(R.id.contentView)
    private val divider: View get() = findViewById(R.id.divider)
    private val nextButton: Button get() = findViewById(R.id.nextButton)
    private val stepsView: StatusViewScroller get() = findViewById(R.id.stepsView)
    private val titleView: TextView get() = findViewById(R.id.titleView)
    private val wizardIcon: ImageView get() = findViewById(R.id.wizardIcon)

    var icon: Drawable? = null
        set(value) {
            field = value
            wizardIcon.visibility = if (value != null) View.VISIBLE else View.GONE
            wizardIcon.setImageDrawable(value)
        }

    var isBackShown: Boolean = true
        set(value) {
            field = value
            backButton.visibility = if (value) View.VISIBLE else View.GONE
        }

    var isDividerShown: Boolean = true
        set(value) {
            field = value
            divider.visibility = if (value) View.VISIBLE else View.GONE
        }

    var isIndicatorShown: Boolean = true
        set(value) {
            field = value
            stepsView.visibility = if (value) View.VISIBLE else View.GONE
        }

    var isTitleShown: Boolean = true
        set(value) {
            field = value
            titleView.visibility = if (value) View.VISIBLE else View.GONE
        }

    var nextLabel: String = "Next"
        set(value) {
            field = value
            nextButton.text = value
        }

    var submitLabel: String = "Submit"

    init {
        context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.WizardView,
                0, 0).apply {

            try {
                val swipeEnabled = getBoolean(R.styleable.WizardView_swipeEnabled, false)
                if (swipeEnabled) {
                    View.inflate(context, R.layout.view_wizard, this@WizardView)
                } else {
                    View.inflate(context, R.layout.view_wizard_noswipe, this@WizardView)
                }

                icon = getDrawable(R.styleable.WizardView_icon)

                isBackShown = getBoolean(R.styleable.WizardView_showBack, true)
                isDividerShown = getBoolean(R.styleable.WizardView_showDivider, true)
                isIndicatorShown = getBoolean(R.styleable.WizardView_showIndicator, true)
                isTitleShown = getBoolean(R.styleable.WizardView_showTitle, true)

                nextLabel = getString(R.styleable.WizardView_nextLabel) ?: nextLabel
                submitLabel = getString(R.styleable.WizardView_submitLabel) ?: submitLabel
            } finally {
                recycle()
            }
        }
    }

    private var onSubmitListener: ((data: SparseArray<Any>) -> Unit)? = null

    private lateinit var mSteps: List<WizardViewStep>

    private val isCurrentInputValid: Boolean
        get() = mSteps[stepsView.statusView.currentCount - 1].isDataValid()

    private val isFirstStep: Boolean
        get() = stepsView.statusView.currentCount == 1

    private val isLastStep: Boolean
        get() = stepsView.statusView.currentCount == mSteps.size

    private val data: SparseArray<Any>
        get() {
            val wizardData = SparseArray<Any>()
            for (step in mSteps) {
                for (i in 0 until step.data.size()) {
                    val key = step.data.keyAt(i)
                    val value = step.data.valueAt(i)
                    wizardData.put(key, value)
                }
            }
            return wizardData
        }

    fun setupWithWizardSteps(supportFragmentManager: FragmentManager, steps: List<WizardViewStep>) {
        this.mSteps = steps
        stepsView.statusView.stepCount = steps.size

        val adapter = WizardStepAdapter(supportFragmentManager, steps)
        contentView.adapter = adapter
        contentView.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(i: Int, v: Float, i1: Int) {}
            override fun onPageSelected(newPosition: Int) {
                val currentPosition = stepsView.statusView.currentCount - 1
                if (newPosition <= currentPosition || isCurrentInputValid) {
                    selectPosition(newPosition)
                } else {
                    contentView.currentItem = currentPosition
                }

                backButton.visibility = if (isFirstStep || !isBackShown) View.GONE else View.VISIBLE
                nextButton.text = if (isLastStep) submitLabel else nextLabel
            }

            override fun onPageScrollStateChanged(i: Int) {}
        })

        selectPosition(0)
        backButton.visibility = View.GONE
        nextButton.text = if (steps.size == 1) submitLabel else nextLabel

        nextButton.setOnClickListener {
            if (isCurrentInputValid) {
                onNextClicked()
            }
        }

        backButton.setOnClickListener {
            onBackPressed()
        }
    }

    fun setOnSubmitListener(l: ((data: SparseArray<Any>) -> Unit)?) {
        onSubmitListener = l
    }

    fun onBackPressed(): Boolean {
        return if (!isFirstStep) {
            contentView.currentItem = contentView.currentItem - 1
            true
        } else false
    }

    private fun onNextClicked() {
        if (isLastStep) {
            onSubmitListener?.let { it(data) }
        } else {
            contentView.currentItem = contentView.currentItem + 1
        }
    }

    private fun selectPosition(position: Int) {
        stepsView.statusView.currentCount = position + 1
        titleView.text = mSteps[position].title
    }

    class WizardStepAdapter(manager: FragmentManager, private val steps: List<WizardViewStep>)
        : FragmentPagerAdapter(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        override fun getCount(): Int {
            return steps.size
        }

        override fun getItem(position: Int): WizardViewStep {
            return steps[if (position < count) position else 0]
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return steps[position].title
        }

    }

}