package com.example.data.workout

import com.example.data.entity.workout.WorkoutMeta
import com.example.data.entity.workout.WorkoutRecord
import com.example.data.entity.workout.WorkoutType
import com.example.util.Time

interface WorkoutRepository {
    suspend fun getWorkout(time: Time): WorkoutRecord
    suspend fun postWorkout(workoutRecord: WorkoutRecord): WorkoutRecord
    suspend fun getMeta(name: String?): WorkoutMeta?
}

class FakeWorkoutRepositoryImpl() : WorkoutRepository {
    override suspend fun getWorkout(time: Time): WorkoutRecord {
        TODO("Not yet implemented")
    }

    override suspend fun postWorkout(workoutRecord: WorkoutRecord): WorkoutRecord {
        TODO("Not yet implemented")
    }

    override suspend fun getMeta(name: String?): WorkoutMeta? {
        name ?: return null
        return WorkoutMeta.Custom(
            type = WorkoutType.WEIGHT,
            name = name,
            part = ""
        )
    }

}