package com.example.cache.pojo

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WorkoutRecordDao {
    @Query("select * from workout_record")
    suspend fun getWorkout(): List<WorkoutRecordPOJO>

    @Query("select * from workout_record where createdAt between :from and :to")
    suspend fun getWorkoutBetween(from:Long, to:Long): List<WorkoutRecordPOJO>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun postWorkout(records: List<WorkoutRecordPOJO>)
}