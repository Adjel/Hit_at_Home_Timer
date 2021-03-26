package com.HitatHomeTimer.ui.practice

import android.os.Bundle
import androidx.lifecycle.*
import androidx.lifecycle.ViewModel
import androidx.navigation.NavArgs
import androidx.savedstate.SavedStateRegistryOwner
import com.HitatHomeTimer.repository.SessionRepository
import com.HitatHomeTimer.repository.localdata.entities.Exercise
import com.HitatHomeTimer.repository.localdata.entities.Session
import com.HitatHomeTimer.repository.localdata.entities.Step
import com.HitatHomeTimer.repository.localdata.relations.SessionWithStepsAndExercises
import com.HitatHomeTimer.repository.localdata.relations.StepWithExercises
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException


class PracticeViewModel(
    private val repository: SessionRepository,
    private val stateHandle: SavedStateHandle
) : ViewModel() {

    // Using LiveData and caching what allSteps returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    private val lastSessionWithStepsAndExercises: LiveData<List<SessionWithStepsAndExercises>> =
        repository.lastSessionWithStepsAndExercises.asLiveData()

    private val sessionWithStepsAndExercisesFromStateHandle = stateHandle.getLiveData<SessionWithStepsAndExercises>("sessionToPractice", lastSessionWithStepsAndExercises.value?.last())

    val sessionWithStepsAndExercises = stateHandle.getLiveData<SessionWithStepsAndExercises>("currentStep", sessionWithStepsAndExercisesFromStateHandle.value)
//        val sessionWithStepsAndExercises : MutableLiveData<SessionWithStepsAndExercises> = stateHandle.getLiveData("currentStep",

//    /**
//     * Launching a new coroutine to insert the data in a non-blocking way
//     */
//    fun insertSessionWithStepsAndExercises(sessionWithStepsAndExercises: SessionWithStepsAndExercises) =
//        viewModelScope.launch {
//            repository.insertSessionWithStepsAndExercises(sessionWithStepsAndExercises)
//        }

    fun deleteSession(session: Session) = viewModelScope.launch {
        repository.deleteSession(session)
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