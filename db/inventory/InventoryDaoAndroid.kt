package com.ionob.pos.db.inventory

import java.util.ArrayList

import android.content.ContentValues
import android.util.Log

import com.ionob.pos.domain.inventory.Product
import com.ionob.pos.domain.inventory.ProductLot
import com.ionob.pos.db.Database
import com.ionob.pos.db.DatabaseContents
import java.math.BigDecimal

/**
 * DAO used by android for Inventory.
 *
 * @author Ionob Team
 */
class InventoryDaoAndroid
/**
 * Constructs InventoryDaoAndroid.
 * @param database database for use in InventoryDaoAndroid.
 */
(private val database: Database) : InventoryDao {

    override val allProduct: List<Product>
        get() = getAllProduct(" WHERE status = 'ACTIVE'")

    override val allProductLot: List<ProductLot>
        get() = getAllProductLot("")

    override fun addProduct(product: Product): Int {
        val content = ContentValues()
        content.put("name", product.name)
        content.put("product_id ", product.productId)
        content.put("category_id", product.category_id)
        content.put("barcode", product.barcode)
        content.put("unit_price", product.unitPrice.toDouble())
        content.put("uom", product.uom)
        //content.put("stock_qty", product.stockQty.toDouble())
        content.put("image", product.image)
        content.put("Tax_id", product.tax_id)
        content.put("tax_percent", product.tax_percent)
        content.put("is_tax_inclusive", product.is_tax_inclusive)
        content.put("tax_amount", product.tax_amount.toDouble())
        content.put("status", "ACTIVE")


        val id = database.insert(DatabaseContents.TABLE_PRODUCT_CATALOG.toString(), content)
        val content2 = ContentValues()
        content2.put("product_id ", product.productId)
        content2.put("quantity", 5 + id) // for test
        val id2 = database.insert(DatabaseContents.TABLE_STOCK_SUM.toString(), content2)
        return id
    }

    /**
     * Converts list of object to list of product.
     * @param objectList list of object.
     * @return list of product.
     */
    private fun toProductList(objectList: List<Any>): List<Product> {
        val list = ArrayList<Product>()
        for (`object` in objectList) {
            val content = `object` as ContentValues
            list.add(Product(
                    content.getAsInteger("_id")!!,
                    content.getAsString("name"),
                    content.getAsInteger("product_id"),
                    content.getAsString("barcode"),
                    content.getAsDouble("unit_price").toBigDecimal() ?: BigDecimal.ZERO,
                    content.getAsDouble("quantity")?.toBigDecimal() ?: BigDecimal.ZERO,
                    content.getAsString("uom")!!, content.getAsInteger("category_id"),
                    content.getAsString("image"),
                    content.getAsInteger("tax_id"),
                    content.getAsDouble("tax_percent"),
                    content.getAsBoolean("is_tax_inclusive"),
                    content.getAsDouble("tax_amount").toBigDecimal() ?: BigDecimal.ZERO
            )
            )
        }
        return list
    }

    /**
     * Returns list of all products in inventory.
     * @param condition specific condition for getAllProduct.
     * @return list of all products in inventory.
     */
    private fun getAllProduct(condition: String): List<Product> {
        val queryString = "SELECT distinct P.* , S.quantity FROM " + DatabaseContents.TABLE_PRODUCT_CATALOG.toString() + " AS P " + " JOIN " +
                DatabaseContents.TABLE_STOCK_SUM.toString() + " AS S " + " ON  P.product_id = S.product_id" +
                condition + " ORDER BY name"
        Log.d("query", queryString)
        return toProductList(database.select(queryString)!!)
    }

    /**
     * Returns product from inventory finds by specific reference.
     * @param reference reference value.
     * @param value value for search.
     * @return list of product.
     */
    private fun getProductBy(reference: String, value: String): List<Product> {
        val condition = " WHERE $reference = $value ;"
        return getAllProduct(condition)
    }

    /**
     * Returns product from inventory finds by similar name.
     * @param reference reference value.
     * @param value value for search.
     * @return list of product.
     */
    private fun getSimilarProductBy(reference: String, value: String): List<Product> {
        val condition = " WHERE $reference LIKE '%$value%' ;"
        return getAllProduct(condition)
    }

    override fun getProductByBarcode(barcode: String): Product? {
        val list = getProductBy("barcode", barcode)
        return if (list.isEmpty()) null else list[0]
    }

    // P IS general alias for PRODUCT
    override fun getProductById(id: Int): Product {
        return getProductBy("P.product_id", id.toString() + "")[0]
    }

    override fun editProduct(product: Product?): Boolean {
        val content = ContentValues()
        content.put("_id", product?.id)
        content.put("name", product?.name)
        content.put("category_id", product?.category_id)
        content.put("barcode", product?.barcode)
        content.put("image", product?.image)
        content.put("status", "ACTIVE")
        content.put("unit_price", product?.unitPrice?.toDouble())
        return database.update(DatabaseContents.TABLE_PRODUCT_CATALOG.toString(), content)
    }


    override fun addProductLot(productLot: ProductLot): Int {
        val content = ContentValues()
        content.put("date_added", productLot?.dateAdded)
        content.put("quantity", productLot?.quantity)
        content.put("product_id", productLot?.product?.id)
        content.put("cost", productLot?.unitCost())
        val id = database.insert(DatabaseContents.TABLE_STOCK.toString(), content)

        val productId = productLot.product?.id
        val content2 = ContentValues()
        content2.put("_id", productId)
        content2.put("quantity", getStockSumById(productId!!) + productLot.quantity)
        Log.d("inventory dao android", "" + getStockSumById(productId) + " " + productId + " " + productLot.quantity)
        database.update(DatabaseContents.TABLE_STOCK_SUM.toString(), content2)

        return id
    }


    override fun getProductByName(name: String): List<Product> {
        return getSimilarProductBy("name", name)
    }

    override fun searchProduct(search: String): List<Product> {
        val condition = " WHERE name LIKE '%$search%' OR barcode LIKE '%$search%' ;"
        return getAllProduct(condition)
    }

    /**
     * Returns list of all ProductLot in inventory.
     * @param condition specific condition for get ProductLot.
     * @return list of all ProductLot in inventory.
     */
    private fun getAllProductLot(condition: String): List<ProductLot> {
        val queryString = "SELECT * FROM " + DatabaseContents.TABLE_STOCK.toString() + condition
        return toProductLotList(database.select(queryString)!!)
    }

    /**
     * Converts list of object to list of ProductLot.
     * @param objectList list of object.
     * @return list of ProductLot.
     */
    private fun toProductLotList(objectList: List<Any>): List<ProductLot> {
        val list = ArrayList<ProductLot>()
        for (`object` in objectList) {
            val content = `object` as ContentValues
            val productId = content.getAsInteger("product_id")!!
            val product = getProductById(productId)
            list.add(
                    ProductLot(content.getAsInteger("_id")!!,
                            content.getAsString("date_added"),
                            content.getAsInteger("quantity")!!,
                            product,
                            content.getAsDouble("cost")!!)
            )
        }
        return list
    }

    override fun getProductLotByProductId(id: Int): List<ProductLot> {
        return getAllProductLot(" WHERE product_id = $id")
    }

    override fun getProductLotById(id: Int): List<ProductLot> {
        return getAllProductLot(" WHERE _id = $id")
    }

    override fun getStockSumById(id: Int?): Int {
        val queryString = "SELECT * FROM " + DatabaseContents.TABLE_STOCK_SUM + " WHERE product_id = " + id
        val objectList = database.select(queryString)
        val content = objectList?.get(0) as ContentValues
        val quantity = content.getAsInteger("quantity")!!
        Log.d("inventoryDaoAndroid", "stock sum of $id is $quantity")
        return quantity
    }

    override fun updateStockSum(productId: Int?, quantity: Double) {
        val content = ContentValues()
        content.put("product_id", productId)
        content.put("quantity", getStockSumById(productId) - quantity)
        database.updateStock(DatabaseContents.TABLE_STOCK_SUM.toString(), content)
    }

    override fun clearProductCatalog() {
        database.execute("DELETE FROM " + DatabaseContents.TABLE_PRODUCT_CATALOG)
    }

    override fun clearStock() {
        database.execute("DELETE FROM " + DatabaseContents.TABLE_STOCK)
        database.execute("DELETE FROM " + DatabaseContents.TABLE_STOCK_SUM)
    }

    override fun suspendProduct(product: Product?) {
        val content = ContentValues()
        content.put("_id", product?.id)
        content.put("name", product?.name)
        content.put("barcode", product?.barcode)
        content.put("status", "INACTIVE")
        content.put("unit_price", product?.unitPrice?.toDouble())
        database.update(DatabaseContents.TABLE_PRODUCT_CATALOG.toString(), content)
    }


}
