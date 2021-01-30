package com.example.baseproject.workoutrecord

import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.baseproject.R
import com.example.util.tag
import com.example.presentation.workout.RecordingState
import com.example.presentation.workout.WorkoutRecordViewModel
import kotlinx.android.synthetic.main.activity_workout_record.*
import org.koin.android.ext.android.inject


class WorkoutRecordActivity : AppCompatActivity() {
    val workoutRecordViewModel: WorkoutRecordViewModel by inject()
    lateinit var mRecognizer: SpeechRecognizer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_record)
        mRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        mRecognizer.setRecognitionListener(listener)
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, packageName)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")

        mBtnWorkoutRecordStart.setOnClickListener {
            Toast.makeText(this, "음성인식시작", Toast.LENGTH_SHORT).show()
            mRecognizer.startListening(intent)
        }
        mBtnWorkoutRecordStop.setOnClickListener {
            mRecognizer.cancel()
        }

        workoutRecordViewModel.state.observe(this, Observer {
            mTvWorkoutRecordMessage.text = it.totalText
            mTvWorkoutRecordState.text = it.recordingState.name
        })
    }

    private val listener = object : RecognitionListener {
        override fun onReadyForSpeech(p0: Bundle?) {
            Log.i(this@WorkoutRecordActivity.tag(), "ready")
            workoutRecordViewModel.setState(RecordingState.RECORDING)
        }

        override fun onRmsChanged(p0: Float) {
        }

        override fun onBufferReceived(p0: ByteArray?) {
        }

        override fun onPartialResults(results: Bundle?) {
            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            Log.i(this@WorkoutRecordActivity.tag(), "partial" + matches.toString())
        }

        override fun onEvent(p0: Int, p1: Bundle?) {
        }

        override fun onBeginningOfSpeech() {
            Log.i(this@WorkoutRecordActivity.tag(), "begin")
        }

        override fun onEndOfSpeech() {
            Log.i(this@WorkoutRecordActivity.tag(), "end")

        }

        override fun onError(p0: Int) {
            Log.i(this@WorkoutRecordActivity.tag(), "Error $p0")
        }

        override fun onResults(results: Bundle?) {
            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            Log.i(this@WorkoutRecordActivity.tag(), matches.toString())
            workoutRecordViewModel.setMessage(matches?.toList())
            workoutRecordViewModel.setState(RecordingState.STOPPED)
        }

    }
}