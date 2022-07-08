package dynamo.user.register

interface RegisterUserService {
    fun saveUser(requestBody: String?, registerRole: String)
    fun getUserByRemoteId(userRemoteId: String): String
}