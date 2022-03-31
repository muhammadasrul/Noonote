package com.acun.note.ui.home

import android.app.AlarmManager
import android.app.Application
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.CountDownTimer
import android.os.SystemClock
import androidx.core.app.AlarmManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.acun.note.model.TaskModel
import com.acun.note.receiver.AlarmReceiver
import com.acun.note.repository.Repository
import com.acun.note.util.Constants.DEFAULT_FOCUS_TIME
import com.acun.note.util.Constants.DEFAULT_LONG_BREAK_TIME
import com.acun.note.util.Constants.DEFAULT_SHORT_BREAK_TIME
import com.acun.note.util.Constants.FOCUS_TIME
import com.acun.note.util.Constants.IS_TIMER_ON
import com.acun.note.util.Constants.LONG_BREAK_TIME
import com.acun.note.util.Constants.POMODORO_STATE
import com.acun.note.util.Constants.REQUEST_CODE
import com.acun.note.util.Constants.SHORT_BREAK_TIME
import com.acun.note.util.Constants.TIME_PREFERENCE_NAME
import com.acun.note.util.Constants.TRIGGER_TIME
import com.acun.note.util.ViewState
import com.acun.note.util.cancelNotification
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val app: Application,
    private val repository: Repository
) : AndroidViewModel(app) {

    private val TAG = HomeViewModel::class.java.simpleName

    private val _isTimerOn = MutableLiveData<Boolean>()
    val isTimerOn: LiveData<Boolean> = _isTimerOn

    private val _elapsedTime = MutableLiveData<Long>()
    val elapsedTime: LiveData<Long> = _elapsedTime

    private val _timeTimerState = MutableLiveData<String>()
    val timeTimerState: LiveData<String> = _timeTimerState
    private val _timeSelection = MutableLiveData<Long>()

    private val notificationIntent = Intent(getApplication(), AlarmReceiver::class.java)
    private val alarmManager = app.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val notifyPendingIntent: PendingIntent
    private lateinit var timer: CountDownTimer

    private val pref = app.getSharedPreferences(TIME_PREFERENCE_NAME, Context.MODE_PRIVATE)

    private val _taskList = MutableLiveData<ViewState<List<TaskModel>>>()
    val taskList: LiveData<ViewState<List<TaskModel>>> = _taskList

    init {
        _isTimerOn.value = pref.getBoolean(IS_TIMER_ON, false)

        notifyPendingIntent = PendingIntent.getBroadcast(
            getApplication(),
            REQUEST_CODE,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        if (_isTimerOn.value!!) {
            createTimer()
        }

        pref.getString(POMODORO_STATE, FOCUS_TIME)?.let { setTimeState(it) }
        getAllTask()
    }

    fun setTimeState(state: String) {
        val time = when(state) {
            FOCUS_TIME -> pref.getLong(FOCUS_TIME, DEFAULT_FOCUS_TIME)
            SHORT_BREAK_TIME -> pref.getLong(SHORT_BREAK_TIME, DEFAULT_SHORT_BREAK_TIME)
            LONG_BREAK_TIME -> pref.getLong(LONG_BREAK_TIME, DEFAULT_LONG_BREAK_TIME)
            else -> 0
        }
        _timeSelection.value = time
        _timeTimerState.value = state
        pref.edit().putString(POMODORO_STATE, state).apply()
    }

    fun setTimer(isStarted: Boolean) {
        if (isStarted) {
            _timeSelection.value?.let { startTimer(it) }
        } else {
            cancelNotification()
        }
    }

    private fun cancelNotification() {
        resetTimer()
        alarmManager.cancel(notifyPendingIntent)
    }

    private fun startTimer(timerLength: Long) {
        _isTimerOn.value?.let { isTimerOn ->
            if (!isTimerOn) {
                _isTimerOn.value = true
                pref.edit().putBoolean(IS_TIMER_ON, true).apply()
                val triggerTime = SystemClock.elapsedRealtime() + timerLength

                val notificationManager = ContextCompat.getSystemService(
                    app,
                    NotificationManager::class.java
                ) as NotificationManager
                notificationManager.cancelNotification()

                AlarmManagerCompat.setExactAndAllowWhileIdle(
                    alarmManager,
                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    triggerTime,
                    notifyPendingIntent
                )

                viewModelScope.launch {
                    saveTime(triggerTime)
                }
            }
        }
        createTimer()
    }

    private fun createTimer() {
        viewModelScope.launch {
            val triggerTime = loadTime()
            timer = object : CountDownTimer(triggerTime, 1000) {
                override fun onTick(millesUntilFinished: Long) {
                    _elapsedTime.value = triggerTime - SystemClock.elapsedRealtime()
                    if (_elapsedTime.value!! <= 0) {
                        resetTimer()
                    }
                }

                override fun onFinish() {
                    resetTimer()
                }
            }
            timer.start()
        }
    }

    private fun resetTimer() {
        timer.cancel()
        _elapsedTime.value = 0
        _isTimerOn.value = false
        pref.edit().putBoolean(IS_TIMER_ON, false).apply()
    }

    private fun saveTime(time: Long) {
        pref.edit().putLong(TRIGGER_TIME, time).apply()
    }

    private fun loadTime(): Long {
        return pref.getLong(TRIGGER_TIME, 0)
    }

    private fun getAllTask() {
        viewModelScope.launch {
            repository.getAllTask().collect { result ->
                try {
                    if (result.isNullOrEmpty()) {
                        _taskList.value = ViewState.Empty()
                    } else {
                        _taskList.value = ViewState.Success(data = result)
                    }
                } catch (e: Exception) {
                    _taskList.value = ViewState.Error(message = e.message)
                }
            }
        }
    }

    fun insertTask(task: TaskModel) {
        viewModelScope.launch {
            repository.insert(task)
        }
    }

    fun updateTask(task: TaskModel) {
        viewModelScope.launch {
            repository.update(task)
        }
    }

    fun deleteTask(task: TaskModel) {
        viewModelScope.launch {
            repository.delete(task)
        }
    }
}