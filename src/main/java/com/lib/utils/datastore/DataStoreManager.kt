package com.lib.utils.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import java.io.IOException

/**
 * DataStore 管理器
 * 对 Android DataStore 的 Kotlin 封装，支持 Preferences DataStore
 */
class DataStoreManager private constructor(private val context: Context) {
    
    // Preferences DataStore 实例
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_preferences")
    private val preferencesDataStore: DataStore<Preferences>
        get() = context.dataStore
    
    /**
     * 保存数据
     * @param key 键
     * @param value 值
     */
    suspend fun <T> save(key: String, value: T) {
        try {
            when (value) {
                is String -> preferencesDataStore.edit { preferences ->
                    preferences[stringPreferencesKey(key)] = value
                }
                is Int -> preferencesDataStore.edit { preferences ->
                    preferences[intPreferencesKey(key)] = value
                }
                is Long -> preferencesDataStore.edit { preferences ->
                    preferences[longPreferencesKey(key)] = value
                }
                is Float -> preferencesDataStore.edit { preferences ->
                    preferences[floatPreferencesKey(key)] = value
                }
                is Double -> preferencesDataStore.edit { preferences ->
                    preferences[doublePreferencesKey(key)] = value
                }
                is Boolean -> preferencesDataStore.edit { preferences ->
                    preferences[booleanPreferencesKey(key)] = value
                }
                is Set<*> -> {
                    @Suppress("UNCHECKED_CAST")
                    val stringSet = value as Set<String>
                    preferencesDataStore.edit { preferences ->
                        preferences[stringSetPreferencesKey(key)] = stringSet
                    }
                }
                else -> throw IllegalArgumentException("Unsupported type: ${value!!::class.java.name}")
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    
    /**
     * 保存多个数据
     * @param data 键值对映射
     */
    suspend fun saveAll(data: Map<String, Any>) {
        data.forEach { (key, value) ->
            save(key, value)
        }
    }
    
    /**
     * 读取数据
     * @param key 键
     * @param defaultValue 默认值
     * @return 数据值或默认值
     */
    @Suppress("UNCHECKED_CAST")
    suspend fun <T> read(key: String, defaultValue: T): T {
        return try {
            when (defaultValue) {
                is String -> preferencesDataStore.data
                    .map { preferences ->
                        preferences[stringPreferencesKey(key)] ?: defaultValue
                    }
                    .first() as T
                is Int -> preferencesDataStore.data
                    .map { preferences ->
                        preferences[intPreferencesKey(key)] ?: defaultValue
                    }
                    .first() as T
                is Long -> preferencesDataStore.data
                    .map { preferences ->
                        preferences[longPreferencesKey(key)] ?: defaultValue
                    }
                    .first() as T
                is Float -> preferencesDataStore.data
                    .map { preferences ->
                        preferences[floatPreferencesKey(key)] ?: defaultValue
                    }
                    .first() as T
                is Double -> preferencesDataStore.data
                    .map { preferences ->
                        preferences[doublePreferencesKey(key)] ?: defaultValue
                    }
                    .first() as T
                is Boolean -> preferencesDataStore.data
                    .map { preferences ->
                        preferences[booleanPreferencesKey(key)] ?: defaultValue
                    }
                    .first() as T
                is Set<*> -> preferencesDataStore.data
                    .map { preferences ->
                        @Suppress("UNCHECKED_CAST")
                        preferences[stringSetPreferencesKey(key)] as T? ?: defaultValue
                    }
                    .first()
                else -> throw IllegalArgumentException("Unsupported type: ${defaultValue!!::class.java.name}")
            }
        } catch (e: IOException) {
            e.printStackTrace()
            defaultValue
        }
    }
    
    /**
     * 读取数据流
     * @param key 键
     * @param defaultValue 默认值
     * @return 数据值流
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> readFlow(key: String, defaultValue: T): Flow<T> {
        return when (defaultValue) {
            is String -> preferencesDataStore.data
                .catch { exception ->
                    if (exception is IOException) {
                        emit(emptyPreferences())
                    } else {
                        throw exception
                    }
                }
                .map { preferences ->
                    preferences[stringPreferencesKey(key)] ?: defaultValue
                } as Flow<T>
            is Int -> preferencesDataStore.data
                .catch { exception ->
                    if (exception is IOException) {
                        emit(emptyPreferences())
                    } else {
                        throw exception
                    }
                }
                .map { preferences ->
                    preferences[intPreferencesKey(key)] ?: defaultValue
                } as Flow<T>
            is Long -> preferencesDataStore.data
                .catch { exception ->
                    if (exception is IOException) {
                        emit(emptyPreferences())
                    } else {
                        throw exception
                    }
                }
                .map { preferences ->
                    preferences[longPreferencesKey(key)] ?: defaultValue
                } as Flow<T>
            is Float -> preferencesDataStore.data
                .catch { exception ->
                    if (exception is IOException) {
                        emit(emptyPreferences())
                    } else {
                        throw exception
                    }
                }
                .map { preferences ->
                    preferences[floatPreferencesKey(key)] ?: defaultValue
                } as Flow<T>
            is Double -> preferencesDataStore.data
                .catch { exception ->
                    if (exception is IOException) {
                        emit(emptyPreferences())
                    } else {
                        throw exception
                    }
                }
                .map { preferences ->
                    preferences[doublePreferencesKey(key)] ?: defaultValue
                } as Flow<T>
            is Boolean -> preferencesDataStore.data
                .catch { exception ->
                    if (exception is IOException) {
                        emit(emptyPreferences())
                    } else {
                        throw exception
                    }
                }
                .map { preferences ->
                    preferences[booleanPreferencesKey(key)] ?: defaultValue
                } as Flow<T>
            is Set<*> -> preferencesDataStore.data
                .catch { exception ->
                    if (exception is IOException) {
                        emit(emptyPreferences())
                    } else {
                        throw exception
                    }
                }
                .map { preferences ->
                    @Suppress("UNCHECKED_CAST")
                    preferences[stringSetPreferencesKey(key)] as T? ?: defaultValue
                }
            else -> throw IllegalArgumentException("Unsupported type: ${defaultValue!!::class.java.name}")
        }
    }
    
    /**
     * 删除数据
     * @param key 键
     */
    suspend fun delete(key: String) {
        try {
            preferencesDataStore.edit { preferences ->
                preferences.asMap().forEach { (preferencesKey, _) ->
                    if (preferencesKey.name == key) {
                        preferences.remove(preferencesKey)
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    
    /**
     * 删除多个数据
     * @param keys 键集合
     */
    suspend fun deleteAll(keys: List<String>) {
        keys.forEach { key ->
            delete(key)
        }
    }
    
    /**
     * 清除所有数据
     */
    suspend fun clear() {
        try {
            preferencesDataStore.edit {
                it.clear()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    
    /**
     * 检查键是否存在
     * @param key 键
     * @return 是否存在
     */
    suspend fun contains(key: String): Boolean {
        return try {
            preferencesDataStore.data
                .map { preferences ->
                    preferences.asMap().any { it.key.name == key }
                }
                .first()
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }
    
    /**
     * 获取所有键
     * @return 键集合
     */
    suspend fun getAllKeys(): Set<String> {
        return try {
            preferencesDataStore.data
                .map { preferences ->
                    preferences.asMap().keys.map { it.name }.toSet()
                }
                .first()
        } catch (e: IOException) {
            e.printStackTrace()
            emptySet()
        }
    }
    
    /**
     * 获取所有数据
     * @return 所有数据的映射
     */
    suspend fun getAll(): Map<String, Any> {
        return try {
            preferencesDataStore.data
                .map { preferences ->
                    preferences.asMap().mapKeys { it.key.name }
                }
                .first()
        } catch (e: IOException) {
            e.printStackTrace()
            emptyMap()
        }
    }
    
    companion object {
        
        @Volatile
        private var INSTANCE: DataStoreManager? = null
        
        /**
         * 在 Application 中初始化 DataStoreManager
         * @param context 上下文（建议使用 Application 上下文）
         */
        fun initialize(context: Context) {
            if (INSTANCE == null) {
                synchronized(this) {
                    if (INSTANCE == null) {
                        INSTANCE = DataStoreManager(context.applicationContext)
                    }
                }
            }
        }
        
        /**
         * 获取 DataStoreManager 实例
         * @return DataStoreManager 实例
         * @throws IllegalStateException 如果未在 Application 中初始化
         */
        fun getInstance(): DataStoreManager {
            return INSTANCE ?: throw IllegalStateException("DataStoreManager not initialized. Call initialize() in Application class first.")
        }
        
        /**
         * 获取 DataStoreManager 实例（兼容旧版本）
         * @param context 上下文
         * @return DataStoreManager 实例
         */
        fun getInstance(context: Context): DataStoreManager {
            if (INSTANCE == null) {
                initialize(context)
            }
            return getInstance()
        }
        
        /**
         * 保存数据（同步）
         * @param key 键
         * @param value 值
         */
        fun saveSync(key: String, value: Any) {
            runBlocking {
                getInstance().save(key, value)
            }
        }
        
        /**
         * 保存数据（同步，兼容旧版本）
         * @param context 上下文
         * @param key 键
         * @param value 值
         */
        fun saveSync(context: Context, key: String, value: Any) {
            runBlocking {
                getInstance(context).save(key, value)
            }
        }
        
        /**
         * 读取数据（同步）
         * @param key 键
         * @param defaultValue 默认值
         * @return 数据值或默认值
         */
        fun <T> readSync(key: String, defaultValue: T): T {
            return runBlocking {
                getInstance().read(key, defaultValue)
            }
        }
        
        /**
         * 读取数据（同步，兼容旧版本）
         * @param context 上下文
         * @param key 键
         * @param defaultValue 默认值
         * @return 数据值或默认值
         */
        fun <T> readSync(context: Context, key: String, defaultValue: T): T {
            return runBlocking {
                getInstance(context).read(key, defaultValue)
            }
        }
        
        /**
         * 删除数据（同步）
         * @param key 键
         */
        fun deleteSync(key: String) {
            runBlocking {
                getInstance().delete(key)
            }
        }
        
        /**
         * 删除数据（同步，兼容旧版本）
         * @param context 上下文
         * @param key 键
         */
        fun deleteSync(context: Context, key: String) {
            runBlocking {
                getInstance(context).delete(key)
            }
        }
        
        /**
         * 清除所有数据（同步）
         */
        fun clearSync() {
            runBlocking {
                getInstance().clear()
            }
        }
        
        /**
         * 清除所有数据（同步，兼容旧版本）
         * @param context 上下文
         */
        fun clearSync(context: Context) {
            runBlocking {
                getInstance(context).clear()
            }
        }
    }
}

/**
 * 保存数据扩展函数（推荐使用）
 */
suspend fun saveData(key: String, value: Any) {
    DataStoreManager.getInstance().save(key, value)
}

/**
 * 读取数据扩展函数（推荐使用）
 */
suspend fun <T> readData(key: String, defaultValue: T): T {
    return DataStoreManager.getInstance().read(key, defaultValue)
}

/**
 * 读取数据流扩展函数（推荐使用）
 */
fun <T> readDataFlow(key: String, defaultValue: T): Flow<T> {
    return DataStoreManager.getInstance().readFlow(key, defaultValue)
}

/**
 * 删除数据扩展函数（推荐使用）
 */
suspend fun deleteData(key: String) {
    DataStoreManager.getInstance().delete(key)
}

/**
 * 清除所有数据扩展函数（推荐使用）
 */
suspend fun clearAllData() {
    DataStoreManager.getInstance().clear()
}

/**
 * 保存数据同步扩展函数（推荐使用）
 */
fun saveDataSync(key: String, value: Any) {
    DataStoreManager.saveSync(key, value)
}

/**
 * 读取数据同步扩展函数（推荐使用）
 */
fun <T> readDataSync(key: String, defaultValue: T): T {
    return DataStoreManager.readSync(key, defaultValue)
}

/**
 * 删除数据同步扩展函数（推荐使用）
 */
fun deleteDataSync(key: String) {
    DataStoreManager.deleteSync(key)
}

/**
 * 清除所有数据同步扩展函数（推荐使用）
 */
fun clearAllDataSync() {
    DataStoreManager.clearSync()
}

/**
 * 保存数据扩展函数（兼容旧版本）
 */
suspend fun Context.saveData(key: String, value: Any) {
    DataStoreManager.getInstance(this).save(key, value)
}

/**
 * 读取数据扩展函数（兼容旧版本）
 */
suspend fun <T> Context.readData(key: String, defaultValue: T): T {
    return DataStoreManager.getInstance(this).read(key, defaultValue)
}

/**
 * 读取数据流扩展函数（兼容旧版本）
 */
fun <T> Context.readDataFlow(key: String, defaultValue: T): Flow<T> {
    return DataStoreManager.getInstance(this).readFlow(key, defaultValue)
}

/**
 * 删除数据扩展函数（兼容旧版本）
 */
suspend fun Context.deleteData(key: String) {
    DataStoreManager.getInstance(this).delete(key)
}

/**
 * 清除所有数据扩展函数（兼容旧版本）
 */
suspend fun Context.clearAllData() {
    DataStoreManager.getInstance(this).clear()
}

/**
 * 保存数据同步扩展函数（兼容旧版本）
 */
fun Context.saveDataSync(key: String, value: Any) {
    DataStoreManager.saveSync(this, key, value)
}

/**
 * 读取数据同步扩展函数（兼容旧版本）
 */
fun <T> Context.readDataSync(key: String, defaultValue: T): T {
    return DataStoreManager.readSync(this, key, defaultValue)
}

/**
 * 删除数据同步扩展函数（兼容旧版本）
 */
fun Context.deleteDataSync(key: String) {
    DataStoreManager.deleteSync(this, key)
}

/**
 * 清除所有数据同步扩展函数（兼容旧版本）
 */
fun Context.clearAllDataSync() {
    DataStoreManager.clearSync(this)
}

/**
 * 保存多个数据扩展函数
 */
suspend fun saveDataAll(data: Map<String, Any>) {
    DataStoreManager.getInstance().saveAll(data)
}

/**
 * 保存多个数据同步扩展函数
 */
fun saveDataAllSync(data: Map<String, Any>) {
    runBlocking {
        DataStoreManager.getInstance().saveAll(data)
    }
}

/**
 * 保存多个数据扩展函数（兼容旧版本）
 */
suspend fun Context.saveDataAll(data: Map<String, Any>) {
    DataStoreManager.getInstance(this).saveAll(data)
}

/**
 * 保存多个数据同步扩展函数（兼容旧版本）
 */
fun Context.saveDataAllSync(data: Map<String, Any>) {
    runBlocking {
        DataStoreManager.getInstance(this@saveDataAllSync).saveAll(data)
    }
}
