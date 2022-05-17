package dynamo.lesson

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import dynamo.DynamoDBUtils
import models.lesson.Lesson

fun getLessonByTeacherId(teacherId: String?): String? {
    val eav = mapOf<String, AttributeValue> (
        ":teacher" to AttributeValue().withS(teacherId)
    )

    val scanExpression = DynamoDBScanExpression()
        .withFilterExpression("contains(teacher, :teacher)")
        .withExpressionAttributeValues(eav)
    val lessons: List<Lesson> = DynamoDBUtils.mapper.scan(Lesson::class.java, scanExpression)
    return DynamoDBUtils.objectMapper.writeValueAsString(lessons)
}

fun getLessonById(lessonId: String?): String {
    val lesson = scanLessonWithId(lessonId)
    return DynamoDBUtils.objectMapper.writeValueAsString(lesson)
}

fun scanLessonWithId(lessonId: String?): Lesson? {
    return DynamoDBUtils.mapper.load(Lesson::class.java, lessonId)
}