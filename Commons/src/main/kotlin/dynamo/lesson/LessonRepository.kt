package dynamo.lesson

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import com.amazonaws.services.dynamodbv2.model.AttributeValueUpdate
import com.amazonaws.services.dynamodbv2.model.UpdateItemRequest
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import dynamo.DynamoDBUtils
import dynamo.user.UserUseCase
import models.lesson.Lesson
import models.lesson.LessonRating
import models.lesson.LessonStatusUpdate
import models.user.User

class LessonRepository(private val mapper: ObjectMapper = jacksonObjectMapper()) : LessonService {

    override fun updateLessonStatus(requestBody: String) {
        val lessonStatusUpdate = mapper.readValue<LessonStatusUpdate>(requestBody)
        val updateLessonStatusRequest = makeUpdateLessonStatusRequest(lessonStatusUpdate)
        DynamoDBUtils.dynamoDB.updateItem(updateLessonStatusRequest)
    }

    override fun rateLesson(requestBody: String) {
        val lessonRating = mapper.readValue<LessonRating>(requestBody)
        val updateLessonRatingRequest = makeUpdateLessonRating(lessonRating)
        updateUserRating(lessonRating)
        DynamoDBUtils.dynamoDB.updateItem(updateLessonRatingRequest)
    }

    override fun getLessonById(lessonId: String): String? {
        val lesson = DynamoDBUtils.mapper.load(Lesson::class.java, lessonId)
        return DynamoDBUtils.objectMapper.writeValueAsString(lesson)
    }

    override fun getLessonByTeacherId(teacherId: String): String? {
        val eav = makeExpressionAttributeValueForTeacherId(teacherId)
        val scanExpression = DynamoDBScanExpression()
            .withFilterExpression("contains(teacherId, :teacherId)")
            .withExpressionAttributeValues(eav)
        val lessons: List<Lesson> = DynamoDBUtils.mapper.scan(Lesson::class.java, scanExpression)
        return DynamoDBUtils.objectMapper.writeValueAsString(lessons)
    }

    override fun getLessonByStudentId(studentId: String): String? {
        val eav = makeExpressionAttributeValueForStudentId(studentId)
        val scanExpression = DynamoDBScanExpression()
            .withFilterExpression("contains(studentId, :studentId)")
            .withExpressionAttributeValues(eav)
        val lessons: List<Lesson> = DynamoDBUtils.mapper.scan(Lesson::class.java, scanExpression)
        return DynamoDBUtils.objectMapper.writeValueAsString(lessons)
    }

    private fun makeExpressionAttributeValueForTeacherId(teacherId: String): Map<String, AttributeValue> {
        return mapOf<String, AttributeValue>(
            ":teacher" to AttributeValue().withS(teacherId)
        )
    }

    private fun makeExpressionAttributeValueForStudentId(studentId: String): Map<String, AttributeValue> {
        return mapOf<String, AttributeValue>(
            ":teacher" to AttributeValue().withS(studentId)
        )
    }

    private fun makeUpdateLessonStatusRequest(lessonStatusUpdate: LessonStatusUpdate): UpdateItemRequest {
        val tableName = "lesson"
        val itemKey = mapOf<String, AttributeValue>(
            "lessonId" to AttributeValue().withS(lessonStatusUpdate.lessonId)
        )
        val statusAttributeValue = AttributeValue().withS(lessonStatusUpdate.lessonStatus)
        val updatedValues = mutableMapOf<String, AttributeValueUpdate>(
            "status" to AttributeValueUpdate().withValue(statusAttributeValue)
        )

        return UpdateItemRequest(tableName, itemKey, updatedValues)
    }

    private fun makeUpdateLessonRating(lessonRating: LessonRating): UpdateItemRequest {
        return if (lessonRating.evaluatedBy == "TEACHER") {
            makeUpdateTeacherLessonRatingRequest(lessonRating)
        } else {
            makeUpdateStudentLessonRatingRequest(lessonRating)
        }
    }

    private fun makeUpdateStudentLessonRatingRequest(lessonRating: LessonRating): UpdateItemRequest {
        val tableName = "lesson"
        val itemKey = mapOf<String, AttributeValue>(
            "lessonId" to AttributeValue().withS(lessonRating.lessonId)
        )
        val ratingAttributeValue = AttributeValue().withN(lessonRating.teacherRating.toString())
        val commentAttributeValue = AttributeValue().withS(lessonRating.studentComments)
        val updatedValues = mutableMapOf<String, AttributeValueUpdate>(
            "teacherRating" to AttributeValueUpdate().withValue(ratingAttributeValue),
            "studentComments" to AttributeValueUpdate().withValue(commentAttributeValue)
        )

        return UpdateItemRequest(tableName, itemKey, updatedValues)
    }

    private fun makeUpdateTeacherLessonRatingRequest(lessonRating: LessonRating): UpdateItemRequest {
        val tableName = "lesson"
        val itemKey = mapOf<String, AttributeValue>(
            "lessonId" to AttributeValue().withS(lessonRating.lessonId)
        )
        val ratingAttributeValue = AttributeValue().withN(lessonRating.studentRating.toString())
        val commentAttributeValue = AttributeValue().withS(lessonRating.teacherComments)
        val updatedValues = mutableMapOf<String, AttributeValueUpdate>(
            "studentRating" to AttributeValueUpdate().withValue(ratingAttributeValue),
            "teacherComments" to AttributeValueUpdate().withValue(commentAttributeValue)
        )

        return UpdateItemRequest(tableName, itemKey, updatedValues)
    }

    private fun updateUserRating(lessonRating: LessonRating) {
        if (lessonRating.evaluatedBy == "TEACHER") {
            updateStudentRating(lessonRating)
        } else {
            updateTeacherRating(lessonRating)
        }
    }

    private fun updateStudentRating(lessonRating: LessonRating) {
        val studentJson = UserUseCase.getUserById(lessonRating.studentId.orEmpty())
        val student = mapper.readValue<User>(studentJson.orEmpty())
        val completedLessons = getCompletedLessons(
            userId = lessonRating.studentId.orEmpty(),
            userRole = "studentId"
        )
        val studentRating = lessonRating.studentRating ?: 0
        val newStudentRating = calcNewRating(
            actualRating = student.rating,
            newRating = studentRating,
            completedLessons = completedLessons.size
        )
        val studentRatingUpdateItemRequest = makeRatingUpdateItemRequest(
            userId = student.userId,
            rating = newStudentRating,
        )
        DynamoDBUtils.dynamoDB.updateItem(studentRatingUpdateItemRequest)
    }

    private fun updateTeacherRating(lessonRating: LessonRating) {
        val teacherJson = UserUseCase.getUserById(lessonRating.teacherId.orEmpty())
        val teacher = mapper.readValue<User>(teacherJson.orEmpty())
        val completedLessons = getCompletedLessons(
            userId = lessonRating.teacherId.orEmpty(),
            userRole = "teacherId"
        )
        val teacherRating = lessonRating.teacherRating ?: 0
        val newTeacherRating = calcNewRating(
            actualRating = teacher.rating,
            newRating = teacherRating,
            completedLessons = completedLessons.size
        )
        val teacherRatingUpdateItemRequest = makeRatingUpdateItemRequest(
            userId = teacher.userId,
            rating = newTeacherRating
        )
        DynamoDBUtils.dynamoDB.updateItem(teacherRatingUpdateItemRequest)
    }

    private fun getCompletedLessons(userId: String, userRole: String): List<Lesson?> {
        val eav = makeExpressionAttributeValueForCompletedLessonsById(userId = userId, userRole = userRole)
        val ean = makeExpressionAttributeNamesForCompletedLessons(userRole)
        val scanExpression = DynamoDBScanExpression()
            .withFilterExpression("#status = :status and #$userRole = :$userRole")
            .withExpressionAttributeValues(eav)
            .withExpressionAttributeNames(ean)
        return DynamoDBUtils.mapper.scan(Lesson::class.java, scanExpression)
    }

    private fun makeExpressionAttributeValueForCompletedLessonsById(userId: String, userRole: String): Map<String, AttributeValue> {
        return mapOf<String, AttributeValue>(
            ":$userRole" to AttributeValue().withS(userId),
            ":status" to AttributeValue().withS("CONCLUDED")
        )
    }

    private fun makeExpressionAttributeNamesForCompletedLessons(userRole: String): Map<String, String> {
        return mapOf(
            "#status" to "status",
            "#$userRole" to userRole
        )
    }

    private fun calcNewRating(actualRating: Int, newRating: Int, completedLessons: Int): Int {
        return (actualRating + newRating) / completedLessons
    }

    private fun makeRatingUpdateItemRequest(userId: String, rating: Int): UpdateItemRequest {
        val tableName = "user"
        val itemKey = mapOf<String, AttributeValue>(
            "userId" to AttributeValue().withS(userId)
        )
        val ratingAttributeValue = AttributeValue().withN(rating.toString())
        val updatedValues = mutableMapOf<String, AttributeValueUpdate>(
            "rating" to AttributeValueUpdate().withValue(ratingAttributeValue)
        )
        return UpdateItemRequest(tableName, itemKey, updatedValues)
    }
}