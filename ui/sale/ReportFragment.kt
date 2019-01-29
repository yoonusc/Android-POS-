package com.ionob.pos.ui.sale

import java.util.Calendar

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.SimpleAdapter
import android.widget.Spinner
import android.widget.TextView

import com.ionob.pos.R
import com.ionob.pos.domain.DateTimeStrategy
import com.ionob.pos.domain.sale.Sale
import com.ionob.pos.domain.sale.SaleLedger
import com.ionob.pos.db.NoDaoSetException
import com.ionob.pos.ui.component.UpdatableFragment
import java.math.BigDecimal
import kotlin.collections.ArrayList

/**
 * UI for showing sale's record.
 * @author Ionob Team
 */
 class ReportFragment : UpdatableFragment() {


    private var saleLedger: SaleLedger? = null
    internal var saleList: MutableList<Map<String, String>>? = null
    private var saleLedgerListView: ListView? = null
    private var totalBox: TextView? = null
    private var spinner: Spinner? = null
    private var previousButton: Button? = null
    private var nextButton: Button? = null
    private var currentBox: TextView? = null
    private var currentTime: Calendar? = null
    private var datePicker: DatePickerDialog? = null



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        try {
            saleLedger = SaleLedger.getInstance()
        } catch (e: NoDaoSetException) {
            e.printStackTrace()
        }

        val view = inflater!!.inflate(R.layout.layout_report, container, false)

        previousButton = view.findViewById<View>(R.id.previousButton) as Button
        nextButton = view.findViewById<View>(R.id.nextButton) as Button
        currentBox = view.findViewById<View>(R.id.currentBox) as TextView
        saleLedgerListView = view.findViewById<View>(R.id.saleListView) as ListView
        totalBox = view.findViewById<View>(R.id.totalBox) as TextView
        spinner = view.findViewById<View>(R.id.spinner1) as Spinner

        initUI()
        return view
    }

    /**
     * Initiate this UI.
     */
    private fun initUI() {
        currentTime = Calendar.getInstance()
        datePicker = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, y, m, d ->
            currentTime!!.set(Calendar.YEAR, y)
            currentTime!!.set(Calendar.MONTH, m)
            currentTime!!.set(Calendar.DAY_OF_MONTH, d)
            update()
        }, currentTime!!.get(Calendar.YEAR), currentTime!!.get(Calendar.MONTH), currentTime!!.get(Calendar.DAY_OF_MONTH))

        val adapter = ArrayAdapter.createFromResource(activity?.baseContext,
                R.array.period, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

      //  spinner!!.adapter = adapter
       /* spinner!!.setSelection(0)
        spinner!!.onItemSelectedListener = object : OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
                update()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}

        }*/

        currentBox!!.setOnClickListener { datePicker!!.show() }



        previousButton!!.setOnClickListener { addDate(-1) }

        nextButton!!.setOnClickListener { addDate(1) }

        saleLedgerListView!!.onItemClickListener = OnItemClickListener { myAdapter, myView, position, mylng ->
            val id = saleList?.get(position)?.get("id")?.toString()
            val newActivity = Intent(activity?.baseContext, SaleDetailActivity::class.java)
            newActivity.putExtra("id", id)
            startActivity(newActivity)
        }

    }

    /**
     * Show list.
     * @param list
     */
    private fun showList(list: List<Sale>) {

        saleList = ArrayList()
        for (sale in list) {
           // (saleList as ArrayList<Map<String, String>>).add(sale.toMap())
        }

        val sAdap = SimpleAdapter(activity?.baseContext, saleList,
                R.layout.listview_report, arrayOf("id", "startTime", "total"),
                intArrayOf(R.id.sid, R.id.startTime, R.id.total))
        saleLedgerListView!!.adapter = sAdap
    }

    override fun update() {
        val period = spinner!!.selectedItemPosition
        var list: List<Sale>? = null
        val cTime = currentTime!!.clone() as Calendar
        var eTime = currentTime!!.clone() as Calendar

        if (period == DAILY) {
            currentBox!!.text = " [" + DateTimeStrategy.getSQLDateFormat(currentTime!!) + "] "
            currentBox!!.textSize = 16f
        } else if (period == WEEKLY) {
            while (cTime.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                cTime.add(Calendar.DATE, -1)
            }

            var toShow = " [" + DateTimeStrategy.getSQLDateFormat(cTime) + "] ~ ["
            eTime = cTime.clone() as Calendar
            eTime.add(Calendar.DATE, 7)
            toShow += DateTimeStrategy.getSQLDateFormat(eTime) + "] "
            currentBox!!.textSize = 16f
            currentBox!!.text = toShow
        } else if (period == MONTHLY) {
            cTime.set(Calendar.DATE, 1)
            eTime = cTime.clone() as Calendar
            eTime.add(Calendar.MONTH, 1)
            eTime.add(Calendar.DATE, -1)
            currentBox!!.textSize = 18f
            currentBox!!.text = " [" + currentTime!!.get(Calendar.YEAR) + "-" + (currentTime!!.get(Calendar.MONTH) + 1) + "] "
        } else if (period == YEARLY) {
            cTime.set(Calendar.DATE, 1)
            cTime.set(Calendar.MONTH, 0)
            eTime = cTime.clone() as Calendar
            eTime.add(Calendar.YEAR, 1)
            eTime.add(Calendar.DATE, -1)
            currentBox!!.textSize = 20f
            currentBox!!.text = " [" + currentTime!!.get(Calendar.YEAR) + "] "
        }
        currentTime = cTime
        list = saleLedger!!.getAllSaleDuring(cTime, eTime)
        var total =BigDecimal.ZERO
        for (sale in list!!)
            total =total.add(sale.total)

        totalBox!!.text = total.toString() + ""
        showList(list)
    }

    override fun onResume() {
        super.onResume()
        // update();
        // it shouldn't call update() anymore. Because super.onResume()
        // already fired the action of spinner.onItemSelected()
    }

    /**
     * Add date.
     * @param increment
     */
    private fun addDate(increment: Int) {
        val period = spinner!!.selectedItemPosition
        if (period == DAILY) {
            currentTime!!.add(Calendar.DATE, 1 * increment)
        } else if (period == WEEKLY) {
            currentTime!!.add(Calendar.DATE, 7 * increment)
        } else if (period == MONTHLY) {
            currentTime!!.add(Calendar.MONTH, 1 * increment)
        } else if (period == YEARLY) {
            currentTime!!.add(Calendar.YEAR, 1 * increment)
        }
        update()
    }

    companion object {

        val DAILY = 0
        val WEEKLY = 1
        val MONTHLY = 2
        val YEARLY = 3
    }

}
