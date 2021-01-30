package com.example.domain.workout

import com.example.data.entity.workout.WorkoutAction
import com.example.data.entity.workout.WorkoutRecord
import com.example.data.workout.WorkoutRepository
import com.example.domain.UseCaseFuture
import com.example.domain.UseCaseParameterNullPointerException
import com.example.util.Time

class GetWorkoutUC(val workoutRepository: WorkoutRepository) : UseCaseFuture<Time, WorkoutRecord> {
    override suspend fun invoke(params: Time?): WorkoutRecord {
        params ?: throw UseCaseParameterNullPointerException()
        return workoutRepository.getWorkout(params)
    }

}

class PostWorkoutUC(val workoutRepository: WorkoutRepository) :
    UseCaseFuture<WorkoutRecord, WorkoutRecord> {
    override suspend fun invoke(params: WorkoutRecord?): WorkoutRecord {
        params ?: throw UseCaseParameterNullPointerException()
        return workoutRepository.postWorkout(params)
    }
}

class ParseWorkoutActionUC(val textParser: TextParser) :
    UseCaseFuture<String, Pair<List<WorkoutAction>, String>> {
    override suspend fun invoke(params: String?): Pair<List<WorkoutAction>, String> {
        params ?: throw UseCaseParameterNullPointerException()
        return textParser.extractWorkout(params)
    }

}