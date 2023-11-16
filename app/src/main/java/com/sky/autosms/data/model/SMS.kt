package com.sky.autosms.data.model

data class SMS(
    val id: Long,        // 短信的唯一标识符
    val sender: String, // 发送者或接收者的电话号码
    val body: String,    // 短信内容
    val date: Long,      // 短信的日期时间戳
    val type: Int,       // 短信的类型，例如收件箱、发件箱等
    val read: Int,       // 标记短信是否已读
    val seen: Int,       // 标记短信是否被用户查看
    val status: Int      // 短信的状态，例如发送成功、发送失败等
)
