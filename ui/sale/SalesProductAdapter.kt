package com.ionob.pos.ui.sale

import android.content.Context
import android.preference.PreferenceManager
import android.support.v4.app.FragmentActivity
import android.util.Log
import android.util.LogPrinter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.ionob.pos.R
import com.ionob.pos.domain.inventory.LineItem
import com.ionob.pos.domain.inventory.Product
import java.math.BigDecimal

class SalesProductAdapter (private var context: Context, private var notesList: MutableList<LineItem>?,var callback:SalesAdapterCallback) : BaseAdapter() {

    private  var mlayoutInflater:LayoutInflater?=null
    private var  productList: MutableList<Product>?=null
    var haveOutOfstocks=false
    init {
        this.mlayoutInflater= LayoutInflater.from(context)
    }
   interface  SalesAdapterCallback
   {
       fun onPlusClicked(data:LineItem?)
       fun onMinusClicked(data:LineItem?)
       fun onItemClicked(data:LineItem?)
   }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {

        val view: View?
        val vh: ViewHolder

        if (convertView == null) {
            view = mlayoutInflater?.inflate(R.layout.cart_item_adapter, parent, false)
            vh = ViewHolder(view)
            view?.tag = vh
            Log.i("JSA", "set Tag for ViewHolder, position: " + position)
        } else {
            view = convertView
            vh = view.tag as ViewHolder
        }

        vh.name.text = notesList?.get(position)?.product?.name
        vh.qty.setText(notesList?.get(position)?.quantity?.toString())
        vh.price.text = notesList?.get(position)?.priceAtSale.toString()
        var discount=notesList?.get(position)?.discount
        vh.lindeDiscount.text= discount.toString()
        vh.textTotalAmoutn.text = notesList?.get(position)?.totalAmount?.subtract(discount).toString()
        validateStock(position, vh)
        vh.qtyPlus.setOnClickListener {
            if(vh.qty.text.toString().isNullOrEmpty()||vh.qty.text.toString().equals("")) return@setOnClickListener
            if(validateStock(position,vh)) {
                var selectedLine = notesList?.get(position)
                selectedLine?.quantity = selectedLine?.quantity?.add(BigDecimal.ONE)!!
                vh.qty.setText(selectedLine?.quantity?.toString())
                vh.textTotalAmoutn.setText(notesList?.get(position)?.totalAmount?.toString())
                callback.onPlusClicked(selectedLine)
            }
        }
        vh.qtyMinus.setOnClickListener {
            if(vh.qty.text.toString().isNullOrEmpty()||vh.qty.text.toString().toBigDecimal().compareTo(BigDecimal.ZERO)<=0) {
                return@setOnClickListener
            }
            if(validateStock(position,vh)==true||validateStock(position,vh)==false) {
                var selectedLine = notesList?.get(position)
                selectedLine?.quantity = selectedLine?.quantity?.subtract(BigDecimal.ONE)!!
                if(selectedLine?.quantity.compareTo(BigDecimal.ZERO)<=0)
                {
                    notesList?.get(position)?.discount= BigDecimal.ZERO
                    var discount=notesList?.get(position)?.discount
                    vh.lindeDiscount.text= discount.toString()
                }
                vh.qty.setText(notesList?.get(position)?.quantity?.toString())
                vh.textTotalAmoutn.setText(notesList?.get(position)?.totalAmount?.toString())
                callback.onMinusClicked(selectedLine)
            }

        }
        vh.itemContainer.setOnClickListener{
            callback.onItemClicked(notesList?.get(position))
        }

        return view
    }
 /*@return true if stock vailable*/
    private fun validateStock(position: Int, vh: ViewHolder):Boolean {
        var stock = notesList?.get(position)?.product?.stockQty ?: BigDecimal.ZERO!!
       var qty = notesList?.get(position)?.quantity ?: BigDecimal.ZERO!!
        var isStockCheckenabled = PreferenceManager.getDefaultSharedPreferences(this.context).getBoolean(context.getString(R.string.key_check_stock), true)
        if (isStockCheckenabled) {
            if (stock?.minus(qty).signum()<=0 ) {
                vh.stock_text.text = stock.toString()
                vh.outofStockContainer.visibility = View.VISIBLE
                haveOutOfstocks = true
                return false
            } else {
                vh.outofStockContainer.visibility = View.GONE
                return true
            }
        }
        else
        {  if ((stock?.minus(qty).signum()) <=0) {
            vh.stock_text.text = stock.toString()
            vh.outofStockContainer.visibility = View.VISIBLE
        } else {
            vh.outofStockContainer.visibility = View.GONE
        }
            return true
        }
    }

    override fun getItem(position: Int): Any? {
        return notesList!![position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return notesList?.size!!
    }
}

private class ViewHolder(view: View?) {
    val name: TextView
    val qty: EditText
    val price: TextView
    val qtyPlus: ImageView
    val qtyMinus: ImageView
    val itemContainer:LinearLayout
    val textTotalAmoutn:TextView
    val outofStockContainer:LinearLayout
    val stock_text:TextView
    val lindeDiscount:TextView

    init {
        this.name = view?.findViewById(R.id.cart_item_name) as TextView
        this.stock_text = view?.findViewById(R.id.stock_text) as TextView
        this.lindeDiscount = view?.findViewById(R.id.line_dicount) as TextView
        this.qty = view?.findViewById(R.id.cart_product_quantity) as EditText
        this.price = view?.findViewById(R.id.cart_price_text) as TextView
        this.qtyPlus = view?.findViewById(R.id.cart_plus_img) as ImageView
        this.qtyMinus = view?.findViewById(R.id.cart_minus_img) as ImageView
        this.itemContainer = view?.findViewById(R.id.item_container) as LinearLayout
        this.textTotalAmoutn=view?.findViewById(R.id.cart_total_price_text) as TextView
        this.outofStockContainer=view.findViewById(R.id.out_stock_layout) as LinearLayout
    }

    //  if you target API 26, you should change to:
//        init {
//            this.tvTitle = view?.findViewById<TextView>(R.id.tvTitle) as TextView
//            this.tvContent = view?.findViewById<TextView>(R.id.tvContent) as TextView
//        }
}