package com.HitatHomeTimer.ui.addeditsession

import android.annotation.SuppressLint
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.text.method.TextKeyListener.clear
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.text.isDigitsOnly
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.HitatHomeTimer.repository.localdata.entities.Exercise
import com.HitatHomeTimer.repository.localdata.relations.StepWithExercises
import com.hitathometimer.R
import com.hitathometimer.databinding.ItemCreationExerciseBinding
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.properties.Delegates

class CreateSessionListChildAdapter(val event: MutableLiveData<CreationListEvent> = MutableLiveData() , private val parentPosition: CustomClickListener) :
    ListAdapter<Exercise, CreateSessionListChildAdapter.CreateSessionChildViewHolder>(CreateExerciseListDiffCallback()) {

    inner class CreateSessionChildViewHolder(private val binding: ItemCreationExerciseBinding) :
    RecyclerView.ViewHolder(binding.root) {


        init {

            val adapterParentPosition = parentPosition.onItemClick()

            binding.apply {

                imageButtonCreationExerciseMenu.setOnClickListener {

                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        val exercise = getItem(adapterPosition)

                        val popupMenu = PopupMenu(
                            imageButtonCreationExerciseMenu.context,
                            imageButtonCreationExerciseMenu
                        )
                        popupMenu.menuInflater.inflate(
                            R.menu.edit_step_and_exercise_item_menu,
                            popupMenu.menu
                        )
                        popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->

                            when (item.itemId) {
                                R.id.session_item_menu_duplicate ->
                                    event.value = CreationListEvent.OnDuplicateExerciseClicked(
                                        adapterParentPosition,
                                        exercise
                                    )
                                R.id.session_item_menu_delete ->
                                    event.value = CreationListEvent.OnDeleteExerciseClicked(
                                        adapterParentPosition,
                                        adapterPosition
                                    )
                            }
                            true
                        })
                        popupMenu.show()
                    }
                }

                buttonItemCreationIncrementExerciseTimer.setOnClickListener {

                    if (adapterPosition != RecyclerView.NO_POSITION) {

                        Log.d("TextWatcher", "parentPos: ${adapterParentPosition}, ${adapterPosition}" )
                        Log.d("TextWatcher", "exerciseList: ${getItem(adapterPosition)}" )

                        event.value = CreationListEvent.OnExerciseTimerChanged(
                            adapterParentPosition,
                            adapterPosition,
                            UpdateTimeNumber.INCREMENT
                        )
                    }
                }

                buttonItemCreationDecrementExerciseTimer.setOnClickListener {
                    if (adapterPosition != RecyclerView.NO_POSITION) {

                        event.value = CreationListEvent.OnExerciseTimerChanged(
                            adapterParentPosition,
                            adapterPosition,
                            UpdateTimeNumber.DECREMENT
                        )
                    }
                }

//                editTextCreationExerciseName.setOnClickListener {
//                    event.value = CreationListEvent.OnExerciseNameChanged(
//                        adapterParentPosition,
//                        adapterPosition,
//                        newName = editTextCreationExerciseName.text.toString()
//                    )
//                    Log.d("TextWatcher", "newName: ${editTextCreationExerciseName.text.toString()}" )
//                }

                editTextCreationExerciseName.addTextChangedListener { editableName ->

                    if (editableName != null && editableName.isNotEmpty() && editableName.toString() != getItem(
                            adapterPosition
                        ).name
                    ) {

                        Log.d("TextWatcher", "onTextChanged: ${editableName}")

                        event.value = CreationListEvent.OnExerciseNameChanged(
                            adapterParentPosition,
                            adapterPosition,
                            editableName.toString()
                        )
                    }
                }

//                editTextCreationExerciseName.addTextChangedListener { exerciseNameEditable ->
//                    if (adapterPosition != RecyclerView.NO_POSITION) {
//
//                        Log.d(
//                            "CreateSessionChild",
//                            "doAfterTextChanged: BUG HERE = $adapterParentPosition"
//                        )
//
//                        val exercise = getItem(adapterPosition)
//
//
//                        if (exerciseNameEditable!!.isNotEmpty() && exerciseNameEditable.toString() != exercise.name) {
//
//                                Log.d(
//                                    "CreateSessionChild",
//                                    "doAfterTextChanged: editableToName: adapter parent POSITION = $adapterParentPosition"
//                                )
//                                event.value = CreationListEvent.OnExerciseNameChanged(
//                                    adapterParentPosition, adapterPosition, exerciseNameEditable.toString()
//                                )
//                        }
////                            else {
////                            // TODO in case app crash when user click on save modifications button
////                            event.value = CreationListEvent.OnExerciseNameChanged(adapterParentPosition, adapterPosition, "New Exercise")
////                            }
//                    }
//                }

//                binding.editTextTimerItemExerciseCreation.doAfterTextChanged { exerciseTimerEditable ->
//                    val position = adapterPosition
//                    val dateFormat: SimpleDateFormat = SimpleDateFormat("mm:ss")
//                    dateFormat.timeZone = TimeZone.getTimeZone("GMT")
//                    if (position != RecyclerView.NO_POSITION) {
//                        val exercise = getItem(position)
//                        if (exerciseTimerEditable != null) {
//                            if (exerciseTimerEditable.isNotEmpty()) {
//                                val editableToTime = dateFormat.parse(exerciseTimerEditable.toString())?.time
//                                if (editableToTime != exercise.timer) {
//                                    Log.d(
//                                        "CreateSessionChild",
//                                        "doAfterTextChanged: editableToTime != exercise.timer . exercise.timer : ${exercise.timer.toString()}"
//                                    )
//                                    event.value = CreationListEvent.OnExerciseTimerChanged(
//                                        adapterParentPosition,
//                                        position,
//                                        UpdateTimeNumber.EDIT,
//                                        editableToTime!!
//
//                                    )
//                                    Log.d(
//                                        "CreateSessionChild",
//                                        "doAfterTextChanged: editableToTime != exercise.timer . editableToTime : ${editableToTime}"
//                                    )
//                                }
//                            }
//                        }
//                    }
////                        }
////                else {
//                    // TODO in case app crash when user click on save modifications button
////                    event.value = CreationListEvent.OnStepTimerChanged(viewHolder.adapterPosition, CreationListEvent.UpdateTimeNumber.EDIT, 1)
////                }
//                }


            }
        }

        fun bind(exercise: Exercise) {
            binding.apply {

                var timerFormatted: String = SimpleDateFormat("mm:ss").format(exercise.timer)

                editTextCreationExerciseName.setText(exercise.name)
                Log.d("CreateSessionChild", "bind editTextName: ${exercise.name}")

                editTextTimerItemExerciseCreation.setText(timerFormatted)
            }
        }
    }

    interface CustomClickListener {
        fun onItemClick(): Int
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CreateSessionChildViewHolder {
        val binding = ItemCreationExerciseBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return CreateSessionChildViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CreateSessionChildViewHolder, position: Int) {
        val current = getItem(holder.adapterPosition)
        holder.bind(current)
    }

}

class CreateExerciseListDiffCallback: DiffUtil.ItemCallback<Exercise>() {
    override fun areItemsTheSame(oldItem: Exercise, newItem: Exercise) =
        oldItem === newItem

    override fun areContentsTheSame(oldItem: Exercise, newItem: Exercise)=
        oldItem == newItem
}
