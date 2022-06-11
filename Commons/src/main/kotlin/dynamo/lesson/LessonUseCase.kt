package dynamo.lesson

import dynamo.DynamoDBUtils
import models.lesson.Lesson
import models.lesson.request.LessonRequest
import models.lesson.response.LessonRequestResponse

object LessonUseCase {

    fun createLesson(lessonRequestResponse: LessonRequestResponse) {
        val lessonRequest = getLessonRequest(lessonRequestResponse.lessonRequestId)
        val lesson = Lesson.makeLessonFromLessonRequest(lessonRequest)
        saveLesson(lesson)
    }

    fun saveLesson(lesson: Lesson) {
        DynamoDBUtils.mapper.save(lesson)
    }

    private fun getLessonRequest(lessonRequestId: String): LessonRequest {
        return DynamoDBUtils.mapper.load(LessonRequest::class.java, lessonRequestId)
    }

    fun scanLessonWithId(lessonId: String): Lesson? {
        return DynamoDBUtils.mapper.load(Lesson::class.java, lessonId)
    }
}