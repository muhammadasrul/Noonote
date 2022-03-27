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
import com.acun.note.util.Constants.requestCode
import com.acun.note.util.Constants.timerDataStoreName
import com.acun.note.util.Constants.triggerTime
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

    private val _isTimerOn = MutableLiveData<Boolean>()
    val isTimerOn: LiveData<Boolean> = _isTimerOn

    private val _elapsedTime = MutableLiveData<Long>()
    val elapsedTime: LiveData<Long> = _elapsedTime

    private val _timer = MutableLiveData<Long>()
    fun setTimer(timer: Long) {
        _timer.value = timer
    }

    private val notificationIntent = Intent(getApplication(), AlarmReceiver::class.java)
    private val alarmManager = app.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val notifyPendingIntent: PendingIntent
    private lateinit var timer: CountDownTimer

    private val pref = app.getSharedPreferences(timerDataStoreName, Context.MODE_PRIVATE)

    private val _taskList = MutableLiveData<ViewState<List<TaskModel>>>()
    val taskList: LiveData<ViewState<List<TaskModel>>> = _taskList

    init {
        _isTimerOn.value = PendingIntent.getBroadcast(
            getApplication(),
            requestCode,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE
        ) != null

        notifyPendingIntent = PendingIntent.getBroadcast(
            getApplication(),
            requestCode,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        if (_isTimerOn.value!!) {
            createTimer()
        }

        getAllTask()
    }

    fun setTimer(isStarted: Boolean) {
        if (isStarted) {
            _timer.value?.let { startTimer(it) }
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
    }

    private fun saveTime(time: Long) {
        pref.edit().putLong(triggerTime, time).apply()
    }

    private fun loadTime(): Long {
        return pref.getLong(triggerTime, 0)
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