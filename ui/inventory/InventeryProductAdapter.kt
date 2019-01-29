package com.ionob.pos.ui.inventory

import android.annotation.TargetApi
import android.content.Context
import android.content.res.ColorStateList
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.ionob.pos.R
import com.ionob.pos.domain.inventory.Product
import java.math.BigDecimal

class InventeryProductAdapter (private var cb:CilckCallBack,private var context: Context, private var notesList: MutableList<Product>?) : BaseAdapter() {

    private  var mlayoutInflater:LayoutInflater?=null
    private var  productList: MutableList<Product>?=null
    init {
        this.mlayoutInflater= LayoutInflater.from(context)
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {

        val view: View?
        val vh: ViewHolder

        if (convertView == null) {
            view = mlayoutInflater?.inflate(R.layout.item_adapter, parent, false)
            vh = ViewHolder(view)
            view?.tag = vh
            Log.i("JSA", "set Tag for ViewHolder, position: " + position)
        } else {
            view = convertView
            vh = view.tag as ViewHolder
        }
        vh.id=notesList?.get(position)?.id
        vh.name.text = notesList?.get(position)?.name
        vh.stockQty.text = notesList?.get(position)?.stockQty.toString()
        if( (notesList?.get(position)?.stockQty?:BigDecimal.ZERO).signum()<=0) {
            vh.stockQty.setTextColor(context.resources.getColor(R.color.red))
            vh.stockImg.setImageResource((R.drawable.stock_out))
        }
        else {
            vh.stockQty.setTextColor(context.resources.getColor(R.color.black))
            vh.stockImg.setImageResource((R.drawable.stock_con))
        }
        vh.price.text = notesList?.get(position)?.unitPrice.toString()
        vh.optView.setOnClickListener {

            cb.onClick(notesList?.get(position))
        }
//        view?.setOnClickListener {
//            cb.onClick(notesList?.get(position))
//        }
        vh.cartPlus.setOnClickListener {
            var data=vh.qty.text?.toString()?:"0"
            var finalQty=BigDecimal.ZERO
            if (data.length>0)
            {finalQty=data.toBigDecimal().add(BigDecimal.ONE)}
            vh.qty.setText(finalQty.toString())
            cb.onPlusClicked(notesList?.get(position))
        }

        vh.cartMinus.setOnClickListener {
            var data=vh.qty.text?.toString()?:"0"
            var finalQty=BigDecimal.ZERO
            if (data.length>0)
            {finalQty=data.toBigDecimal().subtract(BigDecimal.ONE)}
            vh.qty.setText(finalQty.toString())
            cb.onPlusClicked(notesList?.get(position))
        }
        vh.qty.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (s.length > 0) {
                   cb.onQtyEntered(notesList?.get(position),s.toString().toBigDecimal())
                }
                else if(s.length==0)
                {
                    vh.qty.text="0"
                }
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

        return view
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
    val qty: TextView
    val stockQty: TextView
    val stockImg: ImageView
    val price: TextView
    val cartPlus: ImageView
    val cartMinus: ImageView
    var id:Int?=0
    val optView:View

    init {
        this.name = view?.findViewById(R.id.from_name) as TextView
        this.qty = view?.findViewById(R.id.cart_product_quantity_tv) as TextView
        this.stockQty = view?.findViewById(R.id.plist_stock_text) as TextView
        this.price = view?.findViewById(R.id.plist_price_text) as TextView
        this.optView=view?.findViewById(R.id.optionView)
        this.cartPlus=view?.findViewById(R.id.cart_plus_img)
        this.cartMinus=view?.findViewById(R.id.cart_minus_img)
        this.stockImg=view?.findViewById(R.id.stock_img)
    }

    //  if you target API 26, you should change to:
//        init {
//            this.tvTitle = view?.findViewById<TextView>(R.id.tvTitle) as TextView
//            this.tvContent = view?.findViewById<TextView>(R.id.tvContent) as TextView
//        }
}