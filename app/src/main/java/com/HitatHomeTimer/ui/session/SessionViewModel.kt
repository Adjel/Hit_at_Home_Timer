package com.HitatHomeTimer.ui.session

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.*
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.HitatHomeTimer.repository.SessionRepository
import com.HitatHomeTimer.repository.localdata.entities.Session
import com.HitatHomeTimer.repository.localdata.entities.Step
import com.HitatHomeTimer.repository.localdata.relations.SessionWithStepsAndExercises
import com.HitatHomeTimer.repository.localdata.relations.StepWithExercises
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.hitathometimer.R
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class SessionViewModel(
    private val repository: SessionRepository,
    val stateHandle: SavedStateHandle
) : ViewModel() {


    // Launching a new coroutine to insert the data in a non-blocking way
    private fun insertSessionWithStepsAndExercises(sessionWithStepsAndExercises: SessionWithStepsAndExercises) =
        viewModelScope.launch {  repository.insertSessionWithStepsAndExercises(sessionWithStepsAndExercises) }


    fun onDuplicateSessionClicked(sessionWithStepsAndExercises: SessionWithStepsAndExercises)  =
        viewModelScope.launch { insertSessionWithStepsAndExercises(sessionWithStepsAndExercises)
        }


    // Using LiveData and caching what allSteps returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    val allSessionWithStepsAndExercises: LiveData<List<SessionWithStepsAndExercises>> =
        repository.allSessionWithStepsAndExercises.asLiveData()

    fun onDeleteSessionClicked(session: Session) = viewModelScope.launch {
        repository.deleteSession(session)
    }


    // bottomNavigationView icons triggers and logic

    fun setBottomNavigationViewsCheckable(
        bottomNavigationView: BottomNavigationView,
        itemId: Int,
        isPracticeCheckable: Boolean,
    ) {
        bottomNavigationView.menu.getItem(itemId).isChecked = isPracticeCheckable
        bottomNavigationView.menu.getItem(itemId).isEnabled = isPracticeCheckable
    }

    fun setFloatingActionButtonColor(
        floatingActionButton: FloatingActionButton,
        view: View,
    ) {
        floatingActionButton.drawable.setTint(view.resources.getColor(R.color.ivory))
    }
}


@Suppress("UNCHECKED_CAST")
class SessionViewModelFactory(
    private val repository: SessionRepository,
    owner: SavedStateRegistryOwner,
    defaultArgs: Bundle? = null
) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
    override fun <T : ViewModel> create(
        key: String, modelClass: Class<T>, handle: SavedStateHandle
    ): T {
        if (modelClass.isAssignableFrom(SessionViewModel::class.java)) {
            return SessionViewModel(repository, handle) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}