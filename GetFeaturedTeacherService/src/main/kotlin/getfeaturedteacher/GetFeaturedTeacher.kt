package getfeaturedteacher

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression
import dynamo.DynamoDBUtils
import models.Teacher

fun getFeaturedTeacher(subjectId: String): String? {
    return getFeaturedTeacherBySubject(subjectId)
}

private fun getFeaturedTeacherBySubject(subjectId: String): String? {
    val scanExpression = DynamoDBScanExpression()
        .withFilterExpression("subjectId = $subjectId")
    val featuredTeachers: List<Teacher> = DynamoDBUtils.mapper.scan(Teacher::class.java, scanExpression).subList(0, 2)
    return DynamoDBUtils.objectMapper.writeValueAsString(featuredTeachers)
}