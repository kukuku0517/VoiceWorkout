package com.example.presentation.workout

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.data.entity.workout.WorkoutAction
import com.example.domain.workout.ParseWorkoutActionUC
import com.example.presentation.meal.BaseViewModel
import com.example.presentation.set
import com.example.presentation.update
import com.example.util.tag
import kotlinx.coroutines.launch

data class WorkoutRecordView(
    val totalText: String,
    val remainderText: String,
    val records: List<WorkoutAction>,
    val recordingState: RecordingState
)

enum class RecordingState {
    RECORDING,
    STOPPED
}

class WorkoutRecordViewModel(
    private val parseWorkoutActionUC: ParseWorkoutActionUC
) : BaseViewModel<WorkoutRecordView>() {

    init {
        state.set {
            WorkoutRecordView("", "", listOf(), RecordingState.STOPPED)
        }
    }

    private val cachedText = listOf<String>()


    fun parseRemainderText(text: String) {

    }

    private fun parseText(text: String) {
        viewModelScope.launch {
            val (actions, remainder) = parseWorkoutActionUC.invoke(text)

            state.update {
                it.copy(
                    remainderText = remainder,
                    records = it.records.toMutableList().apply { addAll(actions) }.toList()
                )
            }
        }
    }

    fun setState(recordingState: RecordingState) {
        Log.i(tag(), "setState $recordingState ${Thread.currentThread()}")
        state.update {
            it.copy(
                recordingState = recordingState
            )
        }
    }

    fun setMessage(words: List<String>?) {
        Log.i(tag(), "setMessage $words ${Thread.currentThread()}")
        words ?: return
        state.update {
            it.copy(
                totalText = it.totalText + "\n " + words.reduce { a, b -> "$a $b" }
            )
        }
    }
}