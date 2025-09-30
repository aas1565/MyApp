package com.example.hw_3


sealed class Routes(val route: String) {
    //object Welcome: Routes("welcomeScreen")
    object Welcome : Routes("welcome")
    object Screen1 : Routes("screen1")
    object Screen2 : Routes("screen2")
    object Screen3 : Routes("screen3")
    object MatchDetail : Routes("match_detail/{matchId}") {
        fun createRoute(matchId: Int) = "match_detail/$matchId"
    }//
}