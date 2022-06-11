package dynamo.lesson.request

class LessonRequestInteractor(
    private val repository: LessonRequestRepository = LessonRequestRepository()
) {

    fun doGetLessonRequest(lessonRequestId: String?, teacherId: String?, studentId: String?): String? {
        return when {
            lessonRequestId != null -> {
                println(lessonRequestId)
                repository.getLessonRequestById(lessonRequestId)
            }
            teacherId != null -> {
                println(teacherId)
                repository.getLessonRequestByTeacherId(teacherId)
            }
            studentId != null -> {
                println(studentId)
                repository.getLessonRequestByStudentId(studentId)
            }
            else -> {
                NotImplementedError().toString()
            }
        }
    }

}