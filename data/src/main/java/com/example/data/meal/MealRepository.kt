package com.example.data.meal

import com.example.data.entity.Record

interface MealRepository {
    suspend fun getMeal(time: Record): Record
    suspend fun getMeals(from:Dis):List<Record>
    suspend fun postMeal(params: Record): Record

}

class MealRepositoryImpl : MealRepository {


}