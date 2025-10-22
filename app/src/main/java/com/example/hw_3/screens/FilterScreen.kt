package com.example.hw_3.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.hw_3.viewmodel.FootballViewModel
import com.example.hw_3.Routes
import com.example.hw_3.filter.LocalFilterBadgeState
import com.example.hw_3.filter.FilterBadgeProvider
import com.example.hw_3.FilterPreferences

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterScreen(
    navController: NavController,
    viewModel: FootballViewModel
) {
    // Получаем текущие значения фильтров из ViewModel
    val currentStatus by viewModel.matchStatus.collectAsState()
    val currentLeague by viewModel.league.collectAsState()
    val currentTeamFilter by viewModel.teamFilter.collectAsState()
    
    // Локальные состояния для управления UI
    var selectedStatus by remember { mutableStateOf(currentStatus) }
    var selectedLeague by remember { mutableStateOf(currentLeague) }
    var teamFilter by remember { mutableStateOf(currentTeamFilter) }
    
    // Состояния для выпадающих списков
    var statusExpanded by remember { mutableStateOf(false) }
    var leagueExpanded by remember { mutableStateOf(false) }
    
    // Варианты для выпадающих списков
    val statusOptions = listOf("ALL", "SCHEDULED", "LIVE", "FINISHED", "POSTPONED", "CANCELLED")
    val leagueOptions = listOf("ALL", "Premier League", "La Liga", "Bundesliga", "Serie A", "Ligue 1")
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Настройки фильтрации") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF2196F3),
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Выпадающий список для статуса матча
            Text(
                text = "Статус матча",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp)
            )
            
            ExposedDropdownMenuBox(
                expanded = statusExpanded,
                onExpandedChange = { statusExpanded = it },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedStatus,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                
                ExposedDropdownMenu(
                    expanded = statusExpanded,
                    onDismissRequest = { statusExpanded = false }
                ) {
                    statusOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                selectedStatus = option
                                statusExpanded = false
                            }
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Выпадающий список для лиги
            Text(
                text = "Лига",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp)
            )
            
            ExposedDropdownMenuBox(
                expanded = leagueExpanded,
                onExpandedChange = { leagueExpanded = it },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedLeague,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = leagueExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                
                ExposedDropdownMenu(
                    expanded = leagueExpanded,
                    onDismissRequest = { leagueExpanded = false }
                ) {
                    leagueOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                selectedLeague = option
                                leagueExpanded = false
                            }
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Поле ввода для фильтра по команде
            Text(
                text = "Поиск по команде",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp)
            )
            
            OutlinedTextField(
                value = teamFilter,
                onValueChange = { teamFilter = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Введите название команды") }
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Кнопки действий
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.weight(1f).padding(end = 8.dp)
                ) {
                    Text("Отмена")
                }
                
                Button(
                    onClick = {
                        // Сохраняем настройки фильтрации
                        viewModel.saveFilterPreferences(selectedStatus, selectedLeague, teamFilter)
                        
                        // Обновляем состояние бейджа
                        FilterBadgeProvider.provideFilterBadgeState().updateBadgeState(
                            status = selectedStatus,
                            league = selectedLeague,
                            teamFilter = teamFilter
                        )
                        
                        // Возвращаемся на предыдущий экран
                        navController.popBackStack()
                    },
                    modifier = Modifier.weight(1f).padding(start = 8.dp)
                ) {
                    Text("Применить")
                }
            }
        }
    }
}