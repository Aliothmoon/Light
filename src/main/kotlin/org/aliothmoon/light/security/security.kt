package org.aliothmoon.light.security

import org.aliothmoon.light.security.Security.Companion.CTX
import org.aliothmoon.light.security.Security.Companion.SOURCE_CODE
import org.apache.commons.pool2.BasePooledObjectFactory
import org.apache.commons.pool2.PooledObject
import org.apache.commons.pool2.impl.AbandonedConfig
import org.apache.commons.pool2.impl.DefaultPooledObject
import org.apache.commons.pool2.impl.GenericObjectPool
import org.apache.commons.pool2.impl.GenericObjectPoolConfig
import org.graalvm.polyglot.Context
import org.graalvm.polyglot.HostAccess
import org.graalvm.polyglot.Source
import org.graalvm.polyglot.Value
import org.springframework.core.io.ClassPathResource
import java.time.Duration


class Security {
    companion object {
        internal val SOURCE_CODE: String = ClassPathResource("index.js").inputStream
            .use {
                it.bufferedReader().readText()
            }
        val CTX = Context.newBuilder("js")
            .allowHostAccess(HostAccess.ALL)
            .allowHostClassLookup { true }
            .allowCreateProcess(true)
            .err(System.err)
            .option("js.ecmascript-version", "2022")
            .build()!!
        private val pool = encryptObjectPool()


        fun encrypt(pwd: String, exponent: String, modulus: String): String {
            var exec: Value? = null
            try {
                exec = pool.borrowObject()
                return exec.execute(pwd, exponent, modulus).asString()
            } finally {
                pool.returnObject(exec)
            }
        }


        private fun encryptObjectPool(): GenericObjectPool<Value> {
            val poolConfig: GenericObjectPoolConfig<Value> = GenericObjectPoolConfig<Value>()
            poolConfig.setEvictionPolicy { _, _, _ -> true }
            poolConfig.blockWhenExhausted = true
            poolConfig.jmxEnabled = false
            poolConfig.timeBetweenEvictionRuns = Duration.ofMillis((1000 * 60))
            poolConfig.minEvictableIdleDuration = Duration.ofMinutes(5)
            poolConfig.testWhileIdle = true
            poolConfig.testOnReturn = true
            poolConfig.testOnBorrow = true
            poolConfig.maxTotal = 20
            // 设置抛弃策略
            val abandonedConfig = AbandonedConfig()
            abandonedConfig.removeAbandonedOnMaintenance = true
            abandonedConfig.removeAbandonedOnBorrow = true
            return GenericObjectPool(EvalValueObjectFactory(), poolConfig, abandonedConfig)
        }
    }
}

class EvalValueObjectFactory : BasePooledObjectFactory<Value>() {
    override fun create(): Value {
        return CTX.eval(Source.create("js", SOURCE_CODE))
    }

    override fun wrap(v: Value?): PooledObject<Value> {
        return DefaultPooledObject(v)
    }
}
