package models.lesson

data class Evaluation(
    val lessonId: String,
    val comments: String? = null,
    val rating: Int? = null,
)
