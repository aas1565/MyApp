package com.example.hw_3

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hw_3.favorites.FavoritesViewModel

@Composable
fun Screen2(favoritesViewModel: FavoritesViewModel) {
    val favorites by favoritesViewModel.favorites.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Избранные матчи", style = MaterialTheme.typography.titleLarge)
        LazyColumn(modifier = Modifier.fillMaxSize().padding(top = 8.dp)) {
            items(favorites) { fav ->
                Card(modifier = Modifier.padding(vertical = 6.dp)) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = "${fav.homeTeamName} vs ${fav.awayTeamName}", style = MaterialTheme.typography.titleMedium)
                        Text(text = fav.date ?: "Дата неизвестна")
                        Text(text = "Статус: ${fav.status ?: "unknown"}")
                        val scoreExists = fav.homeScore != null || fav.awayScore != null
                        if (scoreExists) {
                            Text(text = "Счет: ${fav.homeScore ?: "-"} : ${fav.awayScore ?: "-"}")
                        }
                    }
                }
            }
        }
    }
}