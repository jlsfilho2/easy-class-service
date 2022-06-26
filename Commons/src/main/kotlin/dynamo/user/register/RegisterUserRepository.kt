package dynamo.user.register

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList
import com.amazonaws.services.dynamodbv2.model.AttributeValue
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

    override fun getUserByRemoteId(userRemoteId: String): String {
        val eav = mapOf<String, AttributeValue>(
            ":userRemoteId" to AttributeValue().withS(userRemoteId)
        )
        val scanExpression = DynamoDBScanExpression()
            .withFilterExpression("contains(userRemoteId, :userRemoteId)")
            .withExpressionAttributeValues(eav)
        val user = scanUser(scanExpression)
        return DynamoDBUtils.objectMapper.writeValueAsString(user)
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

    private fun scanUser(scanExpression: DynamoDBScanExpression): PaginatedScanList<User>? {
        return DynamoDBUtils.mapper.scan(User::class.java, scanExpression)
    }

}