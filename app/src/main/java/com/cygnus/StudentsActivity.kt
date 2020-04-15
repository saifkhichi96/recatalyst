package com.cygnus

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import co.aspirasoft.adapter.ModelViewAdapter
import com.cygnus.core.DashboardChildActivity
import com.cygnus.model.Student
import com.cygnus.model.Teacher
import com.cygnus.model.User
import com.cygnus.view.AddStudentDialog
import com.cygnus.view.StudentView
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_list.*

class StudentsActivity : DashboardChildActivity() {

    private val students: ArrayList<Student> = ArrayList()

    private lateinit var currentTeacher: Teacher
    private lateinit var adapter: StudentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        // Only allow a class teacher to access this page
        currentTeacher = when (currentUser) {
            is Teacher -> {
                val teacher = currentUser as Teacher
                if (teacher.classId == null) {
                    finish()
                    return
                }
                teacher
            }
            else -> {
                finish()
                return
            }
        }

        adapter = StudentAdapter(this, students)
        contentList.adapter = adapter

        addButton.setOnClickListener { onAddStudentClicked() }
    }

    override fun updateUI(currentUser: User) {
        FirebaseDatabase.getInstance()
                .getReference("$schoolId/users/")
                .orderByChild("classId")
                .equalTo(currentTeacher.classId)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val t = object : GenericTypeIndicator<HashMap<String, Student>>() {}
                        students.clear()
                        snapshot.getValue(t)?.forEach { entry ->
                            if (entry.value.rollNo.isNotBlank()) {
                                students.add(entry.value)
                            }
                        }
                        adapter.notifyDataSetChanged()
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })
    }

    private fun onAddStudentClicked() {
        val dialog = AddStudentDialog.newInstance(currentTeacher.classId!!, currentTeacher.id, schoolId)
        dialog.show(supportFragmentManager, dialog.toString())
    }

    private inner class StudentAdapter(context: Context, val students: List<Student>)
        : ModelViewAdapter<Student>(context, students, StudentView::class) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val v = super.getView(position, convertView, parent)
            v.setOnClickListener {
                val dialog = AddStudentDialog.newInstance(
                        currentTeacher.classId!!,
                        currentTeacher.id,
                        schoolId,
                        students[position]
                )
                dialog.show(supportFragmentManager, dialog.toString())
            }
            return v
        }

    }

}