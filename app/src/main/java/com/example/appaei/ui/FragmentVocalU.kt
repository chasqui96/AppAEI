package com.example.appaei.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

import com.example.appaei.R

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentVocalU.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentVocalU : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Vincula este fragmento al layout fragment_vocal_a.xml
        return inflater.inflate(R.layout.fragment_vocal_u, container, false)
    }
}