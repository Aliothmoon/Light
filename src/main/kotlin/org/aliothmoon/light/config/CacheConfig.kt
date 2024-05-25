package org.aliothmoon.light.config

import org.aliothmoon.light.config.CacheConfig.DBCacheManager.Companion.update
import org.aliothmoon.light.storage.COURSE_CACHE
import org.springframework.cache.Cache
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.concurrent.ConcurrentMapCache
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.ConcurrentMap

@Configuration
@EnableCaching
class CacheConfig {

    @Bean
    fun cacheManager(): DBCacheManager {
        return DBCacheManager()
    }

    class DBCacheManager : CacheManager {
        companion object {
            val cache = run {
                @Suppress("UNCHECKED_CAST")
                COURSE_CACHE as ConcurrentMap<String, Cache>
            }

            fun update(name: String, c: MapCache) {
                cache[name] = c
            }
        }

        override fun getCache(name: String): Cache? {
            return cache.computeIfAbsent(name) {
                MapCache(it)
            }
        }

        override fun getCacheNames(): MutableCollection<String> {
            return cache.keys
        }


    }


    data class MapCache(
        private val name: String,
    ) : ConcurrentMapCache(name) {
        override fun put(key: Any, value: Any?) {
            super.put(key, value)
            update(name, this)
        }

        override fun invalidate(): Boolean {
            return super.invalidate().also {
                update(name, this)
            }
        }

        override fun putIfAbsent(key: Any, value: Any?): Cache.ValueWrapper? {
            return super.putIfAbsent(key, value).also {
                update(name, this)
            }
        }

        override fun evict(key: Any) {
            super.evict(key)
            update(name, this)
        }

        override fun evictIfPresent(key: Any): Boolean {
            return super.evictIfPresent(key).also {
                update(name, this)
            }
        }

        override fun clear() {
            super.clear()
            update(name, this)
        }
    }
}