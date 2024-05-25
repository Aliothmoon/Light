package org.aliothmoon.light.biz

// 认证相关

enum class StatusCode(
    val code: Int,
    val description: String,
) {
    OK(0, "OK"),
    LOGIN_FAIL(1500, "Login Failure"),
    REMOTE_SERVICE_ERROR(1502, "Remote Service Error"),
    LOGIN_SUCCESS(1200, "Login Success"),
    LOGOUT_SUCCESS(2200, "Logout Success"),
    HAS_LOGIN(3200, "Has Login"),
    LOGOUT(3401, "Logout"),
    OUT_OF_CREDIT(3403, "Out of Credit"),
    TIMEOUT(5501, "Timeout"),
    SERVER_CLOSE(5502, "Server Close");
}