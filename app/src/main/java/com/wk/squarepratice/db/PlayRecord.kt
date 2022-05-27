package com.wk.squarepratice.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playRecord")
data class PlayRecord(
    @PrimaryKey(autoGenerate = true)
    val id:Long? = null,
    val timeMills: Long, //开始时间
    val level: Int,//难度等级
    val success: Boolean,//是否成功
    val finish: Boolean,//是否完成
    val costTime: Long,//成功耗费时间 ms
    val costLife: Int,//消耗了几次生命（错误和提醒）
)
