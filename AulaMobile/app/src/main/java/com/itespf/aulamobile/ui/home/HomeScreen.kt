package com.itespf.aulamobile.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.itespf.aulamobile.ui.attendance.AttendanceScreen
import com.itespf.aulamobile.ui.common.UiState
import com.itespf.aulamobile.ui.common.ViewModelFactory
import com.itespf.aulamobile.ui.grades.GradesScreen
import com.itespf.aulamobile.ui.grades.GradesViewModel
import com.itespf.aulamobile.ui.profile.ProfileScreen

private data class HomeTab(val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)

private val tabs = listOf(
    HomeTab("Perfil", Icons.Filled.Person),
    HomeTab("Calificaciones", Icons.Filled.School),
    HomeTab("Asistencia", Icons.Filled.CalendarMonth)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onLoggedOut: () -> Unit) {
    val viewModel: GradesViewModel = viewModel(factory = ViewModelFactory())
    val gradesState by viewModel.gradesState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(1) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(tabs[selectedTab].label) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                actions = {
                    IconButton(onClick = {
                        viewModel.logout()
                        onLoggedOut()
                    }) {
                        Icon(Icons.Filled.Logout, contentDescription = "Cerrar sesión")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                tabs.forEachIndexed { index, tab ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        icon = { Icon(tab.icon, contentDescription = tab.label) },
                        label = { Text(tab.label) }
                    )
                }
            }
        }
    ) { padding ->
        val successData = (gradesState as? UiState.Success)?.data

        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (selectedTab) {
                0 -> ProfileScreen(
                    name = viewModel.profileName,
                    username = viewModel.profileUsername,
                    grades = successData
                )
                1 -> GradesScreen(state = gradesState, onRetry = { viewModel.loadGrades() })
                2 -> AttendanceScreen(state = gradesState, onRetry = { viewModel.loadGrades() })
            }
        }
    }
}
