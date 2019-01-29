package com.ionob.pos.ui.sale

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.DialogFragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView

import com.ionob.pos.R
import com.ionob.pos.domain.inventory.LineItem
import com.ionob.pos.domain.sale.Register
import com.ionob.pos.db.NoDaoSetException
import com.ionob.pos.domain.inventory.Inventory
import com.ionob.pos.domain.inventory.ProductCatalog
import com.ionob.pos.ui.component.UpdatableFragment
import kotlinx.android.synthetic.main.dialog_saleedit.*
import java.math.BigDecimal

/**
 * A dialog for edit a LineItem of sale,
 * overriding price or set the quantity.
 * @author Ionob Team
 */
@SuppressLint("ValidFragment")
class EditFragmentDialog
/**
 * Construct a new  EditFragmentDialog.
 * @param saleFragment
 * @param reportFragment
 */
(private val saleFragment: UpdatableFragment, private val reportFragment: UpdatableFragment) : DialogFragment() , View.OnClickListener{


    private var register: Register? = null
    private var quantityBox: EditText? = null
    private var priceBox: EditText? = null
    private var comfirmButton: Button? = null
    private var saleId: String? = null
    private var position: String? = null
    private var lineItem: LineItem? = null
    private var removeButton: Button? = null
    private var isNew:Boolean=false
    private var productId:Int = 0
    private var produName:TextView?= null
    private var outofStockText:TextView?= null
    var productCatalog: ProductCatalog? = null
    private var plusButton: ImageView? = null
    private var minusButton: ImageView? = null
    private var discountImAmt:EditText?=null
    private var discountPerccent:EditText?=null
    private var netAmount:BigDecimal= BigDecimal.ZERO
    private var stockQty:BigDecimal= BigDecimal.ZERO
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.dialog_saleedit, container, false)
    }

    override fun onViewCreated(v: View, savedInstanceState: Bundle?) {

        try {
            register = Register.getInstance()
        } catch (e: NoDaoSetException) {
            e.printStackTrace()
        }
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        quantityBox = v.findViewById<View>(R.id.quantityBox) as EditText
        priceBox = v.findViewById<View>(R.id.priceBox) as EditText
        comfirmButton = v.findViewById<View>(R.id.confirmButton) as Button
        removeButton = v.findViewById<View>(R.id.removeButton) as Button
        produName=v.findViewById(R.id.product_name) as TextView
        outofStockText=v.findViewById(R.id.out_of_stock_text) as TextView
        plusButton=v.findViewById(R.id.plus_btn) as ImageView
        minusButton=v.findViewById(R.id.minus_icon) as ImageView

        plusButton?.setOnClickListener(this)
        minusButton?.setOnClickListener(this)
        saleId = arguments?.getString("sale_id")
        position = arguments?.getString("position")
        if (arguments!!.containsKey("isNewItem") && arguments!!.containsKey("product_id")) {
            try {
                productCatalog = Inventory.getInstance().getProductCatalog()
            } catch (e: NoDaoSetException) {
                e.printStackTrace()
            }

            isNew = arguments!!.getBoolean("isNewItem")
            productId = arguments!!.getInt("product_id")
            removeButton?.visibility=View.GONE
            var line=register?.getSalesLine(saleId?.toInt()?:0,productId)
            quantityBox!!.setText((line?.quantity?:0).toString())
            priceBox?.setText(productCatalog?.getProductById(productId)?.unitPrice.toString() )
            produName?.setText(productCatalog?.getProductById(productId)?.name)
            stockQty=productCatalog?.getProductById(productId)?.stockQty?: BigDecimal.ZERO
            stock_qty.setText(stockQty.toString())
            uom.setText(productCatalog?.getProductById(productId)?.uom)


        } else {
            removeButton?.visibility=View.VISIBLE
            lineItem = register?.getCurrentSale()!!.getLineItemAt(Integer.parseInt(position))
            stockQty=lineItem?.product?.stockQty?: BigDecimal.ZERO
            quantityBox?.setText(lineItem?.quantity.toString())
            priceBox?.setText(lineItem?.product!!.unitPrice.toString() )
            produName?.setText(lineItem?.product!!.name)
        }
        removeButton!!.setOnClickListener {
            Log.d("remove", "id=" + lineItem!!.id)
            register!!.removeItem(lineItem!!)
            dismissDialog()
        }

        comfirmButton!!.setOnClickListener {

            var isStockCheckenabled=PreferenceManager.getDefaultSharedPreferences(activity).getBoolean(getString(R.string.key_check_stock),true)
            if(quantityBox?.text?.equals("0")==true) return@setOnClickListener
            if(isStockCheckenabled)
              validateStockandInsert()
            else {
                addOrupdateLine()
                dismissDialog()
            }
        }

        }

    private fun validateStockandInsert() {
        if ((quantityBox!!.text?.toString()?.toBigDecimal()
                        ?: BigDecimal.ZERO).compareTo(stockQty) <= 0) {
            outofStockText?.visibility = View.GONE
            addOrupdateLine()
            dismissDialog()
        } else {
            outofStockText?.visibility = View.VISIBLE
        }
    }

    private fun addOrupdateLine() {
        if (isNew) {
            register?.addItem(productCatalog?.getProductById(productId), quantityBox!!.text.toString().toBigDecimal())

        } else {
            register!!.updateItem(
                    Integer.parseInt(saleId),
                    lineItem!!,
                    (quantityBox!!.text.toString().toBigDecimal()),
                    (priceBox!!.text.toString().toBigDecimal())
            )
        }

    }

    override fun onClick(p0: View) {

        when(p0.id)
        {
            R.id.plus_btn->{
                var qty=quantityBox?.text
                if(qty.isNullOrEmpty()) {
                    quantityBox?.setText("0")
                }
                quantityBox?.setText(quantityBox?.text.toString()?.toBigDecimal().add(BigDecimal.ONE).toString()) }
            R.id.minus_icon->{
                var qty=quantityBox?.text
                if(qty.isNullOrEmpty()) {
                    quantityBox?.setText("0")
                }
                if((quantityBox?.text?.toString().equals("0")))
                     return
                    quantityBox?.setText(quantityBox?.text.toString()?.toBigDecimal().subtract(BigDecimal.ONE).toString())
            }
        }

    }

    /**
     * End.
     */
    private fun dismissDialog() {
        saleFragment.update()
    //    reportFragment.update()
        this.dismiss()
    }


}
