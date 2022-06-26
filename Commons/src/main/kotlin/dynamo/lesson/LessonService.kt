package dynamo.lesson

interface LessonService {
    fun getLessonById(lessonId: String): String?
    fun getLessonByTeacherId(teacherId: String): String?
    fun getLessonByStudentId(studentId: String): String?
    fun updateLessonStatus(requestBody: String)
    fun rateLesson(requestBody: String)
}