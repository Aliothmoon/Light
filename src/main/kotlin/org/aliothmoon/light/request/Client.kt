package org.aliothmoon.light.request

import okhttp3.CookieJar
import okhttp3.OkHttpClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration

@Configuration
class Client {

    companion object {
        lateinit var CLIENT: OkHttpClient
    }

    @Bean
    fun okHttpClient(store: CookieJar): OkHttpClient {
        return OkHttpClient.Builder()
            .cookieJar(store)
            .callTimeout(Duration.ofSeconds(30))
            .build().apply {
                CLIENT = this;
            }
    }

}