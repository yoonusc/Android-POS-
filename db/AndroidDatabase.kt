package com.ionob.pos.db

import java.util.ArrayList

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.sql.SQLException
import kotlin.math.log

/**
 * Real database connector, provides all CRUD operation.
 * database tables are created here.
 *
 * @author Ionob Team
 */
class AndroidDatabase
/**
 * Constructs a new AndroidDatabase.
 * @param context The current stage of the application.
 */
(context: Context) : SQLiteOpenHelper(context, DatabaseContents.DATABASE.toString(), null, DATABASE_VERSION), Database {

    override fun onCreate(database: SQLiteDatabase) {
        database.beginTransaction()
        try {

            database.execSQL("CREATE TABLE " + DatabaseContents.TABLE_PRODUCT_CATALOG + "("

                    + "_id INTEGER PRIMARY KEY,"
                    + "name TEXT(100),"
                    + "product_id INTEGER,"
                    + "category_id INTEGER,"
                    + "barcode TEXT(100),"
                    + "unit_price DOUBLE,"
                    + "stock_qty DOUBLE,"
                    + "uom TEXT,"
                    + "image TEXT,"
                    + "tax_id INTEGER,"
                    + "tax_percent DOUBLE,"
                    + "is_tax_inclusive BOOLEAN,"
                    + "tax_amount DOUBLE,"
                    + "status TEXT(10)"

                    + ");")
            Log.d("CREATE DATABASE", "Create " + DatabaseContents.TABLE_PRODUCT_CATALOG + " Successfully.")



            database.execSQL("CREATE TABLE " + DatabaseContents.TABLE_PRODUCT_CATEGORY + "("

                    + "_id INTEGER PRIMARY KEY,"
                    + "category_id INTEGER,"
                    + "category_name INTEGER,"
                    + "image TEXT"
                    + ");")
            Log.d("CREATE DATABASE", "Create " + DatabaseContents.TABLE_STOCK + " Successfully.")

            database.execSQL("CREATE TABLE " + DatabaseContents.TABLE_STOCK + "("

                    + "_id INTEGER PRIMARY KEY,"
                    + "product_id INTEGER,"
                    + "quantity INTEGER,"
                    + "cost DOUBLE,"
                    + "date_added DATETIME"

                    + ");")
            Log.d("CREATE DATABASE", "Create " + DatabaseContents.TABLE_STOCK + " Successfully.")

            /*internal var customerCode: String? = null
    internal var customerId: Int? = null
    internal var salesRep: String? = null
    internal var invoiceDate: String? = null
    internal var invoiceNo: String? = null
    internal var profileId: Int? = null
    internal var subTotal: BigDecimal? = null
    internal var grandTotal: BigDecimal? = null
    internal var discountAmountt: BigDecimal? = null
    internal var isChecked: Boolean? = null
    internal var bpLocationId: Int? = null
    internal var remoteRecordId: Int? = null
    internal var isPurchase: Boolean? = null
    internal var priceListId:Int? = null
    internal var roundOff: BigDecimal? = null
    internal var errorMessage: String? = null*/

            database.execSQL("CREATE TABLE " + DatabaseContents.TABLE_SALE + "("

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
                    + "orders INTEGER,"
                    + "reference_number TEXT"

                    + ");")
            Log.d("CREATE DATABASE", "Create " + DatabaseContents.TABLE_SALE + " Successfully.")




            database.execSQL("CREATE TABLE " + DatabaseContents.TABLE_TAX + "("

                    + "_id INTEGER PRIMARY KEY,"
                    + "tax_id INTEGER,"
                    + "tax_name TEXT,"
                    + "tax_percent DOUBLE,"
                    + "is_tax_inclusive BOOLEAN "
                    + ");")
            Log.d("CREATE DATABASE", "Create " + DatabaseContents.TABLE_TAX + " Successfully.")



            database.execSQL("CREATE TABLE " + DatabaseContents.TABLE_SALE_LINEITEM + "("

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
                    + "tax_amount DOUBLE"
                    + ");")
            Log.d("CREATE DATABASE", "Create " + DatabaseContents.TABLE_SALE_LINEITEM + " Successfully.")


            // this _id is product_id but for update method, it is easier to use name _id
            database.execSQL("CREATE TABLE " + DatabaseContents.TABLE_STOCK_SUM + "("

                    + "_id INTEGER PRIMARY KEY,"
                    + "product_id INTEGER,"
                    + "quantity DOUBLE"

                    + ");")
            Log.d("CREATE DATABASE", "Create " + DatabaseContents.TABLE_STOCK_SUM + " Successfully.")

            database.execSQL("CREATE TABLE " + DatabaseContents.LANGUAGE + "("

                    + "_id INTEGER PRIMARY KEY,"
                    + "language TEXT(5)"

                    + ");")
            Log.d("CREATE DATABASE", "Create " + DatabaseContents.LANGUAGE + " Successfully.")

            database.execSQL("CREATE TABLE IF NOT EXISTS " + DatabaseContents.TABLE_CUSTOMER + "(" + "_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL ," +
                    " customer_id INTEGER , customer_name TEXT , image TEXT,customer_code TEXT , " +
                    " country TEXT , city TEXT , address1 TEXT , address2 TEXT ," +
                    " email TEXT, postal_code TEXT, seaquenceNo TEXT,isActive BOOLEAN,isNew BOOLEAN," +
                    " status TEXT , poPriceListId INTEGER , soPriceListId INTEGER , phone TEXT , " +
                    " taxId  INTEGER , isNoTax BOOLEAN , regionName TEXT , stateCode TEXT , " +
                    " isCustomer  BOOLEAN , isVendor BOOLEAN , isEmployee BOOLEAN , isSalesRep BOOLEAN , " +
                    " countryId  INTEGER ,regionID INTEGER, profile_id INTEGER" + ");")
            Log.d("CREATE DATABASE", "Create " + DatabaseContents.TABLE_CUSTOMER + " Successfully.")
            database.setTransactionSuccessful()
            database.endTransaction()
        }
        catch(e:Exception)
        {
            Log.d("sql exception",e.message)
            database.endTransaction()
        }

        Log.d("CREATE DATABASE", "Create Database Successfully+.")


    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

    }

    override fun select(queryString: String): List<Any>? {
        try {
            val database = this.writableDatabase
            val list = ArrayList<Any>()
            val cursor = database.rawQuery(queryString, null)

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        val content = ContentValues()
                        val columnNames = cursor.columnNames
                        for (columnName in columnNames) {
                            content.put(columnName, cursor.getString(cursor
                                    .getColumnIndex(columnName)))
                        }
                        list.add(content)
                    } while (cursor.moveToNext())
                }
            }
            cursor!!.close()
            database.close()
            return list

        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }

    }

    override fun insert(tableName: String, content: Any): Int {
        try {
            val database = this.writableDatabase
            val id = database.insert(tableName, null,
                    content as ContentValues).toInt()
            database.close()
            return id
        } catch (e: Exception) {
            e.printStackTrace()
            return -1
        }

    }

    override fun update(tableName: String, content: Any): Boolean {
        try {
            val database = this.writableDatabase
            val cont = content as ContentValues
            // this array will always contains only one element.
            val array = arrayOf(cont.get("_id").toString() + "")
            database.update(tableName, cont, " _id = ?", array)
            Log.d("query","success"+cont.toString())
            return true

        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("dbError",e.message)
            return false
        }

    }

    override fun updateStock(tableName: String, content: Any): Boolean {
        try {
            val database = this.writableDatabase
            val cont = content as ContentValues
            // this array will always contains only one element.
            val array = arrayOf(cont.get("product_id").toString() + "")
            database.update(tableName, cont, " product_id = ?", array)
            Log.d("query","success")
            return true

        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("dbError",e.message)
            return false
        }

    }

    override fun delete(tableName: String, id: Int): Boolean {
        try {
            val database = this.writableDatabase
            database.delete(tableName, " _id = ?", arrayOf(id.toString() + ""))
            return true

        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }

    }

    override fun execute(query: String): Boolean {
        try {
            val database = this.writableDatabase
            database.execSQL(query)
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }

    }

    companion object {

        private val DATABASE_VERSION = 1
        val DATABASE_NAME: String?=""
    }

}
