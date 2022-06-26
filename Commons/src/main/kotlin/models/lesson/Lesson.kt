package models.lesson

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAutoGeneratedKey
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable
import models.lesson.request.LessonRequest

@DynamoDBTable(tableName = "lesson")
data class Lesson(
    @DynamoDBAutoGeneratedKey
    @DynamoDBHashKey(attributeName = "lessonId")
    var lessonId: String = "",
    @DynamoDBAttribute(attributeName = "subjectId")
    var subject: String,
    @DynamoDBAttribute(attributeName = "studentId")
    var studentId: String,
    @DynamoDBAttribute(attributeName = "teacherId")
    var teacherId: String,
    @DynamoDBAttribute(attributeName = "price")
    var price: Int = 0,
    @DynamoDBAttribute(attributeName = "status")
    var status: String,
    @DynamoDBAttribute(attributeName = "rating")
    var rating: Int = 0,
    @DynamoDBAttribute(attributeName = "startDate")
    var startDate: String = "",
    @DynamoDBAttribute(attributeName = "endDate")
    var endDate: String = "",
    @DynamoDBAttribute(attributeName = "teacherEvaluation")
    var teacherEvaluation: String? = null,
    @DynamoDBAttribute(attributeName = "studentEvaluation")
    var studentEvaluation: String? = null,
) {
    companion object {
        fun makeLessonFromLessonRequest(lessonRequest: LessonRequest): Lesson {
            return Lesson(
                lessonId = lessonRequest.lessonRequestId,
                subject = lessonRequest.subject,
                studentId = lessonRequest.studentId,
                teacherId = lessonRequest.teacherId,
                price = lessonRequest.hourlyPrice,
                status = lessonRequest.status,
                rating = 0,
                startDate = lessonRequest.startDate,
                endDate = lessonRequest.endDate
            )
        }
    }
}