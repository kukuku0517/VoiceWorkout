package com.example.cache.mapper

import com.example.cache.pojo.WorkoutMetaPOJO
import com.example.cache.pojo.WorkoutMetaType
import com.example.cache.pojo.WorkoutRecordPOJO
import com.example.cache.pojo.WorkoutRecordType
import com.example.data.entity.workout.WorkoutAction
import com.example.data.entity.workout.WorkoutMeta
import com.example.data.entity.workout.WorkoutType
import com.example.util.Duration
import com.example.util.Time

class WorkoutRecordMapper() : EntityMapper<WorkoutRecordPOJO, WorkoutAction> {
    override fun mapFromCached(type: WorkoutRecordPOJO): WorkoutAction {
        return when (WorkoutRecordType.values()[type.recordType]) {
            WorkoutRecordType.COUNT -> {
                WorkoutAction.Count(
                    id = type.recordId,
                    set = type.set,
                    weight = type.weight,
                    count = type.countOrDuration,
                    meta = type.meta?.let { WorkoutMetaMapper().mapFromCached(it) },
                    createdAt = Time.from(Duration.millis(type.createdAt))
                )

            }
            WorkoutRecordType.DURATION -> {
                WorkoutAction.Duration(
                    id = type.recordId,
                    set = type.set,
                    weight = type.weight,
                    duration = type.countOrDuration,
                    meta = type.meta?.let { WorkoutMetaMapper().mapFromCached(it) },
                    createdAt = Time.from(Duration.millis(type.createdAt))
                )
            }
        }
    }

    override fun mapToCached(type: WorkoutAction): WorkoutRecordPOJO {
        return WorkoutRecordPOJO(
            recordId = type.id,
            set = type.set,
            weight = type.weight,
            countOrDuration = type.getCountOrDuration(),
            meta = type.meta?.let { WorkoutMetaMapper().mapToCached(it) },
            recordType = when (type) {
                is WorkoutAction.Duration -> WorkoutRecordType.DURATION.ordinal
                is WorkoutAction.Count -> WorkoutRecordType.COUNT.ordinal
            },
            createdAt = type.createdAt.toMillis()
        )
    }
}

class WorkoutMetaMapper() : EntityMapper<WorkoutMetaPOJO, WorkoutMeta> {
    override fun mapFromCached(type: WorkoutMetaPOJO): WorkoutMeta {
        return when (WorkoutMetaType.values()[type.metaType]) {
            WorkoutMetaType.ORIGINAL -> WorkoutMeta.Original(
                id = type.id,
                type = WorkoutType.values()[type.type],
                name = type.name,
                part = type.part
            )
            WorkoutMetaType.CUSTOM -> WorkoutMeta.Custom(
                type = WorkoutType.values()[type.type],
                name = type.name,
                part = type.part
            )
        }
    }

    override fun mapToCached(type: WorkoutMeta): WorkoutMetaPOJO {
        return WorkoutMetaPOJO(
            id = when (type) {
                is WorkoutMeta.Original -> type.id
                is WorkoutMeta.Custom -> -1
            },
            type = type.type.ordinal,
            name = type.name,
            part = type.part,
            metaType = when (type) {
                is WorkoutMeta.Original -> WorkoutMetaType.ORIGINAL.ordinal
                is WorkoutMeta.Custom -> WorkoutMetaType.CUSTOM.ordinal
            }
        )
    }

}