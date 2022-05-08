package getteacher

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import dynamo.DynamoDBUtils
import models.Teacher

fun getTeacher(teacherId: String?, subjectId: String?, isFeatured: Boolean): String? {
    return if (isFeatured) {
        getFeaturedTeacherBySubject(subjectId = subjectId)
    } else {
        if (teacherId != null && teacherId.isNotEmpty()) {
            getTeacherById(teacherId)
        } else if (subjectId != null && subjectId.isNotEmpty()) {
            getTeacherBySubject(subjectId)
        } else {
            getAllTeachers()
        }
    }
}

private fun getAllTeachers(): String? {
    val scanExpression = DynamoDBScanExpression()
    val teachers: List<Teacher> = DynamoDBUtils.mapper.scan(Teacher::class.java, scanExpression)
    return DynamoDBUtils.objectMapper.writeValueAsString(teachers)
}

private fun getTeacherById(teacherId: String): String? {
    val teacher = DynamoDBUtils.mapper.load(Teacher::class.java, teacherId)
    return DynamoDBUtils.objectMapper.writeValueAsString(teacher)
}

private fun getTeacherBySubject(subjectId: String): String? {
    val eav = mapOf<String, AttributeValue>(
        "subjectId" to AttributeValue().withN(subjectId),
        "teacher" to AttributeValue().withS("Teacher")
    )
    val scanExpression = DynamoDBScanExpression()
        .withFilterExpression("subjectId = $subjectId")
        .withExpressionAttributeValues(eav)
    val featuredTeachers: List<Teacher> = DynamoDBUtils.mapper.scan(Teacher::class.java, scanExpression)
    return DynamoDBUtils.objectMapper.writeValueAsString(featuredTeachers)
}

private fun getFeaturedTeacherBySubject(subjectId: String?): String? {
    val eav = mapOf<String, AttributeValue>(
        "subjectId" to AttributeValue().withN(subjectId),
        "teacher" to AttributeValue().withS("Teacher")
    )
    val scanExpression = DynamoDBScanExpression()
        .withFilterExpression("subjectId = $subjectId")
        .withExpressionAttributeValues(eav)
    val featuredTeachers: List<Teacher> = DynamoDBUtils.mapper.scan(Teacher::class.java, scanExpression).subList(0, 2)
    return DynamoDBUtils.objectMapper.writeValueAsString(featuredTeachers)
}