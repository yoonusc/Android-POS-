package com.ionob.pos.ui.component

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.SimpleAdapter

/**
 * An adapter for ListView which able to assign a sub-button in each row data.
 *
 * @author Ionob Team
 */
class ButtonAdapter
/**
 * Construct a new ButtonAdapter
 * @param context
 * @param data
 * @param resource
 * @param from
 * @param to
 * @param buttonId
 * @param tag
 */
(context: Context, private val data: List<Map<String, *>>,
 resource: Int, from: Array<String>, to: IntArray, private val buttonId: Int, private val tag: String) : SimpleAdapter(context, data, resource, from, to) {

    override fun getView(position: Int, convertView: View, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)
        (view.findViewById(buttonId) as View).tag = data[position][tag]
        return view
    }

}
