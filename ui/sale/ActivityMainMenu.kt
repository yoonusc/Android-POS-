package com.ionob.pos.ui.sale


import android.content.Intent
import com.ionob.pos.R


/**
 * This is the first activity page, core-app and database created here.
 * Dependency injection happens here.
 *
 * @author Ionob Team
 */
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.support.design.widget.CollapsingToolbarLayout
import android.support.design.widget.FloatingActionButton
import android.support.v4.view.ViewCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.graphics.Palette
import android.support.v7.widget.CardView
import android.support.v7.widget.Toolbar
import android.transition.Slide
import android.view.*
import android.widget.GridView
import android.widget.ImageView
import com.fitbae.fitness.dialogs.FBCustomerDialog
import com.ionob.pos.db.customer.CustomerDbao
import com.ionob.pos.settings.MainSettingsActivity
import com.ionob.pos.ui.sale.model.CustomerModel
import com.ionob.pos.utils.IONPreferences
import java.util.ArrayList


class ActivityMainMenu : AppCompatActivity() ,View.OnClickListener {


    private var collapsingToolbarLayout: CollapsingToolbarLayout? = null
    private var salseBuuton:CardView?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initActivityTransitions()
        setContentView(R.layout.activity_main)
        ViewCompat.setTransitionName(findViewById(R.id.app_bar_layout), EXTRA_IMAGE)
        supportPostponeEnterTransition()
        setSupportActionBar(findViewById(R.id.toolbar) as Toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        val itemTitle = intent.getStringExtra(EXTRA_TITLE)
        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar) as CollapsingToolbarLayout
        collapsingToolbarLayout!!.title = itemTitle
        collapsingToolbarLayout!!.setExpandedTitleColor(resources.getColor(android.R.color.transparent))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            var  window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(resources.getColor(R.color.primary));
        }
        val image = findViewById(R.id.image) as ImageView
//        Picasso.with(this).load(intent.getStringExtra(EXTRA_IMAGE)).into(image, object : Callback() {
//            fun onSuccess() {
//                val bitmap = (image.drawable as BitmapDrawable).bitmap
//                Palette.from(bitmap).generate { palette -> applyPalette(palette) }
//            }
//
//            fun onError() {
//
//            }
//        })

//        val title = findViewById(R.id.title) as TextView
//        title.text = itemTitle

        setListeners()
    }

    fun setListeners()
    {
        salseBuuton=findViewById(R.id.sales_card) as CardView
        salseBuuton?.setOnClickListener(this)

    }

    override fun dispatchTouchEvent(motionEvent: MotionEvent): Boolean {
        try {
            return super.dispatchTouchEvent(motionEvent)
        } catch (e: NullPointerException) {
            return false
        }

    }

    override fun onEnterAnimationComplete() {
        super.onEnterAnimationComplete()
       // (findViewById(R.id.mainGrid)as android.support.v7.widget.GridLayout)!!.scheduleLayoutAnimation()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.choose_customer -> {
                var customerDb= CustomerDbao.getInstance(applicationContext)
                var data=customerDb.allCustomer
                var customerSearchDlg= FBCustomerDialog()
                customerSearchDlg.setCallback(object : FBCustomerDialog.SelectCstomerCallback{
                    override fun onSelectCustomer(customr: CustomerModel) {
                        IONPreferences.setSelectedCustomerId(customr?.customerId)

                    }

                    override fun onSelectedNoCustomer() {


                    }
                })
                var bundle=Bundle()
                bundle.putSerializable("COSTOMER_LIST",data as ArrayList<CustomerModel>)
                customerSearchDlg.arguments=bundle
                customerSearchDlg?.show(supportFragmentManager,"customer_search-dialog")
            }
            R.id.settings -> {

                startActivity(Intent(this@ActivityMainMenu, MainSettingsActivity::class.java))
            }

            else -> return super.onOptionsItemSelected(item)
        }
        return false
    }

    private fun initActivityTransitions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val transition = Slide()
            transition.excludeTarget(android.R.id.statusBarBackground, true)
            window.enterTransition = transition
            window.returnTransition = transition
        }
    }

    private fun applyPalette(palette: Palette) {
        val primaryDark = resources.getColor(R.color.ColorPrimary)
        val primary = resources.getColor(R.color.primary)
        collapsingToolbarLayout!!.setContentScrimColor(palette.getMutedColor(primary))
        collapsingToolbarLayout!!.setStatusBarScrimColor(palette.getDarkMutedColor(primaryDark))
        updateBackground(findViewById(R.id.fab) as FloatingActionButton, palette)
        supportStartPostponedEnterTransition()
    }

    private fun updateBackground(fab: FloatingActionButton, palette: Palette) {
        val lightVibrantColor = palette.getLightVibrantColor(resources.getColor(android.R.color.white))
        val vibrantColor = palette.getVibrantColor(resources.getColor(R.color.accent_material_dark))

        fab.rippleColor = lightVibrantColor
        fab.backgroundTintList = ColorStateList.valueOf(vibrantColor)
    }

    companion object {

        private val EXTRA_IMAGE = "com.antonioleiva.materializeyourapp.extraImage"
        private val EXTRA_TITLE = "com.antonioleiva.materializeyourapp.extraTitle"

//        fun navigate(activity: AppCompatActivity, transitionImage: View) {
//            val intent = Intent(activity, DetailActivity::class.java)
//            intent.putExtra(EXTRA_IMAGE, viewModel.getImage())
//            intent.putExtra(EXTRA_TITLE, viewModel.getText())
//
//            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, transitionImage, EXTRA_IMAGE)
//            ActivityCompat.startActivity(activity, intent, options.toBundle())
//        }
    }

    override fun onClick(view: View?) {

        when(view?.id!!)
        {
           R.id.sales_card->
           {
               startActivity(Intent(this,ActivitySalesRecord::class.java))
           }


        }
    }

}
