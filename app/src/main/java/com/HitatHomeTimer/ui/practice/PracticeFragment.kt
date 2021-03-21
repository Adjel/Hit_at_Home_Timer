package com.HitatHomeTimer.ui.practice

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.HitatHomeTimer.di.SessionApplication
import com.hitathometimer.R
import com.hitathometimer.databinding.FragmentPracticeBinding

class PracticeFragment : Fragment(R.layout.fragment_practice) {

    private val practiceParentListAdapter = PracticeParentListAdapter()

    private val practiceViewModel: PracticeViewModel by viewModels{
        PracticeViewModelFactory(
            ((requireActivity().application as SessionApplication).repository),
            args.sessionWith
        )
    }

    private val args: PracticeFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentPracticeBinding.bind(view)


        binding.apply {
            recyclerViewPracticeSession.apply {
                adapter = practiceParentListAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            practiceParentListAdapter.submitList(practiceViewModel.arguments?.stepList)
            binding.textViewPracticeSessionName.text = practiceViewModel.arguments?.session?.name
        }

    }
}
