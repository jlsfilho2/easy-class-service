package models.lesson

data class LessonStatusUpdate(
    var lessonId: String = "",
    var lessonStatus: String = "",
    val userId: String,
)