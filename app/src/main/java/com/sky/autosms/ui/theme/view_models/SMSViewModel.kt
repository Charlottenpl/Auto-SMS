package com.sky.autosms.ui.theme.view_models

import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.Telephony
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sky.autosms.data.model.SMS
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * SMS ViewModel
 */
class SMSViewModel(private val application: Application) : AndroidViewModel(application) {
    private val _smsList = MutableLiveData<List<SMS>>()
    val smsList: LiveData<List<SMS>> get() = _smsList

    init {
        // 初始化 smsList
        _smsList.value = emptyList()
    }

    // 提供一个公共方法来更新 smsList
    fun updateSmsList(newList: List<SMS>) {
        _smsList.value = newList
    }


    fun getSMS() {
        viewModelScope.launch {
            // 在IO线程中执行短信查询
            val result = withContext(Dispatchers.IO) {
                querySMS()
            }

            // 更新 UI
            _smsList.value = result
        }
    }

    private fun querySMS(): List<SMS> {
        val smsList = mutableListOf<SMS>()
        val contentResolver: ContentResolver = application.contentResolver

        // 查询短信
        val uri: Uri = Telephony.Sms.CONTENT_URI
        val cursor = contentResolver.query(uri, null, null, null, null)

        cursor?.use {
            val indexAddress = it.getColumnIndex(Telephony.Sms.ADDRESS)
            val indexBody = it.getColumnIndex(Telephony.Sms.BODY)

            while (it.moveToNext()) {
                val address = it.getString(indexAddress)
                val body = it.getString(indexBody)

                // 创建SMS对象并添加到列表
                val sms = SMS(sender = address, body = body)
                smsList.add(sms)
            }
        }

        return smsList
    }

}