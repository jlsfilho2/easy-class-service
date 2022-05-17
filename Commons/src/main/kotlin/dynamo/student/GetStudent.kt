package dynamo.student

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import dynamo.DynamoDBUtils
import models.lesson.Lesson
import models.student.Student

fun getAllStudents(): String? {
    val scanExpression = DynamoDBScanExpression()
    val students: List<Student> = DynamoDBUtils.mapper.scan(Student::class.java, scanExpression)
    return DynamoDBUtils.objectMapper.writeValueAsString(students)
}

fun getStudentById(teacherId: String): String? {
    val student = DynamoDBUtils.mapper.load(Student::class.java, teacherId)
    return DynamoDBUtils.objectMapper.writeValueAsString(student)
}

fun getStudents(ids: List<Lesson>): List<Student> {
    var studentIds : List<String> = ids.filter {it: Lesson ->
        it.studentId.isNullOrEmpty().not()
    }.map { it.studentId  }
    val eav: HashMap<String, AttributeValue> = HashMap<String, AttributeValue>()
    eav[":studentId"] = AttributeValue().withSS(studentIds)
    val scanExpression = DynamoDBScanExpression()
        .withFilterExpression("contains(studentId,:studentId)")
        .withExpressionAttributeValues(eav);
    val students: List<Student> = DynamoDBUtils.mapper.scan(Student::class.java, scanExpression)
    return students
}