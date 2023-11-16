package com.sky.autosms.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.PathPermission
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

/**
 * 权限使用工具类
 */
object PermissionUtil {

    /**
     * 检查是否获得权限
     */
    fun check(permission: String = Manifest.permission.READ_SMS, context: Context): Boolean {
        var result = ContextCompat.checkSelfPermission(context, permission)
        return result == PackageManager.PERMISSION_GRANTED
    }
}