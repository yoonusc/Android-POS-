package com.ionob.pos.ui.inventory

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentIntegratorSupportV4
import com.ionob.pos.R
import com.ionob.pos.domain.inventory.Inventory
import com.ionob.pos.domain.inventory.ProductCatalog
import com.ionob.pos.db.NoDaoSetException
import com.ionob.pos.db.Tax.Tax
import com.ionob.pos.db.Tax.TaxDbo
import com.ionob.pos.ui.component.UpdatableFragment
import kotlinx.android.synthetic.main.layout_addproduct.*
import java.math.BigDecimal
import java.util.ArrayList

/**
 * A dialog of adding a Product.
 *
 * @author Ionob Team
 */
@SuppressLint("ValidFragment")
class AddProductDialogFragment
/**
 * Construct a new AddProductDialogFragment
 * @param fragment
 */
(private val fragment: UpdatableFragment) : DialogFragment() {

    private var barcodeBox: EditText? = null
    private var productCatalog: ProductCatalog? = null
    private var scanButton: Button? = null
    private var priceBox: EditText? = null
    private var nameBox: EditText? = null
    private var confirmButton: Button? = null
    private var clearButton: Button? = null
    private var res: Resources? = null
    private var tax_list: ArrayList<String?> = ArrayList()
    private var unit_price: BigDecimal = BigDecimal.ZERO
    private var tax_price: BigDecimal = BigDecimal.ZERO
    private var tax_id: Int = 0
    private var tax_percent: Double = 0.toDouble()
    private var isinclusive_tax_mode: Boolean = true
    private var TaxSpinner: Spinner? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        try {
            productCatalog = Inventory.getInstance().getProductCatalog()
        } catch (e: NoDaoSetException) {
            e.printStackTrace()
        }

        val v = inflater!!.inflate(R.layout.layout_addproduct, container,
                false)

        res = resources

        barcodeBox = v.findViewById<View>(R.id.barcodeBox) as EditText
        scanButton = v.findViewById<View>(R.id.scanButton) as Button
        priceBox = v.findViewById<View>(R.id.priceBox) as EditText
        nameBox = v.findViewById<View>(R.id.nameBox) as EditText
        TaxSpinner = v.findViewById<View>(R.id.taxSpinner) as Spinner
        confirmButton = v.findViewById<View>(R.id.confirmButton) as Button
        clearButton = v.findViewById<View>(R.id.clearButton) as Button

        initUI()
        taxlisting()
        return v
    }

    /**
     * Construct a new
     */
    private fun initUI() {
        scanButton!!.setOnClickListener {
            val scanIntegrator = IntentIntegratorSupportV4(this@AddProductDialogFragment)
            scanIntegrator.initiateScan()
        }

        confirmButton!!.setOnClickListener {
            if (nameBox!!.text.toString() == ""
                    || barcodeBox!!.text.toString() == ""
                    || priceBox!!.text.toString() == "") {

                Toast.makeText(activity?.baseContext,
                        res!!.getString(R.string.please_input_all), Toast.LENGTH_SHORT)
                        .show()

            } else {


                unit_price = (priceBox!!.text.toString().toBigDecimal())
                val success = productCatalog!!.addProduct(
                        nameBox!!.text.toString(),
                        55,
                        barcodeBox!!.text.toString(),
                        unit_price,
                        uom!!.text.toString(),
                        stock?.text?.toString()?.toBigDecimal() ?: BigDecimal.ZERO,
                        554,
                        "image",
                        tax_id.toInt(), /// on;y need tax id
                        tax_percent, //uselesss
                        isinclusive_tax_mode,
                        tax_price // use less
                )

                if (success) {
                    Toast.makeText(activity?.baseContext,
                            res!!.getString(R.string.success) + ", "
                                    + nameBox!!.text.toString(),
                            Toast.LENGTH_SHORT).show()

                    fragment.update()
                    clearAllBox()
                    this@AddProductDialogFragment.dismiss()

                } else {
                    Toast.makeText(activity?.baseContext,
                            res!!.getString(R.string.fail),
                            Toast.LENGTH_SHORT).show()
                }
            }
        }

        clearButton!!.setOnClickListener {
            if (barcodeBox!!.text.toString() == "" && nameBox!!.text.toString() == "" && priceBox!!.text.toString() == "") {
                this@AddProductDialogFragment.dismiss()
            } else {
                clearAllBox()
            }
        }
    }

    /**
     * Clear all box
     */
    private fun clearAllBox() {
        barcodeBox!!.setText("")
        nameBox!!.setText("")
        priceBox!!.setText("")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        val scanningResult = IntentIntegrator.parseActivityResult(
                requestCode, resultCode, intent)

        if (scanningResult != null) {
            val scanContent = scanningResult.contents
            barcodeBox!!.setText(scanContent)
        } else {
            Toast.makeText(activity?.baseContext,
                    res!!.getString(R.string.fail),
                    Toast.LENGTH_SHORT).show()
        }
    }

    private fun taxlisting() {

        var allTax: List<Tax> = TaxDbo.getInstance(activity!!.applicationContext).allTax
        for (i in 0..allTax.size - 1) {
            tax_list.add(allTax[i].tax_name)

        }

        val adapter = ArrayAdapter<String?>(activity!!.applicationContext, android.R.layout.simple_expandable_list_item_1, tax_list)
        TaxSpinner?.setAdapter(adapter)


        TaxSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

//              val selectedItem = parent!!.getItemAtPosition(position)
                val taxposition: Int = parent!!.getItemIdAtPosition(position).toInt()
                tax_id = allTax[taxposition].tax_id
                tax_percent = allTax[taxposition].tax_percent!!.toDouble()
                isinclusive_tax_mode = allTax[taxposition].tax_mode


            }


        }
    }
}
