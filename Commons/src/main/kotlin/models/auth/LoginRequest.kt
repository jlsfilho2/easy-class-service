package models.auth

data class LoginRequest(
    var userRemoteId: String? = null,
    var email: String? = null
)