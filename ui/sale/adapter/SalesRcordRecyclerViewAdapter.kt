

package com.ionob.pos.ui.sale.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.ionob.pos.R
import com.ionob.pos.domain.sale.Sale
import com.ionob.pos.ui.sale.model.SalesRecordModel

class SalesRcordRecyclerViewAdapter(private val items: List<Sale>, var context:Context, var onItemClick:OnItemClickListener,var onlongclicklistner:OnlongClickListener)
    : RecyclerView.Adapter<SalesRcordRecyclerViewAdapter.ViewHolder>(), View.OnClickListener ,View.OnLongClickListener{



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.sales_record_recycler, parent, false)
        v.setOnClickListener(this)
        v.setOnLongClickListener(this)
        return ViewHolder(v)
    }
    fun setLine(lines:List<Sale>)
    {
        var data=(items as MutableList)
        data.clear()
        data.addAll(lines)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.itemView.setOnClickListener(View.OnClickListener {
            onItemClick.onItemClick(SalesRecordModel(item.id))

        })

        holder.itemView.setOnLongClickListener(View.OnLongClickListener {

            onlongclicklistner.onlongitemclickListener(SalesRecordModel(item.id))
            true
        })

        holder.text.text=item.id.toString()
        holder.netamount.text=item.subTotal?.toString()
        holder.paymentMode.text=item.paymentType
    }

    override fun getItemCount(): Int {
        return items.size //items.size
    }

    override fun onClick(v: View) {
      //  onItemClickListener!!.onItemClick(v, v.tag as SalesRecordModel)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var image: ImageView
        var text: TextView
        var netamount: TextView
        var paymentMode: TextView

        init {
            image = itemView.findViewById(R.id.image) as ImageView
            text = itemView.findViewById(R.id.text) as TextView
            netamount= itemView.findViewById(R.id.net_amount) as TextView
            paymentMode = itemView.findViewById(R.id.payment_mode) as TextView
        }
    }
    override fun onLongClick(p0: View?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
    interface OnItemClickListener {

        fun onItemClick(viewModel: SalesRecordModel)

    }
    interface OnlongClickListener {

        fun onlongitemclickListener(viewModel: SalesRecordModel)

    }


}
