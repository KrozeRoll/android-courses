package com.example.fragments.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.fragments.R
import kotlinx.android.synthetic.main.fragment_dictionary2.*

class DictionaryFragment2 : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_dictionary2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fragmentNumber = DictionaryFragment2Args.fromBundle(requireArguments()).count
        var newString = ""
        for (number in 0 until fragmentNumber) {
            newString += "$number -> "
        }
        newString += fragmentNumber.toString()
        dictionaryText2.text = newString
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }
}