package com.wk.squarepratice.db

import androidx.room.Room
import com.wk.squarepratice.WkApplication

object SingleRoom {
    val playRecordDao = Room.databaseBuilder(WkApplication.instance!!,PlayRecordDatabase::class.java,"db_play_record").build().playRecordDao()
}