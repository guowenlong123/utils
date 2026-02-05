package com.lib.utils.permission

import android.Manifest

/**
 * 权限描述信息
 */
object PermissionDescription {
    
    /**
     * 获取权限描述
     */
    fun getDescription(permission: String): String {
        return when (permission) {
            // 相机
            Manifest.permission.CAMERA -> "用于拍摄照片和录制视频"
            
            // 存储
            Manifest.permission.READ_EXTERNAL_STORAGE -> "用于读取设备上的图片、视频和文件"
            Manifest.permission.WRITE_EXTERNAL_STORAGE -> "用于保存图片、视频和文件到设备存储"
            Manifest.permission.MANAGE_EXTERNAL_STORAGE -> "用于管理设备上的所有文件"
            
            // 位置
            Manifest.permission.ACCESS_FINE_LOCATION -> "用于获取精确位置信息，提供基于位置的服务"
            Manifest.permission.ACCESS_COARSE_LOCATION -> "用于获取大致位置信息"
            Manifest.permission.ACCESS_BACKGROUND_LOCATION -> "用于在后台获取位置信息"
            
            // 录音
            Manifest.permission.RECORD_AUDIO -> "用于录制音频和语音通话"
            
            // 联系人
            Manifest.permission.READ_CONTACTS -> "用于读取联系人信息"
            Manifest.permission.WRITE_CONTACTS -> "用于添加或修改联系人信息"
            Manifest.permission.GET_ACCOUNTS -> "用于获取账户信息"
            
            // 电话
            Manifest.permission.CALL_PHONE -> "用于拨打电话"
            Manifest.permission.READ_PHONE_STATE -> "用于读取手机状态和识别码"
            Manifest.permission.READ_CALL_LOG -> "用于读取通话记录"
            Manifest.permission.WRITE_CALL_LOG -> "用于写入通话记录"
            Manifest.permission.ADD_VOICEMAIL -> "用于添加语音邮件"
            Manifest.permission.USE_SIP -> "用于使用SIP服务"
            
            // 短信
            Manifest.permission.SEND_SMS -> "用于发送短信"
            Manifest.permission.READ_SMS -> "用于读取短信"
            Manifest.permission.RECEIVE_SMS -> "用于接收短信"
            Manifest.permission.RECEIVE_WAP_PUSH -> "用于接收WAP推送消息"
            Manifest.permission.RECEIVE_MMS -> "用于接收彩信"
            
            // 日历
            Manifest.permission.READ_CALENDAR -> "用于读取日历事件"
            Manifest.permission.WRITE_CALENDAR -> "用于添加或修改日历事件"
            
            // 传感器
            Manifest.permission.BODY_SENSORS -> "用于访问健康传感器数据"
            
            // 蓝牙
            Manifest.permission.BLUETOOTH -> "用于连接蓝牙设备"
            Manifest.permission.BLUETOOTH_ADMIN -> "用于管理蓝牙连接"
            Manifest.permission.BLUETOOTH_CONNECT -> "用于连接已配对的蓝牙设备"
            Manifest.permission.BLUETOOTH_SCAN -> "用于扫描附近的蓝牙设备"
            Manifest.permission.BLUETOOTH_ADVERTISE -> "用于让设备可被其他蓝牙设备发现"
            
            // 通知
            Manifest.permission.POST_NOTIFICATIONS -> "用于发送通知消息"
            
            // 其他
            Manifest.permission.VIBRATE -> "用于设备振动反馈"
            Manifest.permission.WAKE_LOCK -> "用于保持设备唤醒状态"
            
            else -> "应用需要此权限以提供完整功能"
        }
    }
    
    /**
     * 获取多个权限的组合描述
     */
    fun getDescriptions(permissions: List<String>): String {
        if (permissions.isEmpty()) {
            return "应用需要一些权限以提供完整功能"
        }
        
        val descriptions = permissions.map { permission ->
            val desc = getDescription(permission)
            "• $desc"
        }
        
        return descriptions.joinToString("\n")
    }
    
    /**
     * 获取权限组的标题
     */
    fun getPermissionGroupTitle(permissions: List<String>): String {
        return when {
            permissions.size == 1 -> "权限说明"
            permissions.any { it.contains("CAMERA") } && permissions.size > 1 -> "相机和存储权限说明"
            permissions.any { it.contains("LOCATION") } -> "位置权限说明"
            permissions.any { it.contains("STORAGE") || it.contains("EXTERNAL_STORAGE") } -> "存储权限说明"
            permissions.any { it.contains("AUDIO") } -> "录音权限说明"
            permissions.any { it.contains("CONTACT") } -> "联系人权限说明"
            permissions.any { it.contains("PHONE") || it.contains("CALL") } -> "电话权限说明"
            else -> "权限说明"
        }
    }
}
