package com.acun.note.ui.setting

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.acun.note.databinding.FragmentSettingsBinding
import com.acun.note.util.Constants.DEFAULT_FOCUS_TIME
import com.acun.note.util.Constants.DEFAULT_LONG_BREAK_TIME
import com.acun.note.util.Constants.DEFAULT_SHORT_BREAK_TIME
import com.acun.note.util.Constants.FOCUS_TIME
import com.acun.note.util.Constants.LONG_BREAK_TIME
import com.acun.note.util.Constants.SHORT_BREAK_TIME
import com.acun.note.util.Constants.TIME_PREFERENCE_NAME

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pref = requireContext().getSharedPreferences(TIME_PREFERENCE_NAME, Context.MODE_PRIVATE)

        binding.focusTimeEditText.setText(((pref.getLong(FOCUS_TIME, DEFAULT_FOCUS_TIME)/1000)/60).toString())
        binding.shortBreakEditText.setText(((pref.getLong(SHORT_BREAK_TIME, DEFAULT_SHORT_BREAK_TIME)/1000)/60).toString())
        binding.longBreakEditText.setText(((pref.getLong(LONG_BREAK_TIME, DEFAULT_LONG_BREAK_TIME)/1000)/60).toString())

        binding.focusTimeEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }
            override fun afterTextChanged(value: Editable?) {
                if (value.isNullOrEmpty()) {
                    binding.focusTimeLayout.error = "Can not be empty"
                } else {
                    binding.focusTimeLayout.isErrorEnabled = false
                    pref.edit().putLong(FOCUS_TIME ,((value.toString().toLong()*1000)*60)).apply()
                }
            }
        })

        binding.shortBreakEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }
            override fun afterTextChanged(value: Editable?) {
                if (value.isNullOrEmpty()) {
                    binding.shortBreakLayout.error = "Can not be empty"
                } else {
                    binding.shortBreakLayout.isErrorEnabled = false
                    pref.edit().putLong(SHORT_BREAK_TIME ,((value.toString().toLong()*1000)*60)).apply()
                }
            }
        })

        binding.longBreakEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }
            override fun afterTextChanged(value: Editable?) {
                if (value.isNullOrEmpty()) {
                    binding.longBreakLayout.error = "Can not be empty"
                } else {
                    binding.longBreakLayout.isErrorEnabled = false
                    pref.edit().putLong(LONG_BREAK_TIME, ((value.toString().toLong()*1000)*60)).apply()
                }
            }
        })
    }
}