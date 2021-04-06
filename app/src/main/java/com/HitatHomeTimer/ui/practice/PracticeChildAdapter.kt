package com.HitatHomeTimer.ui.practice

import android.annotation.SuppressLint
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.HitatHomeTimer.repository.localdata.entities.Exercise
import com.hitathometimer.R
import com.hitathometimer.databinding.ItemPracticeExerciseBinding

class PracticeChildAdapter(val fragment: PracticeFragment) :
    ListAdapter<Exercise, PracticeChildAdapter.PracticeChildViewHolder>(
        PracticeChildListItemDiffCallback()
    ) {

    inner class PracticeChildViewHolder(private val binding: ItemPracticeExerciseBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("ResourceAsColor")
        fun bind(exercise: Exercise) {

            binding.apply {
                textViewItemPracticeExerciseName.text = exercise.name
                textViewItemPracticeExerciseTimer.text = exercise.timerFormatted


            }

            fragment.currentExerciseTimer.observe(fragment.viewLifecycleOwner) {

                Log.d("practice", "bind in child: ${it}")

                if (getItem(adapterPosition).exerciseId == it.exerciseId) {
//                    itemView.requestFocus()
                    binding.cardviewCreationExercise.setBackgroundResource(R.drawable.practice_focus_exercise_cardview_color_and_border)
                } else {
                    binding.cardviewCreationExercise.setBackgroundResource(R.drawable.practice_exercise_cardview_color_and_border)
                }
            }

        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): PracticeChildViewHolder {
        val binding = ItemPracticeExerciseBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
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


