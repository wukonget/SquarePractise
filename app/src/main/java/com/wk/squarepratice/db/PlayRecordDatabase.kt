package com.wk.squarepratice.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [PlayRecord::class], exportSchema = false, version = 1)
abstract class PlayRecordDatabase : RoomDatabase() {
    abstract fun playRecordDao(): PlayRecordDao
}