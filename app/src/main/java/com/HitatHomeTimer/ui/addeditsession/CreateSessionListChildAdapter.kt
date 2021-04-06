package com.HitatHomeTimer.ui.addeditsession

import android.app.Activity
import android.text.Editable
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.PopupMenu
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.HitatHomeTimer.repository.localdata.entities.Exercise
import com.hitathometimer.R
import com.hitathometimer.databinding.ItemCreationExerciseBinding
import java.text.SimpleDateFormat
import java.util.*

class CreateSessionListChildAdapter(val event: MutableLiveData<CreationListEvent> = MutableLiveData(), private val parentPosition: CustomClickListener) :
    ListAdapter<Exercise, CreateSessionListChildAdapter.CreateSessionChildViewHolder>(CreateExerciseListDiffCallback()) {

    inner class CreateSessionChildViewHolder(private val binding: ItemCreationExerciseBinding) :
    RecyclerView.ViewHolder(binding.root) {

        /** close keyboard on editText and set focus to null */
        private fun closeKeyBoard(view: View) {
            view.clearFocus()
            val inputMethodManager  = view.context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
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

                editTextCreationExerciseName.doAfterTextChanged { editableName ->
                    fun sendEventToViewModel(adapterParentPosition: Int, adapterPosition: Int, editableName: Editable) : MutableLiveData<CreationListEvent> {
                        event.value = CreationListEvent.OnExerciseNameChanged(
                            adapterParentPosition,
                            adapterPosition,
                            editableName.toString()
                        )
                        return event
                    }

                    if (adapterPosition != RecyclerView.NO_POSITION) {

                        if (editableName?.length!! > 0) {

                            /** input is send only if value changed and is not empty */
                            if (editableName.isNotEmpty() && editableName.toString() != getItem(
                                    adapterPosition
                                ).name
                            ) {

                                /** when user hit enter */
                                editTextCreationExerciseName.setOnKeyListener { _, keyCode, keyEvent ->

                                    if (keyEvent.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_ENTER) {

                                        sendEventToViewModel(
                                            adapterParentPosition,
                                            adapterPosition,
                                            editableName
                                        )
                                    }
                                    /** close the keyboard and tell to KeyListener that event is consumed */
                                    closeKeyBoard(this.editTextCreationExerciseName)
                                    true
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
                            }

                        }
                    }

                }
                editTextTimerItemExerciseCreation.doAfterTextChanged { editableTimer ->
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

                                         Log.d("TextWatcher", ":length = 4 parse it")

                                         if (editableTimer.isNotEmpty() && editableTimer.toString() != getItem(
                                                 adapterPosition
                                             ).timer.toString()
                                         ) {

                                             sendEventToViewModel(
                                                 adapterParentPosition,
                                                 adapterPosition,
                                                 timer
                                             )
                                             closeKeyBoard(this.editTextTimerItemExerciseCreation)
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
                                         closeKeyBoard(this.editTextTimerItemExerciseCreation)

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

                                        Log.d("TextWatcher", ":length = 4 parse it")

                                        if (editableTimer.isNotEmpty() && editableTimer.toString() != getItem(
                                                adapterPosition
                                            ).timer.toString()
                                        ) {

                                            sendEventToViewModel(
                                                adapterParentPosition,
                                                adapterPosition,
                                                timer
                                            )
                                            closeKeyBoard(this.editTextTimerItemExerciseCreation)
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
                                        closeKeyBoard(this.editTextTimerItemExerciseCreation)
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
