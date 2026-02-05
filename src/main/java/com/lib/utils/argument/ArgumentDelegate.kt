package com.lib.utils.argument

import android.os.Bundle
import android.os.Parcelable
import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * 核心委托类 - 用于 Activity 和 Fragment 获取参数值
 */
class ArgumentDelegate<T : Any?>(
    private val key: String,
    private val defaultValue: T,
    private val bundleProvider: () -> Bundle?
) : ReadOnlyProperty<Any, T> {

    @Suppress("UNCHECKED_CAST", "DEPRECATION")
    override fun getValue(thisRef: Any, property: KProperty<*>): T {
        val bundle = bundleProvider() ?: return defaultValue

        return when {
            // 基本类型处理
            defaultValue is String -> bundle.getString(key) as? T ?: defaultValue
            defaultValue is Int -> bundle.getInt(key, defaultValue) as T
            defaultValue is Long -> bundle.getLong(key, defaultValue) as T
            defaultValue is Float -> bundle.getFloat(key, defaultValue) as T
            defaultValue is Double -> bundle.getDouble(key, defaultValue) as T
            defaultValue is Boolean -> bundle.getBoolean(key, defaultValue) as T
            defaultValue is ByteArray -> bundle.getByteArray(key) as? T ?: defaultValue
            defaultValue is CharArray -> bundle.getCharArray(key) as? T ?: defaultValue
            defaultValue is BooleanArray -> bundle.getBooleanArray(key) as? T ?: defaultValue
            defaultValue is IntArray -> bundle.getIntArray(key) as? T ?: defaultValue
            defaultValue is LongArray -> bundle.getLongArray(key) as? T ?: defaultValue
            defaultValue is FloatArray -> bundle.getFloatArray(key) as? T ?: defaultValue
            defaultValue is DoubleArray -> bundle.getDoubleArray(key) as? T ?: defaultValue
            defaultValue is Parcelable -> bundle.getParcelable(key) as? T ?: defaultValue
            
            // 可空类型处理
            defaultValue == null -> {
                if (!bundle.containsKey(key)) {
                    throw IllegalStateException("Required argument '$key' not found")
                }
                bundle.get(key) as? T ?: throw IllegalStateException(
                    "Argument '$key' has wrong type"
                )
            }
            
            // 尝试处理 ArrayList
            defaultValue is ArrayList<*> -> {
                try {
                    // 尝试作为 Parcelable 列表
                    val parcelableList = bundle.getParcelableArrayList<Parcelable>(key) as? T
                    if (parcelableList != null) return parcelableList
                    
                    // 尝试作为 Serializable 列表
                    val serializableList = bundle.getSerializable(key) as? T
                    if (serializableList != null) return serializableList
                    
                    defaultValue
                } catch (e: Exception) {
                    bundle.getSerializable(key) as? T ?: defaultValue
                }
            }
            
            // 尝试处理 Array
            defaultValue.javaClass.isArray -> {
                try {
                    // 尝试作为 Parcelable 数组
                    bundle.getParcelableArray(key) as? T ?: defaultValue
                } catch (e: Exception) {
                    defaultValue
                }
            }
            
            // 其他类型
            else -> {
                // 尝试序列化类型
                if (defaultValue is java.io.Serializable) {
                    bundle.getSerializable(key) as? T ?: defaultValue
                } else {
                    throw IllegalArgumentException("Unsupported type: ${defaultValue.javaClass.name}")
                }
            }
        }
    }
}

/**
 * Fragment 扩展函数 - 获取参数
 */
fun <T : Any?> Fragment.arg(key: String, defaultValue: T): ArgumentDelegate<T> {
    return ArgumentDelegate(key, defaultValue) { arguments }
}

/**
 * Activity 扩展函数 - 获取参数
 */
fun <T : Any?> ComponentActivity.arg(key: String, defaultValue: T): ArgumentDelegate<T> {
    return ArgumentDelegate(key, defaultValue) { intent?.extras }
}

/**
 * Fragment 便捷别名 - 可选参数
 */
fun <T : Any?> Fragment.optionalArg(key: String, defaultValue: T): ArgumentDelegate<T> {
    return arg(key, defaultValue)
}

/**
 * Fragment 便捷别名 - 必需参数
 */
fun <T : Any> Fragment.requiredArg(key: String): ArgumentDelegate<T> {
    return arg<T?>(key, null) as ArgumentDelegate<T>
}

/**
 * Activity 便捷别名 - 可选参数
 */
fun <T : Any?> ComponentActivity.optionalArg(key: String, defaultValue: T): ArgumentDelegate<T> {
    return arg(key, defaultValue)
}

/**
 * Activity 便捷别名 - 必需参数
 */
fun <T : Any> ComponentActivity.requiredArg(key: String): ArgumentDelegate<T> {
    return arg<T?>(key, null) as ArgumentDelegate<T>
}

/**
 * 示例：Parcelize 数据类
 */
/*
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val id: String,
    val name: String,
    val age: Int
) : Parcelable

@Parcelize
data class Address(
    val street: String,
    val city: String
) : Parcelable
*/

/**
 * 示例：Fragment 使用
 */
/*
class MyFragment : Fragment() {
    // 基本类型
    private val userId by requiredArg<String>("userId")
    private val userName by optionalArg("userName", "Guest")
    private val userAge by optionalArg("userAge", 0)
    
    // Parcelize 类型
    private val user by requiredArg<User>("user")
    private val address by optionalArg<User>("address", null)
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // 使用参数
        println("User ID: $userId")
        println("User Name: $userName")
        println("User Age: $userAge")
        println("User: $user")
        println("Address: $address")
    }
}

// 传递参数给 Fragment
val fragment = MyFragment().apply {
    arguments = Bundle().apply {
        putString("userId", "123")
        putString("userName", "John Doe")
        putInt("userAge", 30)
        putParcelable("user", User("123", "John Doe", 30))
        putParcelable("address", Address("123 Main St", "New York"))
    }
}
*/

/**
 * 示例：Activity 使用
 */
/*
class MyActivity : ComponentActivity() {
    // 基本类型
    private val userId by requiredArg<String>("userId")
    private val userName by optionalArg("userName", "Guest")
    
    // Parcelize 类型
    private val user by requiredArg<User>("user")
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 使用参数
        println("User ID: $userId")
        println("User Name: $userName")
        println("User: $user")
    }
}

// 启动 Activity 并传递参数
val intent = Intent(this, MyActivity::class.java).apply {
    putExtra("userId", "123")
    putExtra("userName", "John Doe")
    putExtra("user", User("123", "John Doe", 30))
}
startActivity(intent)
*/