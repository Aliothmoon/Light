package org.aliothmoon.light.extract

import cn.hutool.extra.spring.SpringUtil
import com.alibaba.fastjson2.JSON
import com.alibaba.fastjson2.TypeReference
import okhttp3.Response
import org.aliothmoon.light.domain.Course
import org.aliothmoon.light.utils.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.slf4j.LoggerFactory
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.concurrent.Callable
import java.util.regex.Pattern
import java.util.stream.Collectors
import java.util.stream.Stream

private val log = LoggerFactory.getLogger("ParserKt")
private val executor = lazy(mode = LazyThreadSafetyMode.NONE) {
    SpringUtil.getBean("asyncExecutor", ThreadPoolTaskExecutor::class.java)
}
val DIGIT = Pattern.compile("\\d+")!!


private fun pretreatment(): Triple<Int, String, List<List<String>>> {

    val html = request4Experiment(SJ_TEACHN_ACTION_URL).use { it.body.string() }
    val root = Jsoup.parse(html)

    val doc = root.select(".tablelist > tbody ").last()!!
    val children = doc.children()
//  If need
//        val tags = children.first()!!.children().take(5).map { it.text() }.toString()
    val content = when {
        children.size > 1 -> {
            children.stream().skip(1).map { h ->
                h.children().take(5).map { it.text() }
            }.collect(Collectors.toList())
        }

        else -> {
            emptyList()
        }
    }


    val parent = root.select("#myPage ul li")

    val href = parent.select("a").first()?.attr("href") ?: ""
    val p = parent.select("p").text().split("/").last()

    val digit = DIGIT.matcher(p)
    val num = when {
        digit.find() -> digit.group().trim().toInt()
        else -> 1
    }
    return Triple(num, href, content)
}

private fun request4Experiment(url: String): Response {
    return request()
        .apply {
            addHeader("Referer", "http://sjjx.swust.edu.cn/aexp/stuLeft.jsp")
            url(url)
        }
        .execute()

}

private fun content(document: Document): List<List<String>> {
    val doc = document.select(".tablelist > tbody ").last()!!
    val children = doc.children()
    if (children.size < 2) return emptyList()
    return children.stream().skip(1).map { h ->
        h.children().take(5).map { it.text() }
    }.collect(Collectors.toList())
}


/**
 * 实践教学JSON课程信息列表
 * @return [List<Course>]
 */
fun jsonCourseInfoList(): List<Course> {

    val (num, href, list) = pretreatment()
    if (num < 2) {
        return emptyList()
    }
    val origin = list.stream()

    val surplus = (2..num).map {
        "$SJJX_ORIGIN${href.replace("page.pageNum=1", "page.pageNum=${it}")}"
    }.map {
        executor.value.submit(Callable {
            log.info("Submit")
            request4Experiment(it).use { r -> r.body.string() }
        })
    }.map {
        val c = it.get()
        log.info("Accept")
        content(Jsoup.parse(c)).stream()
    }.reduce { acc, s ->
        Stream.concat(acc, s)
    }
    return Stream.concat(origin, surplus)
        .map(::toCourse)
        .collect(Collectors.toList())
}

/**
 * 实践教学课程信息列表
 * @return [List<Course>]
 */
fun courseInfoList(): List<Course> {

    val form = mapOf("op" to "getJwTimeTable").toForm()

    val json = request().apply {
        addHeader("Host", "sjjx.swust.edu.cn")
        url(JW_COURSE_URL)
        post(form)
    }.string()
    if (json.startsWith("<")) {
        throw RuntimeException("登录失败")
    }
    val list = JSON.parseObject(json, object : TypeReference<List<Map<String, String>>>() {})

    return toCourseList(list)
}