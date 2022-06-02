package com.wk.squarepratice.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayRecordDao {

    /**
     * 查询所有的节气数据
     */
    @Query("SELECT * FROM playRecord order by timeMills Desc")
    fun queryAll(): Flow<List<PlayRecord>>

    /**
     * 插入一条数据
     */
    @Insert()
    fun insert(entity: PlayRecord)

    /**
     * 清空term表中所有内容
     */
    @Query("DELETE FROM playRecord")
    fun delete()
}