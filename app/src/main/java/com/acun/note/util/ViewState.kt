package com.acun.note.util

sealed class ViewState<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T?): ViewState<T>(data = data)
    class Error<T>(message: String?): ViewState<T>(message = message)
    class Empty<T>: ViewState<T>()
}
