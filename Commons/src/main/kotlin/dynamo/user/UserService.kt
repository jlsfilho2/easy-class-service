package dynamo.user

interface UserService {

    fun getUser(role: String, subject: String, userId: String): String?
    fun getUserById(userId: String): String?
    fun getUserByRole(role: String): String?
    fun getUserBySubject(subject: String): String?
    fun getUsers(): String?

}