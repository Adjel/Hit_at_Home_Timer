package com.HitatHomeTimer.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.HitatHomeTimer.repository.localdata.entities.Exercise
import com.HitatHomeTimer.repository.localdata.relations.StepWithExercises
import com.HitatHomeTimer.ui.practice.ExerciseListInnerViewBinder
import com.HitatHomeTimer.util.nestedlistadapterfactory.FeedAdapter
import com.HitatHomeTimer.util.nestedlistadapterfactory.FeedItemBinder
import com.HitatHomeTimer.util.nestedlistadapterfactory.FeedItemClass
import com.HitatHomeTimer.util.nestedlistadapterfactory.FeedItemViewBinder
import com.hitathometimer.R

class StepListViewBinder(val block : (data: StepWithExercises) -> Unit) : FeedItemViewBinder<StepWithExercises, StepListViewHolder>(
    StepWithExercises::class.java) {

    override fun createViewHolder(parent: ViewGroup): StepListViewHolder {
        return StepListViewHolder(
            LayoutInflater.from(parent.context).inflate(getFeedItemType(), parent, false), block)
    }

    override fun bindViewHolder(model: StepWithExercises, viewHolder: StepListViewHolder) {
        viewHolder.bind(model)
    }

    override fun getFeedItemType() = R.layout.item_practice_step

    override fun areContentsTheSame(oldItem: StepWithExercises, newItem: StepWithExercises)
    = oldItem == newItem

    override fun areItemsTheSame(oldItem: StepWithExercises, newItem: StepWithExercises)
        = oldItem == newItem
}

class StepListViewHolder(val view : View, val block : (data: StepWithExercises) -> Unit)
    : RecyclerView.ViewHolder(view) {

        fun bind(data: StepWithExercises) {

            

            itemView.setOnClickListener {
                block(data)
            }

            itemView.apply {

//                val exerciseListViewBinder = ExerciseListInnerViewBinder { exercise: Exercise ->
//                    block(ExerciseListInnerViewBinder) }
//                val viewBinder = MutableMapOf<FeedItemClass, FeedItemBinder>()
//                @Suppress("UNCHECKED_CAST")
//                viewBinders.put(
//                    exerciseListViewBinder.modelClass,
//                    exerciseListViewBinder as FeedItemBinder)
//                adapter = FeedAdapter(viewBinders)
//                recyclerview?.apply {
//
//                    layoutManager = LinearLayoutManager(recyclerview?.context, LinearLayoutManager.VERTICAL, false)
//                    if (recyclerview?.adapter == null) {
//                        recycler.adapter = adapter
//                    }
//                    (recyclerview?.adapter as FeedAdapter).submitList(
//                        data.exerciseLists as List<Any>? ?: emptyList())
//                }
            }
        }
    }