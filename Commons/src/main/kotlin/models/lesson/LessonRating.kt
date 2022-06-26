package models.lesson

data class LessonRating(
    val lessonId: String,
    val studentId: String? = null,
    val teacherId: String? = null,
    val studentRating: Int? = null,
    val teacherRating: Int? = null,
    val teacherComments: String? = null,
    val studentComments: String? = null,
    val evaluatedBy: String,
)