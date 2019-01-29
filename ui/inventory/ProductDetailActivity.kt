package com.ionob.pos.ui.inventory

import java.util.ArrayList

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.SimpleAdapter
import android.widget.TabHost
import android.widget.TextView
import android.widget.Toast

import com.ionob.pos.R
import com.ionob.pos.domain.DateTimeStrategy
import com.ionob.pos.domain.inventory.*
import com.ionob.pos.db.NoDaoSetException

/**
 * UI for shows the datails of each Product.
 * @author Ionob Team
 */
@SuppressLint("NewApi")
class ProductDetailActivity : Activity() {

    private var productCatalog: ProductCatalog? = null
    private var stock: Stock? = null
    private var product: Product? = null
    private var stockList: MutableList<Map<String, String>>? = null
    private var nameBox: EditText? = null
    private var barcodeBox: EditText? = null
    private var stockSumBox: TextView? = null
    private var priceBox: EditText? = null
    private var addProductLotButton: Button? = null
    private var submitEditButton: Button? = null
    private var cancelEditButton: Button? = null
    private var openEditButton: Button? = null
    private var mTabHost: TabHost? = null
    private var stockListView: ListView? = null
    private var id: String? = null
    private var remember: Array<String?>? = null
    private var popDialog: AlertDialog.Builder? = null
    private var inflater: LayoutInflater? = null
    private var res: Resources? = null
    private var costBox: EditText? = null
    private var quantityBox: EditText? = null
    private var confirmButton: Button? = null
    private var clearButton: Button? = null
    private var Viewlayout: View? = null
    private var alert: AlertDialog? = null

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.detail_menu, menu)
        return true
    }

    @SuppressLint("NewApi")
    private fun initiateActionBar() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            //			ActionBar actionBar = getActionBar();
            //			actionBar.setDisplayHomeAsUpEnabled(true);
            //			actionBar.setTitle(res.getString(R.string.product_detail));
            //			actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#33B5E5")));
        }
    }

    @SuppressLint("MissingSuperCall")
    public override fun onCreate(savedInstanceState: Bundle?) {

        res = resources
        initiateActionBar()

        try {
            stock = Inventory().stock
            productCatalog = Inventory.getInstance().getProductCatalog()
        } catch (e: NoDaoSetException) {
            e.printStackTrace()
        }

        id = intent.getStringExtra("id")
        product = productCatalog!!.getProductById(Integer.parseInt(id))

        initUI(savedInstanceState)
        remember = arrayOfNulls(3)
        nameBox!!.setText(product!!.name)
        priceBox!!.setText(product!!.unitPrice.toString() + "")
        barcodeBox!!.setText(product!!.barcode)

    }

    /**
     * Initiate this UI.
     * @param savedInstanceState
     */
    private fun initUI(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_productdetail_main)
        stockListView = findViewById<View>(R.id.stockListView) as ListView
        nameBox = findViewById<View>(R.id.nameBox) as EditText
        priceBox = findViewById<View>(R.id.priceBox) as EditText
        barcodeBox = findViewById<View>(R.id.barcodeBox) as EditText
        stockSumBox = findViewById<View>(R.id.stockSumBox) as TextView
        submitEditButton = findViewById<View>(R.id.submitEditButton) as Button
        submitEditButton!!.visibility = View.INVISIBLE
        cancelEditButton = findViewById<View>(R.id.cancelEditButton) as Button
        cancelEditButton!!.visibility = View.INVISIBLE
        openEditButton = findViewById<View>(R.id.openEditButton) as Button
        openEditButton!!.visibility = View.VISIBLE
        addProductLotButton = findViewById<View>(R.id.addProductLotButton) as Button
        mTabHost = findViewById<View>(android.R.id.tabhost) as TabHost
        mTabHost!!.setup()
        mTabHost!!.addTab(mTabHost!!.newTabSpec("tab_test1").setIndicator(res!!.getString(R.string.product_detail))
                .setContent(R.id.tab1))
        mTabHost!!.addTab(mTabHost!!.newTabSpec("tab_test2").setIndicator(res!!.getString(R.string.stock))
                .setContent(R.id.tab2))
        mTabHost!!.currentTab = 0
        popDialog = AlertDialog.Builder(this)
        inflater = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        addProductLotButton!!.setOnClickListener { showAddProductLot() }

        openEditButton!!.setOnClickListener { edit() }

        submitEditButton!!.setOnClickListener { submitEdit() }

        cancelEditButton!!.setOnClickListener { cancelEdit() }
    }

    /**
     * Show list.
     * @param list
     */
    private fun showList(list: List<ProductLot>?) {

        stockList = ArrayList()
        if (list != null) {
            for (productLot in list) {
                stockList!!.add(productLot.toMap())
            }
        }

        val sAdap = SimpleAdapter(this@ProductDetailActivity, stockList,
                R.layout.listview_stock, arrayOf("dateAdded", "cost", "quantity"), intArrayOf(R.id.dateAdded, R.id.cost, R.id.quantity))
        stockListView!!.adapter = sAdap
    }

    override fun onResume() {
        super.onResume()
        val productId = Integer.parseInt(id)
        stockSumBox!!.text = stock!!.getStockSumById(productId).toString() + ""
        showList(stock!!.getProductLotByProductId(productId))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                this.finish()
                return true
            }
            R.id.action_edit -> {
                edit()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    /**
     * Submit editing.
     */
    private fun submitEdit() {
        nameBox!!.isFocusable = false
        nameBox!!.isFocusableInTouchMode = false
        nameBox!!.setBackgroundColor(Color.parseColor("#87CEEB"))
        priceBox!!.isFocusable = false
        priceBox!!.isFocusableInTouchMode = false
        priceBox!!.setBackgroundColor(Color.parseColor("#87CEEB"))
        barcodeBox!!.isFocusable = false
        barcodeBox!!.isFocusableInTouchMode = false
        barcodeBox!!.setBackgroundColor(Color.parseColor("#87CEEB"))
        product!!.name = nameBox!!.text.toString()
        if (priceBox!!.text.toString() == "")
            priceBox!!.setText("0.0")
        product!!.unitPrice = (priceBox!!.text.toString().toBigDecimal())
        product!!.barcode = barcodeBox!!.text.toString()
        productCatalog!!.editProduct(product)
        submitEditButton!!.visibility = View.INVISIBLE
        cancelEditButton!!.visibility = View.INVISIBLE
        openEditButton!!.visibility = View.VISIBLE
    }

    /**
     * Cancel editing.
     */
    private fun cancelEdit() {
        nameBox!!.isFocusable = false
        nameBox!!.isFocusableInTouchMode = false
        nameBox!!.setBackgroundColor(Color.parseColor("#87CEEB"))
        priceBox!!.isFocusable = false
        priceBox!!.isFocusableInTouchMode = false
        priceBox!!.setBackgroundColor(Color.parseColor("#87CEEB"))
        barcodeBox!!.isFocusable = false
        barcodeBox!!.isFocusableInTouchMode = false
        barcodeBox!!.setBackgroundColor(Color.parseColor("#87CEEB"))
        submitEditButton!!.visibility = View.INVISIBLE
        cancelEditButton!!.visibility = View.INVISIBLE
        nameBox!!.setText(remember!![0])
        priceBox!!.setText(remember!![1])
        barcodeBox!!.setText(remember!![2])
        openEditButton!!.visibility = View.VISIBLE
    }

    /**
     * Edit
     */
    private fun edit() {
        nameBox!!.isFocusable = true
        nameBox!!.isFocusableInTouchMode = true
        nameBox!!.setBackgroundColor(Color.parseColor("#FFBB33"))
        priceBox!!.isFocusable = true
        priceBox!!.isFocusableInTouchMode = true
        priceBox!!.setBackgroundColor(Color.parseColor("#FFBB33"))
        barcodeBox!!.isFocusable = true
        barcodeBox!!.isFocusableInTouchMode = true
        barcodeBox!!.setBackgroundColor(Color.parseColor("#FFBB33"))
        remember?.set(0, nameBox!!.text.toString())
        remember?.set(1, priceBox!!.text.toString())
        remember?.set(2, barcodeBox!!.text.toString())
        submitEditButton!!.visibility = View.VISIBLE
        cancelEditButton!!.visibility = View.VISIBLE
        openEditButton!!.visibility = View.INVISIBLE
    }

    /**
     * Show adding product lot.
     */
    private fun showAddProductLot() {
        Viewlayout = inflater!!.inflate(R.layout.layout_addproductlot,
                findViewById<View>(R.id.addProdutlot_dialog) as ViewGroup)
        popDialog!!.setView(Viewlayout)

        costBox = Viewlayout!!.findViewById<View>(R.id.costBox) as EditText
        quantityBox = Viewlayout!!.findViewById<View>(R.id.quantityBox) as EditText
        confirmButton = Viewlayout!!.findViewById<View>(R.id.confirmButton) as Button
        clearButton = Viewlayout!!.findViewById<View>(R.id.clearButton) as Button
        confirmButton!!.setOnClickListener {
            if (quantityBox!!.text.toString() == "" || costBox!!.text.toString() == "") {
                Toast.makeText(this@ProductDetailActivity,
                        res!!.getString(R.string.please_input_all), Toast.LENGTH_SHORT)
                        .show()
            } else {
                val success = stock!!.addProductLot(
                        DateTimeStrategy.currentTime,
                        Integer.parseInt(quantityBox!!.text.toString()),
                        product,
                        java.lang.Double.parseDouble(costBox!!.text.toString()))

                if (success) {
                    Toast.makeText(this@ProductDetailActivity, res!!.getString(R.string.success), Toast.LENGTH_SHORT).show()
                    costBox!!.setText("")
                    quantityBox!!.setText("")
                    onResume()
                    alert!!.dismiss()


                } else {
                    Toast.makeText(this@ProductDetailActivity, res!!.getString(R.string.fail), Toast.LENGTH_SHORT).show()
                }
            }
        }
        clearButton!!.setOnClickListener {
            if (quantityBox!!.text.toString() == "" && costBox!!.text.toString() == "") {
                alert!!.dismiss()
                onResume()
            } else {
                costBox!!.setText("")
                quantityBox!!.setText("")
            }
        }

        alert = popDialog!!.create()
        alert!!.show()
    }
}

private fun <E> MutableList<E>.add(element: Map<String?, String?>) {

}
