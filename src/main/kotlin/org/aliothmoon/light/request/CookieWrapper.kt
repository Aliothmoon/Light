package org.aliothmoon.light.request

import okhttp3.Cookie
import java.util.*


class CookieWrapper(
    val cookie: Cookie,
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CookieWrapper) return false

        return cookie.name == other.cookie.name
                && cookie.domain == other.cookie.domain
                && cookie.path == other.cookie.path
    }

    override fun hashCode(): Int {
        var result = Objects.hashCode(cookie.name)
        result = 31 * result + Objects.hashCode(cookie.domain)
        result = 31 * result + Objects.hashCode(cookie.path)
        return result
    }
}