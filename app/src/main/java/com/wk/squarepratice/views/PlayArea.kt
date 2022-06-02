package com.wk.squarepratice.views

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wk.squarepratice.BuildConfig
import com.wk.squarepratice.vm.MainViewModel


@Composable
fun PlayArea() {
    val vm: MainViewModel = viewModel()
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {



        LifeArea()

        LevelSelect()

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.0f)
        )


        TimeSeconds()

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(25.dp)
        )

        SquareGrid(modifier = Modifier.fillMaxWidth(0.9f))

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(15.dp)
                .weight(1.0f)
        )

        Text(
            text = "v ${BuildConfig.VERSION_NAME}",
            style = TextStyle(fontSize = 12.sp, color = MaterialTheme.colors.onBackground),
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            textAlign = TextAlign.Center
        )
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsBottomHeight(WindowInsets.navigationBars)
        )
    }
}