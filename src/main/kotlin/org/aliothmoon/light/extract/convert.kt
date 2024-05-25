package org.aliothmoon.light.extract

import org.aliothmoon.light.domain.Course


fun toCourseList(json: List<Map<String, String>>): List<Course> {
    return json.map {
        Course().apply {
            name = it["jw_course_name"]
            teacher = it["base_teacher_name"]
            address = it["base_room_name"]
            week = it["week"]
            day = it["week_day"]
            section = run {
                "${it["section_start"]}-${it["section_end"]}"
            }
        }
    }
}

fun toCourse(info: List<String>): Course {
    val course = Course()
    for (i in 1 until info.size) {
        val s = info[i]
        when (i) {
            1 -> course.name = s
            2 -> {
                val wk = s.indexOf('周')
                val w = s.substring(0, wk)
                course.week = "$w-$w"
                course.day = getWeekByCN(s.substring(wk + 1, wk + 4))
                course.section = s.substring(wk + 4, s.length - 1)
            }

            3 -> course.address = s
            4 -> course.teacher = s
        }
    }
    return course
}

private fun getWeekByCN(str: String): String {
    return when (str) {
        "星期一" -> "1"
        "星期二" -> "2"
        "星期三" -> "3"
        "星期四" -> "5"
        "星期五" -> "5"
        "星期六" -> "6"
        "星期日" -> "7"
        else -> ""
    }
}