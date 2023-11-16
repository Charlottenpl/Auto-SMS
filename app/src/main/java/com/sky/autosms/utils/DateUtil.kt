package com.sky.autosms.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// 辅助函数，将时间戳转换为日期字符串
fun convertTimestampToDateString(timestamp: Long): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    return sdf.format(Date(timestamp))
}