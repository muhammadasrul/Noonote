package com.acun.note.util

import android.text.format.DateUtils
import android.util.Log
import android.widget.TextView

fun TextView.timeDisplay(time: Long) {
    text = DateUtils.formatElapsedTime(time/1000)
}