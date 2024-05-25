package org.aliothmoon.light.utils

import okhttp3.FormBody
import okhttp3.Headers
import okhttp3.Request
import okhttp3.Response
import org.aliothmoon.light.config.Context
import org.aliothmoon.light.request.Client


private const val KEY_PREFIX = "UIN"


fun getCurrentUserKey(): String {
    return "${KEY_PREFIX}:${Context.get()}"
}

fun Map<String, String>.toForm(): FormBody = this.run {
    FormBody.Builder().also {
        forEach(it::add)
    }.build()
}

fun request() = Request.Builder().headers(VIS_HEADER)

fun Request.Builder.append(headers: Headers) = apply {
    headers.forEach { this.addHeader(it.first, it.second) }
}

fun Request.Builder.execute(): Response {
    return Client.CLIENT.newCall(build()).execute()
}

fun Request.Builder.string(): String {
    return Client.CLIENT.newCall(build()).execute().use {
        it.body.string()
    }
}

fun client() {
    Client.CLIENT
}