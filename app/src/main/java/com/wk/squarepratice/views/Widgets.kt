@file:OptIn(ExperimentalAnimationApi::class)

package com.wk.squarepratice.views

import android.graphics.Paint
import androidx.compose.animation.*
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.twotone.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wk.squarepratice.ui.theme.SquarePraticeTheme
import com.wk.squarepratice.util.leftOne
import com.wk.squarepratice.vm.MainViewModel
import com.wk.squarepratice.vm.PlayState
import kotlin.math.floor

@Preview(showSystemUi = true)
@Composable
fun Pre() {
    SquareGrid(modifier = Modifier.fillMaxWidth(0.9f))
}

@OptIn(
    ExperimentalFoundationApi::class, ExperimentalAnimationApi::class,
    ExperimentalMaterialApi::class
)
@Composable
fun SquareGrid(modifier: Modifier, vm: MainViewModel = viewModel()) {
    val rows = vm.currentLevel.value
    val data = vm.dataList.value
    BoxWithConstraints(contentAlignment = Alignment.Center, modifier = modifier.aspectRatio(1.0f)) {

        NumGrid(
            columns = rows,
            data = data,
            modifier = Modifier.fillMaxSize(),
            maxWidth = maxWidth,
            maxHeight = maxHeight
        ) {
            vm.select(it)
        }

        AnimatedVisibility(
            visible = vm.state.value != PlayState.Playing,
            enter = scaleIn(
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
                    PlayState.Fail -> SquarePraticeTheme.colors.error
                    PlayState.Success -> SquarePraticeTheme.colors.success
                    else -> SquarePraticeTheme.colors.normal
                }
            ) {
                BoxWithConstraints(contentAlignment = Alignment.Center) {
                    Text(
                        text = when (vm.state.value) {
                            PlayState.Fail -> "??????"
                            PlayState.Success -> "??????"
                            else -> "??????"
                        },
                        style = TextStyle(
                            color = SquarePraticeTheme.colors.onActionColor,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    )
                }
            }

        }

    }
}

@Composable
fun NumGrid(
    columns: Int,
    data: List<String>,
    modifier: Modifier,
    maxWidth: Dp,
    maxHeight: Dp,
    vm: MainViewModel = viewModel(),
    click: (String) -> Unit
) {
    val lineColor = SquarePraticeTheme.colors.onBackground
    val successColor = SquarePraticeTheme.colors.success
    val normalColor = SquarePraticeTheme.colors.normal
    val clickModifier = modifier.pointerInput(data) {
        detectTapGestures {
            val clickColumn = floor((it.x) / (maxWidth.toPx() / columns)).toInt()
            val clickRow = floor((it.y) / (maxHeight.toPx() / columns)).toInt()
            click(data[clickRow * columns + clickColumn])
        }
    }
    Canvas(modifier = clickModifier) {
        var lineY: Float
        var lineX: Float
        var itemLeft: Float
        var itemTop: Float
        var itemText: String
        val eachSize = (maxHeight.toPx()) / columns //??????item?????????
        val nativePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = lineColor.toArgb()
            textSize = (maxWidth.toPx()) / (columns * 1.5f)
        }
        (0..columns).forEach { column ->
            lineY = eachSize * column
            lineX = eachSize * column
            drawLine(
                lineColor,
                Offset(0f, lineY),
                Offset(maxWidth.toPx(), lineY),
                strokeWidth = 2.dp.toPx()
            )
            drawLine(
                lineColor,
                Offset(lineX, 0f),
                Offset(lineX, maxHeight.toPx()),
                strokeWidth = 2.dp.toPx()
            )
            if (vm.state.value == PlayState.Playing && column < columns) {
                (0 until columns).forEach { row ->
                    itemText = data[row * columns + column]
                    itemLeft = column * eachSize
                    itemTop = row * eachSize
                    if (vm.showSuggestion && itemText == vm.progress.toString()) {
                        //????????????????????????
                        drawRect(
                            successColor,
                            Offset(itemLeft + 1, itemTop + 1),
                            Size(eachSize - 2, eachSize - 2)
                        )
                    }
                    if (itemText.toInt() < vm.progress) {
                        //?????????????????????
                        drawRect(
                            normalColor,
                            Offset(itemLeft + 1, itemTop + 1),
                            Size(eachSize - 2, eachSize - 2)
                        )
                    } else {
                        //????????????
                        drawTextFromLeftTop(itemText, itemLeft, itemTop, eachSize, nativePaint)
                    }
                }
            }
        }
    }
}


private fun DrawScope.drawTextFromLeftTop(
    text: String,
    left: Float,
    top: Float,
    size: Float,
    nativePaint: Paint
) {
    drawIntoCanvas { canvas ->
        val centerToBaseLine: Float = nativePaint.fontMetrics.let { fm ->
            (fm.top + fm.bottom) / 2
        }
        val textWidth = nativePaint.measureText(text)
        canvas.nativeCanvas.drawText(
            text,
            left + (size - textWidth) / 2,
            top + size / 2 - centerToBaseLine,
            nativePaint
        )
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
            text = "????????????",
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = SquarePraticeTheme.colors.onBackground
            )
        )
        Icon(
            Icons.TwoTone.Favorite,
            contentDescription = "life",
            tint = Color.Red,
            modifier = Modifier.padding(horizontal = 2.dp)
        )

        Text(
            text = " x ${vm.life.value}", style = TextStyle(
                fontSize = 18.sp,
                color = SquarePraticeTheme.colors.actionColor
            )
        )

        Spacer(modifier = Modifier.weight(1f))

        OutlinedButton(
            onClick = {vm.showScoreList()},
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, SquarePraticeTheme.colors.actionColor),
            colors = ButtonDefaults.outlinedButtonColors(backgroundColor = SquarePraticeTheme.colors.background)
        ) {
            Text(
                text = "????????????", style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = SquarePraticeTheme.colors.actionColor
                )
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
            text = "??????",
            style = TextStyle(
                color = SquarePraticeTheme.colors.actionColor, fontSize = 24.sp
            )
        )

        Card(
            modifier = Modifier
                .width(22.dp)
                .height(22.dp)
                .clickable {
                    vm.changeLevel(vm.currentLevel.value - 1)
                },
            backgroundColor = SquarePraticeTheme.colors.actionSecondColor
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Filled.Remove,
                    contentDescription = "remove",
                    tint = SquarePraticeTheme.colors.onActionSecondColor,
                    modifier = Modifier.fillMaxSize(0.8f)
                )
            }
        }

        AnimatedContent(
            targetState = vm.currentLevel.value,
            transitionSpec = { slideInVertically{fullHeight -> fullHeight } with slideOutVertically { fullHeight -> -fullHeight }}
        ) {target->
            Text(
                modifier = Modifier.padding(horizontal = 8.dp),
                text = "$target",
                style = TextStyle(
                    color = SquarePraticeTheme.colors.onBackground,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        Card(
            modifier = Modifier
                .width(22.dp)
                .height(22.dp)
                .clickable {
                    vm.changeLevel(vm.currentLevel.value + 1)
                },
            backgroundColor = SquarePraticeTheme.colors.actionColor
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "add",
                    tint = SquarePraticeTheme.colors.onActionColor,
                    modifier = Modifier.fillMaxSize(0.8f)
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "??????  ",
            textAlign = TextAlign.Center,
            style = TextStyle(color = SquarePraticeTheme.colors.actionColor, fontSize = 24.sp)
        )

        Card(
            modifier = Modifier
                .width(35.dp)
                .height(35.dp),
            shape = CircleShape,
            backgroundColor = SquarePraticeTheme.colors.actionColor
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
                    text = "${vm.progress}",
                    textAlign = TextAlign.Center,
                    style = TextStyle(
                        color = SquarePraticeTheme.colors.onActionColor,
                        fontSize = 25.sp
                    )
                )
            }
        }

    }
}

/**
 * ????????????
 */
@Composable
fun TimeSeconds(vm: MainViewModel = viewModel()) {
    val fs = animateIntAsState(targetValue = if (vm.state.value == PlayState.Success) 60 else 30)
    Text(
        text = "${(vm.costTimeMills.value.toFloat() / 1000).leftOne()} s",
        style = TextStyle(
            fontSize = fs.value.sp,
            color = if (vm.state.value == PlayState.Success) SquarePraticeTheme.colors.success else SquarePraticeTheme.colors.onBackground
        )
    )
}

@Composable
fun ExitDialog(title: String = "??????????????????", sure: () -> Unit, dismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = { dismiss() },
        backgroundColor = SquarePraticeTheme.colors.background,
        modifier = Modifier.padding(10.dp),
        shape = RoundedCornerShape(10.dp),
        title = {
            Text(
                text = title,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                color = SquarePraticeTheme.colors.onBackground
            )
        },
        text = {},
        confirmButton = {
            Row(
                modifier = Modifier
                    .background(SquarePraticeTheme.colors.onBackground)
                    .fillMaxWidth()
                    .padding(top = 1.dp)
            ) {
                Text(
                    text = "??????",
                    fontSize = 18.sp,
                    color = SquarePraticeTheme.colors.onBackground,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .background(SquarePraticeTheme.colors.background)
                        .weight(1f)
                        .padding(vertical = 15.dp)
                        .clickable { dismiss() })
                Spacer(
                    modifier = Modifier
                        .width(1.dp)
                        .fillMaxHeight()
                        .background(SquarePraticeTheme.colors.onBackground)
                )
                Text(
                    text = "??????",
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    color = SquarePraticeTheme.colors.actionColor,
                    modifier = Modifier
                        .background(SquarePraticeTheme.colors.background)
                        .weight(1f)
                        .padding(vertical = 15.dp)
                        .clickable { sure() })
            }
        }
    )
}
