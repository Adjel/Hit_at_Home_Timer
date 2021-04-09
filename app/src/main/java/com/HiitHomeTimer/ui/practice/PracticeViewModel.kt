package com.HiitHomeTimer.ui.practice

import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.*
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.HiitHomeTimer.repository.SessionRepository
import com.HiitHomeTimer.repository.localdata.entities.Exercise
import com.HiitHomeTimer.repository.localdata.entities.Step
import com.HiitHomeTimer.repository.localdata.relations.SessionWithStepsAndExercises
import kotlinx.coroutines.runBlocking
import java.lang.IllegalArgumentException
import java.text.SimpleDateFormat
import java.util.*


class PracticeViewModel(
    private val repository: SessionRepository,
    private val stateHandle: SavedStateHandle
) : ViewModel() {

    private var countDownTimer: CountDownTimer? = null
    private lateinit var prepareCountDownTimer: CountDownTimer

    var stepWithExercisesIndex = 0
    var exerciseIndex = 0
    var stepTimes: Int = 0
    var doRepeat: Boolean = false
    var repeatedTimes = 0
    var timeLeft: Long = 0
    var tones = ToneGenerator(AudioManager.STREAM_MUSIC, 250)
    val dateFormat = SimpleDateFormat("mm:ss")

    /**
     * Fragment can't observe this liveData because is retrieving sessionWithStepsAndExercises, to avoiding a nullPointerException we directly retrieve the data
     */
    private fun lastSessionWithStepsAndExercises(): SessionWithStepsAndExercises? = runBlocking {
        repository.lastSessionWithStepsAndExercises()
    }

    val sessionWithStepsAndExercises = stateHandle.get<SessionWithStepsAndExercises?>("sessionToPractice")
        ?: lastSessionWithStepsAndExercises()

    /**
     * CountdownTimerFunction
     * @Start
     * @Pause
     * @Resum
     * @Reset
     */
    private fun startClicked() {

        dateFormat.timeZone = TimeZone.getTimeZone("GMT")
        _timerState.value = TimerState.PREPARING

        if (stepWithExercisesIndex == 0 && exerciseIndex == 0) {

            prepareCountDownTimer = object : CountDownTimer(5000, 500) {
                override fun onTick(millisUntilFinished: Long) {
                    _textCountDown.value = dateFormat.format(millisUntilFinished)

                    if (millisUntilFinished > 0L && millisUntilFinished.rem(1000) > 500) {
                        tones.startTone(ToneGenerator.TONE_PROP_BEEP,2000)
                    }
                }

                override fun onFinish() {
                    tones.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 2000)
                    startCountDown(sessionWithStepsAndExercises!!.stepList[stepWithExercisesIndex].exerciseLists[exerciseIndex])
                    _timerState.value = TimerState.RUNNING

                    _currentStepTimer.value = sessionWithStepsAndExercises.stepList.first().step
                    _currentExerciseTimer.value = sessionWithStepsAndExercises.stepList.first().exerciseLists.first()

                    _buttonsStates.value = ButtonsStates.isPreparationRunning(false)
                }

            }
            prepareCountDownTimer.start()
        }
        _buttonsStates.value = ButtonsStates.AreButtonsEnable(isStartEnable = false, isPauseEnable = true, isResetEnable = false)
        _buttonsStates.value = ButtonsStates.isPreparationRunning(true)
    }

    fun preparationPauseClicked() {
        Log.d("timer", "preparationPauseClicked")
        tones.stopTone()
        _buttonsStates.value = ButtonsStates.isPreparationRunning(false)
        prepareCountDownTimer.cancel()
        resetCountDown()
    }

    private fun pauseClicked() {
        countDownTimer?.let { onCancelCountDowTimer(it) }
        _buttonsStates.value = ButtonsStates.AreButtonsEnable(isStartEnable = false, isPauseEnable = false, isResetEnable = true, isResumeEnable = true)
    }

    private fun resumedClicked() {
        Log.d("timer", "resumedClicked stepWithExercisesIndex:  ${stepWithExercisesIndex}, exerciseIndex ${exerciseIndex}, timer: ${timeLeft}")
        startCountDown(null, timeLeft)
        _buttonsStates.value = ButtonsStates.AreButtonsEnable(isStartEnable = false, isPauseEnable = true, isResetEnable = false)
    }

    private fun resetClicked() {
        resetCountDown()
    }

    private fun resetCountDown() {
    _buttonsStates.value = ButtonsStates.AreButtonsEnable(
    isStartEnable = true,
    isPauseEnable = false,
    isResetEnable = false,
    isResumeEnable = false
    )
    _textCountDown.value = "00:00"
    stepWithExercisesIndex = 0
    exerciseIndex = 0
    timeLeft = 0
    repeatedTimes = 0
    _currentStepTimer.value = Step(stepId = -1)
    _currentExerciseTimer.value = Exercise("", exerciseId = -1)
    }

    /**
     * LiveData which will be observed in fragment.
     * Adapter will observe fragment LiveData to update focus on step and exercise UI
     */
    private var _currentStepTimer: MutableLiveData<Step> = MutableLiveData()
    val currentStepTimer: LiveData<Step>
        get() = _currentStepTimer
    private var _currentExerciseTimer: MutableLiveData<Exercise> = MutableLiveData()
    val currentExerciseTimer: LiveData<Exercise>
        get() = _currentExerciseTimer

    /**
     * LiveData which will be observed in fragment to update Timer and buttons in UI
     */
    private var _textCountDown: MutableLiveData<String> = MutableLiveData("00:00")
    val textCountDown: LiveData<String>
        get() = _textCountDown

    private var _buttonsStates: MutableLiveData<ButtonsStates> = MutableLiveData()
    val buttonsStates: LiveData<ButtonsStates>
        get() = _buttonsStates

    private var _timerState: MutableLiveData<TimerState> = MutableLiveData(TimerState.PREPARING)
    val timerState: LiveData<TimerState>
    get() = _timerState


    fun buttonsClicks(event: ButtonsClicks) {
        when(event) {
            is ButtonsClicks.OnStart -> startClicked()
            is ButtonsClicks.OnPause -> pauseClicked()
            is ButtonsClicks.OnReset -> resetClicked()
            is ButtonsClicks.OnResumed -> resumedClicked()
        }
    }

    sealed class ButtonsStates {
        data class AreButtonsEnable(val isStartEnable: Boolean = true,
                                    val isPauseEnable: Boolean = false,
                                    val isResetEnable: Boolean = false,
                                    val isResumeEnable: Boolean = false)
            : ButtonsStates()
        data class isPreparationRunning(val isPreparationCountDownRunning: Boolean = false)
            : ButtonsStates()
    }

    sealed class ButtonsClicks {
        object OnStart: ButtonsClicks()
        object OnPause: ButtonsClicks()
        object OnReset: ButtonsClicks()
        object OnResumed: ButtonsClicks()
    }

    enum class TimerState {
        PREPARING, RUNNING, FINISHED, PAUSED, RESET
    }

    /**
     * @CountDownTimer class to create a loop on every timer
     * */

      fun startCountDown(exercise: Exercise?, timer: Long = 0) {

          dateFormat.timeZone = TimeZone.getTimeZone("GMT")

        countDownTimer = object : CountDownTimer(exercise?.timer?.plus(2000) ?: timer,500) {

            override fun onTick(millisUntilFinished: Long) {

                timeLeft = millisUntilFinished

                if (millisUntilFinished < exercise?.timer?.plus(1000) ?: timer) {
                    _textCountDown.value = dateFormat.format(millisUntilFinished)
                }
                if (millisUntilFinished in 500..5000 && millisUntilFinished < exercise?.timer ?: timer && millisUntilFinished.rem(1000) > 500) {
                    tones.startTone(ToneGenerator.TONE_PROP_BEEP,2000)
                }
            }
            /**
             * @CountDown loop which will trigger all timers in a session
             */
            fun launch() {
                startCountDown(sessionWithStepsAndExercises!!.stepList[stepWithExercisesIndex].exerciseLists[exerciseIndex])
                _currentStepTimer.value =
                    sessionWithStepsAndExercises.stepList[stepWithExercisesIndex].step
                _currentExerciseTimer.value =
                    sessionWithStepsAndExercises.stepList[stepWithExercisesIndex].exerciseLists[exerciseIndex]
            }

            override fun onFinish() {
                if (stepWithExercisesIndex < sessionWithStepsAndExercises!!.stepList.size) {

                    tones.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 2000)
                    /**
                     * onFinish is only called when onStart was called. When onStart is called,
                     * only the first timer is launch. If the first is launched, onFinish access to the second and launch loop.
                     */
                    if (stepWithExercisesIndex == 0 && exerciseIndex == 0) {
                        exerciseIndex += 1
                    }

                    /**
                     * Check if all exercises timers have been read, if yes, check if have to read again (repetition)
                     */
                    if (exerciseIndex == sessionWithStepsAndExercises.stepList[stepWithExercisesIndex].exerciseLists.size - 1) {

                        stepTimes =
                            sessionWithStepsAndExercises.stepList[stepWithExercisesIndex].step.timesNumber - repeatedTimes
                        doRepeat = stepTimes > 1

                        if (doRepeat) {
                            launch()
                            if (exerciseIndex + 1 < sessionWithStepsAndExercises.stepList[stepWithExercisesIndex].exerciseLists.size) {
                                exerciseIndex += 1

                            } else if (exerciseIndex + 1 == sessionWithStepsAndExercises.stepList[stepWithExercisesIndex].exerciseLists.size) {
                                exerciseIndex = 0
                                repeatedTimes += 1
                            }
                            return
                        }
                    }

                    if (exerciseIndex < sessionWithStepsAndExercises.stepList[stepWithExercisesIndex].exerciseLists.size) {
                        launch()
                        if (exerciseIndex + 1 < sessionWithStepsAndExercises.stepList[stepWithExercisesIndex].exerciseLists.size) {
                            exerciseIndex += 1
                        } else {
                            stepWithExercisesIndex += 1

                            repeatedTimes = 0
                            exerciseIndex = 0
                        }
                    }

                } else {
                    // TODO Congrats
                        _timerState.value = TimerState.FINISHED
                    tones.startTone(ToneGenerator.TONE_PROP_BEEP2,600)
                 resetCountDown()
                    Log.d("PracticeViewModel", "TIMER FINISHED")
                // TODO Congrats
                }
            }
        }
        countDownTimer!!.start()

      }


    private fun onCancelCountDowTimer(timer: CountDownTimer) : Long {
        timer.cancel()
        tones.stopTone()
        return timeLeft
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