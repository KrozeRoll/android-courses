package com.example.fragments.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.fragments.R
import com.example.fragments.extension.navigate
import kotlinx.android.synthetic.main.fragment_dictionary.*

class DictionaryFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_dictionary, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dictionaryText.text = "0"
        dictionaryButton.setOnClickListener {
            navigate(DictionaryFragmentDirections.actionDictionaryFragmentToDictionaryFragment1(1))
        }
    }
}