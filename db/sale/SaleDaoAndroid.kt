package com.ionob.pos.db.sale

import java.util.ArrayList
import java.util.Calendar

import android.content.ContentValues
import android.util.Log
import com.ionob.pos.R.drawable.log

import com.ionob.pos.domain.DateTimeStrategy
import com.ionob.pos.domain.inventory.LineItem
import com.ionob.pos.domain.inventory.Product
import com.ionob.pos.domain.sale.QuickLoadSale
import com.ionob.pos.domain.sale.Sale
import com.ionob.pos.db.Database
import com.ionob.pos.db.DatabaseContents
import com.ionob.pos.db.sale.SaleDao.Companion.STATUS_DRAFT
import com.ionob.pos.db.sale.SaleDao.Companion.STATUS_COMPLETED
import com.ionob.pos.db.sale.SaleDao.Companion.STATUS_SYNCED
import com.ionob.pos.db.sale.SaleDao.Companion.STATUS_SYNC_FAILED
import java.math.BigDecimal
import com.ionob.pos.db.sale.SaleDao.Companion.STATUS_ALL
import com.ionob.pos.utils.Constance
import com.ionob.pos.utils.IONPreferences


/**
 * DAO used by android for Sale process.
 *
 * @author ioNOB
 */

class SaleDaoAndroid(internal var database: Database) : SaleDao {

    override val allSale: List<Sale>
        get() = getAllSale(" WHERE 1=1")

    override val allCompletedSale: List<Sale>
        get() = getAllSale(" WHERE status = '$STATUS_COMPLETED'")

    override val allDraftSale: List<Sale>
        get() = getAllSale(" WHERE status = '$STATUS_DRAFT'")

    override val allSynced: List<Sale>
        get() = getAllSale(" WHERE status = '$STATUS_SYNCED'")

    override val allFailed: List<Sale>
        get() = getAllSale(" WHERE status = '$STATUS_SYNC_FAILED'")

    override fun getCoutOfRecord(status: String): Int {
        if (status.equals(STATUS_ALL))
            return getCount(" WHERE 1=1")
        else
            return getCount(" WHERE status = '$status'")
    }


    /*

                    + "_id INTEGER PRIMARY KEY,"
                    + "customer_id TEXT,"
                    + "salesrep_id TEXT,"
                    + "is_purchase BOOLEAN,"
                    + "pricelist_id TEXT,"
                    + "error_mg TEXT,"
                    + "rounding TEXT,"
                    + "discount TEXT,"
                    + "line_discount TEXT,"
                    + "status TEXT(40),"
                    + "payment TEXT(50),"
                    + "payment_type TEXT(50),"
                    + "total DOUBLE,"
                    + "sub_total DOUBLE,"
                    + "start_time DATETIME,"
                    + "end_time DATETIME,"
                    + "server_id INTEGER,"
                    + "orders INTEGER"
     */

    override fun initiateSale(startTime: String): Sale? {
        return null
    }


    override fun initiateSale(sales: Sale): Sale {
        val content = ContentValues()
        content.put("start_time", sales.startTime)
        content.put("customer_id", sales.customerId)
        content.put("salesrep_id", sales.salesRep)
        content.put("pricelist_id", sales.priceListId)
        content.put("error_mg", sales.errorMessage)
        content.put("rounding", sales.roundOff?.toDouble())
        content.put("discount", sales.discountAmountt?.toDouble())
        content.put("line_discount", sales.lineDiscount?.toDouble())
        content.put("payment", sales.amountPaid?.toDouble())
        content.put("payment_type", sales.paymentType)
        content.put("status", STATUS_DRAFT)
        content.put("total", sales.grandTotal?.toDouble())
        content.put("sub_total", sales.subTotal?.toDouble())
        content.put("orders", "0")
        content.put("end_time", sales.endTime)
        val id = database.insert(DatabaseContents.TABLE_SALE.toString(), content)
        sales.id = id
        return sales
    }

    override fun endSale(sales: Sale, endTime: String) {
        val content = ContentValues()
        content.put("_id", sales.id)
        content.put("start_time", sales.startTime)
        content.put("customer_id", sales.customerId)
        content.put("salesrep_id", sales.salesRep)
        content.put("pricelist_id", sales.priceListId)
        content.put("error_mg", sales.errorMessage)
        content.put("rounding", sales.roundOff?.toDouble())
        content.put("discount", sales.discountAmountt?.toDouble())
        content.put("line_discount", sales.lineDiscount?.toDouble())
        content.put("payment", sales.amountPaid?.toDouble())
        content.put("payment_type", sales.paymentType)
        content.put("status", STATUS_COMPLETED)
        content.put("total", sales.grandTotal?.toDouble()) // total
        content.put("sub_total", sales.subTotal?.toDouble()) // total- (linediscount+checkut discount)
        content.put("orders", sales.orders.toDouble())
        content.put("end_time", endTime)
        content.put("reference_number", sales.referenceNumber)
        database.update(DatabaseContents.TABLE_SALE.toString(), content)
    }


    override fun updateSale(sales: Sale) {
        val content = ContentValues()
        content.put("_id", sales.id)
        content.put("start_time", sales.startTime)
        content.put("customer_id", sales.customerId)
        content.put("salesrep_id", sales.salesRep)
        content.put("pricelist_id", sales.priceListId)
        content.put("error_mg", sales.errorMessage)
        content.put("rounding", sales.roundOff?.toDouble())
        content.put("discount", sales.discountAmountt?.toDouble())
        content.put("line_discount", sales.lineDiscount?.toDouble())
        content.put("payment", sales.amountPaid?.toDouble())
        content.put("payment_type", sales.paymentType)
        content.put("status", sales.status)
        content.put("total", sales.grandTotal?.toDouble()) // total
        content.put("sub_total", sales.subTotal?.toDouble()) // total- (linediscount+checkut discount)
        content.put("orders", sales.orders.toDouble())
        content.put("end_time", sales.endTime)
        database.update(DatabaseContents.TABLE_SALE.toString(), content)
    }


    /* + "_id INTEGER PRIMARY KEY,"
    + "sale_id INTEGER,"
    + "product_id INTEGER,"
    + "quantity DOUBLE,"
    + "unit_price DOUBLE,"
    + "is_free  BOOLEAN,"
    + "discount_amount DOUBLE,"
    + "line_total DOUBLE,"
    + "net_total DOUBLE,"
    + "tax_id INTEGER,"
    + "tax_amount DOUBLE"*/
    override fun addLineItem(saleId: Int, lineItem: LineItem): Int {
        val content = ContentValues()
        var netTotal = lineItem.totalAmount?.minus(lineItem.discount)
        content.put("sale_id", saleId)
        content.put("product_id", lineItem.product?.productId)
        content.put("product_name", lineItem.product?.name)
        content.put("uom", lineItem.product?.uom)
        content.put("quantity", lineItem.quantity.toDouble())
        content.put("unit_price", lineItem.priceAtSale?.toDouble())
        content.put("tax_amount", lineItem.taxAmount?.toDouble())
        content.put("line_total", lineItem.totalAmount?.toDouble())
        content.put("discount_amount", (lineItem.discount?.toDouble()))
        content.put("net_total", (netTotal?.toDouble()))
        val id = database.insert(DatabaseContents.TABLE_SALE_LINEITEM.toString(), content)
        return id
    }

    override fun updateLineItem(saleId: Int, lineItem: LineItem) {
        val content = ContentValues()
        content.put("_id", lineItem.id)
        var netTotal = lineItem.totalAmount?.minus(lineItem.discount)
        content.put("sale_id", saleId)
        content.put("product_id", lineItem.product?.productId)
        content.put("quantity", lineItem.quantity.toDouble())
        content.put("unit_price", lineItem.priceAtSale?.toDouble())
        content.put("tax_amount", lineItem.taxAmount?.toDouble())
        content.put("line_total", lineItem.totalAmount?.toDouble())
        content.put("discount_amount", (lineItem.discount?.toDouble()))
        content.put("net_total", (netTotal?.toDouble()))
        database.update(DatabaseContents.TABLE_SALE_LINEITEM.toString(), content)
    }

    override fun getAllSaleDuring(start: Calendar, end: Calendar): List<Sale> {
        val startBound = DateTimeStrategy.getSQLDateFormat(start)
        val endBound = DateTimeStrategy.getSQLDateFormat(end)
        val list = getAllSale(" WHERE end_time BETWEEN '$startBound 00:00:00' AND '$endBound 23:59:59' AND status = '$STATUS_COMPLETED'")
        return list
    }

    /**
     * This method get all Sale *BUT* no LineItem will be loaded.
     * @param condition
     * @return
     */
    fun getAllSale(condition: String): List<Sale> {
        val selectedCustomer = IONPreferences.getSelectedCustomerId()
        var queryString = "SELECT * FROM " + DatabaseContents.TABLE_SALE + condition

        if (selectedCustomer?.equals(Constance.UNDIFINED_CUSTOMER) == false)
            queryString = queryString + " and customer_id ='$selectedCustomer'"

        Log.d("query", queryString)
        val objectList = database.select(queryString)
        val list = ArrayList<Sale>()
        if (objectList != null) {
            for (`object` in objectList) {
                val content = `object` as ContentValues
                var sale = (QuickLoadSale(
                        content.getAsInteger("_id")!!,
                        content.getAsString("start_time"),
                        content.getAsString("end_time"),
                        content.getAsString("status") ?: "X",
                        content.getAsDouble("total")?.toBigDecimal() ?: BigDecimal.ZERO,
                        content.getAsInteger("orders")?.toBigDecimal() ?: BigDecimal.ZERO
                )
                        )

                sale.customerId = content.getAsInteger("customer_id")
                sale.salesRep = content.getAsString("salesrep_id")
                sale.priceListId = content.getAsInteger("pricelist_id")
                sale.errorMessage = content.getAsString("error_mg")
                sale.roundOff = content.getAsDouble("rounding")?.toBigDecimal()
                sale.discountAmountt = content.getAsDouble("discount")?.toBigDecimal()
                sale.amountPaid = content.getAsDouble("payment")?.toBigDecimal()
                sale.subTotal = content.getAsDouble("sub_total")?.toBigDecimal()
                sale.paymentType = content.getAsString("payment_type")
                sale.referenceNumber = content.getAsString("reference_number")
                sale.lineDiscount = content.getAsDouble("line_discount")?.toBigDecimal()
                list.add(sale)
            }
        }
        return list
    }

    /**get count of record
     *
     */

    fun getCount(condition: String): Int {
        val selectedCustomer = IONPreferences.getSelectedCustomerId()
        var queryString = "SELECT * FROM " + DatabaseContents.TABLE_SALE + condition
        if (selectedCustomer?.equals(Constance.UNDIFINED_CUSTOMER) == false)
            queryString = queryString + " and customer_id ='$selectedCustomer'"
        Log.d("query", queryString)
        val objectList = database.select(queryString)
        return objectList?.size ?: 0
    }

    /**
     * This load complete data of Sale.
     * @param id Sale ID.
     * @return Sale of specific ID.
     */
    override fun getSaleById(id: Int): Sale {
        val queryString = "SELECT * FROM " + DatabaseContents.TABLE_SALE + " WHERE _id = " + id
        val objectList = database.select(queryString)
        val list = ArrayList<Sale>()
        if (objectList != null) {
            for (`object` in objectList) {
                val content = `object` as ContentValues


                var sale = Sale(
                        content.getAsInteger("_id")!!,
                        content.getAsString("start_time"),
                        content.getAsString("end_time"),
                        content.getAsString("status"),
                        getLineItem(content.getAsInteger("_id")!!))
                sale.customerId = content.getAsInteger("customer_id")
                sale.salesRep = content.getAsString("salesrep_id")
                sale.priceListId = content.getAsInteger("pricelist_id")
                sale.errorMessage = content.getAsString("error_mg")
                sale.roundOff = content.getAsDouble("rounding")?.toBigDecimal()
                sale.discountAmountt = content.getAsDouble("discount")?.toBigDecimal()
                sale.lineDiscount = content.getAsDouble("line_discount")?.toBigDecimal()
                sale.amountPaid = content.getAsDouble("payment")?.toBigDecimal()
                sale.paymentType = content.getAsString("paymen_type")
                sale.referenceNumber = content.getAsString("reference_number")
                sale.subTotal = content.getAsDouble("sub_total")?.toBigDecimal()
                list.add(sale)
            }
        }
        return list[0]
    }


    /*
     salesLines
     + "_id INTEGER PRIMARY KEY,"
                    + "sale_id INTEGER,"
                    + "product_id INTEGER,"
                    + "product_name TEXT,"
                    + "uom TEXT,"
                    + "sku TEXT,"
                    + "upc TEXT,"
                    + "quantity DOUBLE,"
                    + "unit_price DOUBLE,"
                    + "is_free  BOOLEAN,"
                    + "discount_amount DOUBLE,"
                    + "line_total DOUBLE,"
                    + "net_total DOUBLE,"
                    + "tax_id INTEGER,"
                    + "tax_amount DOUBLE"*/

    override fun getLineItem(saleId: Int): MutableList<LineItem> {
        val queryString = "SELECT * FROM " + DatabaseContents.TABLE_SALE_LINEITEM + " WHERE sale_id = " + saleId
        val objectList = database.select(queryString)
        val list = ArrayList<LineItem>()
        if (objectList != null) {
            for (`object` in objectList) {
                val content = `object` as ContentValues
                val productId = content.getAsInteger("product_id")!!
                val id = content.getAsInteger("_id")!!
                // "SELECT * FROM " + DatabaseContents.TABLE_PRODUCT_CATALOG + " WHERE _id = " + productId
                val queryString2 = "SELECT distinct P.* , S.quantity FROM " + DatabaseContents.TABLE_PRODUCT_CATALOG.toString() + " AS P " + " JOIN " +
                        DatabaseContents.TABLE_STOCK_SUM.toString() + " AS S " + " ON  P.product_id = S.product_id" + " where P.product_id=" + productId

                val objectList2 = database.select(queryString2)

                val productList = ArrayList<Product>()
                if (objectList2 != null) {
                    for (object2 in objectList2) {
                        val content2 = object2 as ContentValues
                        productList.add(Product(
                                id,
                                content2.getAsString("name"),
                                productId,
                                content2.getAsString("barcode"),
                                content2.getAsDouble("unit_price").toBigDecimal()!!,
                                content2.getAsDouble("quantity")?.toBigDecimal()!!,
                                content2.getAsString("uom")!!,
                                content2.getAsInteger("category_id")!!,
                                content2.getAsString("image")!!,
                                content2.getAsInteger("tax_id"),
                                content2.getAsDouble("tax_percent"),
                                content2.getAsBoolean("is_tax_inclusive"),
                                content2.getAsDouble("tax_amount").toBigDecimal()))

                    }
                }
                list.add(LineItem(content.getAsInteger("_id")!!, productList[0],
                        content.getAsDouble("quantity").toBigDecimal()!!,
                        content.getAsDouble("unit_price").toBigDecimal()!!,
                        content.getAsDouble("discount_amount").toBigDecimal()!!,
                        content.getAsDouble("tax_amount").toBigDecimal()!!
                ))
            }
        }
        return list
    }

    override fun getLine(saleId: Int, product_id: Int): LineItem? {
        val queryString = "SELECT * FROM " + DatabaseContents.TABLE_SALE_LINEITEM + " WHERE product_id = " + product_id + " AND sale_id = " + saleId
        val objectList = database.select(queryString)
        val list = ArrayList<LineItem>()
        if (objectList != null) {
            for (`object` in objectList) {
                val content = `object` as ContentValues
                val productId = content.getAsInteger("product_id")!!
                val id = content.getAsInteger("_id")!!
                // "SELECT * FROM " + DatabaseContents.TABLE_PRODUCT_CATALOG + " WHERE _id = " + productId
                val queryString2 = "SELECT distinct P.* , S.quantity FROM " + DatabaseContents.TABLE_PRODUCT_CATALOG.toString() + " AS P " + " JOIN " +
                        DatabaseContents.TABLE_STOCK_SUM.toString() + " AS S " + " ON  P.product_id = S.product_id" + " where P.product_id=" + productId

                val objectList2 = database.select(queryString2)

                val productList = ArrayList<Product>()
                if (objectList2 != null) {
                    for (object2 in objectList2) {
                        val content2 = object2 as ContentValues
                        productList.add(Product(id, content2.getAsString("name"), productId,
                                content2.getAsString("barcode"), content2.getAsDouble("unit_price").toBigDecimal()!!,
                                content2.getAsDouble("quantity")?.toBigDecimal()!!,
                                content2.getAsString("uom")!!, content2.getAsInteger("category_id")!!,
                                content2.getAsString("image")!!,
                                content2.getAsInteger("tax_id"),
                                content2.getAsDouble("tax_percent"),
                                content2.getAsBoolean("is_tax_inclusive"),
                                content2.getAsDouble("tax_amount").toBigDecimal())
                        )
                    }
                }
                list.add(LineItem(content.getAsInteger("_id")!!, productList[0],
                        content.getAsDouble("quantity").toBigDecimal()!!,
                        content.getAsDouble("unit_price").toBigDecimal()!!,
                        content.getAsDouble("discount_amount").toBigDecimal()!!,
                        content.getAsDouble("tax_amount").toBigDecimal()!!
                ))
            }
        }
        return if (list.size > 0) list[0] else null
    }

    override fun clearSaleLedger() {
        database.execute("DELETE FROM " + DatabaseContents.TABLE_SALE)
        database.execute("DELETE FROM " + DatabaseContents.TABLE_SALE_LINEITEM)
    }

    override fun deleteSalesRecord(salesid: Int) {
        database.execute("DELETE FROM " + DatabaseContents.TABLE_SALE + " WHERE _id =" + salesid)
        database.execute("DELETE FROM " + DatabaseContents.TABLE_SALE_LINEITEM + " WHERE sale_id =" + salesid)
    }

    override fun cancelSale(sale: Sale, endTime: String) {
        val content = ContentValues()
        content.put("_id", sale.id)
        content.put("status", STATUS_DRAFT)
        content.put("payment", "n/a")
        content.put("total", sale.total.toDouble())
        content.put("orders", sale.orders.toDouble())
        content.put("start_time", sale.startTime)
        content.put("end_time", endTime)
        database.update(DatabaseContents.TABLE_SALE.toString(), content)

    }

    override fun removeLineItem(id: Int) {
        database.delete(DatabaseContents.TABLE_SALE_LINEITEM.toString(), id)
    }


}
