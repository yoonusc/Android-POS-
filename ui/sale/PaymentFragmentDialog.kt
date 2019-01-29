package com.ionob.pos.ui.sale

import com.ionob.pos.R
import com.ionob.pos.ui.component.UpdatableFragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

/**
 * A dialog for input a money for sale.
 * @author Ionob Team
 */
@SuppressLint("ValidFragment")
class PaymentFragmentDialog
/**
 * Construct a new PaymentFragmentDialog.
 * @param saleFragment
 * @param reportFragment
 */
(private val saleFragment: UpdatableFragment, private val reportFragment: UpdatableFragment) : DialogFragment() {

    private var totalPrice: TextView? = null
    private var input: EditText? = null
    private var clearButton: Button? = null
    private var confirmButton: Button? = null
    private var strtext: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater!!.inflate(R.layout.dialog_payment, container, false)
        strtext = arguments?.getString("edttext")
        input = v.findViewById<View>(R.id.dialog_saleInput) as EditText
        totalPrice = v.findViewById<View>(R.id.payment_total) as TextView
        totalPrice!!.text = strtext
        clearButton = v.findViewById<View>(R.id.clearButton) as Button
        clearButton!!.setOnClickListener { end() }

        confirmButton = v.findViewById<View>(R.id.confirmButton) as Button
        confirmButton!!.setOnClickListener(View.OnClickListener {
            val inputString = input!!.text.toString()

            if (inputString == "") {
                Toast.makeText(activity?.baseContext, resources.getString(R.string.please_input_all), Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }
            val a = java.lang.Double.parseDouble(strtext)
            val b = java.lang.Double.parseDouble(inputString)
            if (b < a) {
                Toast.makeText(activity?.baseContext, resources.getString(R.string.need_money) + " " + (b - a), Toast.LENGTH_SHORT).show()
            } else {
                val bundle = Bundle()
                bundle.putString("edttext", (b - a).toString() + "")
                val newFragment = EndPaymentFragmentDialog(
                        saleFragment, reportFragment)
                newFragment.arguments = bundle
                newFragment.show(fragmentManager, "")
                end()
            }
        })
        return v
    }

    /**
     * End.
     */
    private fun end() {
        this.dismiss()

    }


}
