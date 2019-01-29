package com.ionob.pos.ui.sale

import android.app.AlertDialog

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

import com.ionob.pos.R
import com.ionob.pos.domain.DateTimeStrategy
import com.ionob.pos.domain.sale.Sale
import com.ionob.pos.domain.sale.Register
import com.ionob.pos.ui.MainActivity
import com.ionob.pos.ui.component.UpdatableFragment
import kotlinx.android.synthetic.main.layout_payment.*
import suman.com.andoirdbluetoothprint.MainActivityPrint
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat

/**
 * UI for showing payment
 * @author Ionob Team
 */
 class PaymentFragment : UpdatableFragment() {



    var register:Register?=null
    var isViewCreated=false
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.layout_payment, container, false)
        return view
    }

     override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
         register= Register.getInstance()
         isViewCreated=true
         confirmButton.setOnClickListener {
            var sale=register?.getCurrentSale()
            writ_off_amount?.text?.toString()?.let {  if (!it.isNullOrEmpty())  sale?.discountAmountt=it.toBigDecimal()}
             sale?.referenceNumber=reference_no?.text?.toString()
             sale?.subTotal?.let {
                 if(slectedPaymentMode!= CREDIT)
                      sale?.amountPaid = it
                 else
                     sale?.amountPaid = BigDecimal.ZERO
                // sale?.subTotal = it
             }
//             else
//             {
//                 showCreditConfirmClearDialog(sale)
//                // Toast.makeText(,Toast.LENGTH_LONG).show()
//                 return@setOnClickListener
//
//             }}
             sale?.paymentType=slectedPaymentMode
             register?.endSale(DateTimeStrategy.currentTime)
             startActivity(Intent(activity
                     ,MainActivityPrint::class.java))
         }
         manageDicuountSheet()
         initRadioButtons()
    }
    private fun showCreditConfirmClearDialog(sale: Sale?) {
        val dialog = AlertDialog.Builder(activity)
        dialog.setTitle("Warning")
        dialog.setMessage("No payment amount!! Sales will Save as CREDIT ?")
        dialog.setPositiveButton(activity?.getString(R.string.no)) { dialog, which -> }
        dialog.setNegativeButton(activity?.getString(R.string.okay)) { dialog, which ->
            slectedPaymentMode= CREDIT
            sale?.paymentType=slectedPaymentMode
            register?.endSale(DateTimeStrategy.currentTime)
            startActivity(Intent(activity
                    ,MainActivityPrint::class.java))
            dialog.dismiss()
        }

        dialog.show()
    }
     override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
         if(isVisibleToUser&&isViewCreated) {
             update()
             (activity as MainActivity)?.hideView()
         }
    }


    override fun update() {
        if (register!!.hasSale()) {
            writ_off_amount?.setText("")
            writ_off_percent?.setText("")

            gross_amount?.text =register?.total!!.subtract(register?.totaltax).toFormatedString()
            taxt_total?.text = register!!.totaltax.toFormatedString() + ""
            line_discount_total?.text=register?.totalDiscount?.toFormatedString()
            net_amount?.setText(register?.total?.subtract(register?.totalDiscount)?.toFormatedString())
            line_net_total?.text=register?.total?.subtract(register?.totalDiscount)?.toFormatedString()
            this.netAmount=register?.total?.subtract(register?.totalDiscount)
            register?.getCurrentSale()?.subTotal=netAmount

        } else {

            gross_amount?.text ="00.00"
            line_discount_total?.text="00.00"
            taxt_total?.text="00.00"
            this.netAmount= BigDecimal.ZERO
        }

    }

   var netAmount=BigDecimal.ZERO



    override fun onResume() {
        super.onResume()
        update();
        // it shouldn't call update() anymore. Because super.onResume()
        // already fired the action of spinner.onItemSelected()
    }



    fun manageDicuountSheet()
    {
        var  discountAmt=BigDecimal.ZERO
        var tempNet=BigDecimal.ZERO
        writ_off_amount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                if ( writ_off_amount.hasFocus()) {
                    try{
                    if (s.length >=0) {
                        try {
                            discountAmt = BigDecimal(s.toString()).setScale(2, RoundingMode.HALF_UP)
                        } catch (e: Exception) {
                            discountAmt = BigDecimal.ZERO
                        }
                         tempNet= netAmount.subtract(discountAmt)

                    }
                    if (tempNet.signum()!! < 0) {
                        discountAmt =  netAmount
                        tempNet = BigDecimal.ZERO
                        writ_off_amount.setText(discountAmt.toFormatedString())
                    }
                    if (netAmount.signum()==0){ return}
                    var discPercent = discountAmt.divide(netAmount,2, RoundingMode.HALF_UP)
                    discPercent = discPercent.scaleByPowerOfTen(2)
                    writ_off_percent.setText(discPercent.toFormatedString())
                    register?.getCurrentSale()?.subTotal=tempNet
                    net_amount.setText(tempNet.toFormatedString())
                }
                catch (e:NumberFormatException){e.printStackTrace()}
                }

            }
        })

        writ_off_percent.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                if (writ_off_percent.hasFocus()) {

                    try {
                        if (s.length >= 0) {
                            var percent = if (s.toString().isNullOrEmpty()) BigDecimal.ZERO else s.toString()?.toBigDecimal()
                            discountAmt = (percent.setScale(2, RoundingMode.HALF_UP).multiply(netAmount).scaleByPowerOfTen(-2))
                            tempNet = netAmount.subtract(discountAmt)
                        }
                        if (tempNet.signum()!! < 0) {
                            discountAmt = netAmount
                            tempNet = BigDecimal.ZERO
                            writ_off_amount.setText(discountAmt.toFormatedString())
                            writ_off_percent.setText("100")
                        }
                        writ_off_amount.setText((discountAmt).toFormatedString())
                        register?.getCurrentSale()?.subTotal = tempNet
                        net_amount.setText(tempNet.toFormatedString())
                    }
                    catch (e:NumberFormatException){e.printStackTrace()}
                }

            }
        })




    }

    var slectedPaymentMode= CASH
    private fun initRadioButtons() {
        radio_cash.isChecked=true
        payment_type_group.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->

            when (checkedId) {
                R.id.radio_cash -> {

                    slectedPaymentMode= CASH
                }
                R.id.radio_credit -> {

                    slectedPaymentMode= CREDIT

                }
                R.id.radio_card -> {
                    slectedPaymentMode= CARD
                }
                R.id.radio_cheque -> {
                    slectedPaymentMode= CHECK
                }
                R.id.radio_netbanking -> {
                  slectedPaymentMode= NET
                }
                R.id.radio_upi-> {
                   slectedPaymentMode= UPI
                }

            }

        })
    }

    companion object {
        val CREDIT = "credit"
        val CASH = "cash"
        val CARD = "card"
        val CHECK = "check"
        val NET = "net"
        val UPI="upi"
    }


}

 fun BigDecimal.toFormatedString(): String {

    val df = DecimalFormat("#,###.00")
    return df.format(this)
}
fun String.toFormatedString(): String {

    val df = DecimalFormat("#,###.00")
    return df.format(this)
}