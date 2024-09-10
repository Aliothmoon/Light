package org.aliothmoon.light.storage

import io.fury.Fury
import org.mapdb.DBMaker
import org.mapdb.DataInput2
import org.mapdb.DataOutput2
import org.mapdb.Serializer
import java.io.IOException
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit

private val FURY = Fury.builder()
    .requireClassRegistration(false)
    .withRefTracking(true)
    .buildThreadSafeFuryPool(2, 50)

val FURY_SERIALIZER: Serializer<Any?> = object : Serializer<Any?> {

    override fun serialize(out: DataOutput2, value: Any) {
        val buf = FURY.serialize(value)
        out.write(buf)
    }

    @Throws(IOException::class)
    override fun deserialize(input: DataInput2, available: Int): Any? {
        if (available < 0) {
            return null
        }
        val buf = ByteArray(available)
        input.readFully(buf)
        return FURY.deserialize(buf)
    }
}

val SCHEDULED_EXECUTOR = ScheduledThreadPoolExecutor(2)


const val DN = "info.db"
const val MK = "cookie"
const val SK = "session"

val DB = run {
    DBMaker.fileDB(DN)
        .fileMmapEnable()
        .closeOnJvmShutdown()
        .make()
}


val COOKIE_STORE = DB.hashMap(MK)
    .keySerializer(Serializer.STRING_ASCII)
    .valueSerializer(FURY_SERIALIZER)
    .expireExecutor(SCHEDULED_EXECUTOR)
    .expireAfterCreate(4, TimeUnit.HOURS)
    .createOrOpen()


val SESSION_STORE = DB.hashMap(SK)
    .keySerializer(Serializer.STRING_ASCII)
    .valueSerializer(FURY_SERIALIZER)
    .expireExecutor(SCHEDULED_EXECUTOR)
    .expireAfterCreate(4, TimeUnit.HOURS)
    .createOrOpen()




