package com.example.domain.workout

import com.example.data.entity.workout.WorkoutAction
import com.example.data.entity.workout.WorkoutRecord
import com.example.data.workout.WorkoutRepository
import com.example.domain.UseCaseFuture
import com.example.domain.UseCaseParameterNullPointerException
import com.example.util.Time

class GetWorkoutUC(private val workoutRepository: WorkoutRepository) :
    UseCaseFuture<Time, WorkoutRecord> {
    override suspend fun invoke(params: Time?): WorkoutRecord {
        params ?: throw UseCaseParameterNullPointerException()
        return workoutRepository.getWorkout(params)
    }

}

class PostWorkoutUC(private val workoutRepository: WorkoutRepository) :
    UseCaseFuture<WorkoutRecord, WorkoutRecord> {
    override suspend fun invoke(params: WorkoutRecord?): WorkoutRecord {
        params ?: throw UseCaseParameterNullPointerException()
        return workoutRepository.postWorkout(params)
    }
}

class ParseWorkoutActionUC(
    private val textParser: TextParser,
    private val workoutRepository: WorkoutRepository
) : UseCaseFuture<Pair<String, Time>, Pair<List<WorkoutAction>, String>> {
    override suspend fun invoke(params: Pair<String, Time>?): Pair<List<WorkoutAction>, String> {
        params ?: throw UseCaseParameterNullPointerException()
        val parseResults = textParser.extractWorkoutForTest(params.first).first
        return parseResults.map {
            val meta = workoutRepository.getMeta(it.name)
            WorkoutAction.Count(
                id = 0,
                set = it.set ?: 0,
                count = it.unitCount ?: 0,
                weight = it.weight,
                meta = meta,
                createdAt = params.second
            )
        } to ""
    }

}