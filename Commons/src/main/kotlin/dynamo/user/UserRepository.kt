package dynamo.user

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import dynamo.DynamoDBUtils
import models.user.User

class UserRepository : UserService {

    override fun getUser(role: String, subject: String, userId: String): String? {
        return if (userId.isNotEmpty()) {
            getUserById(userId)
        } else if (role.isNotEmpty()) {
            getUserByRole(role)
        } else if (subject.isNotEmpty()) {
            getUserBySubject(subject)
        } else {
            getUsers()
        }
    }

    override fun getUserById(userId: String): String? {
        val teacher = DynamoDBUtils.mapper.load(User::class.java, userId)
        return DynamoDBUtils.objectMapper.writeValueAsString(teacher)
    }

    override fun getUserByRole(role: String): String? {
        val eav = mapOf<String, AttributeValue>(
            ":role" to AttributeValue().withS(role)
        )
        val ean = mapOf<String, String>(
            "#role" to "role"
        )
        val scanExpression = DynamoDBScanExpression()
            .withFilterExpression("#role = :role")
            .withExpressionAttributeValues(eav)
            .withExpressionAttributeNames(ean)
        val usersByRole = scanUserWithScanExpression(scanExpression)
        return DynamoDBUtils.objectMapper.writeValueAsString(usersByRole)
    }

    override fun getUserBySubject(subject: String): String? {
        val eav = mapOf<String, AttributeValue>(
            ":subject" to AttributeValue().withS(subject)
        )
        val scanExpression = DynamoDBScanExpression()
            .withFilterExpression("contains(subject, :subject)")
            .withExpressionAttributeValues(eav)
        val usersBySubject = scanUserWithScanExpression(scanExpression)
        return DynamoDBUtils.objectMapper.writeValueAsString(usersBySubject)
    }

    override fun getUsers(): String? {
        val users = scanUserWithScanExpression()
        return DynamoDBUtils.objectMapper.writeValueAsString(users)
    }

    private fun scanUserWithScanExpression(scanExpression: DynamoDBScanExpression = DynamoDBScanExpression()): PaginatedScanList<User>? {
        return DynamoDBUtils.mapper.scan(User::class.java, scanExpression)
    }
}