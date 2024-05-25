package org.aliothmoon.light.request;

import okhttp3.Cookie
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

class MultiHostStore : ConcurrentHashMap<String, ConcurrentMap<CookieWrapper, CookieWrapper>>() {


    private fun add(host: String, wrapper: CookieWrapper) {
        computeIfAbsent(host) {
            ConcurrentHashMap()
        }[wrapper] = wrapper
    }

    fun append(host: String, cookies: List<Cookie>) {
        cookies.forEach {
            add(host, CookieWrapper(it))
        }
    }

    fun values(host: String): List<Cookie> {
        return computeIfAbsent(host) {
            ConcurrentHashMap()
        }.values.mapNotNull { it.cookie }
    }
}