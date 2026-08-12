package com.itespf.aulamobile.ui.attendance

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.itespf.aulamobile.data.model.Attendance
import com.itespf.aulamobile.data.model.AttendanceStatus
import com.itespf.aulamobile.data.model.GradesResponse
import com.itespf.aulamobile.ui.common.UiState
import com.itespf.aulamobile.ui.theme.StatusAbsent
import com.itespf.aulamobile.ui.theme.StatusExcused
import com.itespf.aulamobile.ui.theme.StatusLate
import com.itespf.aulamobile.ui.theme.StatusPresent

@Composable
fun AttendanceScreen(
    state: UiState<GradesResponse>,
    onRetry: () -> Unit
) {
    when (state) {
        is UiState.Loading, UiState.Idle -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        is UiState.Error -> Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = state.message, color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(onClick = onRetry) { Text("Reintentar") }
        }
        is UiState.Success -> AttendanceContent(state.data.attendances)
    }
}

@Composable
private fun AttendanceContent(attendances: List<Attendance>) {
    if (attendances.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Aún no hay registros de asistencia.", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
        }
        return
    }

    val presentCount = attendances.count { it.status == AttendanceStatus.PRESENT }
    val total = attendances.size

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Asistencia registrada",
                        color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.85f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "$presentCount / $total clases presentes",
                        color = androidx.compose.ui.graphics.Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        items(attendances.sortedByDescending { it.lesson?.number ?: 0 }) { attendance ->
            AttendanceRow(attendance)
        }
    }
}

@Composable
private fun AttendanceRow(attendance: Attendance) {
    val (statusColor, statusLabel) = statusStyle(attendance.status)

    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = attendance.lesson?.title ?: "Clase #${attendance.lesson?.number ?: attendance.lessonId}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                attendance.lesson?.unit?.let {
                    Text(text = it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                }
                if (attendance.status == AttendanceStatus.EXCUSED && !attendance.excuseReason.isNullOrBlank()) {
                    Text(
                        text = "Motivo: ${attendance.excuseReason}" + (attendance.excuseStatus?.let { " ($it)" } ?: ""),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
            }

            Surface(
                shape = CircleShape,
                color = statusColor.copy(alpha = 0.15f)
            ) {
                Text(
                    text = statusLabel,
                    color = statusColor,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }
        }
    }
}

private fun statusStyle(status: String): Pair<Color, String> = when (status) {
    AttendanceStatus.PRESENT -> StatusPresent to "Presente"
    AttendanceStatus.ABSENT -> StatusAbsent to "Ausente"
    AttendanceStatus.LATE -> StatusLate to "Tardanza"
    AttendanceStatus.EXCUSED -> StatusExcused to "Excusado"
    else -> Color.Gray to status
}
