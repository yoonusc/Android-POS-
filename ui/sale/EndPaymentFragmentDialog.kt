package com.ionob.pos.ui.sale

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView

import com.ionob.pos.R
import com.ionob.pos.domain.DateTimeStrategy
import com.ionob.pos.domain.sale.Register
import com.ionob.pos.db.NoDaoSetException
import com.ionob.pos.ui.component.UpdatableFragment

/**
 * A dialog shows the total change and confirmation for Sale.
 * @author Ionob Team
 */
@SuppressLint("ValidFragment")
class EndPaymentFragmentDialog
/**
 * End this UI.
 * @param saleFragment
 * @param reportFragment
 */
(private val saleFragment: UpdatableFragment, private val reportFragment: UpdatableFragment) : DialogFragment() {

    private var doneButton: Button? = null
    private var chg: TextView? = null
    private var regis: Register? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        try {
            regis = Register.getInstance()
        } catch (e: NoDaoSetException) {
            e.printStackTrace()
        }

        val v = inflater!!.inflate(R.layout.dialog_paymentsuccession, container, false)
        val strtext = arguments!!.getString("edttext")
        chg = v.findViewById<View>(R.id.changeTxt) as TextView
        chg!!.text = strtext
        doneButton = v.findViewById<View>(R.id.doneButton) as Button
        doneButton!!.setOnClickListener { end() }

        return v
    }

    /**
     * End
     */
    private fun end() {
        regis!!.endSale(DateTimeStrategy.currentTime)
        saleFragment.update()
        reportFragment.update()
        this.dismiss()
    }

}
