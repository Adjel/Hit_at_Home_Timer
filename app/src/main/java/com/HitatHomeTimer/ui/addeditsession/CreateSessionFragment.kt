package com.HitatHomeTimer.ui.addeditsession

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.HitatHomeTimer.di.SessionApplication
import com.HitatHomeTimer.repository.localdata.relations.SessionWithStepsAndExercises
import com.HitatHomeTimer.repository.localdata.relations.StepWithExercises
import com.hitathometimer.R
import com.hitathometimer.databinding.FragmentCreateSessionBinding

class CreateSessionFragment : Fragment(R.layout.fragment_create_session) {

    private val args: CreateSessionFragmentArgs by navArgs()
    private val createSessionListParentAdapter = CreateSessionListParentAdapter(this)

    val createSessionViewModel: CreateSessionViewModel by viewModels{
        CreateSessionViewModelFactory(
            this,
            (requireActivity().application as SessionApplication).repository,
            args.toBundle()
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentCreateSessionBinding.bind(view)

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
//            createSessionViewModel.insertSessionWithStepsAndExercises(sessionWithStepsAndExercises)
//        }


        binding.apply {
            createSessionViewModel.sessionWithStepsAndExercises.observe(viewLifecycleOwner) {
                editTextViewCreationStepName.setText(it?.session?.name)

                editTextViewCreationStepName.addTextChangedListener { editable ->
                    if (editable != null) {
                        if (editable.isNotEmpty()) {
                            createSessionViewModel.sessionWithStepsAndExercises.value?.session?.name =
                                editable.toString()
                        }
                    }
                }
            }

            recyclerViewCreationStep.apply {
                adapter = createSessionListParentAdapter
                layoutManager = LinearLayoutManager(requireContext())
                itemAnimator = null

                createSessionListParentAdapter.event.observe(
                    viewLifecycleOwner) {
                    createSessionViewModel.handleEvent(it)
                }
            }

            buttonCreationAddSet.setOnClickListener {
                createSessionViewModel.handleEvent(CreationListEvent.OnNewStepClicked)
            }

            buttonSaveSession.setOnClickListener {
                // TODO
            }
        }

        createSessionViewModel.sessionWithStepsAndExercises.observe(viewLifecycleOwner) {
            createSessionListParentAdapter.submitList(
                null
            )
            createSessionListParentAdapter.submitList(
                it.stepList.toMutableList()
            )

            Log.d("CreateSessionFragment", "onViewCreated: session: ${it}")

            Log.d("CreateSessionFragment", "onViewCreated: session.stepList ${it}")

        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.create_step_item_menu, menu)
    }

}