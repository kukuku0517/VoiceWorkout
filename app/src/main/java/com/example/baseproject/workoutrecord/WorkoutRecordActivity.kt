package com.example.baseproject.workoutrecord

import android.animation.ValueAnimator
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.DatePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.baseproject.R
import com.example.baseproject.util.SimpleRecyclerViewAdapter
import com.example.data.entity.workout.WorkoutAction
import com.example.presentation.workout.RecordingState
import com.example.presentation.workout.WorkoutRecordViewModel
import com.example.util.Time
import com.example.util.tag
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.android.synthetic.main.activity_workout_record.*
import kotlinx.android.synthetic.main.item_workout_record.view.*
import org.koin.android.ext.android.inject


class WorkoutRecordActivity : AppCompatActivity() {
    val workoutRecordViewModel: WorkoutRecordViewModel by inject()
    lateinit var mRecognizer: SpeechRecognizer
    var listening = false

    override fun onResume() {
        super.onResume()
        workoutRecordViewModel.refresh()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_record)
        mRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        mRecognizer.setRecognitionListener(listener)

        val adapter = SimpleRecyclerViewAdapter(
            this,
            object : SimpleRecyclerViewAdapter.RecyclerProvider<WorkoutAction>() {
                override fun getLayoutId(): Int {
                    return R.layout.item_workout_record
                }

                override fun onBindView(containerView: View, item: WorkoutAction) {
                    containerView.mTvWorkoutRecordItemName.text = item.meta?.name
                    containerView.mEtWorkoutRecordItemSet.setText(item.set.toString())
                    containerView.mEtWorkoutRecordItemWeight.setText(item.weight.toString())

                    when (item) {
                        is WorkoutAction.Count -> {
                            containerView.mEtWorkoutRecordItemCount.setText(item.count.toString())
                            containerView.mTvWorkoutRecordItemCount.setText("회")
                        }
                        is WorkoutAction.Duration -> {
                            containerView.mEtWorkoutRecordItemCount.setText(item.duration.toString())
                            containerView.mTvWorkoutRecordItemCount.setText("분")
                        }
                    }
                    updateIfChanged(containerView, item)

                    listOf(
                        containerView.mEtWorkoutRecordItemSet,
                        containerView.mEtWorkoutRecordItemCount,
                        containerView.mEtWorkoutRecordItemWeight
                    ).forEach {
                        it.addTextChangedListener {
                            updateIfChanged(containerView, item)
                        }
                        it.setOnTouchListener { view, motionEvent ->
                            it.onTouchEvent(motionEvent)
                            it.setSelection(it.text.length)
                            true
                        }
                    }

                    containerView.mBtnWorkoutSave.setOnClickListener {
                        workoutRecordViewModel.updateAction(
                            copyAndUpdateAction(item, containerView)
                        )
                    }
                }

                private fun copyAndUpdateAction(
                    item: WorkoutAction,
                    containerView: View
                ): WorkoutAction {
                    return when (item) {
                        is WorkoutAction.Duration -> item.copy(
                            set = containerView.mEtWorkoutRecordItemSet.text.toString().toInt(),
                            duration = containerView.mEtWorkoutRecordItemCount.text.toString()
                                .toInt(),
                            weight = containerView.mEtWorkoutRecordItemWeight.text.toString()
                                .toInt()
                        )
                        is WorkoutAction.Count -> item.copy(
                            set = containerView.mEtWorkoutRecordItemSet.text.toString()
                                .toIntOrNull() ?: 0,
                            count = containerView.mEtWorkoutRecordItemCount.text.toString()
                                .toIntOrNull() ?: 0,
                            weight = containerView.mEtWorkoutRecordItemWeight.text.toString()
                                .toIntOrNull() ?: 0
                        )
                    }
                }

                private fun updateIfChanged(containerView: View, item: WorkoutAction) {
                    containerView.mBtnWorkoutSave.isEnabled =
                        (containerView.mEtWorkoutRecordItemSet.text.toString() != item.set.toString()
                                || containerView.mEtWorkoutRecordItemCount.text.toString() != item.getCountOrDuration()
                            .toString()
                                || containerView.mEtWorkoutRecordItemWeight.text.toString() != item.weight
                            .toString())
                }

                override fun onClick(adapterPosition: Int) {

                }

            })
        mRvWorkoutRecord.adapter = adapter
        mRvWorkoutRecord.layoutManager = LinearLayoutManager(this)

        mBtnWorkoutRecord.setOnClickListener {
            if (!listening) {
                Toast.makeText(this, "음성인식시작", Toast.LENGTH_SHORT).show()
                listening = true
                startListening()
            } else {
                listening = false
                mRecognizer.cancel()
                mPbWorkoutRecord.progress = 0
            }
        }
        mTvWorkoutRecordDate.setOnClickListener {
            val now = Time.now()
            DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { picker, year, month, day ->
                    val newDate = Time.of(year, month + 1, day)
                    workoutRecordViewModel.setDate(newDate)
                }, now.getYear(), (now.getMonth() + 11).rem(12), now.getDay()
            ).show()
        }
        workoutRecordViewModel.state.observe(this, Observer {
            mTvWorkoutRecordMessage.text = it.totalText
            mTvWorkoutRecordState.text = it.recordingState.name
            mTvWorkoutRecordDate.text =
                if (it.date.getStartOfDay() == Time.now().getStartOfDay()) {
                    "오늘"
                } else {
                    it.date.format("MM/dd")
                }
            adapter.provider.items = it.records
        })
        workoutRecordViewModel.toastEvent.observe(this, Observer {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        })
    }

    private val recordTimer = ValueAnimator.ofInt(0, 100).apply {
        interpolator = LinearInterpolator()
        duration = 5 * 1000L
    }

    fun startListening() {

        if (!recordTimer.isRunning) {
            mPbWorkoutRecord.visibility = View.VISIBLE
            recordTimer.addUpdateListener { animation ->
                val progress = (animation?.animatedFraction ?: 0f) * 100f
                Log.i(tag(), "progress: " + progress.toString())
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    mPbWorkoutRecord.setProgress(progress.toInt(), true)
                } else {
                    mPbWorkoutRecord.progress = progress.toInt()
                }
                if (progress >= 99f) {
                    listening = false
                }
                if (!listening) {
                    recordTimer.cancel()
                    mPbWorkoutRecord.visibility = View.GONE
                }
            }
            recordTimer.start()
        }

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 10000L)
        intent.putExtra(
            RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS,
            10000L
        )
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 10000L)
        intent.putExtra("android.speech.extra.DICTATION_MODE", true)
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, packageName)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")
        mRecognizer.startListening(intent)
    }

    private val listener = object : RecognitionListener {
        override fun onReadyForSpeech(p0: Bundle?) {
            Log.i(this@WorkoutRecordActivity.tag(), "ready")
            workoutRecordViewModel.setRecordState(RecordingState.RECORDING)
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
            workoutRecordViewModel.setRecordState(RecordingState.STOPPED)
            if (listening) {
                startListening()
            }
        }

        override fun onResults(results: Bundle?) {
            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            Log.i(this@WorkoutRecordActivity.tag(), matches.toString())
            workoutRecordViewModel.setMessage(matches?.toList())
            workoutRecordViewModel.setRecordState(RecordingState.STOPPED)
            if (listening) {
                startListening()
            }
        }

    }
}