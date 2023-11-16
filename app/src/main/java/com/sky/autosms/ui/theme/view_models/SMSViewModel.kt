package com.sky.autosms.ui.theme.view_models

import android.app.Application
import android.content.ContentResolver
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
                querySMS(Telephony.Sms.CONTENT_URI, null, null, null, "date DESC")
            }

            // 更新 UI
            _smsList.value = result
        }
    }


    /**
     * 根据指定的条件查询短信内容提供程序中的消息。
     *
     * @param uri 用于查询短信消息的 URI。Telephony.Sms.CONTENT_URI
     * @param projection 要包含在结果集中的列。
     * @param selection 应用于查询的过滤器，格式为 SQL WHERE 子句。
     * @param selectionArgs 可以在选择中包含 ?，这些将由 selectionArgs 中的值替换。
     * @param sortOrder 用于对结果集进行排序的顺序，格式为 SQL ORDER BY 子句。
     * @return 包含发送者地址和消息正文的 SMS 对象列表。
     */
    private fun querySMS(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String
    ): List<SMS> {
        Telephony.Sms.CONTENT_URI
        val smsList = mutableListOf<SMS>()
        val contentResolver: ContentResolver = application.contentResolver

        // 查询短信消息
        val cursor = contentResolver.query(uri, projection, selection, selectionArgs, sortOrder)

        cursor?.use {
            val indexId = it.getColumnIndex(Telephony.Sms._ID)
            val indexAddress = it.getColumnIndex(Telephony.Sms.ADDRESS)
            val indexBody = it.getColumnIndex(Telephony.Sms.BODY)
            val indexDate = it.getColumnIndex(Telephony.Sms.DATE)
            val indexType = it.getColumnIndex(Telephony.Sms.TYPE)
            val indexRead = it.getColumnIndex(Telephony.Sms.READ)
            val indexSeen = it.getColumnIndex(Telephony.Sms.SEEN)
            val indexStatus = it.getColumnIndex(Telephony.Sms.STATUS)

            while (it.moveToNext()) {
                val id = it.getLong(indexId)
                val address = it.getString(indexAddress)
                val body = it.getString(indexBody)
                val date = it.getLong(indexDate)
                val type = it.getInt(indexType)
                val read = it.getInt(indexRead)
                val seen = it.getInt(indexSeen)
                val status = it.getInt(indexStatus)

                // 创建 SMS 对象并添加到列表
                val sms = SMS(
                    id = id,
                    sender = address,
                    body = body,
                    date = date,
                    type = type,
                    read = read,
                    seen = seen,
                    status = status
                )
                smsList.add(sms)
            }
        }

        return smsList
    }

}