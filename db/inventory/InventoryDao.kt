package com.ionob.pos.db.inventory

import com.ionob.pos.domain.inventory.Product
import com.ionob.pos.domain.inventory.ProductLot

/**
 * DAO for Inventory.
 *
 * @author Ionob Team
 */
interface InventoryDao {

    /**
     * Returns list of all products in inventory.
     * @return list of all products in inventory.
     */
    val allProduct: List<Product>

    /**
     * Returns list of all products in inventory.
     * @return list of all products in inventory.
     */
    val allProductLot: List<ProductLot>

    /**
     * Adds product to inventory.
     * @param product the product to be added.
     * @return id of this product that assigned from database.
     */
    fun addProduct(product: Product): Int

    /**
     * Adds ProductLot to inventory.
     * @param productLot the ProductLot to be added.
     * @return id of this ProductLot that assigned from database.
     */
    fun addProductLot(productLot: ProductLot): Int

    /**
     * Edits product.
     * @param product the product to be edited.
     * @return true if product edits success ; otherwise false.
     */
    fun editProduct(product: Product?): Boolean

    /**
     * Returns product from inventory finds by id.
     * @param id id of product.
     * @return product
     */
    fun getProductById(id: Int): Product

    /**
     * Returns product from inventory finds by barcode.
     * @param barcode barcode of product.
     * @return product
     */
    fun getProductByBarcode(barcode: String): Product?

    /**
     * Returns list of product in inventory finds by name.
     * @param name name of product.
     * @return list of product in inventory finds by name.
     */
    fun getProductByName(name: String): List<Product>

    /**
     * Search product from string in inventory.
     * @param search string for searching.
     * @return list of product.
     */
    fun searchProduct(search: String): List<Product>

    /**
     * Returns list of product in inventory finds by id.
     * @param id id of product.
     * @return list of product in inventory finds by id.
     */
    fun getProductLotById(id: Int): List<ProductLot>

    /**
     * Returns list of ProductLot in inventory finds by id.
     * @param id id of ProductLot.
     * @return list of ProductLot in inventory finds by id.
     */
    fun getProductLotByProductId(id: Int): List<ProductLot>

    /**
     * Returns Stock in inventory finds by id.
     * @param id id of Stock.
     * @return Stock in inventory finds by id.
     */
    fun getStockSumById(id: Int?): Int

    /**
     * Updates quantity of product.
     * @param productId id of product.
     * @param quantity quantity of product.
     */
    fun updateStockSum(productId: Int?, quantity: Double)

    /**
     * Clears ProductCatalog.
     */
    fun clearProductCatalog()

    /**
     * Clear Stock.
     */
    fun clearStock()

    /**
     * Hidden product from inventory.
     * @param product The product to be hidden.
     */
    fun suspendProduct(product: Product?)

}
