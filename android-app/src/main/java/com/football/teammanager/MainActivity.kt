package com.football.teammanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.football.teammanager.ui.FootballTeamManagerApp
import com.football.teammanager.ui.theme.FootballTeamManagerTheme
import com.football.teammanager.viewmodel.PlayerViewModel
import com.football.teammanager.viewmodel.TeamViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FootballTeamManagerTheme {
                Surface {
                    FootballTeamManagerApp(
                        playerViewModel = viewModel<PlayerViewModel>(),
                        teamViewModel = viewModel<TeamViewModel>()
                    )
                }
            }
        }
    }
}

@Composable
private fun DemoContent() {}
