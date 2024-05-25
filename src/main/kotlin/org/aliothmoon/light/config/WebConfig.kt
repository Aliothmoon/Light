package org.aliothmoon.light.config

import org.aliothmoon.light.config.Context.Companion.remove
import org.aliothmoon.light.config.Context.Companion.set
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Configuration
class WebConfig : WebMvcConfigurer {


    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(object : HandlerInterceptor {
            override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
                set(request.session.id)
                return true
            }

            override fun afterCompletion(
                request: HttpServletRequest,
                response: HttpServletResponse,
                handler: Any,
                ex: Exception?,
            ) {
                remove()
            }
        }).addPathPatterns("/**")
    }
}