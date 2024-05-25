package org.aliothmoon.light.config

import com.alibaba.ttl.TransmittableThreadLocal

class Context {
    companion object {
        private val holder: TransmittableThreadLocal<String> = TransmittableThreadLocal<String>()


        fun get(): String? = holder.get()
        fun set(value: String) = holder.set(value)
        fun remove() = holder.remove()
    }
}