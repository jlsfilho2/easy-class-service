package models.teacher

import dynamo.lesson.getLessonById
import dynamo.lesson.scanLessonWithId
import models.lesson.Lesson

//object TeacherFactory {
//
//    fun makeViewFromRawObject(teacherDAO: TeacherDAO): Teacher {
//        return Teacher(
//            teacherId = teacherDAO.teacherId,
//            firstName = teacherDAO.firstName,
//            lastName = teacherDAO.lastName,
//            subjects = teacherDAO.subjects,
//            lessons = getTeacherLessons(teacherDAO.subjects),
//            rating = teacherDAO.rating,
//            hourlyPrice = teacherDAO.hourlyPrice,
//        )
//    }
//
//    private fun getTeacherLessons(lessonsStringList: List<String>): List<String> {
//        return lessonsStringList.map { lessonId ->
//            getLessonById(lessonId)
//        }
//    }
//}