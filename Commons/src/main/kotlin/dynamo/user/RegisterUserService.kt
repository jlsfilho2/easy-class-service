package dynamo.user

interface RegisterUserService {
    fun saveUser(requestBody: String?, registerRole: String)
    fun getUserByRemoteId(userRemoteId: String): String
}