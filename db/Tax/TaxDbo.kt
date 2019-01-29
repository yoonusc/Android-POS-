package com.ionob.pos.db.Tax

import android.content.ContentValues
import android.content.Context
import android.util.Log
import com.ionob.pos.db.AndroidDatabase
import com.ionob.pos.db.Database
import com.ionob.pos.db.DatabaseContents
import com.ionob.pos.db.NoDaoSetException
import com.ionob.pos.domain.inventory.Category
import java.util.ArrayList

class TaxDbo(private val database: Database) : TaxDao {



    override val allTax: List<Tax>
        get() = getTaxlist()


    override fun addTax(tax: Tax): Int {
        val content = ContentValues()
        content.put("tax_id", tax.tax_id)
        content.put("tax_name", tax.tax_name)
        content.put("tax_percent", tax.tax_percent?.toDouble())
        content.put("is_tax_inclusive", tax.tax_mode)
        val id = database.insert(DatabaseContents.TABLE_TAX.toString(), content)
        return id

    }

    override fun EditTax(tax: Tax) {


        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun UpdateTax(tax: Tax) {


        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteTax(TaxId: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun getTaxlist(): List<Tax> {
        val queryString = "SELECT * FROM " + DatabaseContents.TABLE_TAX.toString()
        Log.d("query", queryString)
        return totax(database.select(queryString)!!)
    }


    override fun getTax_byid(tax_id: Int):List<Tax> {

        val queryString = "SELECT * FROM " + DatabaseContents.TABLE_TAX.toString()+" WHERE tax_id= "+tax_id.toString()+""
        Log.d("query", queryString)
        return totax(database.select(queryString)!!)

    }


    private fun totax(objectList: List<Any>): List<Tax> {
        val list = ArrayList<Tax>()
        for (`object` in objectList) {
            val content = `object` as ContentValues

            list.add(Tax(
                    content.getAsInteger("tax_id")!!,
                    content.getAsString("tax_name"),
                    content.getAsDouble("tax_percent"),
                    content.getAsBoolean("is_tax_inclusive")
            )
            )
        }
        return list
    }





    companion object {

        private var instance: TaxDbo? = null
        @Throws(NoDaoSetException::class)
        fun getInstance(context: Context): TaxDbo {
            if (TaxDbo.instance == null) TaxDbo.instance = TaxDbo(AndroidDatabase(context))
            return TaxDbo.instance as TaxDbo
        }
    }


}


