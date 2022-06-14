package com.wk.squarepratice.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayRecordDao {

    /**
     * 查询所有的数据
     */
    @Query("SELECT * FROM playRecord order by timeMills Desc")
    fun queryAll(): Flow<List<PlayRecord>>

    /**
     * 根据level查询所有的数据
     */
    @Query("SELECT * FROM playRecord where level = :level order by timeMills Desc")
    fun queryByLevel(level: Int): Flow<List<PlayRecord>>

    /**
     * 根据结果查询所有的节气数据
     */
    @Query("SELECT * FROM playRecord where success = :result order by timeMills Desc")
    fun queryByResult(result: Boolean): Flow<List<PlayRecord>>

    /**
     * 同时根据level和结果 查询所有的数据
     */
    @Query("SELECT * FROM playRecord where level = :level and success = :result order by timeMills Desc")
    fun queryByLevelAndResult(level: Int, result: Boolean): Flow<List<PlayRecord>>

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