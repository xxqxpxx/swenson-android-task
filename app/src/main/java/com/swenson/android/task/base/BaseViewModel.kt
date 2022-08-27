package com.swenson.android.task.base

import androidx.lifecycle.ViewModel
import com.swenson.android.task.network.ErrorManager

abstract class BaseViewModel : ViewModel() {
    fun getStatusCode(throwable: Throwable): Int {
        return ErrorManager.getCode(throwable = throwable)
    }
}