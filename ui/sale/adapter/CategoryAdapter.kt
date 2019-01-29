package com.ionob.pos.ui.sale.adapter

/**
 * Created by yoonus on 2/16/2017.
 */

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.icu.util.ULocale
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import com.ionob.pos.R
import com.ionob.pos.domain.inventory.Category
import com.ionob.pos.ui.sale.model.CategoryModel
import com.ionob.pos.utils.ListViewCallback


import java.io.File


class CategoryAdapter : RecyclerView.Adapter<CategoryAdapter.MyViewHolder> {
    private var categoryModels: List<Category>? = null
     var context: Context?=null
     var itemview: View?=null
     var TAG = this.javaClass.simpleName


    fun setData(data:List<Category>)
    {
        this.categoryModels=data
        notifyDataSetChanged()
    }

    inner class MyViewHolder(v: View) : RecyclerView.ViewHolder(v) {

         val categoryName: TextView


        init {

            categoryName = v.findViewById(R.id.txtTitle) as TextView

        }


    }

    fun showMenu(view: View, record: Any) {
        val menu = PopupMenu(context, view)
        val selectedObject = record as CategoryModel
        menu.setOnMenuItemClickListener { item ->
            val id = item.itemId

            true
        }




    }



    var adapterCallback:ListViewCallback?=null
    @JvmOverloads constructor(categoryModels: List<Category>, callback: ListViewCallback?, context: Context) {
        this.categoryModels = categoryModels
        try {
            this.adapterCallback = callback
            this.context = context

        } catch (e: ClassCastException) {
            throw ClassCastException("Activity must implement AdapterCallback.")
        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

       itemview = LayoutInflater.from(parent.context)
                    .inflate(R.layout.category_items_adapter, parent, false)
        return MyViewHolder(itemview!!)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        //        holder.rootLayout.setOrientation(isListView ? LinearLayout.HORIZONTAL : LinearLayout.VERTICAL);
        val eachItem = categoryModels!![position]// Offset for header
        holder.categoryName.setText(eachItem.name)
        itemview?.setOnClickListener {
            if (adapterCallback != null)
                adapterCallback!!.onItemClicked(eachItem)
        }

        itemview?.setOnLongClickListener { view ->
         //   adapterCallback!!.onItemLongClick(eachItem)
            false
        }

        holder.itemView.setOnTouchListener { v, event -> false }

    }


    override fun getItemCount(): Int {
       // return categoryModels!!.size
        return categoryModels?.size?:0
    }



}


