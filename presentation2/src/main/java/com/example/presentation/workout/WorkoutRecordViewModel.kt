package com.example.presentation.workout

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.data.entity.workout.WorkoutAction
import com.example.data.entity.workout.WorkoutRecord
import com.example.domain.workout.GetWorkoutUC
import com.example.domain.workout.ParseWorkoutActionUC
import com.example.domain.workout.PostWorkoutUC
import com.example.presentation.meal.BaseViewModel
import com.example.presentation.set
import com.example.presentation.update
import com.example.util.Time
import com.example.util.replaceIf
import com.example.util.tag
import kotlinx.coroutines.launch
import java.lang.Exception

data class WorkoutRecordView(
    val totalText: String,
    val remainderText: String,
    val records: List<WorkoutAction>,
    val recordingState: RecordingState,
    val diff: Boolean = false,
    val date: Time
)

enum class RecordingState {
    RECORDING,
    STOPPED
}

class WorkoutRecordViewModel(
    private val parseWorkoutActionUC: ParseWorkoutActionUC,
    private val getWorkoutUC: GetWorkoutUC,
    private val postWorkoutUC: PostWorkoutUC
) : BaseViewModel<WorkoutRecordView>() {

    init {
        state.set {
            WorkoutRecordView("", "", listOf(), RecordingState.STOPPED, false, Time.now())
        }
    }

    private val cachedText = listOf<String>()
    private val cachedActions = listOf<WorkoutAction>()


    fun setRecordState(recordingState: RecordingState) {
        Log.i(tag(), "setState $recordingState ${Thread.currentThread()}")
        state.update {
            it.copy(
                recordingState = recordingState
            )
        }
    }

    fun refresh() {
        viewModelScope.launch {
            val records = getWorkoutUC.invoke(state.value!!.date).workoutActions
            state.update {
                it.copy(
                    records = records
                )
            }
        }
    }

    fun setMessage(words: List<String>?) {
        Log.i(tag(), "setMessage $words ${Thread.currentThread()}")
        words ?: return

        viewModelScope.launch {
            try {

                val (actions, remainder) = parseWorkoutActionUC.invoke(words.joinToString(" ") to state.value!!.date)

                state.update {
                    it.copy(
                        totalText = it.totalText + "\n " + words.reduce { a, b -> "$a $b" },
                        remainderText = remainder,
                        records = it.records.toMutableList().apply { addAll(actions) }.toList()
                    )
                }

                postWorkoutUC.invoke(WorkoutRecord(Time.now(), actions))
            } catch (e: Exception) {
                Log.w(tag(), e)
                toastEvent.value = "에러"
            }
        }
    }

    fun setDate(date: Time) {
        viewModelScope.launch {
            val records = getWorkoutUC.invoke(date).workoutActions

            state.update {
                it.copy(
                    records = records,
                    date = date
                )
            }
        }
    }

    fun updateAction(action: WorkoutAction) {
        viewModelScope.launch {
            val actions = postWorkoutUC.invoke(
                WorkoutRecord(
                    Time.now(),
                    state.value!!.records.replaceIf(action) { it.id == action.id }
                )
            ).workoutActions
            state.update { it.copy(records = actions) }
        }
    }
}