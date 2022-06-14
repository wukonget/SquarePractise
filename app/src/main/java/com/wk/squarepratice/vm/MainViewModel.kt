package com.wk.squarepratice.vm

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wk.squarepratice.db.PlayRecord
import com.wk.squarepratice.db.SingleRoom
import com.wk.squarepratice.ui.theme.SquarePraticeTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.*

class MainViewModel : ViewModel() {
    private var timerJob: Job? = null
    var levelDir: Int by mutableStateOf(1) //难度是增加还是减少，1增加  0减少
    var currentLevel = mutableStateOf(2) //等级  n*n
    var progress by mutableStateOf(1)//当前该点第几个了
    var dataList = mutableStateOf(listOf("1", "2", "3", "4"))//数据源
    var costTimeMills = mutableStateOf(0L)//总耗时
    var state: MutableState<PlayState> = mutableStateOf(PlayState.Prepared)
    var showSuggestion by mutableStateOf(false)  //显示提示
    var life = mutableStateOf(currentLevel.value) //生命
    var currentTheme by mutableStateOf(SquarePraticeTheme.Theme.Light)
    var showScoreList by mutableStateOf(false)
    var showLevelDropDown: Boolean by mutableStateOf(false)
    var levelDropDown: Int by mutableStateOf(0)
    var showResultDropDown: Boolean by mutableStateOf(false)
    var resultDropDown: Int by mutableStateOf(0) //0全部 ，1成功的，2失败的
    val resultDropMap: Map<Int, String> = mapOf(
        0 to "全部结果",
        1 to "成功",
        2 to "失败"
    )

    /**
     * 调整难度等级
     */
    fun changeLevel(level: Int) {
        if (level in (1..9) && level != currentLevel.value) {
            stopPlay()
            state.value = PlayState.Prepared
            levelDir = if (level > currentLevel.value) 1 else 0
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
        progress = 1
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
        showSuggestion = false
        if (progress.toString() == content) {
            progress++
            if (progress > dataList.value.size) {
                state.value = PlayState.Success
                saveRecord()
                stopPlay()
            }
        } else {
            reduceLife()
        }

    }


    /**
     * 保存一条记录
     */
    private fun saveRecord(success:Boolean = true,finish:Boolean = true) {
        viewModelScope.launch(Dispatchers.IO) {
            insertRecord(
                PlayRecord(
                    timeMills = Date().time,
                    level = currentLevel.value,
                    success = success,
                    finish = finish,
                    costTime = costTimeMills.value,
                    costLife = currentLevel.value - life.value
                )
            )
        }
    }

    private fun reduceLife() {
        life.value--
        if (life.value < 1) {
            state.value = PlayState.Fail
            saveRecord(success = false, finish = false)
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
        progress = 1
        timerJob?.cancel()
    }

    /**
     * 提示要找的数字位置
     */
    fun showTip() {
        if (state.value == PlayState.Playing && !showSuggestion) {
            showSuggestion = true
            reduceLife()
        }
    }

    /**
     * 切换主题
     */
    fun changeTheme() {
        Log.e("peng","changeTheme")
        currentTheme = when(currentTheme){
            SquarePraticeTheme.Theme.Dark -> {
                SquarePraticeTheme.Theme.NewYear
            }
            SquarePraticeTheme.Theme.NewYear -> {
                SquarePraticeTheme.Theme.Light
            }
            SquarePraticeTheme.Theme.Light -> {
                SquarePraticeTheme.Theme.Dark
            }
        }
    }

    /**
     * 显示历史成绩列表
     */
    fun showScoreList(show:Boolean = true) {
        showScoreList = show
    }


    /**
     * 获取所有数据
     */
    fun loadAllRecords(): Flow<List<PlayRecord>> {
        return when {
            levelDropDown != 0 && resultDropDown != 0 -> SingleRoom.playRecordDao.queryByLevelAndResult(
                levelDropDown,
                resultDropDown == 1
            )
            levelDropDown != 0 && resultDropDown == 0 -> SingleRoom.playRecordDao.queryByLevel(
                levelDropDown
            )
            levelDropDown == 0 && resultDropDown != 0 -> SingleRoom.playRecordDao.queryByResult(
                resultDropDown == 1
            )
            else -> SingleRoom.playRecordDao.queryAll()
        }

    }

    fun insertRecord(record: PlayRecord){
        SingleRoom.playRecordDao.insert(record)
    }

}

sealed class PlayState {
    object Prepared : PlayState()
    object Playing : PlayState()
    object Success : PlayState()
    object Fail : PlayState()
}