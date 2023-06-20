package net.ghiassy.smsmonitor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MyShareViewModel: ViewModel() {

    private val _text = MutableLiveData<String>()
    val text: LiveData<String> get() = _text

    init {
        _text.value = ""
    }
    fun update(newText: String)
    {
        _text.value = newText
    }
}