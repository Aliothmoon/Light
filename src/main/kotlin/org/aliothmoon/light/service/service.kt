package org.aliothmoon.light.service

import org.aliothmoon.light.biz.StatusCode
import org.aliothmoon.light.biz.StatusCode.HAS_LOGIN
import org.aliothmoon.light.config.Context
import org.aliothmoon.light.domain.Course
import org.aliothmoon.light.domain.R
import org.aliothmoon.light.domain.User
import org.aliothmoon.light.extract.captcha
import org.aliothmoon.light.extract.info
import org.aliothmoon.light.extract.login
import org.aliothmoon.light.storage.COOKIE_STORE
import org.aliothmoon.light.storage.COURSE_CACHE
import org.aliothmoon.light.storage.SESSION_STORE
import org.aliothmoon.light.utils.getCurrentUserKey
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession

private val log = LoggerFactory.getLogger("Service")
const val STU = "STUN"

@Service
class Service(
    val session: HttpSession,
) {

    fun doLogin(user: User): R<Any> {
        val result = try {
            login(user).also {
                session.setAttribute(STU, user.account)
            }
        } catch (e: Exception) {
            session.invalidate()
            log.error("Login Fail", e)
            StatusCode.LOGIN_FAIL
        }
        return R<Any>().apply {
            code = result.code
            msg = result.description
        }
    }

    @CacheEvict(
        cacheNames = ["CourseInfo"],
        key = "#no",
    )
    fun doLogout(no: String?): R<Any> {
        val usk = getCurrentUserKey()
        COOKIE_STORE.remove(usk)
        SESSION_STORE.remove(Context.get())
        return R<Any>().apply {
            code = StatusCode.OK.code
        }
    }

    fun doFetchCaptcha(resp: HttpServletResponse) {
        return captcha(resp)
    }

    @Cacheable(
        cacheNames = ["CourseInfo"],
        key = "#no",
        unless = "#result == null || #result.code != 0"
    )
    fun doFetchCourseInfo(no: String?): R<List<Course>> {
        if (no == null) {
            return R<List<Course>>().apply {
                code = HAS_LOGIN.code
                msg = HAS_LOGIN.description
            }
        }
        val resp = try {
            info() to StatusCode.OK
        } catch (e: Exception) {
            emptyList<Course>() to HAS_LOGIN
        }
        return R<List<Course>>().apply {
            data = resp.first
            code = resp.second.code
            msg = resp.second.description
        }
    }
}