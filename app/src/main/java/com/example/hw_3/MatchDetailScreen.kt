package com.example.hw_3

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.hw_3.data.Match
import com.example.hw_3.presentation.ui.formatDate
import com.example.hw_3.presentation.viewmodel.FootballViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchDetailScreen(
    matchId: Int,
    navController: NavHostController,
    viewModel: FootballViewModel
) {
    val matches by viewModel.matches.collectAsState()
    val match = matches.find { it.id == matchId }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Детали матча",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (match == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Матч не найден",
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        "ID: $matchId",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        "Загружено матчей: ${matches.size}",
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center
                    )
                    Button(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Text("Назад")
                    }
                }
            }
        } else {
            MatchDetailContent(match = match, modifier = Modifier.padding(paddingValues))
        }
    }
}

@Composable
fun MatchDetailContent(match: Match, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TeamVsTeamSection(match)
        Spacer(modifier = Modifier.height(24.dp))
        MatchInfoSection(match)
        Spacer(modifier = Modifier.height(24.dp))
        
        if (match.status == "FINISHED" && match.score?.fullTime != null) {
            ScoreDetailsSection(match)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamVsTeamSection(match: Match) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TeamInfo(match.homeTeam, isHomeTeam = true)
            Spacer(modifier = Modifier.height(16.dp))

            if (match.score?.fullTime?.home != null && match.score.fullTime.away != null) {
                Text(
                    text = "${match.score.fullTime.home} - ${match.score.fullTime.away}",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            } else {
                Text(
                    text = "VS",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            TeamInfo(match.awayTeam, isHomeTeam = false)
        }
    }
}

@Composable
fun TeamInfo(team: com.example.hw_3.data.Team, isHomeTeam: Boolean) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = team.name,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            text = team.shortName,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        if (isHomeTeam) {
            Text(
                text = "(Дома)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
        } else {
            Text(
                text = "(В гостях)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchInfoSection(match: Match) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Информация о матче",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp),
                textAlign = TextAlign.Center
            )

            CenteredInfoRow("Статус", when (match.status) {
                "FINISHED" -> "Завершен"
                "LIVE" -> "В прямом эфире"
                "SCHEDULED" -> "Запланирован"
                "POSTPONED" -> "Отложен"
                "CANCELLED" -> "Отменен"
                else -> match.status
            })

            CenteredInfoRow("Дата и время", formatDate(match.date))
            CenteredInfoRow("ID матча", match.id.toString())
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScoreDetailsSection(match: Match) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Результат",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp),
                textAlign = TextAlign.Center
            )

            match.score?.fullTime?.let { score ->
                CenteredInfoRow("Финальный счет", "${score.home} - ${score.away}")

                val winner = when {
                    score.home!! > score.away!! -> match.homeTeam.name
                    score.away > score.home -> match.awayTeam.name
                    else -> "Ничья"
                }
                CenteredInfoRow("Победитель", winner)
            }
        }
    }
}

@Composable
fun CenteredInfoRow(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
    }
}
