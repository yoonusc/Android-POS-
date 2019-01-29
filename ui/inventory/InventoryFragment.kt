package com.ionob.pos.ui.inventory


import java.util.ArrayList

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemClickListener

import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentIntegratorSupportV4
import com.ionob.pos.R
import com.ionob.pos.domain.inventory.Inventory
import com.ionob.pos.domain.inventory.Product
import com.ionob.pos.domain.inventory.ProductCatalog
import com.ionob.pos.domain.sale.Register
import com.ionob.pos.db.DatabaseExecutor
import com.ionob.pos.db.Demo
import com.ionob.pos.db.NoDaoSetException
import com.ionob.pos.ui.MainActivity
import com.ionob.pos.ui.component.UpdatableFragment
import android.widget.AbsListView
import com.ionob.pos.domain.inventory.Category
import com.ionob.pos.ui.sale.EditFragmentDialog
import com.ionob.pos.ui.sale.SaleFragment
import java.math.BigDecimal


/**
 * UI for Inventory, shows list of Product in the ProductCatalog.
 * Also use for a sale process of adding Product into sale.
 *
 * @author Ionob Team
 */
@SuppressLint("ValidFragment")
class InventoryFragment
/**
 * Construct a new InventoryFragment.
 * @param saleFragment
 */
(private val saleFragment: UpdatableFragment) : UpdatableFragment()  {
    private var inventoryListView: ListView? = null
    private var productCatalog: ProductCatalog? = null
    private var inventoryList= ArrayList<Product>()
    private var addProductButton: Button? = null
    private var searchBox: EditText? = null
    private var scanButton: Button? = null
    private var categoty: TextView? = null
    private var viewPager: ViewPager? = null
    private var register: Register? = null
    private var main: MainActivity? = null
    private var res: Resources? = null
    private var noDataLayout:ViewGroup?=null
    var   previousDistanceFromFirstCellToTop=0
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        try {
            productCatalog = Inventory.getInstance().getProductCatalog()
            register = Register.getInstance()
        } catch (e: NoDaoSetException) {
            e.printStackTrace()
        }

        val view = inflater!!.inflate(R.layout.layout_inventory, container, false)

        res = resources
        inventoryListView = view.findViewById<View>(R.id.productListView) as ListView
        addProductButton = view.findViewById<View>(R.id.addProductButton) as Button
        scanButton = view.findViewById<View>(R.id.scanButton) as Button
        searchBox = view.findViewById<View>(R.id.searchBox) as EditText
        categoty = view.findViewById<View>(R.id.category_switch) as TextView
        noDataLayout=view.findViewById(R.id.no_data_container) as ViewGroup
        main = activity as MainActivity
        viewPager = main!!.viewPager
        initUI()
        return view
    }

    /**
     * Initiate this UI.
     */

    fun updateCategory(categor:Category)
    {
        categoty?.text=categor.name
    }


    private fun initUI() {

        categoty?.setOnClickListener{
        (activity as MainActivity).toggleCategoryBottomSheet()
    }
        addProductButton!!.setOnClickListener { v -> showPopup(v) }
        searchBox!!.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (s.length >= SEARCH_LIMIT) {
                    search()
                }
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

        inventoryListView!!.onItemClickListener = OnItemClickListener { myAdapter, myView, position, mylng ->
         showEditPopup(myView,inventoryList.get(position).productId!!)
        }

        scanButton!!.setOnClickListener {
            val scanIntegrator = IntentIntegratorSupportV4(this@InventoryFragment)
            scanIntegrator.initiateScan()
        }
        inventoryListView?.setOnScrollListener(object : AbsListView.OnScrollListener {

            override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {

            }

            override fun onScroll(listview: AbsListView, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {

                /* Check if the first item is already reached to top.*/
                val firstCell = listview.getChildAt(0)
                if(firstCell == null){ return }
                val distanceFromFirstCellToTop = listview.getFirstVisiblePosition() * firstCell.getHeight() - firstCell.getTop()

                if (distanceFromFirstCellToTop < previousDistanceFromFirstCellToTop) {
                    (activity as MainActivity).showView()
                } else if (distanceFromFirstCellToTop > previousDistanceFromFirstCellToTop) {
                    (activity as MainActivity).hideView()

                }
                previousDistanceFromFirstCellToTop = distanceFromFirstCellToTop


            }

        })
        (activity as MainActivity)?.showView()

    }

    /**
     * Show list.
     * @param list
     */
    private fun showList(list: List<Product>?) {

        inventoryList = ArrayList()
        if (list != null) {
            for (product in list) {
                inventoryList.add(product)
            }
        }

        inventoryList?.let {
            if(it.size>0)
            {
                noDataLayout?.visibility=View.GONE
            }
            else
            {
                noDataLayout?.visibility=View.VISIBLE
            }
        }

        val sAdap = InventeryProductAdapter(this , activity!!.applicationContext,inventoryList as MutableList<Product>)
        inventoryListView!!.adapter = sAdap
    }

    // daapter call back
     override fun onClick(data: Product?) {
        super.onClick(data)
         viewPager!!.currentItem = 0
         val id = data?.id
         try {
             productCatalog = Inventory.getInstance().getProductCatalog()
         } catch (e: NoDaoSetException) {
             e.printStackTrace()
         }

        var product = productCatalog!!.getProductById(Integer.parseInt(id.toString()))
         if(activity is MainActivity)
         {
             (activity as MainActivity).openDetailDialog(product)
         }


     }

    override fun onPlusClicked(data: Product?) {
        val id = data?.id!!
        register!!.addItem(productCatalog!!.getProductById(id), BigDecimal.ONE)
        saleFragment.update()
    }

    override fun onMinuslicked(data: Product?) {
        val id = data?.id!!
      //  register!!.addItem(productCatalog!!.getProductById(id), BigDecimal.ONE)
       // saleFragment.update()
    }

    override fun onQtyEntered(data: Product?,qty:BigDecimal) {
        val id = data?.id!!
       // register!!.addItem(productCatalog!!.getProductById(id), qty)
       // saleFragment.update()
    }



    private fun search() {
        val search = searchBox!!.text.toString()

        if (search == "/demo") {
            kotlin.run {

                testAddProduct()
            }

            searchBox!!.setText("")
        } else if (search == "/clear") {
            DatabaseExecutor.getInstance().dropAllData()
            searchBox!!.setText("")
        } else if (search == "") {
            showList(productCatalog!!.allProduct)
        } else {
            val result = productCatalog!!.searchProduct(search)
            showList(result)
            if (result != null) {
                if (result.isEmpty()) {

                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        val scanningResult = IntentIntegrator.parseActivityResult(
                requestCode, resultCode, intent)

        if (scanningResult != null) {
            val scanContent = scanningResult.contents
            searchBox!!.setText(scanContent)
        } else {
            Toast.makeText(activity!!.baseContext, res!!.getString(R.string.fail),
                    Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Test adding product
     */
    protected fun testAddProduct() {
        Demo.testProduct(activity!!.applicationContext)
        Demo.testCategory(activity!!.applicationContext)
        Demo.testCustomer(activity!!.applicationContext)
        Demo.testaddTax(activity!!.applicationContext)
        Toast.makeText(activity!!.applicationContext, res!!.getString(R.string.success),
                Toast.LENGTH_SHORT).show()
    }

    /**
     * Show popup.
     * @param anchorView
     */
     fun showPopup(anchorView: View) {
        val newFragment = AddProductDialogFragment(this@InventoryFragment)
        newFragment.show(fragmentManager, "")
    }
    fun showEditPopup(anchorView: View, productId: Int) {
        val bundle = Bundle()
        bundle.putBoolean("isNewItem", true)
        bundle.putString("sale_id", register?.getCurrentSale()!!.id.toString() + "")
        bundle.putInt("product_id", productId)
        val newFragment = EditFragmentDialog(saleFragment, (saleFragment as SaleFragment).reportFragment)
        newFragment.arguments = bundle
        newFragment.show(fragmentManager, "")

    }



    override fun update() {
        search()
    }

    override fun onResume() {
        super.onResume()
        update()
    }

    companion object {

        protected val SEARCH_LIMIT = 0
    }

}

private fun <E> List<E>.add(element: Map<String?, String?>) {

}
