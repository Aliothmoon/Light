package org.aliothmoon.light.domain

data class Course(
    var name: String?,
    var teacher: String?,
    var address: String?,
    var week: String?,
    var section: String?,
    var day: String?,
) {
    constructor() : this("", "", "", "", "", "")
}