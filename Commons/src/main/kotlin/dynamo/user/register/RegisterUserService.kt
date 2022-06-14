package dynamo.user.register

import models.auth.LoginRequest

interface RegisterUserService {
    fun saveUser(requestBody: String?, registerRole: String)
    fun getUser(loginRequest: LoginRequest): String
    fun getUserByRemoteId(userRemoteId: String): String
    fun getUserByEmail(email: String): String
    fun updateUserRemoteId(userRemoteId: String, email: String)
}