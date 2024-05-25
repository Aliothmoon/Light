package org.aliothmoon.light.extract

import com.alibaba.fastjson2.JSON
import com.alibaba.fastjson2.TypeReference
import okio.buffer
import okio.sink
import okio.source
import org.aliothmoon.light.biz.StatusCode
import org.aliothmoon.light.biz.StatusCode.LOGIN_SUCCESS
import org.aliothmoon.light.domain.User
import org.aliothmoon.light.request.Client.Companion.CLIENT
import org.aliothmoon.light.security.Security.Companion.encrypt
import org.aliothmoon.light.utils.*
import org.jsoup.Jsoup
import org.slf4j.LoggerFactory
import javax.servlet.http.HttpServletResponse

private val log = LoggerFactory.getLogger("SecurityKt")


fun fetchKey(): Pair<String, String> {
    val request = request().get().url(KEY_URL).build()
    return CLIENT.newCall(request).execute().use {
        it.body.string()
    }.let {
        val map = JSON.parseObject(it, object : TypeReference<Map<String, String>>() {})
        (map["exponent"] ?: "") to (map["modulus"] ?: "")
    }
}

fun fetchExecution(): String {
    val content = request().get().url(LOGIN_URL).string()
    return try {
        val parse = Jsoup.parse(content)
        val formCont = parse.getElementsByAttributeValue("name", "execution")
        formCont.first()?.attr("value") ?: "e1s1"
    } catch (e: Exception) {
        "e1s1"
    }
}

fun captcha(resp: HttpServletResponse) {
    request().apply {
        get()
        url(CPT_URL)
    }.execute().apply {
        resp.outputStream.sink().buffer().use {
            it.writeAll(body.byteStream().source())
        }
    }
}

fun login(user: User): StatusCode {
    val (e, m) = fetchKey()

    val pwd = encrypt(user.password, e, m)

    val request = mapOf(
        "execution" to "e1s1",
        "_eventId" to "submit",
        "geolocation" to "",
        "username" to user.account,
        "lm" to "usernameLogin",
        "password" to pwd,
        "captcha" to user.captcha
    ).toForm()


    return request().apply {
        append(FORM_HEADER)
        url(LOGIN_URL)
        post(request)
    }.execute().use {
        val content = it.body.string()
        if (it.isSuccessful && content.contains(SUCCESS_KEY_WORD)) {
            log.info("Login Success")
            load()
            LOGIN_SUCCESS
        } else {
            log.warn("Check Your Password or Captcha")
            StatusCode.LOGIN_FAIL
        }
    }
}

