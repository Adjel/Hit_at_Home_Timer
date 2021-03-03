package com.HitatHomeTimer.ui.addeditsession

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.HitatHomeTimer.di.SessionApplication
import com.HitatHomeTimer.repository.localdata.entities.Exercise
import com.HitatHomeTimer.repository.localdata.entities.Session
import com.HitatHomeTimer.repository.localdata.SessionDatabase
import com.HitatHomeTimer.repository.localdata.entities.Step
import com.HitatHomeTimer.repository.localdata.relations.SessionWithStepsAndExercises
import com.HitatHomeTimer.repository.localdata.relations.StepWithExercises
import com.HitatHomeTimer.ui.practice.PracticeFragmentArgs
import com.hitathometimer.R
import com.hitathometimer.databinding.FragmentCreateSessionBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class CreateSessionFragment : Fragment(R.layout.fragment_create_session),
    CreateSessionListParentAdapter.OnItemClickListener {

    private val args: CreateSessionFragmentArgs by navArgs()
    private val createSessionListParentAdapter = CreateSessionListParentAdapter(this)

    private val createSessionViewModel: CreateSessionViewModel by viewModels{
        CreateSessionViewModelFactory(
            this,
            (requireActivity().application as SessionApplication).repository
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentCreateSessionBinding.bind(view)

//       val dao = SessionDatabase.getInstance(this.requireContext(), CoroutineScope(SupervisorJob())).sessionDao()
//
//
//        val yoga = Session("Reprise")
//        val firstStep = Step(timesNumber = 2)
//        val secondStep = Step(timesNumber = 2)
//        val firstWork = Exercise("Work legs", timer = 30000)
//        val secondWork = Exercise("Work arms", timer = 30000)
//
//        val thirdWork = Exercise("Work pecs", timer = 30000)
//
//
//        val firstExerciseList = listOf<Exercise>(firstWork, secondWork)
//        val secondExerciseList = listOf<Exercise>(thirdWork)
//
//        val stepWithExercises = listOf<StepWithExercises>(StepWithExercises(firstStep, firstExerciseList), StepWithExercises(secondStep, secondExerciseList))
//        val sessionWithStepsAndExercises = SessionWithStepsAndExercises(yoga, stepWithExercises)
//
//
//
//        lifecycleScope.launch {
//            dao.insertSessionWithStepsAndExercises(sessionWithStepsAndExercises)
//        }

        binding.apply {
                textViewCreationStepName.setText(createSessionViewModel.sessionWithStepsAndExercises.value?.session?.name)
            Log.d("CreateSession", "onViewCreated: ${createSessionViewModel.sessionWithStepsAndExercises.value?.session?.name}")

            recyclerViewCreationStep.apply {
                adapter = createSessionListParentAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            createSessionListParentAdapter.submitList(createSessionViewModel.sessionWithStepsAndExercises.value?.stepList)
            Log.d("CreateSession", "onViewCreated: ${createSessionViewModel.sessionWithStepsAndExercises.value?.stepList}")
        }

    }

    override fun newStepWithExercise() {
        TODO("Not yet implemented")
    }
}