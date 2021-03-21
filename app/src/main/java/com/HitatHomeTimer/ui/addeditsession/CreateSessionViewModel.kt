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

    private val newSessionWithStepsAndExercises = createNewSession()

    private val stateHandleSession =
        stateHandle.getLiveData<SessionWithStepsAndExercises>("addedditsession", newSessionWithStepsAndExercises.value)

    val sessionWithStepsAndExercises = stateHandle.getLiveData<SessionWithStepsAndExercises>("update", stateHandleSession.value ?: newSessionWithStepsAndExercises.value)

    private var mutableSessionWithStepsAndExercises: MutableLiveData<SessionWithStepsAndExercises> = sessionWithStepsAndExercises
        set(value) {
            field = value
            stateHandle.set("update", value)
        }


    private fun onCreateNewExercise(adapterPosition: Int, stepWithExercises: StepWithExercises) = viewModelScope.launch {

        mutableSessionWithStepsAndExercises.value?.stepList?.get(adapterPosition)?.exerciseLists =
            mutableSessionWithStepsAndExercises.value?.stepList.orEmpty()[adapterPosition].exerciseLists.orEmpty().plusElement(
                Exercise("Work")
            )

        sessionWithStepsAndExercises.postValue(mutableSessionWithStepsAndExercises.value)
    }

    //    var sessionWithStepsAndExercises: LiveData<SessionWithStepsAndExercises>? =
//        stateHandleSession.value?.session?.sessionId?.let {
//            repository.getSessionWithStepsAndExercisesById(
//                it
//            ).asLiveData()
//        }

    fun insertSessionWithStepsAndExercises(sessionWithStepsAndExercises: SessionWithStepsAndExercises) =
        viewModelScope.launch {
            repository.insertSessionWithStepsAndExercises(sessionWithStepsAndExercises)
        }


    val firstStepWithExercises: LiveData<List<StepWithExercises>> =
        repository.firstStepWithExercise.asLiveData()



    private fun onUpdateStepTimer(adapterPosition: Int, updateTime: UpdateTimeNumber, timer: Long) = viewModelScope.launch  {
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
                Log.d("ViewModel", "edit: test1 adapterPosition = ${adapterPosition}" )

                    mutableSessionWithStepsAndExercises.value!!.stepList[adapterPosition].step.timesNumber =
                        timer
                    Log.d("ViewModel", "edit: ")
                sessionWithStepsAndExercises.postValue(mutableSessionWithStepsAndExercises.value)
            }
        }
    }

    private fun onDuplicateStep(stepWithExercises: StepWithExercises) = viewModelScope.launch {

        // get a copy of step and exercises (because liveData keep tracking first values and modifies it when base value is modified)
        val duplicateExercisesList = mutableListOf<Exercise>()
        (stepWithExercises.exerciseLists.forEach {
            duplicateExercisesList.add(it.copy())
        })

        // duplicate the value in a new value and add it to the base steps list
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


    }


    private fun onDeleteExercise(parentPosition: Int, position: Int) = viewModelScope.launch {

        mutableSessionWithStepsAndExercises.value!!.stepList[parentPosition].exerciseLists =
            mutableSessionWithStepsAndExercises.value!!.stepList[parentPosition].exerciseLists.minus(
                mutableSessionWithStepsAndExercises.value!!.stepList[parentPosition].exerciseLists[position])

        sessionWithStepsAndExercises.postValue(mutableSessionWithStepsAndExercises.value)
    }




    private fun onUpdateExerciseTimer(parentPosition: Int, adapterPosition: Int, updateTime: UpdateTimeNumber, timer: Long) = viewModelScope.launch {
        when (updateTime) {

            UpdateTimeNumber.INCREMENT -> {
                Log.d(
                    "CreateSessionChild",
                    "in ViewModel : onUpdateExerciseTimer: parentPosition:  $parentPosition, adapterPosition : $adapterPosition"
                )

                mutableSessionWithStepsAndExercises.value!!.stepList[parentPosition].exerciseLists[adapterPosition].timer += 1000L

                Log.d(
                    "CreateSessionChild",
                    "in ViewModel : onUpdateExerciseTimer Values: parentPosition:  $parentPosition, timer : ${mutableSessionWithStepsAndExercises.value!!.stepList[parentPosition].exerciseLists[adapterPosition].timer}"
                )

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
        Log.d("TextWatcher", "onUpdateExerciseName newName = $newName")

        mutableSessionWithStepsAndExercises.value!!.stepList[parentPosition].exerciseLists[adapterPosition].name =
            newName

        sessionWithStepsAndExercises.postValue(mutableSessionWithStepsAndExercises.value)
    }


//    val stepWithExercises = sessionWithStepsAndExercises.value?.stepList
//
//
//
//    var sessionName = stateHandle.get<String>("sessionName") ?: sessionWithStepsAndExercises.value?.session?.name ?: "Workout"
//        set(value) {
//            field = value
//            stateHandle.set("sessionName", value)
//        }
//
//    var position = 0
//
//    var stepTimesNumber = stateHandle.get<Long>("stepTimesNumber") ?: sessionWithStepsAndExercises.value?.stepList?.get(position)?.step?.timesNumber
//        set(value) {
//            field = value
//            stateHandle.set("stepTimesNumber", value)
//        }


//    fun onStepButtonDeleteClicked(step: Step) = viewModelScope.launch { repository.deleteStep(step) }

    private fun createNewSession() = MutableLiveData<SessionWithStepsAndExercises>(
        SessionWithStepsAndExercises(
            Session("Workout"),
            stepList = listOf(
                StepWithExercises(
                    Step(),
                    exerciseLists = listOf(
                        Exercise(
                            "wooork", timer = 30000
                        ),
                        Exercise("Rest", timer = 15000)
                    )))))


    fun handleEvent(event: CreationListEvent) {
        when (event) {

            // Steps clicks events
            is CreationListEvent.OnNewStepClicked -> onCreateNewStep()
            is CreationListEvent.OnDuplicateStepClicked -> onDuplicateStep(event.stepWithExercises)
            is CreationListEvent.OnStepTimerChanged -> onUpdateStepTimer(event.adapterPosition, event.updateTime, event.timer)
            is CreationListEvent.OnDeleteStepClick -> onDeleteStep(event.adapterPosition)


            // Exercises clicks events
            is CreationListEvent.OnNewExerciseClicked -> onCreateNewExercise(event.adapterPosition, event.stepWithExercises)
            is CreationListEvent.OnDuplicateExerciseClicked -> onDuplicateExercise(event.parentPosition, event.exercise)
            is CreationListEvent.OnExerciseTimerChanged -> onUpdateExerciseTimer(event.parentPosition ,event.adapterPosition, event.updateTime, event.timer)
            is CreationListEvent.OnExerciseNameChanged -> onUpdateExerciseName(event.parentPosition ,event.adapterPosition, event.newName)
            is CreationListEvent.OnDeleteExerciseClicked -> onDeleteExercise(event.parentPosition, event.position)
        }
    }
}

enum class UpdateTimeNumber { INCREMENT , DECREMENT, EDIT }

sealed class CreationListEvent {

    // Edit Steps
    data class OnStepTimerChanged(val adapterPosition: Int, val updateTime: UpdateTimeNumber, val timer: Long = 0L) : CreationListEvent()
    data class OnDuplicateStepClicked(val stepWithExercises: StepWithExercises) : CreationListEvent()
    object OnNewStepClicked : CreationListEvent()
    data class OnNewExerciseClicked(val adapterPosition: Int, val stepWithExercises: StepWithExercises) : CreationListEvent()
    data class OnDeleteStepClick(val adapterPosition: Int) : CreationListEvent()


    // Edit exercises
    data class OnExerciseTimerChanged(val parentPosition: Int, val adapterPosition: Int, val updateTime: UpdateTimeNumber, val timer: Long = 0L) : CreationListEvent()
    data class OnExerciseNameChanged(val parentPosition: Int, val adapterPosition: Int, val newName: String) : CreationListEvent()
    data class OnDuplicateExerciseClicked(val parentPosition: Int, val exercise: Exercise) : CreationListEvent()
    data class OnDeleteExerciseClicked(val parentPosition: Int, val position: Int) : CreationListEvent()
}


// ViewModel Factory to retrieve Fragment args in SavedStateHandle (bundle)
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


