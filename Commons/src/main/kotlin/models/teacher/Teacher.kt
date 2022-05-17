package models.teacher

import models.user.User

class Teacher(
    var teacherId: String = "",
    var subjects: List<String> = listOf(),
    var hourlyPrice: Int = 0,
) : User()