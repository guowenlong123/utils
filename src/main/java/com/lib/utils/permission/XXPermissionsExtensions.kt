package com.lib.utils.permission

import android.Manifest
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * XXPermissions 扩展函数
 * 提供更便捷的使用方式
 */

/**
 * FragmentActivity 扩展 - 请求权限
 */
fun FragmentActivity.requestPermissions(
    vararg permissions: String
): Flow<PermissionResult> {
    return XXPermissionsHelper.request(this)
        .permissions(*permissions)
        .start()
}

/**
 * Fragment 扩展 - 请求权限
 */
fun Fragment.requestPermissions(
    vararg permissions: String
): Flow<PermissionResult> {
    return XXPermissionsHelper.request(this)
        .permissions(*permissions)
        .start()
}

/**
 * FragmentActivity 扩展 - 检查权限
 */
fun FragmentActivity.hasPermissions(vararg permissions: String): Boolean {
    return XXPermissionsHelper.isGranted(this, *permissions)
}

/**
 * Fragment 扩展 - 检查权限
 */
fun Fragment.hasPermissions(vararg permissions: String): Boolean {
    return XXPermissionsHelper.isGranted(this, *permissions)
}

/**
 * FragmentActivity 扩展 - 检查权限是否被永久拒绝
 */
fun FragmentActivity.isPermissionPermanentDenied(vararg permissions: String): Boolean {
    return XXPermissionsHelper.isPermanentDenied(this, *permissions)
}

/**
 * Fragment 扩展 - 检查权限是否被永久拒绝
 */
fun Fragment.isPermissionPermanentDenied(vararg permissions: String): Boolean {
    return XXPermissionsHelper.isPermanentDenied(this, *permissions)
}

/**
 * FragmentActivity 扩展 - 跳转到应用设置页面
 */
fun FragmentActivity.openPermissionSettings(vararg permissions: String) {
    XXPermissionsHelper.startPermissionActivity(this, *permissions)
}

/**
 * Fragment 扩展 - 跳转到应用设置页面
 */
fun Fragment.openPermissionSettings(vararg permissions: String) {
    XXPermissionsHelper.startPermissionActivity(this, *permissions)
}

/**
 * FragmentActivity 扩展 - 跳转到应用设置页面并等待结果
 */
fun FragmentActivity.openPermissionSettingsForResult(
    vararg permissions: String
): Flow<PermissionResult> {
    return XXPermissionsHelper.startPermissionActivityForResult(this, *permissions)
}

/**
 * Fragment 扩展 - 跳转到应用设置页面并等待结果
 */
fun Fragment.openPermissionSettingsForResult(
    vararg permissions: String
): Flow<PermissionResult> {
    return XXPermissionsHelper.startPermissionActivityForResult(this, *permissions)
}

/**
 * 便捷方法 - 在 Activity 中请求权限
 * 
 * 使用示例：
 * ```
 * launchRequestPermissions(Permission.CAMERA) { result ->
 *     if (result.isAllGranted) {
 *         // 权限已授予
 *     } else {
 *         // 权限被拒绝
 *     }
 * }
 * ```
 */
fun FragmentActivity.launchRequestPermissions(
    vararg permissions: String,
    onResult: (PermissionResult) -> Unit
) {
    lifecycleScope.launch {
        requestPermissions(*permissions).collect { result ->
            onResult(result)
        }
    }
}

/**
 * 便捷方法 - 在 Fragment 中请求权限
 */
fun Fragment.launchRequestPermissions(
    vararg permissions: String,
    onResult: (PermissionResult) -> Unit
) {
    lifecycleScope.launch {
        requestPermissions(*permissions).collect { result ->
            onResult(result)
        }
    }
}

/**
 * 便捷方法 - 请求权限，如果被永久拒绝则引导到设置页面
 */
fun FragmentActivity.launchRequestPermissionsWithSettings(
    vararg permissions: String,
    onResult: (PermissionResult) -> Unit
) {
    lifecycleScope.launch {
        requestPermissions(*permissions).collect { result ->
            if (result.hasPermanentDenied) {
                // 有权限被永久拒绝，引导到设置页面
                openPermissionSettings(*permissions)
            }
            onResult(result)
        }
    }
}

/**
 * 便捷方法 - 请求权限，如果被永久拒绝则引导到设置页面
 */
fun Fragment.launchRequestPermissionsWithSettings(
    vararg permissions: String,
    onResult: (PermissionResult) -> Unit
) {
    lifecycleScope.launch {
        requestPermissions(*permissions).collect { result ->
            if (result.hasPermanentDenied) {
                // 有权限被永久拒绝，引导到设置页面
                openPermissionSettings(*permissions)
            }
            onResult(result)
        }
    }
}

// ==================== 常用权限快捷方法 ====================

/**
 * 请求相机权限
 */
fun FragmentActivity.requestCameraPermission(): Flow<PermissionResult> {
    return requestPermissions(Manifest.permission.CAMERA)
}

fun Fragment.requestCameraPermission(): Flow<PermissionResult> {
    return requestPermissions(Manifest.permission.CAMERA)
}

/**
 * 请求存储权限
 */
fun FragmentActivity.requestStoragePermission(): Flow<PermissionResult> {
    return requestPermissions(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
}

fun Fragment.requestStoragePermission(): Flow<PermissionResult> {
    return requestPermissions(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
}

/**
 * 请求位置权限
 */
fun FragmentActivity.requestLocationPermission(): Flow<PermissionResult> {
    return requestPermissions(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
}

fun Fragment.requestLocationPermission(): Flow<PermissionResult> {
    return requestPermissions(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
}

/**
 * 请求录音权限
 */
fun FragmentActivity.requestRecordAudioPermission(): Flow<PermissionResult> {
    return requestPermissions(Manifest.permission.RECORD_AUDIO)
}

fun Fragment.requestRecordAudioPermission(): Flow<PermissionResult> {
    return requestPermissions(Manifest.permission.RECORD_AUDIO)
}

/**
 * 请求联系人权限
 */
fun FragmentActivity.requestContactsPermission(): Flow<PermissionResult> {
    return requestPermissions(
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.WRITE_CONTACTS
    )
}

fun Fragment.requestContactsPermission(): Flow<PermissionResult> {
    return requestPermissions(
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.WRITE_CONTACTS
    )
}

/**
 * 请求电话权限
 */
fun FragmentActivity.requestPhonePermission(): Flow<PermissionResult> {
    return requestPermissions(Manifest.permission.CALL_PHONE)
}

fun Fragment.requestPhonePermission(): Flow<PermissionResult> {
    return requestPermissions(Manifest.permission.CALL_PHONE)
}

/**
 * 请求日历权限
 */
fun FragmentActivity.requestCalendarPermission(): Flow<PermissionResult> {
    return requestPermissions(
        Manifest.permission.READ_CALENDAR,
        Manifest.permission.WRITE_CALENDAR
    )
}

fun Fragment.requestCalendarPermission(): Flow<PermissionResult> {
    return requestPermissions(
        Manifest.permission.READ_CALENDAR,
        Manifest.permission.WRITE_CALENDAR
    )
}
