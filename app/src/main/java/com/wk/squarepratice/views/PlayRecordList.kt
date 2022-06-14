package com.wk.squarepratice.views

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.twotone.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.blankj.utilcode.util.TimeUtils
import com.wk.squarepratice.db.PlayRecord
import com.wk.squarepratice.ui.theme.SquarePraticeTheme
import com.wk.squarepratice.ui.theme.grey1
import com.wk.squarepratice.ui.theme.yellow1
import com.wk.squarepratice.util.leftOne
import com.wk.squarepratice.vm.MainViewModel

@Composable
fun PlayRecordList(vm: MainViewModel = viewModel()) {
    val data = vm.loadAllRecords().collectAsState(initial = listOf()).value

    AnimatedVisibility(
        visible = vm.showScoreList,
        enter = slideIn(
            animationSpec = tween(durationMillis = 200, delayMillis = 0),
            initialOffset = { fullSize -> IntOffset(fullSize.width, 0) }
        ),
        exit = slideOut(
            animationSpec = tween(durationMillis = 200, delayMillis = 0),
            targetOffset = { fullSize -> IntOffset(fullSize.width, 0) }
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(SquarePraticeTheme.colors.background)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    vm.showScoreList(false)
                }) {
                    Icon(
                        Icons.Filled.ArrowBack,
                        contentDescription = "返回",
                        tint = SquarePraticeTheme.colors.onBackground
                    )
                }

                Text(
                    text = "全部记录",
                    modifier = Modifier.wrapContentWidth(),
                    textAlign = TextAlign.Center,
                    style = TextStyle(
                        fontSize = 16.sp,
                        color = SquarePraticeTheme.colors.onActionSecondColor
                    )
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.TopCenter
            ) {
                RecordList(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 40.dp), data
                )

                Row(modifier = Modifier.fillMaxWidth(0.95f)) {
                    DropDownSelect(
                        modifier = Modifier.weight(1f),
                        stringData = (0..9).map { if (it == 0) "全部" else it.toString() },
                        onButton = "${if (vm.levelDropDown == 0) "全部难度" else "难度${vm.levelDropDown}"}",
                        showDropDown = vm.showLevelDropDown,
                        onClickButton = {
                            vm.showLevelDropDown = !vm.showLevelDropDown
                        },
                        onSelect = {
                            vm.levelDropDown = it
                            vm.showLevelDropDown = false
                        }
                    )
                    Spacer(modifier = Modifier.weight(0.1f))
                    DropDownSelect(
                        modifier = Modifier.weight(1f),
                        stringData = listOf("全部", "成功", "失败"),
                        onButton = vm.resultDropMap[vm.resultDropDown] ?: "全部结果",
                        showDropDown = vm.showResultDropDown,
                        onClickButton = {
                            vm.showResultDropDown = !vm.showResultDropDown
                        },
                        onSelect = {
                            vm.resultDropDown = it
                            vm.showResultDropDown = false
                        }
                    )
                }

            }
        }

    }
}

@Composable
private fun RecordList(modifier: Modifier, data: List<PlayRecord>) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        content = {
            items(data, key = { it.id ?: 0 }) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .padding(vertical = 5.dp)
                        .clickable {

                        },
                    shape = RoundedCornerShape(10.dp),
                    backgroundColor = if (item.success) SquarePraticeTheme.colors.success else SquarePraticeTheme.colors.error
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {

                        IconAndText(
                            Icons.Outlined.Star,
                            yellow1,
                            "${item.level}"
                        )
                        IconAndText(
                            Icons.Outlined.Timer,
                            grey1,
                            "${(item.costTime / 1000f).leftOne()} s"
                        )
                        IconAndText(
                            Icons.TwoTone.Favorite,
                            grey1,
                            "${item.costLife}"
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = TimeUtils.millis2String(item.timeMills),
                            style = TextStyle(
                                fontSize = 13.sp,
                                color = SquarePraticeTheme.colors.textOnItem
                            )
                        )
                    }
                }
            }
        })
}

@Composable
private fun IconAndText(ic: ImageVector, tintColor: Color, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(end = 10.dp)) {
        Icon(ic, contentDescription = "难度$text", tint = tintColor, modifier = Modifier.size(15.dp))
        Text(
            text = " $text  ",
            style = TextStyle(
                fontSize = 15.sp,
                color = SquarePraticeTheme.colors.textOnItem,
                fontWeight = FontWeight.Bold
            )
        )
    }
}