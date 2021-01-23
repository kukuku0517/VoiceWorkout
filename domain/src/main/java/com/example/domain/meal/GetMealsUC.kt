package com.example.domain.meal

import com.example.data.entity.Record
import com.example.data.meal.MealRepository
import com.example.domain.Time
import com.example.domain.UseCaseFuture
import com.example.domain.UseCaseParameterNullPointerException

class GetMealsUC(val mealRepository: MealRepository) : UseCaseFuture<Time, List<Record>> {
    override suspend fun invoke(params: Time?): List<Record> {

    }
}

class PostMealUC(val mealRepository: MealRepository) : UseCaseFuture<Record, Record> {
    override suspend fun invoke(params: Record?): Record {
        params ?: throw UseCaseParameterNullPointerException()

        return mealRepository.postMeal(params)
    }

}