package dynamo.user.register

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import com.amazonaws.services.dynamodbv2.model.AttributeValueUpdate
import com.amazonaws.services.dynamodbv2.model.UpdateItemRequest
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import dynamo.DynamoDBUtils
import ext.isNotNull
import models.auth.LoginRequest
import models.user.Role
import models.user.User
import requests.generateUUID

class RegisterUserRepository(private val mapper: ObjectMapper = jacksonObjectMapper()) : RegisterUserService {

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

    override fun getUser(loginRequest: LoginRequest): String {
        var user: String
        if (loginRequest.userRemoteId != null) {
            user = getUserByEmail(loginRequest.email.orEmpty())
            val parsedUserJson = mapper.readValue<User>(user)
            if (parsedUserJson.userRemoteId.isEmpty()) {
                updateUserRemoteId(
                    userRemoteId = loginRequest.userRemoteId.orEmpty(),
                    email = loginRequest.email.orEmpty()
                )
                user = getUserByRemoteId(loginRequest.userRemoteId.orEmpty())
            }
        } else {
            user = getUserByEmail(loginRequest.email.orEmpty())
        }

        return user
    }

    override fun getUserByEmail(email: String): String {
        val eav = mapOf<String, AttributeValue>(
            ":email" to AttributeValue().withS(email)
        )
        val scanExpression = DynamoDBScanExpression()
            .withFilterExpression("userRemoteId = :userRemoteId)")
            .withExpressionAttributeValues(eav)
        val user = scanUser(scanExpression)
        return DynamoDBUtils.objectMapper.writeValueAsString(user)
    }

    override fun updateUserRemoteId(userRemoteId: String, email: String) {
        val updateItemRequest = makeUpdateUserRemoteId(userRemoteId, email)
        DynamoDBUtils.dynamoDB.updateItem(updateItemRequest)
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

    private fun makeUpdateUserRemoteId(userRemoteId: String, email: String): UpdateItemRequest {
        val tableName = "user"
        val itemKey = mapOf<String, AttributeValue>(
            "email" to AttributeValue().withS(email)
        )
        val userRemoteIdAttributeValue = AttributeValue().withS(userRemoteId)
        val updatedValues = mutableMapOf<String, AttributeValueUpdate>(
            "userRemoteId" to AttributeValueUpdate().withValue(userRemoteIdAttributeValue)
        )
        return UpdateItemRequest(tableName, itemKey, updatedValues)
    }
}