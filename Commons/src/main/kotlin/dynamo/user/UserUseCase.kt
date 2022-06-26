package dynamo.user

import dynamo.DynamoDBUtils
import models.user.User

object UserUseCase {

    fun getUserById(userId: String): String? {
        val teacher = DynamoDBUtils.mapper.load(User::class.java, userId)
        return DynamoDBUtils.objectMapper.writeValueAsString(teacher)
    }

}