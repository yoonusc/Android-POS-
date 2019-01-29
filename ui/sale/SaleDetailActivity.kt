package com.ionob.pos.ui.sale

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ListView
import android.widget.TextView

import com.ionob.pos.R
import com.ionob.pos.domain.inventory.LineItem
import com.ionob.pos.domain.sale.Sale
import com.ionob.pos.domain.sale.SaleLedger
import com.ionob.pos.db.NoDaoSetException

/**
 * UI for showing the detail of Sale in the record.
 * @author Ionob Team
 */
class SaleDetailActivity : Activity() {

    private var totalBox: TextView? = null
    private var dateBox: TextView? = null
    private var lineitemListView: ListView? = null
    private var lineitemList: MutableList<LineItem>? = null
    private var sale: Sale? = null
    private var saleId: Int = 0
    private var saleLedger: SaleLedger? = null

    @SuppressLint("MissingSuperCall")
    public override fun onCreate(savedInstanceState: Bundle?) {

        try {
            saleLedger = SaleLedger.getInstance()
        } catch (e: NoDaoSetException) {
            e.printStackTrace()
        }

        saleId = Integer.parseInt(intent.getStringExtra("id"))
        sale = saleLedger!!.getSaleById(saleId)

        initUI(savedInstanceState)
    }


    /**
     * Initiate actionbar.
     */
    @SuppressLint("NewApi")
    private fun initiateActionBar() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            val actionBar = actionBar
            actionBar!!.setDisplayHomeAsUpEnabled(true)
            actionBar.title = resources.getString(R.string.sale)
            actionBar.setBackgroundDrawable(ColorDrawable(Color.parseColor("#33B5E5")))
        }
    }


    /**
     * Initiate this UI.
     * @param savedInstanceState
     */
    private fun initUI(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_saledetail)
        initiateActionBar()
        totalBox = findViewById<View>(R.id.totalBox) as TextView?
        dateBox = findViewById<View>(R.id.dateBox) as TextView?
        lineitemListView = findViewById<View>(R.id.lineitemList) as ListView?
    }

    /**
     * Show list.
     * @param list
     */
    private fun showList(list: List<LineItem>) {
        lineitemList 
        for (line in list) {
            val add = lineitemList!!.add(line)
        }

        val sAdap = SalesProductAdapter(this,lineitemList,object:SalesProductAdapter.SalesAdapterCallback{
            override fun onPlusClicked(data: LineItem?) {

            }

            override fun onMinusClicked(data: LineItem?) {

            }

            override fun onItemClicked(data: LineItem?) {

            }
        })
        lineitemListView!!.adapter = sAdap
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                this.finish()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    /**
     * Update UI.
     */
    fun update() {
        totalBox!!.text = sale!!.total.toString() + ""
        dateBox!!.text = sale!!.endTime + ""
        showList(sale!!.allLineItem)
    }

    override fun onResume() {
        super.onResume()
        update()
    }
}

