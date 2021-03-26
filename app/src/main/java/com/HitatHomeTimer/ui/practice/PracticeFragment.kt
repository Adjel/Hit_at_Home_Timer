package com.HitatHomeTimer.ui.practice

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.HitatHomeTimer.di.SessionApplication
import com.HitatHomeTimer.repository.localdata.SessionDao
import com.HitatHomeTimer.repository.localdata.SessionDatabase
import com.hitathometimer.R
import com.hitathometimer.databinding.FragmentPracticeBinding

class PracticeFragment : Fragment(R.layout.fragment_practice) {

    private val practiceParentListAdapter = PracticeParentListAdapter()
    private val args: PracticeFragmentArgs by navArgs()

    private val practiceViewModel: PracticeViewModel by viewModels {
        PracticeViewModelFactory(
            ((requireActivity().application as SessionApplication).repository),
            this,
            args.toBundle()
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentPracticeBinding.bind(view)

        binding.apply {
//            practiceViewModel.sessionWithStepsAndExercises.observe(viewLifecycleOwner) {
//                textViewPracticeSessionName.text = it.session.name
//            }

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                textViewPracticeSessionName.text = practiceViewModel.sessionWithStepsAndExercises?.session?.name
            }

            recyclerViewPracticeSession.apply {
                adapter = practiceParentListAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }
        }

//        practiceViewModel.sessionWithStepsAndExercises.observe(viewLifecycleOwner) {
//            practiceParentListAdapter.submitList(it.stepList)
//        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            practiceParentListAdapter.submitList(
                practiceViewModel.sessionWithStepsAndExercises?.stepList
            )
        }
    }
}
