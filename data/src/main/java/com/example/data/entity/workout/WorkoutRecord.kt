package com.example.data.entity.workout

import com.example.util.Time


/**
 * 운동 기록
 * 운동 세트
 * 운동 정보
 */
data class WorkoutRecord(
    val time: Time,
    val workoutActions: List<WorkoutAction>
)

sealed class WorkoutAction {
    data class Weight(
        val meta: WorkoutMeta?,
        val set: Int,
        val weight: Int
    )

    data class Duration(
        val meta: WorkoutMeta?,
        val set: Int,
        val duration: Int
    )

    data class Count(
        val meta: WorkoutMeta?,
        val set: Int,
        val count: Int
    )
}
sealed class WorkoutMeta{
    data class Original(
        val id: Int,
        val type: WorkoutType,
        val name: String,
        val part: String?
    )

    data class Custom(
        val type: WorkoutType,
        val name: String,
        val part: String?
    )
}

enum class WorkoutType {
    WEIGHT,
    NON_WEIGHT,
    REST
}