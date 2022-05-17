package dynamo.user

import dynamo.DynamoDBUtils
import models.user.Role
import models.user.User
import requests.generateUUID

class RegisterUserRepository : RegisterUserService {
    override fun saveUser(
        requestBody: String?,
        registerRole: String
    ) {
        val user = DynamoDBUtils.objectMapper.readValue(requestBody, User::class.java)
        if (registerRole == "REGISTER_STUDENT") {
            saveStudent(user)
        } else {
            saveTeacher(user)
        }
    }

    private fun saveStudent(user: User) {
        user.userId = generateUUID()
        user.role = Role.STUDENT.name
        DynamoDBUtils.mapper.save(user)
    }

    private fun saveTeacher(user: User) {
        user.userId = generateUUID()
        user.role = Role.TEACHER.name
        DynamoDBUtils.mapper.save(user)
    }
}