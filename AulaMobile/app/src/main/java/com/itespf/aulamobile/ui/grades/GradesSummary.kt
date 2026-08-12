package com.itespf.aulamobile.ui.grades

import com.itespf.aulamobile.data.model.Assignment
import com.itespf.aulamobile.data.model.CustomGrade
import com.itespf.aulamobile.data.model.ExamResult
import com.itespf.aulamobile.data.model.GradesResponse
import com.itespf.aulamobile.data.model.Submission

/** Une un assignment con su submission (si existe) para pintarlo en una sola fila. */
data class AssignmentWithSubmission(
    val assignment: Assignment,
    val submission: Submission?
)

/** Resumen consolidado de la boleta: promedios por categoría + promedio general. */
data class GradesSummary(
    val assignmentsAverage: Double?,
    val examsAverage: Double?,
    val customAverage: Double?,
    val overallAverage: Double?,
    val pendingCount: Int
)

fun GradesResponse.combineAssignments(): List<AssignmentWithSubmission> {
    val submissionsByAssignmentId = submissions.associateBy { it.assignmentId }
    return assignments
        .map { AssignmentWithSubmission(it, submissionsByAssignmentId[it.id]) }
        .sortedBy { it.assignment.dueDate ?: "" }
}

/**
 * Calcula un resumen/consolidado de la boleta (requisito de la sección 3):
 * - Promedio de tareas/proyectos: ponderado por "weight" cuando está disponible,
 *   si no, promedio simple de las notas entregadas.
 * - Promedio de exámenes: promedio simple de "score".
 * - Promedio de notas personalizadas (customGrades).
 * - Promedio general: promedio de las categorías que sí tengan datos.
 */
fun GradesResponse.buildSummary(): GradesSummary {
    val gradedSubmissions = submissions.filter { it.grade != null }
    val submissionsByAssignmentId = submissions.associateBy { it.assignmentId }

    val hasWeights = assignments.any { it.weight != null && it.weight > 0.0 }
    val assignmentsAverage: Double? = if (hasWeights) {
        var weightedSum = 0.0
        var weightTotal = 0.0
        assignments.forEach { assignment ->
            val grade = submissionsByAssignmentId[assignment.id]?.grade
            val weight = assignment.weight
            if (grade != null && weight != null && weight > 0.0) {
                weightedSum += grade * weight
                weightTotal += weight
            }
        }
        if (weightTotal > 0.0) weightedSum / weightTotal else null
    } else if (gradedSubmissions.isNotEmpty()) {
        gradedSubmissions.mapNotNull { it.grade }.average()
    } else {
        null
    }

    val examScores = examResults.mapNotNull { it.score }
    val examsAverage = if (examScores.isNotEmpty()) examScores.average() else null

    val customScores = customGrades.mapNotNull { it.score }
    val customAverage = if (customScores.isNotEmpty()) customScores.average() else null

    val categoryAverages = listOfNotNull(assignmentsAverage, examsAverage, customAverage)
    val overallAverage = if (categoryAverages.isNotEmpty()) categoryAverages.average() else null

    val pendingCount = assignments.count { submissionsByAssignmentId[it.id]?.grade == null }

    return GradesSummary(
        assignmentsAverage = assignmentsAverage,
        examsAverage = examsAverage,
        customAverage = customAverage,
        overallAverage = overallAverage,
        pendingCount = pendingCount
    )
}

fun Double.formatGrade(): String = "%.1f".format(this)

fun List<ExamResult>.sortedByDate(): List<ExamResult> = sortedByDescending { it.createdAt ?: "" }

fun List<CustomGrade>.nonEmptyOrNull(): List<CustomGrade>? = ifEmpty { null }
