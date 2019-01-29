package com.ionob.pos.domain.inventory

import java.math.BigDecimal
import java.util.HashMap

/**
 * Product or item represents the real product in store.
 *
 * @author Ionob Team
 */
open class Product
/**
 * Constructs a new Product.
 * @param id ID of the product, This value should be assigned from database.
 * @param name name of this product.
 * @param barcode barcode (any standard format) of this product.
 * @param salePrice price for using when doing sale.
 *
 */
(
        /**
         * Returns id of this product.
         * @return id of this product.
         */
        val id: Int,
        /**
         * Returns name of this product.
         * @return name of this product.
         */
        /**
         * Sets name of this product.
         * @param name name of this product.
         */
        var name: String?,
        /**
         * Returns barcode of this product.
         * @return barcode of this product.
         */
        /**
         * Sets product_id of this product.
         * @param productId barcode of this product.
         */
        var productId: Int?,

        /**
         * Returns barcode of this product.
         * @return barcode of this product.
         */
        /**
         * Sets barcode of this product.
         * @param barcode barcode of this product.
         */

        var barcode: String?,
        /**
         * Returns price of this product.
         * @return price of this product.
         */
        /**
         * Sets price of this product.
         * @param unitPrice price of this product.
         */

        var unitPrice: BigDecimal,


        var stockQty: BigDecimal,

        var uom: String,

        var category_id: Int,

        var image: String,
        var tax_id: Int,

        var tax_percent: Double,

        var is_tax_inclusive: Boolean,

        var tax_amount: BigDecimal

) {

    /**
     * Constructs a new Product.
     * @param name name of this product.
     * @param barcode barcode (any standard format) of this product.
     * @param salePrice price for using when doing sale.
     */
    constructor(
            name: String,
            productId: Int,
            barcode: String,
            salePrice: BigDecimal,
            stockQty: BigDecimal,
            uom: String,
            categori: Int,
            image: String,
            tax_id: Int,
            tax_percent:Double,
            tax_mode: Boolean,
            tax_amount: BigDecimal
    ) : this(UNDEFINED_ID, name, productId, barcode, salePrice, stockQty, uom, categori, image, tax_id, tax_percent, tax_mode, tax_amount) {
    }

    /**
     * Returns the description of this Product in Map format.
     * @return the description of this Product in Map format.
     */
    fun toMap(): Map<String?, String?> {
        val map = HashMap<String?, String?>()
        map["id"] = id.toString() + ""
        map["name"] = name
        map["barcode"] = barcode
        map["unitPrice"] = unitPrice.toString() + ""
        return map

    }

    companion object {

        /**
         * Static value for UNDEFINED ID.
         */
        val UNDEFINED_ID = -1
    }

}
