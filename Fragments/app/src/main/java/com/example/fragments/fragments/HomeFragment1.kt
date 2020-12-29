package com.example.fragments.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.fragments.R
import com.example.fragments.extension.navigate
import kotlinx.android.synthetic.main.fragment_home1.*

class HomeFragment1 : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_home1, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeButton1.setOnClickListener {
            navigate(HomeFragment1Directions.actionHomeFragment1ToHomeFragment2(2))
        }

        val fragmentNumber = HomeFragment1Args.fromBundle(requireArguments()).count
        var newString = ""
        for (number in 0 until fragmentNumber) {
            newString += "$number -> "
        }
        newString += fragmentNumber.toString()
        homeText1.text = newString
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true;
    }
}