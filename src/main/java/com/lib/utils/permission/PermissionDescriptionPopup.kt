package com.lib.utils.permission

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.PopupWindow
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.lib.utils.R

/**
 * 权限描述弹窗
 * 以顶部弹窗形式展示权限描述信息，与系统权限请求同时显示
 */
class PermissionDescriptionPopup(
    private val activity: FragmentActivity,
    private val permissions: List<String>,
    private val customDescription: String? = null
) {

    private var popupWindow: PopupWindow? = null

    /**
     * 显示弹窗
     */
    fun show() {
        val contentView = LayoutInflater.from(activity)
            .inflate(R.layout.layout_permission_description, null, false)

        val tvTitle = contentView.findViewById<TextView>(R.id.tvPermissionTitle)
        val tvDescription = contentView.findViewById<TextView>(R.id.tvPermissionDescription)

        // 设置标题和描述
        tvTitle.text = PermissionDescription.getPermissionGroupTitle(permissions)
        tvDescription.text = customDescription ?: PermissionDescription.getDescriptions(permissions)

        // 计算宽度（屏幕宽度 - 左右边距 32dp）
        val margin = (16 * activity.resources.displayMetrics.density).toInt()
        val screenWidth = activity.resources.displayMetrics.widthPixels
        val popupWidth = screenWidth - margin * 2

        // 创建 PopupWindow
        popupWindow = PopupWindow(
            contentView,
            popupWidth,
            android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
            false  // 不可点击关闭
        ).apply {
            isOutsideTouchable = false
            isFocusable = false
        }

        // 显示在顶部，左右各留 16dp 边距
        val decorView = activity.window.decorView
        popupWindow?.showAtLocation(
            decorView, 
            Gravity.TOP or Gravity.CENTER_HORIZONTAL, 
            0, 
            getStatusBarHeight() + margin
        )
    }

    /**
     * 关闭弹窗
     */
    fun dismiss() {
        popupWindow?.dismiss()
        popupWindow = null
    }

    /**
     * 获取状态栏高度
     */
    private fun getStatusBarHeight(): Int {
        val resourceId = activity.resources.getIdentifier("status_bar_height", "dimen", "android")
        return if (resourceId > 0) {
            activity.resources.getDimensionPixelSize(resourceId)
        } else {
            0
        }
    }

    companion object {
        /**
         * 快速创建并显示弹窗
         */
        fun show(
            activity: FragmentActivity,
            permissions: List<String>,
            customDescription: String? = null
        ): PermissionDescriptionPopup {
            val popup = PermissionDescriptionPopup(activity, permissions, customDescription)
            popup.show()
            return popup
        }
    }
}
