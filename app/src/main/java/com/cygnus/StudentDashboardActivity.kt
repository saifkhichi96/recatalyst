package com.cygnus

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import co.aspirasoft.adapter.ModelViewAdapter
import com.cygnus.core.DashboardActivity
import com.cygnus.dao.SubjectsDao
import com.cygnus.model.Student
import com.cygnus.model.Subject
import com.cygnus.model.User
import com.cygnus.view.SubjectView
import com.google.android.gms.tasks.OnSuccessListener
import kotlinx.android.synthetic.main.activity_dashboard_student.*

/**
 * StudentDashboardActivity is the students' homepage.
 *
 * This is the dashboard which is first displayed when a [Student]
 * user signs into the app. All actions for students are defined
 * in this activity.
 *
 * @author saifkhichi96
 * @since 1.0.0
 */
class StudentDashboardActivity : DashboardActivity() {

    private lateinit var currentStudent: Student

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard_student)
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""

        // Only allow a signed in teacher to access this page
        currentStudent = when (currentUser) {
            is Student -> currentUser as Student
            else -> {
                finish()
                return
            }
        }

        // TODO: Set up click listeners
        attendanceButton.setOnClickListener { }
        classAnnouncementsButton.setOnClickListener { }
    }

    /**
     * Displays the signed in user's details.
     */
    override fun updateUI(currentUser: User) {
        className.text = currentStudent.classId
        getSubjectsList()
    }

    /**
     * Gets a list of courses from database taught by [currentTeacher].
     */
    private fun getSubjectsList() {
        SubjectsDao.getSubjectsByClass(schoolId, currentStudent.classId, OnSuccessListener {
            onSubjectsReceived(it)
        })
    }

    private fun onSubjectsReceived(subjects: List<Subject>) {
        coursesList.adapter = SubjectAdapter(this, subjects)
    }

    private inner class SubjectAdapter(context: Context, val subjects: List<Subject>)
        : ModelViewAdapter<Subject>(context, subjects, SubjectView::class) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val v = super.getView(position, convertView, parent)
            v.setOnClickListener {
                val subject = subjects[position]
                startSecurely(SubjectActivity::class.java, Intent().apply {
                    putExtra(CygnusApp.EXTRA_SCHOOL_SUBJECT, subject)
                })
            }
            return v
        }

    }

}