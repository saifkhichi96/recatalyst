package com.cygnus

import android.content.Context
import android.os.Bundle
import android.view.View
import co.aspirasoft.adapter.ModelViewAdapter
import com.cygnus.core.DashboardActivity
import com.cygnus.model.*
import com.cygnus.view.SubjectView
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_dashboard_teacher.*
import java.util.*
import kotlin.collections.ArrayList

/**
 * TeacherDashboardActivity is the teachers' homepage.
 *
 * This is the dashboard which is first displayed when a [Teacher]
 * user signs into the app. All actions for class teachers and
 * other teachers are defined in this activity.
 *
 * @author saifkhichi96
 * @since 1.0.0
 */
class TeacherDashboardActivity : DashboardActivity() {

    private lateinit var currentTeacher: Teacher

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard_teacher)
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""

        // Only allow a signed in teacher to access this page
        currentTeacher = when (currentUser) {
            is Teacher -> currentUser as Teacher
            else -> {
                finish()
                return
            }
        }

        // Set up click listeners
        attendanceButton.setOnClickListener { startSecurely(AttendanceActivity::class.java) }
        classAnnouncementsButton.setOnClickListener { startSecurely(NoticeActivity::class.java) }
        manageStudentsButton.setOnClickListener { startSecurely(StudentsActivity::class.java) }
    }

    /**
     * Displays the signed in user's details.
     */
    override fun updateUI(currentUser: User) {
        getSubjectsList()
        if (currentTeacher.isClassTeacher()) {
            classTeacherCard.visibility = View.VISIBLE
            className.text = currentTeacher.classId
            getStudentCount()
        } else {
            classTeacherCard.visibility = View.GONE
        }
    }

    private fun getStudentCount() {
        onStudentCountReceived(0)
        FirebaseDatabase.getInstance()
                .getReference("$schoolId/users/")
                .orderByChild("classId")
                .equalTo(currentTeacher.classId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val t = object : GenericTypeIndicator<HashMap<String, Student>>() {}
                        var count = 0
                        snapshot.getValue(t)?.forEach { entry ->
                            if (entry.value.rollNo.isNotBlank()) count++
                        }
                        onStudentCountReceived(count)
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })
    }

    private fun onStudentCountReceived(count: Int) {
        studentCount.text = String.format(getString(R.string.ph_student_count), count)
    }

    /**
     * Gets a list of courses from database taught by [currentTeacher].
     */
    private fun getSubjectsList() {
        FirebaseDatabase.getInstance()
                .getReference("$schoolId/classes/")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val t = object : GenericTypeIndicator<HashMap<String, SchoolClass>>() {}
                        val subjects = ArrayList<Subject>()
                        snapshot.getValue(t)?.values?.forEach { schoolClass ->
                            schoolClass.subjects?.let {
                                it.values.forEach { subject ->
                                    if (subject.teacherId == currentTeacher.email) {
                                        subjects += subject
                                    }
                                }
                            }
                        }
                        onSubjectsReceived(subjects)
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })
    }

    private fun onSubjectsReceived(subjects: List<Subject>) {
        coursesList.adapter = SubjectAdapter(this, subjects)
    }

    class SubjectAdapter(context: Context, subjects: List<Subject>)
        : ModelViewAdapter<Subject>(context, subjects, SubjectView::class)

}