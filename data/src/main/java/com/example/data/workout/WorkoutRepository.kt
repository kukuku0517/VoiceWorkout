package com.example.data.workout

import com.example.data.entity.workout.WorkoutRecord
import com.example.util.Time

interface WorkoutRepository {
    fun getWorkout(time: Time): WorkoutRecord
    fun postWorkout(workoutRecord: WorkoutRecord): WorkoutRecord
}

class WorkoutRepositoryImpl() : WorkoutRepository {
    override fun getWorkout(time: Time): WorkoutRecord {
        TODO("Not yet implemented")
    }

    override fun postWorkout(workoutRecord: WorkoutRecord): WorkoutRecord {
        TODO("Not yet implemented")
    }

}