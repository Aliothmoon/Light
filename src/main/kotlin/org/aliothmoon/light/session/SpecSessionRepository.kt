package org.aliothmoon.light.session

import cn.hutool.core.lang.UUID
import org.aliothmoon.light.storage.SESSION_STORE
import org.springframework.context.annotation.Configuration
import org.springframework.session.MapSession
import org.springframework.session.SessionRepository
import org.springframework.session.config.annotation.web.http.EnableSpringHttpSession
import java.util.concurrent.ConcurrentMap

@Configuration
@EnableSpringHttpSession
class SpecSessionRepository : SessionRepository<MapSession> {
    private val map: ConcurrentMap<String, MapSession?> = run {
        @Suppress("UNCHECKED_CAST")
        SESSION_STORE as ConcurrentMap<String, MapSession?>
    }

    override fun createSession(): MapSession {
        val session = MapSession(UUID.fastUUID().toString(true))
        save(session)
        return session
    }

    override fun findById(id: String?): MapSession? {
        return map[id]
    }

    override fun deleteById(id: String?) {
        map.remove(id)
    }

    override fun save(session: MapSession?) {
        map[session?.id] = session
    }
}