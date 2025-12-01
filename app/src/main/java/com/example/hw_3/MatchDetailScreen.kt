package com.example.hw_3

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.hw_3.data.Match
import com.example.hw_3.viewmodel.FootballViewModel

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
            TopAppBar(
                title = { Text("Детали матча") },
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
                Text("Матч не найден")
            }
        } else {
            MatchDetailContent(
                match = match,
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}

@Composable
private fun MatchDetailContent(
    match: Match,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Статус матча
        Text(
            text = "Статус: ${match.status}",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )

        // Команды и счет
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = match.homeTeam.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = match.homeTeam.shortName,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // Счет
            Text(
                text = match.score?.fullTime?.let {
                    "${it.home ?: "-"} : ${it.away ?: "-"}"
                } ?: "- : -",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = match.awayTeam.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = match.awayTeam.shortName,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        // Дата
        Text(
            text = "Дата: ${match.date}",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
