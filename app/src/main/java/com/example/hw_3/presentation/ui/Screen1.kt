package com.example.hw_3.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.hw_3.Routes
import com.example.hw_3.data.Match
import com.example.hw_3.presentation.viewmodel.FootballViewModel

@Composable
fun Screen1(
    navController: NavHostController,
    viewModel: FootballViewModel
) {
    val matches by viewModel.matches.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Футбольные матчи",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Кнопки фильтрации
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { 
                    val today = java.time.LocalDate.now()
                    val tenDaysAgo = today.minusDays(10)
                    viewModel.loadMatches(
                        status = null,
                        dateFrom = tenDaysAgo.toString(),
                        dateTo = today.toString()
                    )
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Все", fontSize = MaterialTheme.typography.bodySmall.fontSize)
            }
            Button(
                onClick = { 
                    val today = java.time.LocalDate.now()
                    val tenDaysAgo = today.minusDays(10)
                    viewModel.loadMatches(
                        status = "FINISHED",
                        dateFrom = tenDaysAgo.toString(),
                        dateTo = today.toString()
                    )
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Завершенные", fontSize = MaterialTheme.typography.bodySmall.fontSize)
            }
            Button(
                onClick = { 
                    val today = java.time.LocalDate.now()
                    val tenDaysAgo = today.minusDays(10)
                    viewModel.loadMatches(
                        status = "SCHEDULED",
                        dateFrom = tenDaysAgo.toString(),
                        dateTo = today.toString()
                    )
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Запланированные", fontSize = MaterialTheme.typography.bodySmall.fontSize)
            }
        }

        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            errorMessage != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = errorMessage ?: "Ошибка",
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.refresh() }) {
                            Text("Повторить")
                        }
                    }
                }
            }

            matches.isNotEmpty() -> {
                LazyColumn {
                    items(matches) { match ->
                        MatchItem(
                            match = match,
                            onMatchClick = {
                                navController.navigate(Routes.MatchDetail.createRoute(match.id))
                            }
                        )
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                    }
                }
            }

            else -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Нет данных. Потяните для обновления или нажмите 'Повторить'")
                }
            }
        }
    }
}

@Composable
fun MatchItem(match: Match, onMatchClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onMatchClick)
            .padding(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        match.homeTeam.name,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(match.homeTeam.shortName, style = MaterialTheme.typography.bodySmall)
                }

                Text(
                    text = match.score?.fullTime?.let { "${it.home ?: "-"} - ${it.away ?: "-"}" }
                        ?: "VS",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        match.awayTeam.name,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.End
                    )
                    Text(
                        match.awayTeam.shortName,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.End
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = when (match.status) {
                        "FINISHED" -> "Завершен"
                        "LIVE" -> "LIVE"
                        "SCHEDULED" -> "Запланирован"
                        else -> match.status
                    },
                    style = MaterialTheme.typography.bodySmall
                )
                Text(text = formatDate(match.date), style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

fun formatDate(dateString: String): String {
    return try {
        val inputFormat = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", java.util.Locale.getDefault())
        val outputFormat = java.text.SimpleDateFormat("dd.MM.yyyy HH:mm", java.util.Locale.getDefault())
        val date = inputFormat.parse(dateString)
        outputFormat.format(date!!)
    } catch (e: Exception) {
        dateString
    }
}
