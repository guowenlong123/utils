package com.lib.utils.event

import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.ConcurrentHashMap

/**
 * 事件总线
 * 支持粘性事件和非粘性事件
 */

// 基础事件类
open class BaseEvent

data class TabEvent(val position:Int) : BaseEvent()



object EventBus {
    // 事件流（改为 internal 以便内联函数访问）
    private val events = MutableSharedFlow<Any>(
        replay = 0, // 不重放事件（非粘性）
        extraBufferCapacity = 16 // 额外缓冲区大小
    )

    // 粘性事件缓存（改为 internal 以便内联函数访问）
    private val stickyEvents = ConcurrentHashMap<Class<*>, Any>()

    /**
     * 发送非粘性事件
     * @param event 事件对象
     */
    fun post(event: Any) {
        runBlocking {
            events.emit(event)
        }
    }

    /**
     * 发送粘性事件
     * @param event 粘性事件对象
     */
    fun postSticky(event: Any) {
        // 缓存粘性事件
        stickyEvents[event::class.java] = event
        // 发送事件
        post(event)
    }

    /**
     * 订阅非粘性事件
     * @param scope 协程作用域
     * @param action 事件处理函数
     */
    fun <T : Any> observe(
        scope: CoroutineScope,
        eventType: Class<T>,
        action: suspend (T) -> Unit
    ) {
        scope.launch {
            events
                .filter { eventType.isInstance(it) }
                .map { it as T }
                .collect { action(it) }
        }
    }

    /**
     * 订阅非粘性事件（内联版本）
     * @param scope 协程作用域
     * @param action 事件处理函数
     */
    inline fun <reified T : Any> observe(
        scope: CoroutineScope,
        crossinline action: suspend (T) -> Unit
    ) {
        observe(scope, T::class.java) { action(it) }
    }

    /**
     * 订阅粘性事件
     * @param scope 协程作用域
     * @param eventType 事件类型
     * @param action 事件处理函数
     */
    fun <T : Any> observeSticky(
        scope: CoroutineScope,
        eventType: Class<T>,
        action: suspend (T) -> Unit
    ) {
        // 先处理缓存的粘性事件
        stickyEvents[eventType]?.let { event ->
            scope.launch {
                @Suppress("UNCHECKED_CAST")
                action(event as T)
            }
        }

        // 再订阅新事件
        observe(scope, eventType, action)
    }

    /**
     * 订阅粘性事件（内联版本）
     * @param scope 协程作用域
     * @param action 事件处理函数
     */
    inline fun <reified T : Any> observeSticky(
        scope: CoroutineScope,
        crossinline action: suspend (T) -> Unit
    ) {
        observeSticky(scope, T::class.java) { action(it) }
    }

    /**
     * 移除粘性事件
     * @param eventType 事件类型
     */
    fun <T : Any> removeStickyEvent(eventType: Class<T>) {
        stickyEvents.remove(eventType)
    }

    /**
     * 移除粘性事件（内联版本）
     * @param T 事件类型
     */
    inline fun <reified T : Any> removeStickyEvent() {
        removeStickyEvent(T::class.java)
    }

    /**
     * 移除所有粘性事件
     */
    fun removeAllStickyEvents() {
        stickyEvents.clear()
    }

    /**
     * 获取粘性事件
     * @param eventType 事件类型
     * @return 粘性事件实例或 null
     */
    fun <T : Any> getStickyEvent(eventType: Class<T>): T? {
        @Suppress("UNCHECKED_CAST")
        return stickyEvents[eventType] as T?
    }

    /**
     * 获取粘性事件（内联版本）
     * @param T 事件类型
     * @return 粘性事件实例或 null
     */
    inline fun <reified T : Any> getStickyEvent(): T? {
        return getStickyEvent(T::class.java)
    }
}

/**
 * 生命周期所有者扩展函数
 */
inline fun <reified T : Any> androidx.lifecycle.LifecycleOwner.observeEvent(
    crossinline action: suspend (T) -> Unit
) {
    EventBus.observe(this.lifecycleScope) { event: T ->
        action(event)
    }
}

inline fun <reified T : Any> androidx.lifecycle.LifecycleOwner.observeStickyEvent(
    crossinline action: suspend (T) -> Unit
) {
    EventBus.observeSticky(this.lifecycleScope) { event: T ->
        action(event)
    }
}