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
    abstract val id: Int
    abstract val set: Int
    abstract val weight: Int?
    abstract val meta: WorkoutMeta?
    abstract val createdAt:Time
    abstract fun getCountOrDuration(): Int

    data class Duration(
        override val id: Int,
        override val set: Int,
        override val weight: Int?,
        override val meta: WorkoutMeta?,
        override val createdAt: Time,
        val duration: Int
    ) : WorkoutAction() {
        override fun getCountOrDuration(): Int {
            return duration
        }
    }

    data class Count(
        override val id: Int,
        override val set: Int,
        override val weight: Int?,
        override val meta: WorkoutMeta?,
        override val createdAt: Time,
        val count: Int
    ) : WorkoutAction() {
        override fun getCountOrDuration(): Int {
            return count
        }
    }
}

sealed class WorkoutMeta {
    abstract val type: WorkoutType
    abstract val name: String
    abstract val part: String?

    data class Original(
        val id: Int,
        override val type: WorkoutType,
        override val name: String,
        override val part: String?
    ) : WorkoutMeta()

    data class Custom(
        override val type: WorkoutType,
        override val name: String,
        override val part: String?
    ) : WorkoutMeta()
}

enum class WorkoutType {

    WEIGHT,
    NON_WEIGHT,
    REST
}