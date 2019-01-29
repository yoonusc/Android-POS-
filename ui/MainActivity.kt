package com.ionob.pos.ui

import java.util.Locale

import android.annotation.SuppressLint
import android.app.ActionBar
import android.app.ActionBar.Tab
import android.app.AlertDialog
import android.app.FragmentTransaction
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.github.rubensousa.bottomsheetbuilder.BottomSheetBuilder
import com.github.rubensousa.bottomsheetbuilder.BottomSheetMenuDialog
import com.github.rubensousa.bottomsheetbuilder.adapter.BottomSheetItemClickListener
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent

import com.ionob.pos.R
import com.ionob.pos.db.NoDaoSetException
import com.ionob.pos.db.inventory.CategoryDbo
import com.ionob.pos.domain.DateTimeStrategy
import com.ionob.pos.domain.LanguageController
import com.ionob.pos.domain.inventory.Category
import com.ionob.pos.domain.inventory.LineItem
import com.ionob.pos.domain.inventory.Product
import com.ionob.pos.domain.inventory.ProductCatalog
import com.ionob.pos.domain.sale.Register
import com.ionob.pos.domain.sale.Sale
import com.ionob.pos.settings.MainSettingsActivity
import com.ionob.pos.settings.SettingsActivity
import com.ionob.pos.ui.component.UpdatableFragment
import com.ionob.pos.ui.inventory.InventoryFragment
import com.ionob.pos.ui.inventory.ProductDetailActivity
import com.ionob.pos.ui.sale.PaymentFragment
import com.ionob.pos.ui.sale.ReportFragment
import com.ionob.pos.ui.sale.SaleFragment
import com.ionob.pos.ui.sale.adapter.CategoryAdapter
import com.ionob.pos.ui.sale.model.CustomerModel
import com.ionob.pos.utils.IconMenuAdapter
import com.ionob.pos.utils.IconPowerMenuItem
import com.ionob.pos.utils.ListViewCallback
import com.skydoves.powermenu.*
import kotlinx.android.synthetic.main.attribute_bottem_sheet.*
import kotlinx.android.synthetic.main.header.*
import kotlinx.android.synthetic.main.layout_main.*
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * This UI loads 3 main pages (Inventory, Sale, Report)
 * Makes the UI flow by slide through pages using ViewPager.
 *
 * @author Ionob Team
 */
@SuppressLint("NewApi")
class MainActivity : FragmentActivity() ,View.OnClickListener {

    /**
     * Get view-pager
     * @return
     */
    var viewPager: ViewPager? = null
        private set
    private var productCatalog: ProductCatalog? = null
    private var productId: String? = null
    private var product: Product? = null
    private var pagerAdapter: PagerAdapter? = null
    private var res: Resources? = null
    private var back: ImageView? = null
    private var headerText: TextView? = null
    private var menuMain: TextView? = null
    private var addMenu: TextView? = null
    private var badge: TextView? = null
    var selectedLine: LineItem? = null
    var selectedSalesId: Int? = null
    var selectedCustomer:CustomerModel?=null

    @SuppressLint("NewApi")
    private
            /**
             * Initiate this UI.
             */
    fun initiateActionBar() {
        if (SDK_SUPPORTED) {
            val actionBar = actionBar

            actionBar!!.navigationMode = ActionBar.NAVIGATION_MODE_TABS

            val tabListener = object : ActionBar.TabListener {
                override fun onTabReselected(tab: Tab, ft: FragmentTransaction) {}

                override fun onTabSelected(tab: Tab, ft: FragmentTransaction) {
                    viewPager!!.currentItem = tab.position
                }

                override fun onTabUnselected(tab: Tab, ft: FragmentTransaction) {}
            }
            actionBar.addTab(actionBar.newTab().setText(res!!.getString(R.string.inventory))
                    .setTabListener(tabListener), 0, false)
            actionBar.addTab(actionBar.newTab().setText(res!!.getString(R.string.sale))
                    .setTabListener(tabListener), 1, true)
            actionBar.addTab(actionBar.newTab().setText(res!!.getString(R.string.report))
                    .setTabListener(tabListener), 2, false)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                actionBar.setStackedBackgroundDrawable(ColorDrawable(Color
                        .parseColor("#73bde5")))
            }

        }
    }

    var context: Context = this
    var bottomSheetCategory: RelativeLayout? = null
    var bottomSheetAttribute: RelativeLayout? = null
    private var register: Register? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            register = Register.getInstance()
        } catch (e: NoDaoSetException) {
            e.printStackTrace()
        }
        /* new sale record for user */
        if(intent.extras?.containsKey("selected_custofmer")==true) {
            selectedCustomer = intent.extras?.getSerializable("selected_custofmer") as CustomerModel
            var saleTemplate = Sale(0, DateTimeStrategy.currentTime, DateTimeStrategy.currentTime, "x", arrayListOf())
            saleTemplate.customerId = selectedCustomer?.customerId
            saleTemplate.isPurchase = false
            register?.salesRecordTemplate = saleTemplate
            register?.setCurrentSalesNull()
        }
        /* if sale id already exisit then there load data*/
        if(intent.extras?.containsKey("sales_id")==true)
            selectedSalesId=intent.extras?.getInt("sales_id")
        selectedSalesId?.let { register?.setCurrentSale(it) }
        initUI()
        setListeners()
        initCategorybottomsheet()
        initAttrBottomsheet()
        manageDicuountSheet()
    }


    fun udateBadge(count: Int)
    {
        badge_text.setNumber(count,true)
    }

    private fun initUI() {
        res = resources
        setContentView(R.layout.layout_main)
        viewPager = findViewById<View>(R.id.pager) as ViewPager
        headerText = findViewById<View>(R.id.title_text) as TextView
        back = findViewById<View>(R.id.back) as ImageView
        menuMain = findViewById<View>(R.id.text_menu) as TextView
        addMenu = findViewById<View>(R.id.text_add_menu) as TextView
//        badge = findViewById<View>(R.id.cart_items) as TextView
        badge?.bringToFront()
        badge_container.visibility=View.VISIBLE
        updateBadge("100")
        headerText!!.setText(R.string.app_name)
        SDK_SUPPORTED = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB
        //initiateActionBar();

        val fragmentManager = supportFragmentManager
        pagerAdapter = PagerAdapter(fragmentManager, res!!)
        viewPager!!.adapter = pagerAdapter
        viewPager!!
                .setOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
                    override fun onPageSelected(position: Int) {
                        //						if (SDK_SUPPORTED)
                        //							getActionBar().setSelectedNavigationItem(position);
                    }
                })
        viewPager!!.currentItem = 0
        var footer = findViewById<View>(R.id.footer) as RelativeLayout
        var main = findViewById<View>(R.id.footer) as ViewGroup
        bottomSheetCategory = findViewById(R.id.bottomSheetLayout) as RelativeLayout
        bottomSheetAttribute = findViewById(R.id.bottomSheetLayoutAttribute) as RelativeLayout
        footer.bringToFront()
        main.requestLayout()
        main.invalidate()
        var k=1
        check_out?.setOnClickListener{
            if(k>2) k=1
            pager.setCurrentItem(k)
            k++
        }
    }


    fun manageDicuountSheet()
    {
        var  discountAmt=BigDecimal.ZERO
        discount_amt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                if ( discount_amt.hasFocus()) {
                    if (s.length > 0&&!s.isNullOrEmpty()&&!s.toString().equals("")) {
                        try {
                            discountAmt = BigDecimal(s.toString()).setScale(2,RoundingMode.HALF_UP)
                        } catch (e: Exception) {
                            discountAmt = BigDecimal.ZERO
                        }

                        selectedLine?.discount=discountAmt
                        selectedLine?.netAmount = selectedLine?.totalAmount?.subtract(discountAmt)

                    }
                    if (selectedLine?.netAmount?.signum()!! < 0) {
                        discountAmt = selectedLine?.totalAmount
                        selectedLine?.netAmount = BigDecimal.ZERO
                        discount_amt.setText(discountAmt.toString())
                    }

                    var discPercent = discountAmt.divide(selectedLine?.totalAmount,3,RoundingMode.HALF_UP)
                    discPercent = discPercent.scaleByPowerOfTen(2)
                    discount_percent.setText(discPercent.toString())
                }
            }
        })

        discount_percent.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                if (discount_percent.hasFocus()) {

                    if(s.length  > 0&&!s.isNullOrEmpty()&&!s.toString().equals("")) {
                        discountAmt = (s.toString()?.toBigDecimal()).setScale(2,RoundingMode.HALF_UP).multiply(selectedLine?.totalAmount).scaleByPowerOfTen(-2)
                        selectedLine?.netAmount = selectedLine?.totalAmount?.subtract(discountAmt)
                    }
                    if (selectedLine?.netAmount?.signum()!! < 0) {
                        discountAmt = selectedLine?.totalAmount
                        selectedLine?.netAmount = BigDecimal.ZERO
                        discount_amt.setText(discountAmt.toString())
                        discount_percent.setText("100")
                    }

                    discount_amt.setText((discountAmt).toString())
                }
            }
        })

        attribut_okay.setOnClickListener{
         toggleAttrBottomSheet()
          updateLine(selectedLine)
            saleFragment?.update()
        }

        attribut_cancel.setOnClickListener {
            toggleAttrBottomSheet()
        }

    }


    fun updateLine(lineItem: LineItem?)
    {
        register!!.updateItem(
                register?.getCurrentSale()?.id!!,
                lineItem!!,
                lineItem.quantity,
                lineItem.priceAtSale!!)
    }

    var sheetBehaviorCategory:BottomSheetBehavior<RelativeLayout>?=null
    var sheetBehaviorAttributes:BottomSheetBehavior<RelativeLayout>?=null
    var categoryRecycler:RecyclerView?=null
    var  viewAdapter : CategoryAdapter?=null
    fun initCategorybottomsheet()
    {
        categoryRecycler=findViewById(R.id.recycler_pos_catecory_view)
        var flexboxLayoutManager =  FlexboxLayoutManager(getApplicationContext());
        // Set flex direction.
        flexboxLayoutManager.setFlexDirection(FlexDirection.ROW);
        // Set JustifyContent.
        flexboxLayoutManager.setJustifyContent(JustifyContent.SPACE_AROUND);
        categoryRecycler?.setLayoutManager(flexboxLayoutManager);
        // Set adapter object.
        var allCategory=CategoryDbo.getInstance(this).allCategory
        viewAdapter = CategoryAdapter(allCategory?: arrayListOf<Category>(),object: ListViewCallback{
            override fun onItemClicked(record: Any?) {
                inventoryFragment?.updateCategory(record as Category)
                sheetBehaviorCategory?.state=BottomSheetBehavior.STATE_HIDDEN
            }

            override fun onItemLongClick(record: Any?) {

            }


        },this);
        categoryRecycler?.setAdapter(viewAdapter)
        sheetBehaviorCategory= BottomSheetBehavior.from(bottomSheetCategory);
        sheetBehaviorCategory?.state=BottomSheetBehavior.STATE_HIDDEN
        /**
         * bottom sheet state change listener
         * we are changing button text when sheet changed state
         * */
        (sheetBehaviorCategory as BottomSheetBehavior<RelativeLayout>?)?.setBottomSheetCallback(object:BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        var allCategory=CategoryDbo.getInstance(applicationContext).allCategory
                        viewAdapter?.setData(allCategory)
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {

                    }
                    BottomSheetBehavior.STATE_DRAGGING -> {
                    }
                    BottomSheetBehavior.STATE_SETTLING -> {
                    }
                }
            }

        });

    }


    fun initAttrBottomsheet()
    {

        sheetBehaviorAttributes= BottomSheetBehavior.from(bottomSheetAttribute);
        sheetBehaviorAttributes?.state=BottomSheetBehavior.STATE_HIDDEN
        /**
         * bottom sheet state change listener
         * we are changing button text when sheet changed state
         * */
        (sheetBehaviorAttributes as BottomSheetBehavior<RelativeLayout>?)?.
                setBottomSheetCallback(object:BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        //   btnBottomSheet.setText("Close Sheet")
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        // btnBottomSheet.setText("Expand Sheet")
                    }
                    BottomSheetBehavior.STATE_DRAGGING -> {
                    }
                    BottomSheetBehavior.STATE_SETTLING -> {
                    }
                }
            }

        });

    }




    public fun updateBadge(value:String?)
    {
        badge?.text=value
    }

    public fun toggleCategoryBottomSheet() {
        if (sheetBehaviorCategory?.getState() != BottomSheetBehavior.STATE_EXPANDED) {

            sheetBehaviorCategory?.setState(BottomSheetBehavior.STATE_EXPANDED);

        } else {
            sheetBehaviorCategory?.peekHeight=0
            sheetBehaviorCategory?.setState(BottomSheetBehavior.STATE_COLLAPSED);

    }
    }


    public fun toggleAttrBottomSheet() {

        if (sheetBehaviorAttributes?.getState() != BottomSheetBehavior.STATE_EXPANDED) {

            discount_amt?.setText(selectedLine?.discount.toString())
            attr_label?.setText(selectedLine?.product?.name)
            if((selectedLine?.totalAmount?:BigDecimal.ZERO).signum()<=0) return
            var discPercent = selectedLine?.discount?.divide(selectedLine?.totalAmount,3,RoundingMode.HALF_EVEN)
            discPercent = discPercent?.scaleByPowerOfTen(2)
            discount_percent?.setText(discPercent.toString())
            tax_amt.setText(selectedLine?.totalTax.toString())
            tax_percent.setText(selectedLine?.totalpercent.toString())

            sheetBehaviorAttributes?.setState(BottomSheetBehavior.STATE_EXPANDED);

        } else {
            sheetBehaviorAttributes?.peekHeight=0
            sheetBehaviorAttributes?.setState(BottomSheetBehavior.STATE_COLLAPSED);

        }
    }



    fun setListeners()
    {
       addMenu?.setOnClickListener(this)
       menuMain?.setOnClickListener(this)

    }

    public fun hideView()
    {
      var footer=findViewById<View>(R.id.footer) as RelativeLayout
        footer.visibility=View.GONE
    }
    public fun showView()
    {
        var footer=findViewById<View>(R.id.footer) as RelativeLayout
        footer.visibility=View.VISIBLE
    }

    var powerMenu :CustomPowerMenu<Any,*>?=null
    override fun onClick(view: View?) {
        when(view?.id)
        {
            R.id.text_menu->
            {
                var dialog :BottomSheetMenuDialog=  BottomSheetBuilder(this, R.style.AppTheme_BottomSheetDialog)
              .setMode(BottomSheetBuilder.MODE_GRID)
              .setMenu(R.menu.bottom_menu)
              .setItemClickListener {
                  run {
                      when (it?.itemId) {
                          R.id.settings -> {
                              startActivity(Intent(this@MainActivity,MainSettingsActivity::class.java))
                          }
                          R.id.info -> {

                          }
                          R.id.sync -> {

                          }
                          else -> {
                          }
                      }
                  }
              }
                        .createDialog();

             dialog.show();
            }


            R.id.text_add_menu-> {
               powerMenu= CustomPowerMenu.Builder(context,  IconMenuAdapter())
                        .addItem(IconPowerMenuItem(context.getResources().getDrawable(R.drawable.add_prodcut), "Product"))
                .addItem( IconPowerMenuItem(context.getResources().getDrawable(R.drawable.category), "Category"))
                .addItem(IconPowerMenuItem(context.getResources().getDrawable(R.drawable.customer), "Customer"))
                .addItem(IconPowerMenuItem(context.getResources().getDrawable(R.drawable.sales), "Sales"))
                        .setAnimation(MenuAnimation.SHOWUP_TOP_LEFT) // Animation start point (TOP | LEFT)
                        .setMenuRadius(10f)
                        .setMenuShadow(10f)
                        .setDivider(ColorDrawable(context.getResources().getColor(R.color.material_grey_300))) // set a divider
                        .setDividerHeight(1) // set divider's height
                        .setLifecycleOwner(this)
                        .setOnMenuItemClickListener(object :OnMenuItemClickListener<IconPowerMenuItem> {
                            override fun onItemClick(position: Int, item: IconPowerMenuItem?) {

                                when(item?.title)
                                {
                                   "Product"->
                                           {
                                            inventoryFragment?.showPopup(view)
                                            powerMenu?.dismiss()

                                           }
                                    "Category"->
                                    {

                                    }
                                    "Customer"->
                                    {

                                    }
                                    "Sales"->
                                    {

                                    }

                                }
                            }
                        }
                        )
                        .build()
                powerMenu?.showAtCenter(view)



            }

        }

    }




    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            openQuitDialog()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    /**
     * Open quit dialog.
     */
    private fun openQuitDialog() {
        val quitDialog = AlertDialog.Builder(
                this@MainActivity)
        quitDialog.setTitle(res!!.getString(R.string.dialog_quit))
        quitDialog.setPositiveButton(res!!.getString(R.string.quit)) { dialog, which -> finish() }
        quitDialog.setNegativeButton(res!!.getString(R.string.no)) { dialog, which -> }
        quitDialog.show()
    }

    /**
     * Option on-click handler.
     * @param view
     */
    fun optionOnClickHandler(view: View) {

    }

    /**
     * Open detail dialog.
     */
    public fun openDetailDialog(product: Product?) {
        val quitDialog = AlertDialog.Builder(this@MainActivity)
        quitDialog.setTitle(product?.name)
        quitDialog.setPositiveButton(res!!.getString(R.string.remove)) { dialog, which -> openRemoveDialog() }
        quitDialog.setNegativeButton(res!!.getString(R.string.product_detail)) { dialog, which ->
            val newActivity = Intent(this@MainActivity,
                    ProductDetailActivity::class.java)
            newActivity.putExtra("id", product?.id)
            startActivity(newActivity)
        }

        quitDialog.show()
    }

    /**
     * Open remove dialog.
     */
    private fun openRemoveDialog() {
        val quitDialog = AlertDialog.Builder(
                this@MainActivity)
        quitDialog.setTitle(res!!.getString(R.string.dialog_remove_product))
        quitDialog.setPositiveButton(res!!.getString(R.string.no)) { dialog, which -> }

        quitDialog.setNegativeButton(res!!.getString(R.string.remove)) { dialog, which ->
            productCatalog!!.suspendProduct(product)
            pagerAdapter!!.update(0)
        }

        quitDialog.show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.lang_en -> {
                setLanguage("en")
                return true
            }



            else -> return super.onOptionsItemSelected(item)
        }
    }

    /**
     * Set language
     * @param localeString
     */
    private fun setLanguage(localeString: String) {
        val locale = Locale(localeString)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        LanguageController.getInstance().language = localeString
        baseContext.resources.updateConfiguration(config,
                baseContext.resources.displayMetrics)
        val intent = intent
        finish()
        startActivity(intent)
    }

    companion object {
        private var SDK_SUPPORTED: Boolean = false
    }

}

/**
 *
 * @author Ionob Team
 */

var reportFragment:ReportFragment?=null
var paymentFragment: PaymentFragment?=null
var saleFragment:SaleFragment?=null
var inventoryFragment:InventoryFragment?=null

internal class PagerAdapter
/**
 * Construct a new PagerAdapter.
 * @param fragmentManager
 * @param res
 */
(fragmentManager: FragmentManager, res: Resources) : FragmentStatePagerAdapter(fragmentManager) {

    private val fragments: Array<UpdatableFragment>
    private val fragmentNames: Array<String>

    init {
       paymentFragment= PaymentFragment()
       reportFragment = ReportFragment()
        saleFragment = SaleFragment(reportFragment!!)
       inventoryFragment = InventoryFragment(
                saleFragment!!)

        fragments = arrayOf(inventoryFragment!!, saleFragment!!, paymentFragment!!)
        fragmentNames = arrayOf(res.getString(R.string.inventory), res.getString(R.string.sale),"Payment")

    }

    override fun getItem(i: Int): Fragment {
        return fragments[i]
    }

    override fun getCount(): Int {
        return fragments.size
    }

    override fun getPageTitle(i: Int): CharSequence {
        return fragmentNames[i]
    }

    /**
     * Update
     * @param index
     */
    fun update(index: Int) {
        fragments[index].update()
    }

}