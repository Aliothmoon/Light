package org.aliothmoon.light.extract

import cn.hutool.extra.spring.SpringUtil
import org.aliothmoon.light.domain.Course
import org.slf4j.LoggerFactory
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor

private val executor = lazy(mode = LazyThreadSafetyMode.NONE) {
    SpringUtil.getBean("asyncExecutor", ThreadPoolTaskExecutor::class.java)
}
private val log = LoggerFactory.getLogger("InfoKt")

fun info(): List<Course> {
    val future = executor.value.submit(::jsonCourseInfoList)
    return ArrayList<Course>(courseInfoList()).also {
        it.addAll(future.get())
    }
}