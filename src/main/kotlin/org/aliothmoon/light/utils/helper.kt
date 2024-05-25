package org.aliothmoon.light.utils

import okhttp3.Headers

val VIS_HEADER = run {
    mapOf(
        "User-Agent" to "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36",
    ).let {
        Headers.Builder().apply {
            it.forEach(this::add)
        }.build()
    }
}

val FORM_HEADER = run {
    VIS_HEADER.newBuilder()
        .add("content-type", "application/x-www-form-urlencoded; charset=UTF-8")
        .build()
}

val JSON_HEADER = run {
    VIS_HEADER.newBuilder()
        .add("content-type", "application/json")
        .build()
}

val SOA_REFER_HEADER = run {
    VIS_HEADER.newBuilder()
        .add("Referer", "http://soa.swust.edu.cn/")
        .build()
}

fun load() {
//    visitES()
//    visitSOA()
    visitAexp()
}


fun visitES() {
    request()
        .get()
        .url(ES_URL)
        .headers(SOA_REFER_HEADER)
        .string()

}

fun visitSOA() {
    request()
        .get()
        .url(SOA_URL)
        .string()
}

fun visitAexp() {
    request()
        .addHeader("Referer", "http://sjjx.swust.edu.cn/aexp/stuLeft.jsp")
        .addHeader("Host", "sjjx.swust.edu.cn")
        .get()
        .url(SJ_TEACHN_ACTION_URL)
        .string()

}