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
    data class Duration(
        val set: Int,
        val duration: Int,
        val weight: Int?,
        val meta: WorkoutMeta?
    )

    data class Count(
        val set: Int,
        val count: Int,
        val weight: Int?,
        val meta: WorkoutMeta?
    )
}

sealed class WorkoutMeta {
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