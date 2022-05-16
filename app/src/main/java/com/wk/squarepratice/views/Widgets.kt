package com.wk.squarepratice.views

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.twotone.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wk.squarepratice.util.leftOne
import com.wk.squarepratice.vm.MainViewModel
import com.wk.squarepratice.vm.PlayState

@Preview(showSystemUi = true)
@Composable
fun Pre() {
    SquareGrid(modifier = Modifier)
}

@OptIn(
    ExperimentalFoundationApi::class, ExperimentalAnimationApi::class,
    ExperimentalMaterialApi::class
)
@Composable
fun SquareGrid(modifier: Modifier, vm: MainViewModel = viewModel()) {
    val rows = vm.currentLevel.value
    val data = vm.dataList.value
    BoxWithConstraints(contentAlignment = Alignment.Center) {
        LazyVerticalGrid(columns = GridCells.Fixed(rows), modifier = modifier) {
            items(data.size) { index ->
                SquareItem(
                    modifier = Modifier.fillMaxSize(),
                    content = if (vm.state.value == PlayState.Playing) data[index] else ""
                )
            }
        }

        AnimatedVisibility(
            visible = vm.state.value != PlayState.Playing,
            enter = expandIn(
                expandFrom = Alignment.Center,
                animationSpec = tween(durationMillis = 500, delayMillis = 0)
            ),
            exit = fadeOut(animationSpec = tween(durationMillis = 500, delayMillis = 0))
        ) {
            val size = (min(maxHeight, maxWidth).value * 0.4).dp
            Surface(
                modifier = Modifier
                    .width(size)
                    .height(size),
                shape = CircleShape,
                onClick = { vm.startPlay() },
                color = when (vm.state.value) {
                    PlayState.Fail -> Color.Red
                    PlayState.Success -> MaterialTheme.colors.primary
                    else -> MaterialTheme.colors.secondary
                }
            ) {
                BoxWithConstraints(contentAlignment = Alignment.Center) {
                    Text(
                        text = when (vm.state.value) {
                            PlayState.Fail -> "失败"
                            PlayState.Success -> "成功"
                            else -> "开始"
                        },
                        style = TextStyle(
                            color = MaterialTheme.colors.onPrimary,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    )
                }
            }

        }

    }


}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SquareItem(modifier: Modifier, content: String, vm: MainViewModel = viewModel()) {
    Card(
        modifier = modifier
            .aspectRatio(1.0f)
            .padding(1.dp),
        backgroundColor = if (vm.showSuggestion.value && content == vm.progress.value.toString()) Color.Cyan else MaterialTheme.colors.onBackground,
        elevation = 5.dp,
        onClick = { vm.select(content) }) {
        BoxWithConstraints(contentAlignment = Alignment.Center) {
            Text(
                content,
                style = TextStyle(
                    fontSize = (maxHeight.value / (if (content == "OK") 3.0 else 1.5)).sp,
                    color = if (content == "OK") MaterialTheme.colors.primary else MaterialTheme.colors.background
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun LifeArea(vm: MainViewModel = viewModel()) {
    Row(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "生命值：",
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.onBackground
            )
        )
        repeat(vm.life.value) {
            Icon(
                Icons.TwoTone.Favorite,
                contentDescription = "life",
                tint = Color.Red,
                modifier = Modifier.padding(horizontal = 2.dp)
            )
        }
    }
}


@Composable
fun LevelSelect(vm: MainViewModel = viewModel()) {
    Row(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.padding(end = 10.dp),
            text = "难度",
            style = TextStyle(
                color = MaterialTheme.colors.secondary, fontSize = 24.sp
            )
        )

        Card(
            modifier = Modifier
                .width(22.dp)
                .height(22.dp)
                .clickable {
                    vm.changeLevel(vm.currentLevel.value - 1)
                },
            backgroundColor = Color.White
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Filled.Remove,
                    contentDescription = "remove",
                    tint = Color.Black,
                    modifier = Modifier.fillMaxSize(0.8f)
                )
            }
        }

        Text(
            modifier = Modifier.padding(horizontal = 8.dp),
            text = "${vm.currentLevel.value}",
            style = TextStyle(
                color = MaterialTheme.colors.onBackground,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        )

        Card(
            modifier = Modifier
                .width(22.dp)
                .height(22.dp)
                .clickable {
                    vm.changeLevel(vm.currentLevel.value + 1)
                },
            backgroundColor = MaterialTheme.colors.secondary
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "add",
                    tint = MaterialTheme.colors.onSecondary,
                    modifier = Modifier.fillMaxSize(0.8f)
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "进度  ",
            textAlign = TextAlign.Center,
            style = TextStyle(color = MaterialTheme.colors.secondary, fontSize = 24.sp)
        )

        Card(
            modifier = Modifier
                .width(35.dp)
                .height(35.dp),
            shape = CircleShape,
            backgroundColor = MaterialTheme.colors.secondary
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable {
                        vm.showTip()
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${vm.progress.value}",
                    textAlign = TextAlign.Center,
                    style = TextStyle(color = MaterialTheme.colors.onSecondary, fontSize = 25.sp)
                )
            }
        }

    }
}

/**
 * 所用时间
 */
@Composable
fun TimeSeconds(vm: MainViewModel = viewModel()) {
    val fs = animateIntAsState(targetValue = if (vm.state.value == PlayState.Success) 60 else 30)
    Text(
        text = "${(vm.costTimeMills.value.toFloat() / 1000).leftOne()} s",
        style = TextStyle(
            fontSize = fs.value.sp,
            color = if (vm.state.value == PlayState.Success) MaterialTheme.colors.primary else MaterialTheme.colors.onBackground
        )
    )
}
