package org.aliothmoon.light.security

import org.graalvm.polyglot.Context
import org.graalvm.polyglot.HostAccess
import org.graalvm.polyglot.Source
import org.graalvm.polyglot.Value
import org.springframework.core.io.ClassPathResource

class Security {
    companion object {
        private val Encrypt: Value

        init {
            ClassPathResource("index.js").inputStream
                .use {
                    Encrypt = Context.newBuilder("js")
                        .allowHostAccess(HostAccess.ALL)
                        .allowHostClassLookup { true }
                        .allowCreateProcess(true)
                        .err(System.err)
                        .option("js.ecmascript-version", "2022")
                        .build()
                        .eval(Source.create("js", it.bufferedReader().readText()))
                }
        }

        fun encrypt(pwd: String, exponent: String, modulus: String): String {
            return Encrypt.execute(pwd, exponent, modulus)
                .asString()
        }
    }
}