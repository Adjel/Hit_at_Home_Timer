package com.HitatHomeTimer.ui.practice

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.*
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.HitatHomeTimer.repository.SessionRepository
import com.HitatHomeTimer.repository.localdata.entities.Exercise
import com.HitatHomeTimer.repository.localdata.relations.SessionWithStepsAndExercises
import kotlinx.coroutines.runBlocking
import java.lang.IllegalArgumentException
import java.text.SimpleDateFormat
import java.util.*


class PracticeViewModel(
    private val repository: SessionRepository,
    private val stateHandle: SavedStateHandle
) : ViewModel() {

    // Fragment can't observe this liveData because is observing sessionWithStepsAndExercises, to avoiding a nullPointerException we directly retrieve the data
    private fun lastSessionWithStepsAndExercises(): SessionWithStepsAndExercises = runBlocking {
        repository.lastSessionWithStepsAndExercises()
    }

    private val sessionWithStepsAndExercisesFromStateHandle =
        stateHandle.get<SessionWithStepsAndExercises>("sessionToPractice")
            ?: lastSessionWithStepsAndExercises()

    val sessionWithStepsAndExercises = stateHandle.get<SessionWithStepsAndExercises>("currentStep")
        ?: sessionWithStepsAndExercisesFromStateHandle

    var updateSessionWithStepsAndExercises: SessionWithStepsAndExercises =
        sessionWithStepsAndExercises
        set(value) {
            field = value
            stateHandle.set("currentStep", value)
        }

    fun startCountdownClicked() {
        onStartCountdownTimer(sessionWithStepsAndExercises)
    }

    private var _TextCountDown: MutableLiveData<String> = MutableLiveData("00:00")
    val textCountDown: LiveData<String>
        get() = _TextCountDown




    private fun onStartCountdownTimer(sessionWithStepsAndExercises: SessionWithStepsAndExercises) {

        val dateFormat = SimpleDateFormat("mm:ss")
        dateFormat.timeZone = TimeZone.getTimeZone("GMT")

        var stepWithExercises = 0
        var exerciseIndex = 0
        var stepTimes: Int
        var doRepeat: Boolean
        var repeatedTimes = 0

        fun startCountDown(exercise: Exercise) {

                    val countdown = object : CountDownTimer(exercise.timer, 1000) {

                        override fun onTick(millisUntilFinished: Long) {

                            _TextCountDown.value = dateFormat.format(millisUntilFinished)

                            Log.d("PracticeViewModel", "millisUntilFinished :  ${dateFormat.format(millisUntilFinished)}")
//                            if (millisUntilFinished <= 5000) {
//                                Log.d("PracticeViewModel", "stop in : ${dateFormat.format(millisUntilFinished)}")
//                            }
                        }

                        fun launch() {
                            Log.d("PracticeViewModel", "LAUNCHED :  ${sessionWithStepsAndExercises.stepList[stepWithExercises].exerciseLists[exerciseIndex].timer}, stepWithExercises : ${stepWithExercises} , exerciseIndex : ${exerciseIndex} ")
                            startCountDown(sessionWithStepsAndExercises.stepList[stepWithExercises].exerciseLists[exerciseIndex])
                        }

                        override fun onFinish() {
                            if (stepWithExercises < sessionWithStepsAndExercises.stepList.size) {

                                stepTimes = sessionWithStepsAndExercises.stepList[stepWithExercises].step.timesNumber - repeatedTimes
                                doRepeat = stepTimes > 1

                                Log.d("PracticeViewModel", "IS stepWithExercises UNDER stepList.size -1 : stepWithExercises : ${stepWithExercises} , exerciseIndex : ${exerciseIndex} ,steptimes : ${stepTimes}, doRepeat : ${doRepeat}")

                                if (doRepeat) {
                                    launch()
                                    if (exerciseIndex + 1 < sessionWithStepsAndExercises.stepList[stepWithExercises].exerciseLists.size) {
                                        exerciseIndex += 1
                                        return
                                    } else if (exerciseIndex + 1 == sessionWithStepsAndExercises.stepList[stepWithExercises].exerciseLists.size) {
                                        exerciseIndex = 0
                                        repeatedTimes += 1
                                        Log.d("PracticeViewModel","repeatedTimes $repeatedTimes")
                                        return
                                    }
                                    return
                                }

                                if (exerciseIndex < sessionWithStepsAndExercises.stepList[stepWithExercises].exerciseLists.size) {
                                    launch()
                                    if (exerciseIndex + 1 < sessionWithStepsAndExercises.stepList[stepWithExercises].exerciseLists.size) {
                                        exerciseIndex += 1
                                        return
                                    } else {
                                        stepWithExercises += 1
                                        repeatedTimes = 0
                                        exerciseIndex = 0
                                        return
                                    }
                                }

                            } else {
                                // TODO Congrats
                                Log.d("PracticeViewModel","TIMER FINISHED")
                                // TODO Congrats
                            }
                        }
                    }
                    countdown.start()
                }
        startCountDown(sessionWithStepsAndExercises.stepList.first().exerciseLists.first())
        exerciseIndex += 1
    }




}

@Suppress("UNCHECKED_CAST")
class PracticeViewModelFactory(
    private val repository: SessionRepository,
    owner: SavedStateRegistryOwner,
    defaultArgs: Bundle? = null
) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
    override fun <T : ViewModel> create(
        key: String, modelClass: Class<T>, handle: SavedStateHandle
    ): T {
        if (modelClass.isAssignableFrom(PracticeViewModel::class.java)) {
            return PracticeViewModel(repository, handle) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}