package com.HitatHomeTimer.ui.addeditsession

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.HitatHomeTimer.di.SessionApplication
import com.hitathometimer.R
import com.hitathometimer.databinding.FragmentCreateSessionBinding

class CreateSessionFragment : Fragment(R.layout.fragment_create_session) {

    // get args passed by fragment navigation when navigate
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

        binding.apply {
            createSessionViewModel.sessionWithStepsAndExercises.observe(viewLifecycleOwner) {
                editTextViewCreationStepName.setText(it?.session?.name)

                editTextViewCreationStepName.addTextChangedListener { editable ->
                    if (editable != null && editable.isNotEmpty()) {
                        createSessionViewModel.sessionWithStepsAndExercises.value?.session?.name =
                            editable.toString()
                    }
                }
            }

            recyclerViewCreationStep.apply {
                adapter = createSessionListParentAdapter
                layoutManager = LinearLayoutManager(requireContext())
                itemAnimator = null

                // observe liveData event in adapter and pass it to the ViewModel
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
            // we must pass null because we don't observe Room entities LiveData, causes submitList doesn't update the list
            // which doesn't update the view
            createSessionListParentAdapter.submitList(
                null
            )
            createSessionListParentAdapter.submitList(
                it.stepList.toMutableList()
            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.create_step_item_menu, menu)
    }

}