package com.HiitHomeTimer.ui.session

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistryOwner
import com.HiitHomeTimer.repository.SessionRepository
import com.HiitHomeTimer.repository.localdata.entities.Session
import com.HiitHomeTimer.repository.localdata.relations.SessionWithStepsAndExercises
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.hiithometimer.R
import kotlinx.coroutines.launch

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
        floatingActionButton.drawable.setTint(ContextCompat.getColor(floatingActionButton.context, R.color.ivory))
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