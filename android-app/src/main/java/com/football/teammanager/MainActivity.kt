package com.football.teammanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.lifecycle.viewmodel.compose.viewModel
import com.football.teammanager.ui.FootballTeamManagerApp
import com.football.teammanager.viewmodel.PlayerViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface {
                    val playerViewModel: PlayerViewModel = viewModel()
                    FootballTeamManagerApp(playerViewModel)
                }
            }
        }
    }
}
