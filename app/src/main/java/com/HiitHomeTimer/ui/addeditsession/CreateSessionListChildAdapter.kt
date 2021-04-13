package com.HiitHomeTimer.ui.addeditsession

import android.app.Activity
import android.text.Editable
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.PopupMenu
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.HiitHomeTimer.repository.localdata.entities.Exercise
import com.hiithometimer.R
import com.hiithometimer.databinding.ItemCreationExerciseBinding
import java.text.SimpleDateFormat
import java.util.*

class CreateSessionListChildAdapter(val event: MutableLiveData<CreationListEvent> = MutableLiveData(), private val parentPosition: CustomClickListener) :
    ListAdapter<Exercise, CreateSessionListChildAdapter.CreateSessionChildViewHolder>(CreateExerciseListDiffCallback()) {

    inner class CreateSessionChildViewHolder(private val binding: ItemCreationExerciseBinding) :
    RecyclerView.ViewHolder(binding.root) {

        /** close keyboard on editText and set focus to null */
        private fun closeKeyBoard(view: View) {
            val inputMethodManager  = view.context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
            view.clearFocus()
        }

        init {

            /** retrieve parent adapter's position */
            val adapterParentPosition = parentPosition.onItemClick()

            binding.apply {

                /** menu popup to delete or duplicate an exercise */
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
                        popupMenu.setOnMenuItemClickListener { item ->

                            when (item.itemId) {
                                R.id.session_item_menu_duplicate -> {
                                    closeKeyBoard(editTextCreationExerciseName)
                                    closeKeyBoard(editTextTimerItemExerciseCreation)
                                    event.value = CreationListEvent.OnDuplicateExerciseClicked(
                                        adapterParentPosition,
                                        exercise
                                    )
                                }
                                R.id.session_item_menu_delete -> {
                                    closeKeyBoard(editTextCreationExerciseName)
                                    closeKeyBoard(editTextTimerItemExerciseCreation)
                                    event.value = CreationListEvent.OnDeleteExerciseClicked(
                                        adapterParentPosition,
                                        adapterPosition
                                    )
                                }
                            }
                            true
                        }
                        popupMenu.show()
                    }
                }

                buttonItemCreationIncrementExerciseTimer.setOnClickListener {

                    if (adapterPosition != RecyclerView.NO_POSITION) {

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

                editTextCreationExerciseName.addTextChangedListener { editableName ->

                    fun sendEventToViewModel(adapterParentPosition: Int, adapterPosition: Int, editableName: Editable) : MutableLiveData<CreationListEvent> {
                        event.value = CreationListEvent.OnExerciseNameChanged(
                            adapterParentPosition,
                            adapterPosition,
                            editableName.toString()
                        )
                        return event
                    }

                    /** input is send only if value changed and is not empty */
                    if (adapterPosition != RecyclerView.NO_POSITION && editableName?.length!! > 0) {

                        /** when user hit enter */
                        editTextCreationExerciseName.setOnKeyListener { view, keyCode, keyEvent ->
                            var clicked = false

                            if (keyEvent.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_ENTER) {

                                    sendEventToViewModel(
                                        adapterParentPosition,
                                        adapterPosition,
                                        editableName
                                    )

                                /** close the keyboard and tell to KeyListener that event is consumed */
                                closeKeyBoard(view)
                                clicked = true
                            }

                            /** get input when editText focus changed */
                            editTextCreationExerciseName.setOnFocusChangeListener { _, hasFocus ->

                                if (!hasFocus) {
                                    sendEventToViewModel(
                                        adapterParentPosition,
                                        adapterPosition,
                                        editableName
                                    )
                                }
                            }
                            clicked
                        }
                    }
                }

                editTextTimerItemExerciseCreation.addTextChangedListener { editableTimer ->
                    /** get format to parse the input string to a validate time */

                    val dateFormat = SimpleDateFormat("mm:ss")
                    dateFormat.timeZone = TimeZone.getTimeZone("GMT")

                    fun sendEventToViewModel(adapterParentPosition : Int, adapterPosition: Int, timer: Long): MutableLiveData<CreationListEvent> {
                        event.value = CreationListEvent.OnExerciseTimerChanged(
                            adapterParentPosition,
                            adapterPosition,
                            UpdateTimeNumber.EDIT,
                            timer
                        )
                        return event
                    }

                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        /** the lambda must be sure it won't get empty editableTimer */
                        if (editableTimer?.length!! > 0) {

                            /** give colon back to user if he deleted it and enter a number */
                            if (editableTimer.filter { !it.isDigit() }.toString() != ":" && editableTimer.none { !it.isDigit() }) {

                                if (editableTimer.filter { it.isDigit() }.length <= 1) {
                                    editableTimer.insert(1,":")
                                }
                                else if (editableTimer.filter { it.isDigit() }.length >= 2) {
                                    editableTimer.insert(2,":")
                                }
                            }

                            /** when user input larger number than editText required */
                             if (editableTimer.filter { it.isDigit() }.length > 4 && editableTimer.none { !it.isDigit() }) {

                                 val minutesInMilli: Long =
                                     editableTimer.filter { it.isDigit() }.substring(0..1)
                                         .toLong().times(60000)
                                 val secondsInMilli: Long =
                                     editableTimer.filter { it.isDigit() }.substring(2..3)
                                         .toLong().times(1000)
                                 val timer = minutesInMilli + secondsInMilli

                                 if (editableTimer.isNotEmpty() && editableTimer.toString() != getItem(
                                         adapterPosition
                                     ).timer.toString()
                                 ) {

                                     sendEventToViewModel(adapterParentPosition, adapterPosition, timer)
                                 }
                             }

                            /** when user click enter */
                             editTextTimerItemExerciseCreation.setOnKeyListener { _, keyCode, keyEvent ->
                                 var enterKeyClicked = false

                                 if (keyEvent.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_ENTER) {
                                     enterKeyClicked = true

                                     /**  when user click enter with input number required */
                                     if ((editableTimer.filter { it.isDigit() }.length == 4 && editableTimer.filter { !it.isDigit() }.length == 1 && editableTimer.filter { !it.isDigit() }
                                             .toString() == ":") ||
                                         (editableTimer.filter { it.isDigit() }.length == 4 && editableTimer.none { !it.isDigit() })
                                     ) {

                                         if ((editableTimer.filter { it.isDigit() }.length == 4 && editableTimer.none { !it.isDigit() })) {
                                             editableTimer.insert(2, ":")

                                         }

                                         val timer =
                                             dateFormat.parse(editableTimer.toString())?.time!!

                                         Log.d("TextWatcher", "setOnKeyListener :length = 4 parse it")

                                         if (editableTimer.isNotEmpty() && editableTimer.toString() != getItem(
                                                 adapterPosition
                                             ).timer.toString()
                                         ) {

                                             sendEventToViewModel(
                                                 adapterParentPosition,
                                                 adapterPosition,
                                                 timer
                                             )
                                         }
                                     }

                                     /** When user click enter but numbers are missing */
                                     if (editableTimer.filter { it.isDigit() }.length < 4 && editableTimer.filter { !it.isDigit() }.length == 1 && editableTimer.filter { !it.isDigit() }
                                             .toString() == ":") {

                                         /**  avoiding a parsing error when user hit enter without number on a colon side and add 0 */
                                         if (editableTimer.toString().first()
                                                 .toString() == ":"
                                         ) {
                                             editableTimer.insert(0, "0")
                                         }

                                         if (editableTimer.toString().last()
                                                 .toString() == ":"
                                         ) {
                                             editableTimer.append("0")
                                         }

                                         val timer =
                                             dateFormat.parse(editableTimer.toString())?.time!!

                                         sendEventToViewModel(
                                             adapterParentPosition,
                                             adapterPosition,
                                             timer
                                         )
                                     }
                                 }
                                 enterKeyClicked

                             }

                            /** when user doesn't validate but click out */
                            editTextTimerItemExerciseCreation.setOnFocusChangeListener { _, hasFocus ->

                                if (!hasFocus) {

                                    /** when user click enter with input number required */
                                    if ((editableTimer.filter { it.isDigit() }.length == 4 && editableTimer.filter { !it.isDigit() }.length == 1 && editableTimer.filter { !it.isDigit() }
                                            .toString() == ":") ||
                                        (editableTimer.filter { it.isDigit() }.length == 4 && editableTimer.none { !it.isDigit() })
                                    ) {

                                        if ((editableTimer.filter { it.isDigit() }.length == 4 && editableTimer.none { !it.isDigit() })) {
                                            editableTimer.insert(2, ":")

                                        }

                                        val timer =
                                            dateFormat.parse(editableTimer.toString())?.time!!

                                        Log.d("TextWatcher", "setOnFocusChangeListener :length = 4 parse it")

                                        if (editableTimer.isNotEmpty() && editableTimer.toString() != getItem(
                                                adapterPosition
                                            ).timer.toString()
                                        ) {

                                            sendEventToViewModel(
                                                adapterParentPosition,
                                                adapterPosition,
                                                timer
                                            )
                                        }
                                    }

                                    /** When user click enter but numbers are missing */
                                    if (editableTimer.filter { it.isDigit() }.length < 4 && editableTimer.filter { !it.isDigit() }.length == 1 && editableTimer.filter { !it.isDigit() }
                                            .toString() == ":") {

                                        /** avoiding a parsing error when user hit enter without number on a colon side and add 0 */
                                        if (editableTimer.toString().first()
                                                .toString() == ":"
                                        ) {
                                            editableTimer.insert(0, "0")
                                        }

                                        if (editableTimer.toString().last()
                                                .toString() == ":"
                                        ) {
                                            editableTimer.append("0")
                                        }

                                        val timer =
                                            dateFormat.parse(editableTimer.toString())?.time!!

                                        sendEventToViewModel(
                                            adapterParentPosition,
                                            adapterPosition,
                                            timer
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
//                        }
//                else {
                // TODO in case app crash when user click on save modifications button
//                    event.value = CreationListEvent.OnStepTimerChanged(viewHolder.adapterPosition, CreationListEvent.UpdateTimeNumber.EDIT, 1)
//                }


            }
        }

        fun bind(exercise: Exercise) {

            binding.apply {

                editTextCreationExerciseName.setText(exercise.name)
                editTextTimerItemExerciseCreation.setText(exercise.timerFormatted)
            }
        }
    }

    /** listen child clicks and require the (parent) position to the parentAdapter */
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
