package com.example.fragments.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.fragments.R
import com.example.fragments.extension.navigate
import kotlinx.android.synthetic.main.fragment_dictionary1.*

class DictionaryFragment1 : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_dictionary1, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dictionaryButton1.setOnClickListener {
            navigate(DictionaryFragment1Directions.actionDictionaryFragment1ToDictionaryFragment2(2))
        }

        val fragmentNumber = DictionaryFragment1Args.fromBundle(requireArguments()).count
        var newString = ""
        for (number in 0 until fragmentNumber) {
            newString += "$number -> "
        }
        newString += fragmentNumber.toString()
        dictionaryText1.text = newString
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }
}