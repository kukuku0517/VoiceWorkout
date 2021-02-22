package com.example.baseproject.di

import com.example.cache.MyRoomDatabase
import com.example.cache.WorkoutRecordRepositoryImpl
import com.example.cache.mapper.WorkoutMetaMapper
import com.example.cache.mapper.WorkoutRecordMapper
import com.example.data.workout.WorkoutRepository
import com.example.domain.workout.GetWorkoutUC
import com.example.domain.workout.ParseWorkoutActionUC
import com.example.domain.workout.PostWorkoutUC
import com.example.domain.workout.TextParser
import com.example.presentation.workout.WorkoutRecordViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val data = module {
    single { MyRoomDatabase.getInstance(get()) }
    single { WorkoutRecordMapper() }
    single { WorkoutMetaMapper() }
    single<WorkoutRepository> { WorkoutRecordRepositoryImpl(get(), get()) }
}

val presentation = module {
    viewModel { WorkoutRecordViewModel(get(), get(), get()) }

}

val domain = module {
    factory { ParseWorkoutActionUC(get(), get()) }
    factory { TextParser() }
    factory { GetWorkoutUC(get()) }
    factory { PostWorkoutUC(get()) }
}

