package com.example.imagegallery

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.imagegallery.ui.DirectoryDetailScreen
import com.example.imagegallery.ui.DirectoryListScreen
import com.example.imagegallery.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface {
                    AppNav()
                }
            }
        }
    }
}

@Composable
fun AppNav() {
    val navController: NavHostController = rememberNavController()
    val viewModel: MainViewModel = viewModel()

    NavHost(navController = navController, startDestination = "list") {
        composable("list") {
            DirectoryListScreen(viewModel = viewModel, onOpenDirectory = { dirId ->
                navController.navigate("detail/$dirId")
            })
        }
        composable("detail/{dirId}") { backStackEntry ->
            val dirId = backStackEntry.arguments?.getString("dirId")?.toLongOrNull() ?: 0L
            DirectoryDetailScreen(dirId = dirId, onBack = { navController.popBackStack() }, viewModel = viewModel)
        }
    }
}
