package dynamo.lesson.request

import ext.isNotNull

class LessonRequestInteractor(
    private val repository: LessonRequestRepository = LessonRequestRepository()
) {

    fun doGetLessonRequest(lessonRequestId: String?, teacherId: String?, studentId: String?): String? {
        println("Is inside interactor method")
        return when {
            !lessonRequestId.isNullOrBlank() -> {
                println("Lesson request id is not null $lessonRequestId")
                repository.getLessonRequestById(lessonRequestId)
            }
            !teacherId.isNullOrBlank() -> {
                println("Teacher id is not null $teacherId")
                repository.getLessonRequestByTeacherId(teacherId)
            }
            !studentId.isNullOrBlank() -> {
                println("Student id is not null $studentId")
                repository.getLessonRequestByStudentId(studentId)
            }
            else -> {
                NotImplementedError().toString()
            }
        }
    }

}