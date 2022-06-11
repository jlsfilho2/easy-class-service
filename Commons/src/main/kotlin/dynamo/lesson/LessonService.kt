package dynamo.lesson

interface LessonService {
    fun getLessonByTeacherId(teacherId: String): String?
}