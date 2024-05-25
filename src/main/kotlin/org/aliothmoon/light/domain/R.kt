package org.aliothmoon.light.domain

class R<T>(
    var code: Int?,
    var msg: String?,
    var data: T?,
) {
    constructor() : this(null, null, null)

}