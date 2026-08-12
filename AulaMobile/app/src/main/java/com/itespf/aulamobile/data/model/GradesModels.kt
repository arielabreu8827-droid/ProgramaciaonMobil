package com.itespf.aulamobile.data.model

/** Tipos posibles de "assignment.type" según el enunciado del proyecto */
object AssignmentType {
    const val PRACTICE = "PRACTICE"
    const val HOMEWORK = "HOMEWORK"
    const val PROJECT = "PROJECT"
    const val EXPOSITION = "EXPOSITION"
}

/** Estados posibles de "attendance.status" */
object AttendanceStatus {
    const val PRESENT = "PRESENT"
    const val ABSENT = "ABSENT"
    const val LATE = "LATE"
    const val EXCUSED = "EXCUSED"
}

data class Assignment(
    val id: Int,
    val title: String,
    val description: String? = null,
    val dueDate: String? = null,
    val type: String,
    val weight: Double? = null
)

/** Versión reducida de assignment que viene embebida dentro de "submission" */
data class AssignmentMini(
    val id: Int,
    val title: String,
    val type: String
)

data class Submission(
    val assignmentId: Int,
    val grade: Double? = null,
    val feedback: String? = null,
    val assignment: AssignmentMini? = null
)

data class Exam(
    val id: Int,
    val title: String,
    val minutes: Int,
    val maxViolations: Int
)

/** Versión reducida de exam que viene embebida dentro de "examResult" */
data class ExamMini(
    val id: Int,
    val title: String
)

data class ExamResult(
    val id: Int,
    val examId: Int,
    val score: Double? = null,
    val correctCount: Int? = null,
    val total: Int? = null,
    val violations: Int? = null,
    val autoSubmitted: Boolean? = null,
    val createdAt: String? = null,
    val exam: ExamMini? = null
)

data class Lesson(
    val id: Int,
    val number: Int,
    val title: String
)

/** Versión reducida de lesson que viene embebida dentro de "attendance" */
data class LessonMini(
    val id: Int,
    val number: Int,
    val title: String,
    val unit: String? = null
)

data class Attendance(
    val id: Int,
    val lessonId: Int,
    val status: String,
    val excuseReason: String? = null,
    val excuseStatus: String? = null,
    val lesson: LessonMini? = null
)

data class CustomGrade(
    val id: Int,
    val title: String,
    val score: Double? = null
)

/** Respuesta completa de GET /api/v1/grades */
data class GradesResponse(
    val assignments: List<Assignment> = emptyList(),
    val submissions: List<Submission> = emptyList(),
    val exams: List<Exam> = emptyList(),
    val examResults: List<ExamResult> = emptyList(),
    val attendances: List<Attendance> = emptyList(),
    val lessons: List<Lesson> = emptyList(),
    val customGrades: List<CustomGrade> = emptyList()
)
