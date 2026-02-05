package com.lib.utils.permission

/**
 * 权限请求结果
 */
data class PermissionResult(
    val isAllGranted: Boolean,
    val grantedList: List<String>,
    val deniedList: List<String>,
    val permanentDeniedList: List<String>
) {
    /**
     * 是否有权限被拒绝
     */
    val hasDenied: Boolean
        get() = deniedList.isNotEmpty()

    /**
     * 是否有权限被永久拒绝
     */
    val hasPermanentDenied: Boolean
        get() = permanentDeniedList.isNotEmpty()

    /**
     * 获取所有被拒绝的权限（包括永久拒绝）
     */
    val allDenied: List<String>
        get() = deniedList

    /**
     * 获取仅被临时拒绝的权限（不包括永久拒绝）
     */
    val temporaryDenied: List<String>
        get() = deniedList.filter { !permanentDeniedList.contains(it) }
}

/**
 * 权限配置
 */
data class PermissionConfig(
    val permissions: List<String> = emptyList(),
    val unchecked: Boolean = false
)
