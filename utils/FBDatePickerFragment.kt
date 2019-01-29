package com.fitbae.fitness.dialogs

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.widget.DatePicker
import java.util.*

/**
 * Created by Deepesh on 29-Jan-18.
 */

class FBDatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {
    private var dateSelected: OnDateSelected? = null
    private var isDateOfBirth = false


    fun setOnDateSelected(onDateSelected: FBDatePickerFragment.OnDateSelected) {
        this.dateSelected = onDateSelected
    }

    fun setDateOfBirth(dateOfBirth: Boolean) {
        isDateOfBirth = dateOfBirth
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current date as the default date in the picker
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(activity, this, year, month, day)
        if (isDateOfBirth) {
            datePickerDialog.datePicker.maxDate = Calendar.getInstance().timeInMillis - 1000
        } else {
            datePickerDialog.datePicker.minDate = Calendar.getInstance().timeInMillis - 1000
        }

        return datePickerDialog
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, dayOfMonth: Int) {
        dateSelected!!.onDateSelect(view, year, month + 1, dayOfMonth)
    }

    interface OnDateSelected {
        fun onDateSelect(view: DatePicker, year: Int, month: Int, dayOfMonth: Int)
    }
}
