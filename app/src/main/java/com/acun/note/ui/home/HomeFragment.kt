package com.acun.note.ui.home

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.acun.note.R
import com.acun.note.databinding.BottomSheetAddTaskBinding
import com.acun.note.databinding.FragmentHomeBinding
import com.acun.note.model.TaskModel
import com.acun.note.ui.adapter.TaskAdapter
import com.acun.note.util.Constants.DEFAULT_FOCUS_TIME
import com.acun.note.util.Constants.DEFAULT_LONG_BREAK_TIME
import com.acun.note.util.Constants.DEFAULT_SHORT_BREAK_TIME
import com.acun.note.util.Constants.FOCUS_TIME
import com.acun.note.util.Constants.IS_DARK_MODE
import com.acun.note.util.Constants.LONG_BREAK_TIME
import com.acun.note.util.Constants.THEME_PREFERENCE_NAME
import com.acun.note.util.Constants.TIME_PREFERENCE_NAME
import com.acun.note.util.Constants.NOTIFICATION_CHANNEL
import com.acun.note.util.Constants.POMODORO_STATE
import com.acun.note.util.Constants.SHORT_BREAK_TIME
import com.acun.note.util.Constants.pomodoroName
import com.acun.note.util.ViewState
import com.acun.note.util.timeDisplay
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()

    private lateinit var themeSharedPref: SharedPreferences
    private lateinit var timeSharedPref: SharedPreferences

    private var currentTaskPos = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        createChannel(NOTIFICATION_CHANNEL, "channelName")
        themeSharedPref = requireActivity().getSharedPreferences(THEME_PREFERENCE_NAME, Context.MODE_PRIVATE)
        timeSharedPref = requireActivity().getSharedPreferences(TIME_PREFERENCE_NAME, Context.MODE_PRIVATE)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)
        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        viewModel.timeTimerState.observe(viewLifecycleOwner) { state ->
            when (state) {
                FOCUS_TIME -> binding.timerDisplayTextView.timeDisplay(timeSharedPref.getLong(state, DEFAULT_FOCUS_TIME))
                SHORT_BREAK_TIME -> binding.timerDisplayTextView.timeDisplay(timeSharedPref.getLong(state, DEFAULT_SHORT_BREAK_TIME))
                LONG_BREAK_TIME -> binding.timerDisplayTextView.timeDisplay(timeSharedPref.getLong(state, DEFAULT_LONG_BREAK_TIME))
            }
            binding.statusTextView.text = pomodoroName[state]

            binding.rightButton.setOnClickListener {
                when (state) {
                    FOCUS_TIME -> viewModel.setTimeState(SHORT_BREAK_TIME)
                    SHORT_BREAK_TIME -> viewModel.setTimeState(LONG_BREAK_TIME)
                    LONG_BREAK_TIME -> viewModel.setTimeState(FOCUS_TIME)
                }
            }
            binding.leftButton.setOnClickListener {
                when (state) {
                    FOCUS_TIME -> viewModel.setTimeState(LONG_BREAK_TIME)
                    SHORT_BREAK_TIME -> viewModel.setTimeState(FOCUS_TIME)
                    LONG_BREAK_TIME -> viewModel.setTimeState(SHORT_BREAK_TIME)
                }
            }
        }


        viewModel.elapsedTime.observe(viewLifecycleOwner) {
            if (it != 0L) {
                binding.timerDisplayTextView.timeDisplay(it)
                binding.leftButton.visibility = View.GONE
                binding.rightButton.visibility = View.GONE
            } else {
                binding.leftButton.visibility = View.VISIBLE
                binding.rightButton.visibility = View.VISIBLE
            }
        }

        viewModel.isTimerOn.observe(viewLifecycleOwner) { isTimerOn ->
            binding.startButton.icon = if (isTimerOn) {
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_pausecircle)
            } else {
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_playcircle)
            }
            binding.startButton.setOnClickListener {
                viewModel.setTimer(!isTimerOn)
            }
        }

        binding.taskRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        val decor = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        binding.taskRecyclerView.addItemDecoration(decor)

        viewModel.taskList.observe(viewLifecycleOwner) { state ->
            when (state) {
                is ViewState.Success -> {
                    binding.emptyState.root.visibility = View.GONE
                    state.data?.let { taskList ->
                        binding.addTaskFAB.visibility = View.VISIBLE
                        binding.addTaskFAB.setOnClickListener {
                            addTaskView()
                        }
                        binding.taskRecyclerView.visibility = View.VISIBLE
                        binding.taskRecyclerView.adapter =
                            TaskAdapter(
                                taskList,
                                onItemClicked = { task ->
                                    viewModel.updateTask(task.copy(isCompleted = !task.isCompleted))
                                },
                                onDotsClicked = { task ->
                                    viewModel.deleteTask(task)
                                },
                                onExpandClicked = { pos, taskDescTextView ->
                                    if (currentTaskPos != pos) {
                                        binding.taskRecyclerView.findViewHolderForAdapterPosition(currentTaskPos)?.itemView?.findViewById<TextView>(R.id.taskDescTextView)?.visibility = View.GONE
                                    }
                                    taskDescTextView.visibility = if (taskDescTextView.isVisible) View.GONE else View.VISIBLE
                                    currentTaskPos = pos
                                }
                            )
                    }
                }
                is ViewState.Empty -> {
                    binding.addTaskFAB.visibility = View.GONE
                    binding.taskRecyclerView.visibility = View.GONE
                    binding.emptyState.root.visibility = View.VISIBLE
                    binding.emptyState.addTaskButton.setOnClickListener {
                        addTaskView()
                    }
                }
                is ViewState.Error -> {
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                setShowBadge(false)
            }

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = "notifincation"

            val notificationManager = requireActivity().getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)

        }
    }

    private fun addTaskView() {
        val addTaskBinding =
            BottomSheetAddTaskBinding.inflate(LayoutInflater.from(requireContext()))
        val builder = MaterialAlertDialogBuilder(
            requireContext(),
            R.style.Theme_NoteApp_MaterialAlertDialog_Rounded
        ).apply {
            setView(addTaskBinding.root)
            create()
        }.show()

        addTaskBinding.saveButton.setOnClickListener {
            viewModel.insertTask(
                TaskModel(
                    id = null,
                    title = addTaskBinding.taskEditText.text.toString(),
                    description = addTaskBinding.taskDescEditText.text.toString(),
                    importance = addTaskBinding.importanceSlider.value,
                    urgency = addTaskBinding.urgencySlider.value,
                    isCompleted = false
                )
            )
            builder.dismiss()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
        if (themeSharedPref.getBoolean(IS_DARK_MODE, false)) {
            menu.findItem(R.id.themePreference).icon =
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_sun)
        } else {
            menu.findItem(R.id.themePreference).icon =
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_moon)
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.themePreference -> {
                val isDarkMode = themeSharedPref.getBoolean(IS_DARK_MODE, false)
                if (isDarkMode) {
                    updateTheme(AppCompatDelegate.MODE_NIGHT_NO)
                } else {
                    updateTheme(AppCompatDelegate.MODE_NIGHT_YES)
                }
                themeSharedPref.edit().putBoolean(IS_DARK_MODE, !isDarkMode).apply()
                true
            }
            R.id.settingsMenu -> {
                findNavController().navigate(R.id.action_homeFragment_to_settingsFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun updateTheme(mode: Int) {
        AppCompatDelegate.setDefaultNightMode(mode)
        requireActivity().recreate()
    }

    override fun onResume() {
        super.onResume()

        timeSharedPref.getString(POMODORO_STATE, FOCUS_TIME)?.let {
            viewModel.setTimeState(it)
        }
    }
}