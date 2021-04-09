package com.HiitHomeTimer.ui.practice

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.HiitHomeTimer.repository.localdata.relations.StepWithExercises
import com.hiithometimer.databinding.ItemPracticeStepBinding

class PracticeParentListAdapter(val fragment: PracticeFragment) :
    ListAdapter<StepWithExercises, PracticeParentListAdapter.PracticeParentViewHolder>(
        PracticeParentListItemDiffCallback()
    ) {

    inner class PracticeParentViewHolder(private val binding: ItemPracticeStepBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                root.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val stepWithExercises = getItem(position)
                    }
                }
            }
        }

        fun bind(stepWithExercises: StepWithExercises) {

            val childLayoutManager =
                LinearLayoutManager(binding.recyclerViewPracticeSessionExercise.context)
            val exerciseListAdapter = PracticeChildAdapter(fragment)
            exerciseListAdapter.submitList(stepWithExercises.exerciseLists)

            binding.apply {
                textViewPracticeSessionStepTime.text = stepWithExercises.step.timesNumber.toString()

                recyclerViewPracticeSessionExercise.apply {
                    adapter = exerciseListAdapter
                    layoutManager = childLayoutManager
                }

                fragment.currentStepTimer.observe(fragment.viewLifecycleOwner) {
                    if (getItem(adapterPosition).step.stepId == it.stepId) {
                        cardviewPracticeSession.requestFocus()
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PracticeParentViewHolder {
        val binding = ItemPracticeStepBinding.inflate(LayoutInflater.from(parent.context), parent, false
        )
        return PracticeParentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PracticeParentViewHolder, position: Int) {
        val currentItem = getItem(holder.adapterPosition)
        holder.bind(currentItem)
    }
}

class PracticeParentListItemDiffCallback : DiffUtil.ItemCallback<StepWithExercises>() {

    override fun areItemsTheSame(oldItem: StepWithExercises, newItem: StepWithExercises) =
        oldItem === newItem

    override fun areContentsTheSame(oldItem: StepWithExercises, newItem: StepWithExercises) =
        oldItem == newItem
}

