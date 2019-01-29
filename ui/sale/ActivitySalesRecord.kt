

package com.ionob.pos.ui.sale
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.design.widget.CollapsingToolbarLayout
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v4.view.ViewCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import com.ionob.pos.R
import com.ionob.pos.R.id.fab
import com.ionob.pos.domain.sale.Sale
import com.ionob.pos.domain.sale.SalesRcord
import com.ionob.pos.ui.MainActivity
import com.ionob.pos.ui.sale.adapter.SalesRcordRecyclerViewAdapter
import com.ionob.pos.ui.sale.model.SalesRecordModel
import java.util.ArrayList
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import com.fitbae.fitness.dialogs.FBCustomerDialog
import com.ionob.pos.db.customer.CustomerDbao
import com.ionob.pos.db.sale.SaleDao
import com.ionob.pos.settings.MainSettingsActivity
import com.ionob.pos.ui.sale.model.CustomerModel
import com.ionob.pos.utils.Constance
import com.ionob.pos.utils.IONPreferences
import kotlinx.android.synthetic.main.activity_sales_record.*


class ActivitySalesRecord : AppCompatActivity(), SalesRcordRecyclerViewAdapter.OnItemClickListener,SalesRcordRecyclerViewAdapter.OnlongClickListener {

    private var drawerLayout: DrawerLayout? = null
    private var content: View? = null
    private var recyclerView: RecyclerView? = null
    private var navigationView: NavigationView? = null
    private var salesRecod:SalesRcord?=null
    private var sales:List<Sale>?=null

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sales_record)
        initRecyclerView()
        initToolbar()
        salesRecod=SalesRcord.getInstance()
        sales=salesRecod?.allSale
        supportPostponeEnterTransition()
        setSupportActionBar(findViewById(R.id.toolbar) as android.support.v7.widget.Toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        var collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar) as CollapsingToolbarLayout
        collapsingToolbarLayout?.title = "IONO BIZ"
        collapsingToolbarLayout?.setExpandedTitleColor(resources.getColor(android.R.color.transparent))
        (findViewById(R.id.fab) as FloatingActionButton).setOnClickListener {
            if(selectedCustomer?.customerId!=Constance.UNDIFINED_CUSTOMER) {
                var intent = Intent(this, MainActivity::class.java)
                var bundel = Bundle()
                bundel.putSerializable("selected_custofmer", selectedCustomer)
                intent.putExtras(bundel)
                startActivity(intent)
            }
            else
            {
                Toast.makeText(this,"Please select customer",Toast.LENGTH_LONG).show()
            }
        }
      //  val avatar = navigationView!!.getHeaderView(0).findViewById(R.id.avatar) as ImageView
     //   Picasso.with(this).load(AVATAR_URL).transform(CircleTransform()).into(avatar)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            var  window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getColor(R.color.primary));
        }
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
//            setRecyclerAdapter(recyclerView!!)
//        }

        initRadioButtons()
        setupRecordCounts()
        setUpDefaultCustomer()
        refreshCustomerlayout()

    }
     fun setUpDefaultCustomer()
     {   if(IONPreferences.getSelectedCustomerId()==Constance.STANDARD_CUSTOMER) {
         var tempCustomerModel = CustomerModel()
         tempCustomerModel.customerId = 0
         tempCustomerModel.customerName = "Standard"
         tempCustomerModel.customerCode = "std"
         tempCustomerModel.taxId = "0"
         tempCustomerModel.isCustomer = true
         tempCustomerModel.email = "std@test.com"
         tempCustomerModel.address1 = "xxxxxxxxx"
         selectedCustomer = tempCustomerModel
     }
         else if(IONPreferences.getSelectedCustomerId()==Constance.UNDIFINED_CUSTOMER)
     {
         var NoCustomerModel = CustomerModel()
         NoCustomerModel.customerId=Constance.UNDIFINED_CUSTOMER
         NoCustomerModel.customerName="No Customer"
         NoCustomerModel.customerCode="xx"
         NoCustomerModel.taxId="0"
         NoCustomerModel.isCustomer=true
         NoCustomerModel.email="No Email"
         NoCustomerModel.address1="No Address"
         selectedCustomer = NoCustomerModel
     }
         else
     {

         var customerDb= CustomerDbao.getInstance(applicationContext)
         var data=customerDb.getCustomerByid(IONPreferences.getSelectedCustomerId()?:0)
         selectedCustomer = data
     }

     }

    override fun onResume() {
        super.onResume()
        initRadioButtons()
        setupRecordCounts()
    }

    private fun setupRecordCounts()
    {
        text_all.text="All ("+salesRecod?.getCount(SaleDao.STATUS_ALL).toString()+")"
        text_draft.text=salesRecod?.getCount(SaleDao.STATUS_DRAFT).toString()
        text_completed.text=salesRecod?.getCount(SaleDao.STATUS_COMPLETED).toString()
        text_synced.text=salesRecod?.getCount(SaleDao.STATUS_SYNCED).toString()
        text_failed.text=salesRecod?.getCount(SaleDao.STATUS_SYNC_FAILED).toString()
        text_all.performClick()
        text_all.setOnClickListener {
            (it as RadioButton).isChecked=true
            sales = salesRecod?.allSale
            adapter?.setLine(sales ?: arrayListOf())
        }

    }

    private fun initRadioButtons() {
        filter_group.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->

            when (checkedId) {
                R.id.text_all -> {
                    sales = salesRecod?.allSale
                    adapter?.setLine(sales ?: arrayListOf())

                }
                R.id.text_draft -> {
                    sales = salesRecod?.allDraft
                    adapter?.setLine(sales ?: arrayListOf())

                }
                R.id.text_completed -> {
                    sales = salesRecod?.allCompleted
                    adapter?.setLine(sales ?: arrayListOf())
                }
                R.id.text_synced -> {
                    sales = salesRecod?.allSynced
                    adapter?.setLine(sales ?: arrayListOf())
                }
                R.id.text_failed -> {
                    sales = salesRecod?.allFailed
                    adapter?.setLine(sales ?: arrayListOf())
                }

            }

        })
    }

    override fun onEnterAnimationComplete() {
        super.onEnterAnimationComplete()
        setRecyclerAdapter(recyclerView!!)
        recyclerView!!.scheduleLayoutAnimation()
    }

    private fun initRecyclerView() {
        recyclerView = findViewById(R.id.sales_record_recycler) as RecyclerView
        recyclerView!!.layoutManager = GridLayoutManager(this, 4)

    }
    var adapter :SalesRcordRecyclerViewAdapter?=null
    private fun setRecyclerAdapter(recyclerView: RecyclerView) {
         adapter = SalesRcordRecyclerViewAdapter(sales?: arrayListOf(),this,this,this)
        recyclerView.adapter = adapter
    }



    private fun initToolbar() {
        val toolbar = findViewById(R.id.toolbar) as Toolbar

        setSupportActionBar(toolbar)
        val actionBar = supportActionBar


    if (actionBar != null) {
       // actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp)
           actionBar.setDisplayHomeAsUpEnabled(true)
           actionBar.title="Sales Records"
       }
    }

//    private fun setupDrawerLayout() {
//        drawerLayout = findViewById(R.id.drawer_layout) as DrawerLayout
//
//        navigationView = findViewById(R.id.navigation_view) as NavigationView
//        navigationView!!.setNavigationItemSelectedListener { menuItem ->
//            Snackbar.make(content!!, menuItem.title.toString() + " pressed", Snackbar.LENGTH_LONG).show()
//            menuItem.isChecked = true
//            drawerLayout!!.closeDrawers()
//            true
//        }
//    }

    var selectedCustomer:CustomerModel?=null
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                return true
            }
            R.id.settings-> {
                startActivity(Intent(this@ActivitySalesRecord, MainSettingsActivity::class.java))
            }
            R.id.choose_customer->
            {
                var customerDb= CustomerDbao.getInstance(applicationContext)
                var data=customerDb.allCustomer
                var customerSearchDlg=FBCustomerDialog()
                customerSearchDlg.setCallback(object :FBCustomerDialog.SelectCstomerCallback{
                    override fun onSelectCustomer(customr: CustomerModel) {
                        selectedCustomer=customr
                        IONPreferences.setSelectedCustomerId(selectedCustomer?.customerId)
                        setupRecordCounts()
                        refreshCustomerlayout()

                    }

                    override fun onSelectedNoCustomer() {


                    }
                })
                var bundle=Bundle()
                bundle.putSerializable("COSTOMER_LIST",data as ArrayList<CustomerModel>)
                customerSearchDlg.arguments=bundle
                customerSearchDlg?.show(supportFragmentManager,"customer_search-dialog")
            }
        }

        return super.onOptionsItemSelected(item)
    }

    fun refreshCustomerlayout()
    {
        customer_name.text=selectedCustomer?.customerName?:"Standard Customer"
        customer_address.text=selectedCustomer?.address1?:"No Address"
        customer_mobile.text=selectedCustomer?.phone?:"No mobile"
        customer_email?.text=selectedCustomer?.email?:"No email"



    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        inflater.inflate(R.menu.filter_menu, menu)
        return true
    }

    override fun onItemClick(salesRecord: SalesRecordModel) {

        var intent=Intent(this,MainActivity::class.java)
        intent?.putExtra("sales_id",salesRecord.id)
        startActivity(intent)

    }

    override fun onlongitemclickListener(viewModel: SalesRecordModel) {

        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Are you sure to delete this item?")
        dialog.setPositiveButton("Yes") { dialog, which ->

            salesRecod?.deleteSalesRecord(viewModel.id)
            setupRecordCounts()
            onEnterAnimationComplete()
            dialog.dismiss()

        }

        dialog.setNegativeButton("No") { dialog, which ->
            dialog.dismiss()

        }

        dialog.show()
    }
    }




