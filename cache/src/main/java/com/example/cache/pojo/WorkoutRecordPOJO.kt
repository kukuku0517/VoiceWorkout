package com.example.cache.pojo

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout_record")
data class WorkoutRecordPOJO(
    @PrimaryKey(autoGenerate = true) val recordId: Int = 0,
    val set: Int,
    val weight: Int?,
    val countOrDuration: Int,
    val recordType: Int,
    @Embedded val meta: WorkoutMetaPOJO?,
    val createdAt:Long
)

enum class WorkoutRecordType {
    COUNT,
    DURATION
}

@Entity(tableName = "workout_meta")
data class WorkoutMetaPOJO(
    @PrimaryKey val id: Int,
    val type: Int,
    val name: String,
    val part: String?,
    val metaType: Int
)

enum class WorkoutMetaType {
    ORIGINAL,
    CUSTOM
}
