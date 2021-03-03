package com.HitatHomeTimer.ui.practice

import androidx.lifecycle.*
import androidx.lifecycle.ViewModel
import androidx.navigation.NavArgs
import com.HitatHomeTimer.repository.SessionRepository
import com.HitatHomeTimer.repository.localdata.entities.Session
import com.HitatHomeTimer.repository.localdata.relations.SessionWithStepsAndExercises
import com.HitatHomeTimer.repository.localdata.relations.StepWithExercises
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException


class PracticeViewModel(
    private val repository: SessionRepository,
    private val args: SessionWithStepsAndExercises?
) : ViewModel() {

    // Using LiveData and caching what allSteps returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    val allSessionWithStepsAndExercises: LiveData<List<SessionWithStepsAndExercises>> =
        repository.allSessionWithStepsAndExercises.asLiveData()

    val firstStepWithExercises : LiveData<List<StepWithExercises>> =
        repository.firstStepWithExercise.asLiveData()

    val arguments = args

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
    private val args: SessionWithStepsAndExercises?
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PracticeViewModel::class.java)) {
            return PracticeViewModel(repository, args) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}