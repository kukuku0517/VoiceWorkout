package com.example.data.entity.meal

import com.example.data.entity.meal.Dish

data class Record(
    val dishes: List<Dish>,
    val timestamp: String
)

