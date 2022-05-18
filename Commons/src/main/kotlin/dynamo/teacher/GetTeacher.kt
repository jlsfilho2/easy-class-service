package dynamo.teacher

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import dynamo.DynamoDBUtils
import models.teacher.TeacherDAO

fun getTeacher(teacherId: String?, subject: String?): String? {
    return if (teacherId != null && teacherId.isNotEmpty()) {
        getTeacherById(teacherId)
    } else if (subject != null && subject.isNotEmpty()) {
        getTeacherBySubject(subject)
    } else {
        getAllTeachers()
    }
}

private fun getTeacherById(teacherId: String): String? {
    val teacherDAO = DynamoDBUtils.mapper.load(TeacherDAO::class.java, teacherId)
//    val teacher = TeacherFactory.makeViewFromRawObject(teacherDAO)
    return DynamoDBUtils.objectMapper.writeValueAsString(teacherDAO)
}

private fun getTeacherBySubject(subject: String): String? {
    val eav = mapOf<String, AttributeValue>(
        ":subjects" to AttributeValue().withS(subject)
    )
    val scanExpression = DynamoDBScanExpression()
        .withFilterExpression("contains(subjects, :subjects)")
        .withExpressionAttributeValues(eav)
    val featuredTeachers = scanTeachers(scanExpression)
    return DynamoDBUtils.objectMapper.writeValueAsString(featuredTeachers)
}

private fun getAllTeachers(): String? {
    val scanExpression = DynamoDBScanExpression()
    val teachers = scanTeachers(scanExpression)
    return DynamoDBUtils.objectMapper.writeValueAsString(teachers)
}

private fun scanTeachers(scanExpression: DynamoDBScanExpression): PaginatedScanList<TeacherDAO>? {
    return DynamoDBUtils.mapper.scan(TeacherDAO::class.java, scanExpression)
}