package com.example.cache

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.cache.pojo.WorkoutMetaPOJO
import com.example.cache.pojo.WorkoutRecordDao
import com.example.cache.pojo.WorkoutRecordPOJO

@Database(
    entities = [WorkoutRecordPOJO::class, WorkoutMetaPOJO::class],
    version = 3
)

abstract class MyRoomDatabase : RoomDatabase() {
    abstract fun getWorkoutDao(): WorkoutRecordDao

    companion object {
        @Volatile
        private var INSTANCE: MyRoomDatabase? = null

        fun getInstance(context: Context): MyRoomDatabase = INSTANCE ?: synchronized(this) {
            INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext, MyRoomDatabase::class.java, "Workout_db"
        ).fallbackToDestructiveMigration()
            .build()
    }
}