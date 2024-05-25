package org.aliothmoon.light.web

import org.aliothmoon.light.domain.Course
import org.aliothmoon.light.domain.R
import org.aliothmoon.light.domain.User
import org.aliothmoon.light.service.STU
import org.aliothmoon.light.service.Service
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.SessionAttribute
import javax.servlet.http.HttpServletResponse

@RestController
class BasicController(
    val service: Service,
) {

    /**
     * 验证码
     * @param [resp] 回复
     */
    @RequestMapping("/captcha")
    fun captcha(resp: HttpServletResponse) {
        service.doFetchCaptcha(resp)
    }

    /**
     * 登录
     * @param [user]
     */
    @RequestMapping("/login")
    fun login(user: User): R<Any> {
        return service.doLogin(user)
    }

    @RequestMapping("/logout")
    fun logout(
        @SessionAttribute(STU, required = false)
        no: String?,
    ): R<Any> {
        return service.doLogout(no)
    }

    /**
     * 信息
     * @return [List<Course>]
     */
    @RequestMapping("/info")
    fun info(
        @SessionAttribute(STU, required = false)
        no: String?,
    ): R<List<Course>> {
        return service.doFetchCourseInfo(no)
    }
}