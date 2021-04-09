package com.HitatHomeTimer.ui.addeditsession

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistryOwner
import com.HitatHomeTimer.repository.SessionRepository
import com.HitatHomeTimer.repository.localdata.entities.Exercise
import com.HitatHomeTimer.repository.localdata.entities.Session
import com.HitatHomeTimer.repository.localdata.entities.Step
import com.HitatHomeTimer.repository.localdata.relations.SessionWithStepsAndExercises
import com.HitatHomeTimer.repository.localdata.relations.StepWithExercises
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class CreateSessionViewModel(
    private val stateHandle: SavedStateHandle,
    private val repository: SessionRepository,
) : ViewModel() {

    /**
     * @SavedStateHandle data retrieved and set LiveData to be observed
     */
    private val newSessionWithStepsAndExercises = createNewSession()


    private val stateHandleSession =
        stateHandle.getLiveData<SessionWithStepsAndExercises>("addedditsession")

    val sessionWithStepsAndExercises = stateHandle.getLiveData<SessionWithStepsAndExercises>("update", stateHandleSession.value ?: newSessionWithStepsAndExercises.value)

    private var mutableSessionWithStepsAndExercises: MutableLiveData<SessionWithStepsAndExercises> = sessionWithStepsAndExercises
        set(value) {
            field = value
            stateHandle.set("update", value)
        }

    /**
     * edit SessionWithStepsAndExercises functions and read events
     */

    private fun onCreateNewExercise(adapterPosition: Int) = viewModelScope.launch {
        mutableSessionWithStepsAndExercises.value?.stepList?.get(adapterPosition)?.exerciseLists =
            mutableSessionWithStepsAndExercises.value?.stepList.orEmpty()[adapterPosition].exerciseLists.plusElement(
                Exercise("Work")
            )

        sessionWithStepsAndExercises.postValue(mutableSessionWithStepsAndExercises.value)
    }

    private fun onUpdateStepTimer(adapterPosition: Int, updateTime: UpdateTimeNumber, times: Int) = viewModelScope.launch  {
        when (updateTime) {

            UpdateTimeNumber.INCREMENT -> {
                mutableSessionWithStepsAndExercises.value!!.copy().stepList[adapterPosition].step.timesNumber += 1

                sessionWithStepsAndExercises.postValue(mutableSessionWithStepsAndExercises.value)
            }

            UpdateTimeNumber.DECREMENT -> {
                if (sessionWithStepsAndExercises.value!!.stepList[adapterPosition].step.timesNumber > 1) {
                    mutableSessionWithStepsAndExercises.value!!.stepList[adapterPosition].step.timesNumber -= 1

                    sessionWithStepsAndExercises.postValue(mutableSessionWithStepsAndExercises.value)
                }
            }

            UpdateTimeNumber.EDIT -> {
                    mutableSessionWithStepsAndExercises.value!!.stepList[adapterPosition].step.timesNumber =
                        times
                sessionWithStepsAndExercises.postValue(mutableSessionWithStepsAndExercises.value)
            }
        }
    }

    private fun onDuplicateStep(stepWithExercises: StepWithExercises) = viewModelScope.launch {
        /**
         * get a copy of step and exercises (because liveData keep tracking first values and modifies it when base value is modified)
         */
        val duplicateExercisesList = mutableListOf<Exercise>()
        (stepWithExercises.exerciseLists.forEach {
            duplicateExercisesList.add(it.copy())
        })

        /**
         * duplicate the value in a new value and add it to the base steps list
         */
        val duplicateStep = StepWithExercises(step = stepWithExercises.step.copy(), exerciseLists = duplicateExercisesList)
        mutableSessionWithStepsAndExercises.value?.stepList = mutableSessionWithStepsAndExercises.value!!.stepList.plusElement(duplicateStep)

        sessionWithStepsAndExercises.postValue(mutableSessionWithStepsAndExercises.value)
    }

    private fun onCreateNewStep() = viewModelScope.launch {
        mutableSessionWithStepsAndExercises.value?.stepList =
            mutableSessionWithStepsAndExercises.value?.stepList?.plus(StepWithExercises(Step(), listOf(Exercise("Work"))))!!

        sessionWithStepsAndExercises.postValue(mutableSessionWithStepsAndExercises.value)
    }


    private fun onDeleteStep(adapterPosition: Int) = viewModelScope.launch {
        mutableSessionWithStepsAndExercises.value?.stepList =
            mutableSessionWithStepsAndExercises.value?.stepList?.minus(sessionWithStepsAndExercises.value?.stepList?.get(adapterPosition)!!)!!

        sessionWithStepsAndExercises.postValue(mutableSessionWithStepsAndExercises.value)
    }

    private fun onDuplicateExercise(parentPosition: Int, exercise: Exercise) = viewModelScope.launch {
        /**
         * get a copy of exercise (because liveData keep tracking first values and modifies it when base value is modified)
         */
        val duplicateExercise: Exercise = exercise.copy()

        mutableSessionWithStepsAndExercises.value?.copy()?.stepList?.get(parentPosition)?.exerciseLists =
            mutableSessionWithStepsAndExercises.value?.copy()?.stepList?.get(parentPosition)?.exerciseLists?.plus(duplicateExercise)!!

        sessionWithStepsAndExercises.postValue(mutableSessionWithStepsAndExercises.value)
    }

    private fun onDeleteExercise(parentPosition: Int, position: Int) = viewModelScope.launch {

        /**
         * get a MutableList from liveData to use removeAt function to delete item at specific position (liveData hasn't this function)
         */
        val itemDeletedList: MutableList<Exercise> = mutableSessionWithStepsAndExercises.value!!.stepList[parentPosition].exerciseLists.toMutableList()
        itemDeletedList.removeAt(position)
        mutableSessionWithStepsAndExercises.value!!.stepList[parentPosition].exerciseLists = itemDeletedList
        sessionWithStepsAndExercises.value = mutableSessionWithStepsAndExercises.value
    }

    private fun onUpdateExerciseTimer(parentPosition: Int, adapterPosition: Int, updateTime: UpdateTimeNumber, timer: Long) = viewModelScope.launch {
        when (updateTime) {

            UpdateTimeNumber.INCREMENT -> {
                mutableSessionWithStepsAndExercises.value!!.stepList[parentPosition].exerciseLists[adapterPosition].timer += 1000L
                sessionWithStepsAndExercises.postValue(mutableSessionWithStepsAndExercises.value)
            }

            UpdateTimeNumber.DECREMENT -> {
                if (sessionWithStepsAndExercises.value!!.stepList[parentPosition].exerciseLists[adapterPosition].timer > 1000L) {
                    mutableSessionWithStepsAndExercises.value!!.stepList[parentPosition].exerciseLists[adapterPosition].timer -= 1000L
                    sessionWithStepsAndExercises.postValue(mutableSessionWithStepsAndExercises.value)
                }
            }

            UpdateTimeNumber.EDIT -> {
                mutableSessionWithStepsAndExercises.value!!.stepList[parentPosition].exerciseLists[adapterPosition].timer =
                        timer
                sessionWithStepsAndExercises.postValue(mutableSessionWithStepsAndExercises.value)
            }
        }
    }

    private fun onUpdateExerciseName(parentPosition: Int, adapterPosition: Int, newName: String) = viewModelScope.launch {
        mutableSessionWithStepsAndExercises.value!!.stepList[parentPosition].exerciseLists[adapterPosition].name =
            newName
        sessionWithStepsAndExercises.postValue(mutableSessionWithStepsAndExercises.value)
    }

    fun saveSessionClicked() {
        viewModelScope.launch {
            repository.upsertSession(sessionWithStepsAndExercises.value!!)
        }
    }

    /**
     * retrieve an event, checks which type is and what it will trigger
     */
    fun handleEvent(event: CreationListEvent) {
        when (event) {

            /**
             *  Steps clicks events
             */

            is CreationListEvent.OnNewStepClicked -> onCreateNewStep()
            is CreationListEvent.OnDuplicateStepClicked -> onDuplicateStep(event.stepWithExercises)
            is CreationListEvent.OnStepTimerChanged -> onUpdateStepTimer(event.adapterPosition, event.updateTime, event.times)
            is CreationListEvent.OnDeleteStepClick -> onDeleteStep(event.adapterPosition)

            /**
             * Exercises clicks events
             */
            is CreationListEvent.OnNewExerciseClicked -> onCreateNewExercise(event.adapterPosition)
            is CreationListEvent.OnDuplicateExerciseClicked -> onDuplicateExercise(event.parentPosition, event.exercise)
            is CreationListEvent.OnExerciseTimerChanged -> onUpdateExerciseTimer(event.parentPosition ,event.adapterPosition, event.updateTime, event.timer)
            is CreationListEvent.OnExerciseNameChanged -> onUpdateExerciseName(event.parentPosition ,event.adapterPosition, event.newName)
            is CreationListEvent.OnDeleteExerciseClicked -> onDeleteExercise(event.parentPosition, event.position)
        }
    }

    /**
     * create a new base session if argument is null or not chosen
     */
    private fun createNewSession() = MutableLiveData(
        SessionWithStepsAndExercises(
            Session("Workout"),
            stepList = listOf(
                StepWithExercises(
                    Step(),
                    exerciseLists = listOf(
                        Exercise(
                            "work", timer = 30000
                        ),
                        Exercise("Rest", timer = 15000)
                    )))))

}

/**
 * Edit events types
 */
enum class UpdateTimeNumber { INCREMENT , DECREMENT, EDIT }

/**
 * event types which retrieve data from adapters and fragment
 */
sealed class CreationListEvent {

    /**
     *  edit steps
     */
    data class OnStepTimerChanged(val adapterPosition: Int, val updateTime: UpdateTimeNumber, val times: Int = 0) : CreationListEvent()
    data class OnDuplicateStepClicked(val stepWithExercises: StepWithExercises) : CreationListEvent()
    object OnNewStepClicked : CreationListEvent()
    data class OnNewExerciseClicked(val adapterPosition: Int) : CreationListEvent()
    data class OnDeleteStepClick(val adapterPosition: Int) : CreationListEvent()


    /**
     *  edit exercises
     */
    data class OnExerciseTimerChanged(val parentPosition: Int, val adapterPosition: Int, val updateTime: UpdateTimeNumber, val timer: Long = 0L) : CreationListEvent()
    data class OnExerciseNameChanged(val parentPosition: Int, val adapterPosition: Int, val newName: String) : CreationListEvent()
    data class OnDuplicateExerciseClicked(val parentPosition: Int, val exercise: Exercise) : CreationListEvent()
    data class OnDeleteExerciseClicked(val parentPosition: Int, val position: Int) : CreationListEvent()
}



/**
 *  @ViewModelFactory to retrieve Fragment args in SavedStateHandle (bundle)
 */
@Suppress("UNCHECKED_CAST")
class CreateSessionViewModelFactory(
    owner: SavedStateRegistryOwner,
    private val repository: SessionRepository,
    defaultArgs: Bundle? = null
) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
    override fun <T : ViewModel?> create(
        key: String, modelClass: Class<T>, handle: SavedStateHandle
    ): T {
        if (modelClass.isAssignableFrom(CreateSessionViewModel::class.java)) {
            return CreateSessionViewModel(handle, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


