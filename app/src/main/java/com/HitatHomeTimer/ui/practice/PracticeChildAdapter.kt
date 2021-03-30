package com.HitatHomeTimer.ui.practice

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.HitatHomeTimer.repository.localdata.entities.Exercise
import com.hitathometimer.databinding.ItemPracticeExerciseBinding

class PracticeChildAdapter :
    ListAdapter<Exercise, PracticeChildAdapter.PracticeChildViewHolder>(
        PracticeChildListItemDiffCallback()
    ) {

    inner class PracticeChildViewHolder(private val binding: ItemPracticeExerciseBinding) :
        RecyclerView.ViewHolder(binding.root) {

            fun bind(exercise: Exercise) {
                binding.apply {
                    textViewItemPracticeExerciseName.text = exercise.name
                    textViewItemPracticeExerciseTimer.text = exercise.timerFormatted
                }
            }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int
    ): PracticeChildViewHolder {
        val binding = ItemPracticeExerciseBinding.inflate(LayoutInflater.from(parent.context), parent, false
        )
        return PracticeChildViewHolder(binding)
    }


    override fun onBindViewHolder(holder: PracticeChildViewHolder, position: Int) {
        val current = getItem(holder.adapterPosition)
        holder.bind(current)
    }


    class PracticeChildListItemDiffCallback : DiffUtil.ItemCallback<Exercise>() {
        override fun areItemsTheSame(oldItem: Exercise, newItem: Exercise) =
            oldItem === newItem

        override fun areContentsTheSame(oldItem: Exercise, newItem: Exercise) =
            oldItem == newItem

    }
}


