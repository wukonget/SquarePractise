package com.wk.squarepratice.vm

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private var timerJob: Job? = null
    val currentLevel = mutableStateOf(2) //等级  n*n
    val progress = mutableStateOf(1)//当前该点第几个了
    val dataList = mutableStateOf(listOf("1", "2", "3", "4"))//数据源
    val costTimeMills = mutableStateOf(0L)//总耗时
    val state: MutableState<PlayState> = mutableStateOf(PlayState.Prepared)
    val showSuggestion = mutableStateOf(false)  //显示提示
    val life = mutableStateOf(currentLevel.value) //生命

    /**
     * 调整难度等级
     */
    fun changeLevel(level: Int) {
        if (level in (1..9) && level != currentLevel.value) {
            stopPlay()
            state.value = PlayState.Prepared
            currentLevel.value = level
            costTimeMills.value = 0
            dataList.value = getRandomList(level)
            life.value = currentLevel.value
        }
    }

    /**
     * 刷新列表
     */
    private fun updateList() {
        progress.value = 1
        life.value = currentLevel.value
        costTimeMills.value = 0L
        dataList.value = getRandomList()
    }

    /**
     * 点击了一个item
     */
    fun select(content: String) {
        if (state.value != PlayState.Playing) {
            return
        }
        showSuggestion.value = false
        if (progress.value.toString() == content) {
            dataList.value.toMutableList().let {
                it[it.indexOf(content)] = "OK"
                dataList.value = it
            }
            progress.value++
            if (progress.value > dataList.value.size) {
                state.value = PlayState.Success
                stopPlay()
            }
        } else {
            reduceLife()
        }

    }

    private fun reduceLife() {
        life.value--
        if (life.value < 1) {
            state.value = PlayState.Fail
            stopPlay()
        }
    }

    /**
     * 获取随机顺序列表
     */
    private fun getRandomList(level: Int = currentLevel.value) =
        (1..level * level).map { it.toString() }.shuffled()

    /**
     * 开始计时
     */
    fun startPlay() {
        if (state.value != PlayState.Playing) {
            if (state.value == PlayState.Fail || state.value == PlayState.Success) {
                state.value = PlayState.Prepared
                return
            }
            updateList()
            state.value = PlayState.Playing
            timerJob = viewModelScope.launch {
                while (state.value == PlayState.Playing) {
                    delay(100)
                    costTimeMills.value += 100
                }
            }
        }
    }

    /**
     * 停止计时
     */
    private fun stopPlay() {
        progress.value = 1
        timerJob?.cancel()
    }

    /**
     * 提示要找的数字位置
     */
    fun showTip() {
        if (state.value == PlayState.Playing && !showSuggestion.value) {
            showSuggestion.value = true
            reduceLife()
        }
    }

}

sealed class PlayState {
    object Prepared : PlayState()
    object Playing : PlayState()
    object Success : PlayState()
    object Fail : PlayState()
}