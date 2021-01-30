package com.example.domain.meal

import com.example.data.entity.meal.Record
import com.example.data.meal.MealRepository
import com.example.domain.UseCaseFuture
import com.example.domain.UseCaseParameterNullPointerException
import com.example.util.Duration
import com.example.util.Time

class GetMealsUC(private val mealRepository: MealRepository) : UseCaseFuture<Time, List<Record>> {
    override suspend fun invoke(params: Time?): List<Record> {
        params ?: throw UseCaseParameterNullPointerException()

        return mealRepository.getMeals(
            params.getStartOfDay(),
            params.getStartOfDay() + Duration.ofDay(1)
        )
    }
}

class PostMealUC(private val mealRepository: MealRepository) : UseCaseFuture<Record, Record> {
    override suspend fun invoke(params: Record?): Record {
        params ?: throw UseCaseParameterNullPointerException()

        return mealRepository.postMeal(params)
    }

}