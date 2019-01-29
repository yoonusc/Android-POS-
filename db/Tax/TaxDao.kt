package com.ionob.pos.db.Tax

interface TaxDao {

    val allTax: List<Tax>

    fun getTax_byid(TaxId: Int):List<Tax>

    fun addTax(tax:Tax):Int

    fun EditTax(tax: Tax)

    fun UpdateTax(tax: Tax)

    fun deleteTax(TaxId:Int)

}