package org.aliothmoon.light.request

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import org.aliothmoon.light.storage.COOKIE_STORE
import org.aliothmoon.light.utils.getCurrentUserKey
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentMap

@Component
class CookieStore : CookieJar {
    private val log = LoggerFactory.getLogger(CookieStore::class.java)
    val map: ConcurrentMap<String, MultiHostStore> = run {
//        ConcurrentHashMap()
        @Suppress("UNCHECKED_CAST")
        COOKIE_STORE as ConcurrentMap<String, MultiHostStore>
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val userKey = getCurrentUserKey()
        return (map[userKey]?.values(url.host) ?: emptyList()).apply {
            log.debug("loadForRequest {} {} {}", userKey, url.host, this)
        }
    }

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        val userKey = getCurrentUserKey()
        log.debug("saveFromResponse {} {} {}", userKey, url.host, cookies)
        val oVal = map.computeIfAbsent(userKey) {
            MultiHostStore()
        }
        map[userKey] = oVal.apply {
            append(url.host, cookies)
        }
    }
}