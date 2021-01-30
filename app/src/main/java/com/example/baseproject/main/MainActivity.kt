package com.example.baseproject.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.baseproject.R
import com.example.baseproject.workoutrecord.WorkoutRecordActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startActivity(Intent(this, WorkoutRecordActivity::class.java))
        finish()
    }
}