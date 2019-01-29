package com.fitbae.fitness.dialogs

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import com.fitbae.fitness.adapter.FBCustomerDialogAdapter
import com.ionob.pos.R
import com.ionob.pos.ui.sale.model.CustomerModel
import com.ionob.pos.utils.Constance
import kotlinx.android.synthetic.main.dialog_select_customer.*
import kotlinx.android.synthetic.main.header.*
import kotlinx.android.synthetic.main.layout_main.*

class FBCustomerDialog : DialogFragment(), View.OnClickListener {
    private var selectedCustomer: CustomerModel? = null
    private var customerList: MutableList<CustomerModel>? = null
    private var costomerAdapter: FBCustomerDialogAdapter? = null
    private var callBack:SelectCstomerCallback?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    fun setCallback(callBack:SelectCstomerCallback)
    {
        this.callBack=callBack
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return inflater.inflate(R.layout.dialog_select_customer, container, false)
    }

    private fun setListeners() {
        search_customer.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                costomerAdapter?.let {
                    it.filter(s.toString())
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        try {
//            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.parseColor("#99000000")))
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
        customerList= arrayListOf()
        var stdCustomerModel = CustomerModel()
        stdCustomerModel.customerId=Constance.STANDARD_CUSTOMER
        stdCustomerModel.customerName="Standard"
        stdCustomerModel.customerCode="std"
        stdCustomerModel.taxId="0"
        stdCustomerModel.isCustomer=true
        stdCustomerModel.email="No Email"
        stdCustomerModel.address1="No Address"

        var NoCustomerModel = CustomerModel()
        NoCustomerModel.customerId=Constance.UNDIFINED_CUSTOMER
        NoCustomerModel.customerName="No Customer"
        NoCustomerModel.customerCode="xx"
        NoCustomerModel.taxId="0"
        NoCustomerModel.isCustomer=true
        NoCustomerModel.email="No Email"
        NoCustomerModel.address1="No Address"
        customerList?.add(NoCustomerModel)
        customerList?.add(stdCustomerModel)
        arguments?.let {
            it.getSerializable("COSTOMER_LIST")?.let {
                customerList?.addAll(it as ArrayList<CustomerModel>)
            }
            it.getBoolean("CHANGE_COUNTRY")?.let {
              //  isFromChangeCountry = it
            }
        }
        initViews()
        setListeners()
    }

    private fun initViews() {

        title_text.text="Search Customer"
        back.setOnClickListener {
            dismiss()
        }
        costomerAdapter = FBCustomerDialogAdapter((customerList as ArrayList<CustomerModel>?)!!, activity!!)
        customer_list.adapter = costomerAdapter
        costomerAdapter!!.setListner(object : FBCustomerDialogAdapter.OnItemSelected {

            override fun onItemSelected(selectedCustomer: CustomerModel) {

                costomerAdapter?.filter("")
                callBack!!.onSelectCustomer(selectedCustomer)
                dismiss()
            }
        })
        back.setOnClickListener { dismiss() }
    }

    override fun onClick(v: View) {
    }



    interface SelectCstomerCallback {

       fun onSelectCustomer(customr:CustomerModel)
       fun onSelectedNoCustomer()

    }


    override fun onResume() {
        if (null != dialog && null != dialog.window) {
            val params = dialog.window!!.attributes
            params.width = android.view.WindowManager.LayoutParams.MATCH_PARENT
            params.height = WindowManager.LayoutParams.MATCH_PARENT
            dialog.window!!.attributes = params as android.view.WindowManager.LayoutParams
            dialog.setCanceledOnTouchOutside(false)
            dialog.window!!.setBackgroundDrawableResource(R.color.tranparent)
//            val wm = context!!.getSystemService(Context.WINDOW_SERVICE) as WindowManager // for activity use context instead of getActivity()
//            val display = wm.defaultDisplay // getting the screen size of device
//            val size = Point()
//            display.getSize(size)
//            val width = size.x /*- getDimen(context!!, R.dimen.margin_extra_large_xxx)*/ // Set your heights
//            val height = size.y /*- 100*/ // set your widths
//            val lp = WindowManager.LayoutParams()
//            lp.copyFrom(dialog.window!!.attributes)
//            lp.width = android.view.WindowManager.LayoutParams.MATCH_PARENT
//            lp.height = android.view.WindowManager.LayoutParams.MATCH_PARENT
//            dialog.window!!.attributes = lp
        }
        super.onResume()
    }
}
