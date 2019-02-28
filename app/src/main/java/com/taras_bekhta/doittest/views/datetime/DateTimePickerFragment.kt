package com.taras_bekhta.doittest.views.datetime


import android.app.TimePickerDialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.content.res.AppCompatResources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TimePicker
import com.taras_bekhta.doittest.R
import kotlinx.android.synthetic.main.fragment_date_time_picker.*
import java.util.*

class DateTimePickerFragment : DialogFragment(), TimePickerDialog.OnTimeSetListener {

    var hour = 10
    var minute = 0
    var date: Date? = null

    var dateTimeSelectedListener: DateTimeSelectedListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_date_time_picker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isCancelable = false

        timeEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
        val drawable = AppCompatResources.getDrawable(context!!, R.drawable.ic_clock)
        val size = resources.getDimensionPixelSize(R.dimen.defaultMargin)
        drawable!!.mutate().setBounds(0, 0, size, size)
        timeEditText.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)

        timeEditText.setOnClickListener {
            val arr = timeEditText.text.split(':')
            hour = arr[0].toInt()
            minute = arr[1].toInt()
            TimePickerDialog(context, this, hour, minute, true).show()
        }

        cancelButton.setOnClickListener {
            dismiss()
        }

        okButton.setOnClickListener {
            val calendar = Calendar.getInstance()
            calendar.set(datePicker.year, datePicker.month, datePicker.dayOfMonth, hour, minute)
            date = calendar.time

            dateTimeSelectedListener?.onDateTimeSelected(date!!)
            dismiss()
        }

        val hour = "%02d".format(hour)
        val min = "%02d".format(minute)
        timeEditText.setText("$hour:$min")

        if (date != null) {
            val calendar = Calendar.getInstance()
            calendar.time = date
            datePicker.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
        }
    }

    fun setDateTime(dateTime: Date) {
        date = dateTime

        val calendar = Calendar.getInstance()
        calendar.time = date

        hour = calendar.get(Calendar.HOUR_OF_DAY)
        minute = calendar.get(Calendar.MINUTE)

        val hour = "%02d".format(hour)
        val min = "%02d".format(minute)
        if (timeEditText != null) {
            timeEditText.setText("$hour:$min")
        }

        if (datePicker != null) {
            datePicker.updateDate(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
        }
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, timeMinute: Int) {
        hour = hourOfDay
        minute = timeMinute
        val hour = "%02d".format(hourOfDay)
        val min = "%02d".format(minute)
        timeEditText.setText("$hour:$min")
    }

    interface DateTimeSelectedListener {
        fun onDateTimeSelected(date: Date)
    }
}
