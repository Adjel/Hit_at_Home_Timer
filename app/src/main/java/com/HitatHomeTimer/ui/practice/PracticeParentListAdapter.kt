package com.HitatHomeTimer.ui.practice

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.HitatHomeTimer.repository.localdata.entities.Exercise
import com.HitatHomeTimer.repository.localdata.entities.Step
import com.HitatHomeTimer.repository.localdata.relations.StepWithExercises
import com.hitathometimer.databinding.ItemPracticeStepBinding

class PracticeParentListAdapter :
    ListAdapter<StepWithExercises, PracticeParentListAdapter.PracticeParentViewHolder>(
        PracticeParentListItemDiffCallback()
    ) {

    private val viewPool = RecyclerView.RecycledViewPool()

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

            val childLayoutManager = LinearLayoutManager(binding.recyclerViewPracticeSessionExercise.context)
            val exerciseListAdapter = PracticeChildAdapter()
            exerciseListAdapter.submitList(stepWithExercises.exerciseLists)

            binding.apply {
                textViewPracticeSessionStepTime.text = stepWithExercises.step.timesNumber.toString()

                recyclerViewPracticeSessionExercise.apply {
                    adapter = exerciseListAdapter
                    layoutManager = childLayoutManager
                    setRecycledViewPool(viewPool)
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
