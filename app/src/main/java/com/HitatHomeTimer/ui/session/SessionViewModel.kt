package com.HitatHomeTimer.ui.session

import android.view.View
import androidx.lifecycle.*
import androidx.lifecycle.ViewModel
import com.HitatHomeTimer.repository.SessionRepository
import com.HitatHomeTimer.repository.localdata.entities.Session
import com.HitatHomeTimer.repository.localdata.relations.SessionWithStepsAndExercises
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.hitathometimer.R
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class SessionViewModel(
    private val repository: SessionRepository,
    private val arguments: SessionWithStepsAndExercises?
) : ViewModel() {

    // Using LiveData and caching what allSteps returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    val allSessionWithStepsAndExercises: LiveData<List<SessionWithStepsAndExercises>> =
        repository.allSessionWithStepsAndExercises.asLiveData()

//    /**
//     * Launching a new coroutine to insert the data in a non-blocking way
//     */
//    fun insertSessionWithStepsAndExercises(sessionWithStepsAndExercises: SessionWithStepsAndExercises) =
//        viewModelScope.launch {
//            repository.insertSessionWithStepsAndExercises(sessionWithStepsAndExercises)
//        }

    val args = arguments

    fun deleteSession(session: Session) = viewModelScope.launch {
        repository.deleteSession(session)
    }

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
    private val args: SessionWithStepsAndExercises?
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SessionViewModel::class.java)) {
            return SessionViewModel(repository, args) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}