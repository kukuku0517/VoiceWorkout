package com.example.data.meal

import com.example.data.entity.meal.Record
import com.example.util.Time

interface MealRepository {
    suspend fun getMeal(time: Record): Record
    suspend fun getMeals(from: Time, to: Time): List<Record>
    suspend fun postMeal(params: Record): Record
}

class MealRepositoryImpl : MealRepository {
    override suspend fun getMeal(time: Record): Record {
        TODO("Not yet implemented")
    }

    override suspend fun getMeals(from: Time, to: Time): List<Record> {
        TODO("Not yet implemented")
    }

    override suspend fun postMeal(params: Record): Record {
        TODO("Not yet implemented")
    }

}

class FakeMealRepository : MealRepository {
    override suspend fun getMeal(time: Record): Record {
        TODO("Not yet implemented")
    }

    override suspend fun getMeals(from: Time, to: Time): List<Record> {
        TODO("Not yet implemented")
    }

    override suspend fun postMeal(params: Record): Record {
        TODO("Not yet implemented")
    }


}