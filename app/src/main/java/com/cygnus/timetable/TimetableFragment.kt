package com.cygnus.timetable

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import co.aspirasoft.view.NestedListView
import com.cygnus.model.Lecture

class TimetableFragment(val data: List<Pair<String, Lecture>>?) : Fragment() {
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View = NestedListView(inflater.context).apply {
        this.adapter = data?.let {
            LectureAdapter(inflater.context, it).apply {
                sort { o1, o2 ->
                    o1.second.startTime.compareTo(o2.second.startTime)
                }
            }
        }
    }
}