package com.ionob.pos.ui.sale

import java.util.ArrayList

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

import com.ionob.pos.R
import com.ionob.pos.domain.inventory.LineItem
import com.ionob.pos.domain.sale.Register
import com.ionob.pos.db.NoDaoSetException
import com.ionob.pos.ui.MainActivity
import com.ionob.pos.ui.component.UpdatableFragment
import com.ionob.pos.ui.sale.SalesProductAdapter.SalesAdapterCallback
import kotlinx.android.synthetic.main.layout_sale.*
import java.math.BigDecimal

/**
 * UI for Sale operation.
 * @author Ionob Team
 */
@SuppressLint("ValidFragment")
class SaleFragment
/**
 * Construct a new SaleFragment.
 * @param
 */
(public val reportFragment: UpdatableFragment) : UpdatableFragment() {

    private var register: Register? = null
    private var saleList= ArrayList<LineItem>()
    private var saleListView: ListView? = null
    private var clearButton: Button? = null
    private var totalPrice: TextView? = null
    private var endButton: Button? = null
    private var res: Resources? = null
    private var noDataLayout:ViewGroup?=null
    var   previousDistanceFromFirstCellToTop=0
    var selectedLine:LineItem?=null;

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.layout_sale, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            register = Register.getInstance()
        } catch (e: NoDaoSetException) {
            e.printStackTrace()
        }



        res = resources
        saleListView = view.findViewById<View>(R.id.sale_List) as ListView
        totalPrice = view.findViewById<View>(R.id.totalPrice) as TextView
        clearButton = view.findViewById<View>(R.id.clearButton) as Button
        endButton = view.findViewById<View>(R.id.endButton) as Button
        noDataLayout=view.findViewById(R.id.no_data_container) as ViewGroup
        initUI()
    }

    /**
     * Initiate this UI.
     */
    private fun initUI() {

        //saleListView!!.onItemClickListener = OnItemClickListener { arg0, arg1, arg2, arg3 -> showEditPopup(arg1, arg2) }

        clearButton!!.setOnClickListener {
            val viewPager = (activity as MainActivity).viewPager
           // viewPager!!.currentItem = 1
        }

        endButton!!.setOnClickListener { v ->
            if (register!!.hasSale()) {
                showPaymetn(v)
            } else {
                Toast.makeText(activity?.baseContext, res!!.getString(R.string.hint_empty_sale), Toast.LENGTH_SHORT).show()
            }
        }

        clearButton!!.setOnClickListener {
            if (!register!!.hasSale() || register!!.getCurrentSale()!!.allLineItem.isEmpty()) {
                Toast.makeText(activity?.baseContext, res!!.getString(R.string.hint_empty_sale), Toast.LENGTH_SHORT).show()
            } else {
                showConfirmClearDialog()
            }
        }


        saleListView?.setOnScrollListener(object : AbsListView.OnScrollListener {

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
     * Show list
     * @param list
     */
    private fun showList(list: List<LineItem>) {
        saleList.clear()
        for (line in list) {

            val add = saleList?.add(line)
        }
      if(saleList!=null&&saleList.size>0) {
          noDataLayout?.visibility=View.GONE
          total_items.text=saleList?.size?.toString()
          (activity as MainActivity).udateBadge(saleList?.size)
          val sAdap = SalesProductAdapter(activity!!.baseContext, saleList,object: SalesAdapterCallback{
              override fun onPlusClicked(data: LineItem?) {
                  updateLine(data)
              }
              override fun onMinusClicked(data: LineItem?) {
                  updateLine(data)
              }

              override fun onItemClicked(data: LineItem?) {
                   selectedLine=data
                  (activity as MainActivity)?.selectedLine=data
                  (activity as MainActivity).toggleAttrBottomSheet()

              }
          })
          saleListView!!.adapter = sAdap
      }
        else
      {
          noDataLayout?.visibility=View.VISIBLE
      }
    }

    /**
     * Try parsing String to double.
     * @param value
     * @return true if can parse to double.
     */
    fun tryParseDouble(value: String): Boolean {
        try {
            java.lang.Double.parseDouble(value)
            return true
        } catch (e: NumberFormatException) {
            return false
        }

    }
    fun updateLine(lineItem: LineItem?)
    {
        register!!.updateItem(
                register?.getCurrentSale()?.id!!,
                lineItem!!, lineItem.quantity,
                lineItem.priceAtSale?: BigDecimal.ZERO)

        var amount=register?.total!!.subtract(register?.totaltax).toString()

        totalPrice!!.text = amount.toFormatedString()
//        totalPrice!!.text = register?.total.toString()
        net_amount?.text=register?.total?.subtract(register?.totalDiscount)?.toFormatedString()
        tax_amount?.text=(register?.totaltax)?.toFormatedString()
    }

    /**
     * Show edit popup.
     * @param anchorView
     * @param position
     */
    fun showEditPopup(anchorView: View, position: Int) {
        val bundle = Bundle()
        bundle.putString("position", position.toString() + "")
        bundle.putString("sale_id", register!!.getCurrentSale()!!.id.toString() + "")
        bundle.putString("product_id", register!!.getCurrentSale()!!.getLineItemAt(position)!!.product?.id.toString() + "")
        val newFragment = EditFragmentDialog(this@SaleFragment, reportFragment)
        newFragment.arguments = bundle
        newFragment.show(fragmentManager, "")

    }

    /**
     * Show popup
     * @param anchorView
     */
    fun showPaymetn(anchorView: View) {
        val bundle = Bundle()
        bundle.putString("edttext", totalPrice!!.text.toString())
        val newFragment = PaymentFragmentDialog(this@SaleFragment, reportFragment)
        newFragment.arguments = bundle
        newFragment.show(fragmentManager, "")
    }

    override fun update() {
        if (register!!.hasSale()) {
            showList(register!!.getCurrentSale()!!.allLineItem)
            totalPrice!!.text = register?.total!!.subtract(register?.totaltax).toFormatedString()

            total_discount.text=register?.totalDiscount.toString()

            net_amount?.text=register?.total?.subtract(register?.totalDiscount)?.toFormatedString()

            tax_amount?.text=(register?.totaltax)?.toFormatedString()

        } else {
            showList(ArrayList())
            totalPrice!!.text = "0.00"
        }
    }

    override fun onResume() {
        super.onResume()
        update()
    }

    /**
     * Show confirm or clear dialog.
     */
    private fun showConfirmClearDialog() {
        val dialog = AlertDialog.Builder(activity)
        dialog.setTitle(res!!.getString(R.string.dialog_clear_sale))
        dialog.setPositiveButton(res!!.getString(R.string.no)) { dialog, which -> }

        dialog.setNegativeButton(res!!.getString(R.string.clear)) { dialog, which ->
            register!!.cancleSale()
            update()
        }

        dialog.show()
    }

}
