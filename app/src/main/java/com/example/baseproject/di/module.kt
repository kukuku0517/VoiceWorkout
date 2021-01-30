package com.example.baseproject.di

import com.example.data.meal.MealRepository
import com.example.data.meal.MealRepositoryImpl
import com.example.data.workout.WorkoutRepository
import com.example.data.workout.WorkoutRepositoryImpl
import com.example.domain.meal.GetMealsUC
import com.example.domain.meal.PostMealUC
import com.example.domain.workout.ParseWorkoutActionUC
import com.example.domain.workout.TextParser
import com.example.presentation.workout.WorkoutRecordViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val data = module {
    single<WorkoutRepository> { WorkoutRepositoryImpl() }
}

val presentation = module {
    viewModel { WorkoutRecordViewModel(get()) }

}

val domain = module {
    factory { ParseWorkoutActionUC(get()) }
    factory { TextParser() }
}

