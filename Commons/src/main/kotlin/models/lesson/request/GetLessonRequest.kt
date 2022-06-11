package models.lesson.request

data class GetLessonRequest(
    val studentId: String? = null,
    val teacherId: String? = null,
    val lessonRequestId: String? = null
)