package com.fitbae.fitness.adapter

import android.app.Activity
import android.content.Context
import android.media.ImageReader
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.ionob.pos.R
import com.ionob.pos.ui.sale.model.CustomerModel
import java.util.*
import kotlin.collections.ArrayList

class FBCustomerDialogAdapter(private val customers: ArrayList<CustomerModel>,
                              private val context: Context) : BaseAdapter() {
    private var searchedData: ArrayList<CustomerModel>? = null
    private var onItemSelectedListner: OnItemSelected? = null

    init {
        searchedData = ArrayList()
        searchedData!!.addAll(customers)
    }

    override fun getCount(): Int {
        return customers.size
    }

    override fun getItem(position: Int): CustomerModel {
        return customers[position]
    }


    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, view: View?, viewGroup: ViewGroup): View {
        var convertView = view
        val holder: ViewHolder
        val inflater = (context as Activity).layoutInflater
        val data = customers[position]
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.adapter_customer_search, null)
            holder = ViewHolder(convertView!!)
            convertView.tag = holder
        } else {
            holder = convertView.tag as ViewHolder
        }
     //   holder.customerName.tag = position
       // holder.countryCodeText.tag = position

        holder.customerName.text=data.customerName
        holder.customerAddress.text=data.address1
        holder.customerPhoneNumebr.text=data.phone
        holder.customerEmail.text=data.email
        holder.view.setOnClickListener {
            onItemSelectedListner!!.onItemSelected(data)
        }


        return convertView
    }

    internal inner class ViewHolder(itemView: View) {
        var view = itemView
        var customerName: TextView = itemView.findViewById<View>(R.id.customer_name) as TextView
        var customerAddress: TextView = itemView.findViewById<View>(R.id.customer_address) as TextView
        var customerPhoneNumebr: TextView = itemView.findViewById<View>(R.id.customer_phone) as TextView
        var customerEmail: TextView = itemView.findViewById<View>(R.id.customer_email) as TextView
    }

    fun setListner(onItemSelectlistner: OnItemSelected?) {
        this.onItemSelectedListner = onItemSelectlistner
    }

    interface OnItemSelected {
        fun onItemSelected(selectedCustomer: CustomerModel)
    }

    // Filter Class
    fun filter(charText: String) {
        val searchWord = charText.toLowerCase(Locale.getDefault())
        customers.clear()
        if (searchWord.isEmpty()) {
            customers.addAll(searchedData!!)
        } else {
            for (wp in searchedData!!) {
                if (wp.customerName!!.toLowerCase(Locale.getDefault()).contains(charText.toLowerCase(Locale.getDefault()))
                        || wp.customerCode!!.toLowerCase(Locale.getDefault()).contains(charText.toLowerCase(Locale.getDefault()))) {
                    customers.add(wp)
                }
            }
        }
        notifyDataSetChanged()
    }
}
