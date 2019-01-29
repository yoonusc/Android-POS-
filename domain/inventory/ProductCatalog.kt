package com.ionob.pos.domain.inventory

import com.ionob.pos.db.inventory.InventoryDao
import java.math.BigDecimal

/**
 * Book that keeps list of Product.
 *
 * @author Ionob Team
 */
class ProductCatalog
/**
 * Constructs Data Access Object of inventory in ProductCatalog.
 * @param inventoryDao DAO of inventory.
 */
(private val inventoryDao: InventoryDao?) {

    /**
     * Returns list of all products in inventory.
     * @return list of all products in inventory.
     */
    val allProduct: List<Product>?
        get() = inventoryDao?.allProduct

    /**
     * Constructs product and adds product to inventory.
     * @param name name of product.
     * @param barcode barcode of product.
     * @param salePrice price of product.
     * @return true if product adds in inventory success ; otherwise false.
     */
    fun addProduct(name: String,
                   product_id: Int
                   , barcode: String,
                   salePrice: BigDecimal
                   , uom:String,
                   stock:BigDecimal,
                   category_id:Int,
                   image:String,
                   tax_id:Int,
                   tax_percent:Double,
                   is_included_tax:Boolean,
                   tax_amount:BigDecimal): Boolean {
        val product = Product(name,product_id, barcode, salePrice, stock,uom,category_id,image,tax_id,tax_percent,is_included_tax,tax_amount)
        val id = inventoryDao?.addProduct(product)
        return id != -1
    }

    /**
     * Edits product.
     * @param product the product to be edited.
     * @return true if product edits success ; otherwise false.
     */
    fun editProduct(product: Product?): Boolean? {
        val respond = inventoryDao?.editProduct(product)
        return respond
    }

    /**
     * Returns product from inventory finds by barcode.
     * @param barcode barcode of product.
     * @return product
     */
    fun getProductByBarcode(barcode: String): Product? {
        return inventoryDao?.getProductByBarcode(barcode)
    }

    /**
     * Returns product from inventory finds by id.
     * @param id id of product.
     * @return product
     */
    fun getProductById(id: Int): Product? {
        return inventoryDao?.getProductById(id)
    }

    /**
     * Returns list of product in inventory finds by name.
     * @param name name of product.
     * @return list of product in inventory finds by name.
     */
    fun getProductByName(name: String): List<Product>? {
        return inventoryDao?.getProductByName(name)
    }

    /**
     * Search product from string in inventory.
     * @param search string for searching.
     * @return list of product.
     */
    fun searchProduct(search: String): List<Product>? {
        return inventoryDao?.searchProduct(search)
    }

    /**
     * Clears ProductCatalog.
     */
    fun clearProductCatalog() {
        inventoryDao?.clearProductCatalog()
    }

    /**
     * Hidden product from inventory.
     * @param product The product to be hidden.
     */
    fun suspendProduct(product: Product?) {
        inventoryDao?.suspendProduct(product)
    }


}
