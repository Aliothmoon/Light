package org.aliothmoon.light.storage

import org.mapdb.Serializer
import java.util.concurrent.TimeUnit

val COURSE_CACHE = DB.hashMap(MK)
    .keySerializer(Serializer.STRING_ASCII)
    .valueSerializer(FURY_SERIALIZER)
    .expireExecutor(SCHEDULED_EXECUTOR)
    .expireAfterCreate(4, TimeUnit.HOURS)
    .createOrOpen()