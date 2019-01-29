package com.ionob.pos.utils

import android.content.Context
import android.widget.TextView
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.skydoves.powermenu.MenuBaseAdapter
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.ionob.pos.R


class IconMenuAdapter : MenuBaseAdapter<IconPowerMenuItem>() {

   override fun getView(index: Int, view: View?, viewGroup: ViewGroup): View {
        var view = view
        val context = viewGroup.context

        if (view == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.title_menu, viewGroup, false)
        }

        val item = getItem(index) as IconPowerMenuItem
        val icon = view!!.findViewById<View>(R.id.icon) as ImageView
        icon.setImageDrawable(item.icon)
        val title = view!!.findViewById<View>(R.id.item_title) as TextView
        title.setText(item.title)
        return super.getView(index, view, viewGroup)
    }
}

class IconPowerMenuItem(public val icon: Drawable, public val title: String)// --- skipped setter and getter methods