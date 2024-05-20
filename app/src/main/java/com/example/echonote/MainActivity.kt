package com.example.echonote

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.echonote.core.navigation.Navigation
import com.example.echonote.domain.repository.NetworkConnectivityObserver
import com.example.echonote.presentation.viewModel.NetworkViewmodel
import com.example.echonote.ui.theme.EchoNoteTheme
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val networkViewmodel = koinViewModel<NetworkViewmodel>()
            val networkState by networkViewmodel.networkState.collectAsState()
            val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.offline_state))
            EchoNoteTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (networkState == NetworkConnectivityObserver.Status.Available) {
                        Navigation()
                    } else {
                        LottieAnimation(
                            composition =composition,
                            iterations = Int.MAX_VALUE
                        )
                    }
                }
            }
        }
    }
}
    
    
    
