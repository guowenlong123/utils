package com.lib.utils.permission

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.XXPermissions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * XXPermissions Kotlin 封装
 * 使用 Flow 实现异步回调
 * 
 * 使用示例：
 * ```
 * // 在 Activity 或 Fragment 中
 * lifecycleScope.launch {
 *     XXPermissionsHelper.request(this@MainActivity)
 *         .permissions(Permission.CAMERA, Permission.RECORD_AUDIO)
 *         .start()
 *         .collect { result ->
 *             if (result.isAllGranted) {
 *                 // 所有权限已授予
 *                 Toast.makeText(this@MainActivity, "权限已授予", Toast.LENGTH_SHORT).show()
 *             } else {
 *                 // 有权限被拒绝
 *                 Toast.makeText(this@MainActivity, "权限被拒绝: ${result.deniedList}", Toast.LENGTH_SHORT).show()
 *             }
 *         }
 * }
 * ```
 */
class XXPermissionsHelper private constructor(
    private val activity: FragmentActivity?,
    private val fragment: Fragment?
) {

    private var permissionList: MutableList<String> = mutableListOf()
    private var interceptor: ((List<String>, OnPermissionCallback) -> Unit)? = null
    private var unchecked: Boolean = false
    private var showDescription: Boolean = false
    private var customDescription: String? = null
    private var descriptionPopup: PermissionDescriptionPopup? = null
    private var hasPermanentDenied: Boolean = false

    companion object {
        /**
         * 请求权限
         */
        fun request(activity: FragmentActivity): XXPermissionsHelper {
            return XXPermissionsHelper(activity, null)
        }

        fun request(fragment: Fragment): XXPermissionsHelper {
            return XXPermissionsHelper(null, fragment)
        }

        /**
         * 检查权限是否已授予
         */
        fun isGranted(activity: FragmentActivity, vararg permissions: String): Boolean {
            return XXPermissions.isGranted(activity, *permissions)
        }

        fun isGranted(fragment: Fragment, vararg permissions: String): Boolean {
            return XXPermissions.isGranted(fragment.requireContext(), *permissions)
        }

        /**
         * 检查权限是否被永久拒绝
         * 注意：由于 XXPermissions 库没有提供直接的 API，这里只能检查权限是否被拒绝
         */
        fun isPermanentDenied(activity: FragmentActivity, vararg permissions: String): Boolean {
            return !XXPermissions.isGranted(activity, *permissions)
        }

        fun isPermanentDenied(fragment: Fragment, vararg permissions: String): Boolean {
            return !XXPermissions.isGranted(fragment.requireContext(), *permissions)
        }

        /**
         * 跳转到应用设置页面
         */
        fun startPermissionActivity(activity: FragmentActivity, vararg permissions: String) {
            XXPermissions.startPermissionActivity(activity, permissions.toList())
        }

        fun startPermissionActivity(fragment: Fragment, vararg permissions: String) {
            XXPermissions.startPermissionActivity(fragment.requireActivity(), permissions.toList())
        }

        /**
         * 跳转到应用设置页面（Flow 版本）
         */
        fun startPermissionActivityForResult(
            activity: FragmentActivity,
            vararg permissions: String
        ): Flow<PermissionResult> = callbackFlow {
            XXPermissions.startPermissionActivity(activity, *permissions)
            // 等待返回后检查权限状态
            trySend(
                PermissionResult(
                    isAllGranted = XXPermissions.isGranted(activity, *permissions),
                    grantedList = permissions.filter { XXPermissions.isGranted(activity, it) },
                    deniedList = permissions.filter { !XXPermissions.isGranted(activity, it) },
                    permanentDeniedList = emptyList()
                )
            )
            close()

            awaitClose {
                // 清理资源
            }
        }

        fun startPermissionActivityForResult(
            fragment: Fragment,
            vararg permissions: String
        ): Flow<PermissionResult> = callbackFlow {
            XXPermissions.startPermissionActivity(fragment, *permissions)
            // 等待返回后检查权限状态
            trySend(
                PermissionResult(
                    isAllGranted = XXPermissions.isGranted(fragment.requireContext(), *permissions),
                    grantedList = permissions.filter { XXPermissions.isGranted(fragment.requireContext(), it) },
                    deniedList = permissions.filter { !XXPermissions.isGranted(fragment.requireContext(), it) },
                    permanentDeniedList = emptyList()
                )
            )
            close()

            awaitClose {
                // 清理资源
            }
        }
    }

    /**
     * 设置要请求的权限
     */
    fun permissions(vararg permissions: String): XXPermissionsHelper {
        permissionList.addAll(permissions)
        return this
    }

    /**
     * 设置要请求的权限（列表）
     */
    fun permissions(permissions: List<String>): XXPermissionsHelper {
        permissionList.addAll(permissions)
        return this
    }

    /**
     * 设置权限拦截器
     */
    fun interceptor(block: (List<String>, OnPermissionCallback) -> Unit): XXPermissionsHelper {
        interceptor = block
        return this
    }

    /**
     * 设置不检查权限，直接请求
     */
    fun unchecked(unchecked: Boolean = true): XXPermissionsHelper {
        this.unchecked = unchecked
        return this
    }

    /**
     * 显示权限描述弹窗
     */
    fun showDescription(show: Boolean = true): XXPermissionsHelper {
        this.showDescription = show
        return this
    }

    /**
     * 设置自定义权限描述
     */
    fun description(description: String): XXPermissionsHelper {
        this.customDescription = description
        this.showDescription = true
        return this
    }

    /**
     * 启动权限请求，返回 Flow
     */
    fun start(): Flow<PermissionResult> = callbackFlow {
        try {
            if (permissionList.isEmpty()) {
                trySend(
                    PermissionResult(
                        isAllGranted = true,
                        grantedList = emptyList(),
                        deniedList = emptyList(),
                        permanentDeniedList = emptyList()
                    )
                )
                close()
                return@callbackFlow
            }

            val context = activity ?: fragment?.requireActivity()
            if (context == null) {
                trySend(
                    PermissionResult(
                        isAllGranted = false,
                        grantedList = emptyList(),
                        deniedList = permissionList,
                        permanentDeniedList = emptyList()
                    )
                )
                close()
                return@callbackFlow
            }

            // 如果需要显示描述弹窗
            val hostActivity = activity ?: fragment?.requireActivity()
            if (showDescription && hostActivity != null) {
                // 检查权限是否已经授予
                val allGranted = XXPermissions.isGranted(hostActivity, *permissionList.toTypedArray())
                
                // 只有在权限未授予且未被永久拒绝时才显示弹窗
                if (!allGranted && !hasPermanentDenied) {
                    // 权限未授予且未被永久拒绝：显示描述弹窗并同时请求权限
                    descriptionPopup = PermissionDescriptionPopup.show(
                        activity = hostActivity,
                        permissions = permissionList,
                        customDescription = customDescription
                    )
                    // 立即请求权限（不等待弹窗关闭）
                    requestPermissionsInternal(this)
                } else {
                    // 权限已授予或已被永久拒绝：直接请求权限（不显示弹窗）
                    requestPermissionsInternal(this)
                }
            } else {
                // 直接请求权限
                requestPermissionsInternal(this)
            }

        } catch (e: Exception) {
            trySend(
                PermissionResult(
                    isAllGranted = false,
                    grantedList = emptyList(),
                    deniedList = permissionList,
                    permanentDeniedList = emptyList()
                )
            )
            close(e)
        }

        awaitClose {
            // 用户取消权限请求（关闭对话框而不是点击允许/拒绝）时，关闭描述弹窗
            descriptionPopup?.dismiss()
            descriptionPopup = null
        }
    }

    /**
     * 内部请求权限方法
     */
    private fun requestPermissionsInternal(producer: kotlinx.coroutines.channels.ProducerScope<PermissionResult>) {
        val permissionsBuilder = if (activity != null) {
            XXPermissions.with(activity)
        } else if (fragment != null) {
            XXPermissions.with(fragment)
        } else {
            producer.trySend(
                PermissionResult(
                    isAllGranted = false,
                    grantedList = emptyList(),
                    deniedList = permissionList,
                    permanentDeniedList = emptyList()
                )
            )
            producer.close()
            return
        }

        permissionsBuilder
            .permission(permissionList)
            .apply {
                if (interceptor != null) {
                    interceptor(interceptor!!)
                }
                if (unchecked) {
                    unchecked()
                }
            }
            .request(object : OnPermissionCallback {
                override fun onGranted(granted: MutableList<String>, allGranted: Boolean) {
                    // 关闭描述弹窗
                    descriptionPopup?.dismiss()
                    descriptionPopup = null
                    
                    val context = activity ?: fragment?.requireActivity()
                    
                    // 使用 XXPermissions API 检查被拒绝的权限是否被永久拒绝
                    val deniedPermissions = permissionList.filter { !granted.contains(it) }
                    val permanentDenied = if (context != null && deniedPermissions.isNotEmpty()) {
                        // 检查哪些被拒绝的权限被永久拒绝
                        deniedPermissions.filter { permission ->
                            XXPermissions.isDoNotAskAgainPermissions(context, listOf(permission))
                        }
                    } else {
                        emptyList()
                    }

                    producer.trySend(
                        PermissionResult(
                            isAllGranted = allGranted,
                            grantedList = granted,
                            deniedList = deniedPermissions,
                            permanentDeniedList = permanentDenied
                        )
                    )
                    producer.close()
                }

                override fun onDenied(denied: MutableList<String>, doNotAskAgain: Boolean) {
                    // 关闭描述弹窗
                    descriptionPopup?.dismiss()
                    descriptionPopup = null
                    
                    val context = activity ?: fragment?.requireActivity()
                    
                    // 如果 denied 列表为空，说明用户取消了权限请求，不发送结果
                    if (denied.isEmpty()) {
                        producer.close()
                        return
                    }
                    
                    // 检查是否有权限被永久拒绝（必须在回调中调用）
                    // 只有当 doNotAskAgain=true 或 API 明确返回永久拒绝时，才设置标志
                    if (context != null && doNotAskAgain) {
                        hasPermanentDenied = XXPermissions.isDoNotAskAgainPermissions(context, permissionList)
                    }
                    
                    // 使用 XXPermissions API 检查每个被拒绝的权限是否被永久拒绝
                    // 注意：只有在 onDenied 回调中才调用才准确
                    // 并且只有当 doNotAskAgain=true 时才可能是永久拒绝
                    val permanentDenied = if (context != null && denied.isNotEmpty() && doNotAskAgain) {
                        denied.filter { permission ->
                            XXPermissions.isDoNotAskAgainPermissions(context, listOf(permission))
                        }
                    } else {
                        // doNotAskAgain=false 表示用户只是拒绝，不是永久拒绝
                        emptyList()
                    }

                    producer.trySend(
                        PermissionResult(
                            isAllGranted = false,
                            grantedList = permissionList.filter { !denied.contains(it) },
                            deniedList = denied,
                            permanentDeniedList = permanentDenied
                        )
                    )
                    producer.close()
                }
            })
    }
}
