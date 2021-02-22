package com.example.cache

import com.example.cache.mapper.WorkoutMetaMapper
import com.example.cache.mapper.WorkoutRecordMapper
import com.example.cache.pojo.WorkoutRecordDao
import com.example.data.entity.workout.WorkoutMeta
import com.example.data.entity.workout.WorkoutRecord
import com.example.data.entity.workout.WorkoutType
import com.example.data.workout.WorkoutRepository
import com.example.util.Duration

class WorkoutRecordRepositoryImpl(
    database: MyRoomDatabase,
    val workoutRecordMapper: WorkoutRecordMapper
) : WorkoutRepository {
    val workoutRecordDao = database.getWorkoutDao()

    override suspend fun getWorkout(time: com.example.util.Time): WorkoutRecord {
        val records = workoutRecordDao.getWorkoutBetween(
            time.getStartOfDay().toMillis(),
            time.getStartOfDay().run { this + Duration.ofDay(1) }.toMillis()
        ).map { workoutRecordMapper.mapFromCached(it) }
        return WorkoutRecord(
            time,
            records
        )
    }

    override suspend fun postWorkout(workoutRecord: WorkoutRecord): WorkoutRecord {
        workoutRecordDao.postWorkout(workoutRecord.workoutActions.map {
            workoutRecordMapper.mapToCached(
                it
            )
        })
        return workoutRecord
    }

    override suspend fun getMeta(name: String?): WorkoutMeta? {
        return WorkoutMeta.Custom(
            name = name ?: "",
            type = WorkoutType.WEIGHT,
            part = ""
        )
    }
}