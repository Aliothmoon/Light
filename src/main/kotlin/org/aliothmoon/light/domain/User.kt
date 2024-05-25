package org.aliothmoon.light.domain

data class User(
    var account: String,
    var password: String,
    var captcha: String,
) {
    constructor() : this("", "", "")
}
