package com.HitatHomeTimer.ui.practice

import android.os.Bundle
import android.view.View
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.HitatHomeTimer.di.SessionApplication
import com.HitatHomeTimer.repository.localdata.entities.Exercise
import com.HitatHomeTimer.repository.localdata.entities.Step
import com.hitathometimer.R
import com.hitathometimer.databinding.FragmentPracticeBinding

class PracticeFragment : Fragment(R.layout.fragment_practice) {

    private val practiceParentListAdapter = PracticeParentListAdapter(this,)
    private val args: PracticeFragmentArgs by navArgs()
    private val practiceViewModel: PracticeViewModel by viewModels {
        PracticeViewModelFactory(
            ((requireActivity().application as SessionApplication).repository),
            this,
            args.toBundle()
        )
    }
    val currentStepTimer: LiveData<Step>
        get() = practiceViewModel.currentStepTimer
    val currentExerciseTimer: LiveData<Exercise>
        get() = practiceViewModel.currentExerciseTimer
    private var prepareCountDownRunning: Boolean = false


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentPracticeBinding.bind(view)

        binding.apply {

//            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
//                textViewPracticeSessionName.text = practiceViewModel.sessionWithStepsAndExercises.session.name
//            }

            practiceViewModel.currentExerciseTimer.observe(viewLifecycleOwner) {
                    textViewPracticeSessionName.text = it.name
            }

            recyclerViewPracticeSession.apply {
                adapter = practiceParentListAdapter
                layoutManager = LinearLayoutManager(requireContext())
            }


            practiceViewModel.buttonsStates.observe(viewLifecycleOwner) { buttonsSates ->
                when(buttonsSates) {
                    is PracticeViewModel.ButtonsStates.AreButtonsEnable -> {
                        buttonPracticeStartSession.isGone = !buttonsSates.isStartEnable
                        buttonPracticePauseSession.isGone = !buttonsSates.isPauseEnable
                        buttonPracticeResetSession.isGone = !buttonsSates.isResetEnable
                        buttonPracticeResumeSession.isGone = !buttonsSates.isResumeEnable
                    }
                    is PracticeViewModel.ButtonsStates.isPreparationRunning -> {
                        prepareCountDownRunning = buttonsSates.isPreparationCountDownRunning
                        if (buttonsSates.isPreparationCountDownRunning) {
                            buttonPracticePauseSession.text = "STOP"
                        } else {
                            buttonPracticePauseSession.text = "PAUSE"
                        }
                    }
                }
            }

            buttonPracticeResetSession.setOnClickListener {
                practiceViewModel.buttonsClicks(PracticeViewModel.ButtonsClicks.OnReset)
            }

            buttonPracticeStartSession.setOnClickListener {
                practiceViewModel.buttonsClicks(PracticeViewModel.ButtonsClicks.OnStart)
            }

            buttonPracticePauseSession.setOnClickListener {
                if (prepareCountDownRunning) { practiceViewModel.preparationPauseClicked() } else {
                    practiceViewModel.buttonsClicks(PracticeViewModel.ButtonsClicks.OnPause)
                }
            }

            buttonPracticeResumeSession.setOnClickListener {
                practiceViewModel.buttonsClicks(PracticeViewModel.ButtonsClicks.OnResumed)
            }

            practiceViewModel.textCountDown.observe(viewLifecycleOwner) {
                textViewPracticeCountdown.text = it
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            practiceParentListAdapter.submitList(
                practiceViewModel.sessionWithStepsAndExercises?.stepList
            )

        }

    }
}
