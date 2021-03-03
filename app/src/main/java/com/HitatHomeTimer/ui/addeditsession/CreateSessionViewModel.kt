package com.HitatHomeTimer.ui.addeditsession

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistryOwner
import com.HitatHomeTimer.repository.SessionRepository
import com.HitatHomeTimer.repository.localdata.entities.Exercise
import com.HitatHomeTimer.repository.localdata.entities.Session
import com.HitatHomeTimer.repository.localdata.entities.Step
import com.HitatHomeTimer.repository.localdata.relations.SessionWithStepsAndExercises
import com.HitatHomeTimer.repository.localdata.relations.StepWithExercises
import com.HitatHomeTimer.ui.practice.PracticeViewModel
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class CreateSessionViewModel(
    private val state: SavedStateHandle,
    private val repository: SessionRepository,
) : ViewModel() {

    // Using LiveData and caching what allSteps returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    val allSessionWithStepsAndExercises: LiveData<List<SessionWithStepsAndExercises>> =
        repository.allSessionWithStepsAndExercises.asLiveData()

    val firstStepWithExercises : LiveData<List<StepWithExercises>> =
        repository.firstStepWithExercise.asLiveData()

    private val getSessionWithStepsAndExercises = state.get<SessionWithStepsAndExercises>("shit")


//    val newSession = state.get<SessionWithStepsAndExercises>("shit") ?: SessionWithStepsAndExercises(Session("Workout"),
//    listOf(StepWithExercises(
//        Step(),
//        listOf(
//            Exercise("Work"), Exercise("Step")
//        )
//    )))
}

@Suppress("UNCHECKED_CAST")
class CreateSessionViewModelFactory(
    owner: SavedStateRegistryOwner,
    private val repository: SessionRepository,
    defaultArgs: Bundle? = null
) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
    override fun <T : ViewModel?> create(key: String, modelClass: Class<T>, handle: SavedStateHandle
    ): T {
        if (modelClass.isAssignableFrom(CreateSessionViewModel::class.java)) {
            return CreateSessionViewModel(handle, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
