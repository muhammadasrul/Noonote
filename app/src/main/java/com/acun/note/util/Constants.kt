package com.acun.note.util

object Constants {
    const val NOTIFICATION_ID = 0
    const val NOTIFICATION_CHANNEL = "notification"

    const val TRIGGER_TIME = "trigger_time"
    const val REQUEST_CODE = 0

    const val NOTE_TABLE_NAME = "note"
    const val TASK_TABLE_NAME = "task"
    const val FOLDER_TABLE_NAME = "folder"
    const val ID = "id"
    const val TITLE = "title"
    const val IS_COMPLETE = "isComplete"
    const val IMPORTANCE = "importance"
    const val URGENCY = "urgency"
    const val DESCRIPTION = "description"
    const val CATEGORY_ID = "category_id"
    const val CREATE_AT = "create_at"
    const val NAME = "name"

    const val THEME_PREFERENCE_NAME = "theme_preference"
    const val IS_DARK_MODE = "is_dark_mode"

    const val TIME_PREFERENCE_NAME = "time_preference"
    const val SHORT_BREAK_TIME = "short_break_time"
    const val LONG_BREAK_TIME = "long_break_time"
    const val FOCUS_TIME = "focus_time"
    const val POMODORO_STATE = "pomodoro_state"
    const val IS_TIMER_ON = "is_timer_on"

    const val DEFAULT_SHORT_BREAK_TIME = 300_000L
    const val DEFAULT_LONG_BREAK_TIME = 900_000L
    const val DEFAULT_FOCUS_TIME = 1_500_000L

    val pomodoroName = hashMapOf(
        SHORT_BREAK_TIME to "Short Break",
        LONG_BREAK_TIME to "Long Break",
        FOCUS_TIME to "Pomodoro"
    )
}