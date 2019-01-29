package com.ionob.pos.domain.inventory

import java.util.HashMap

import com.ionob.pos.domain.DateTimeStrategy

/**
 * Lot or bunch of product that import to inventory.
 *
 * @author Ionob Team
 */
class ProductLot
/**
 * Constructs a new ProductLot.
 * @param id ID of the ProductLot, This value should be assigned from database.
 * @param dateAdded date and time of adding this lot.
 * @param quantity quantity of product.
 * @param product a product of this lot.
 * @param unitCost cost (of buy) of each unit in this lot.
 */
(
        /**
         * Returns id of this ProductLot.
         * @return id of this ProductLot.
         */
        val id: Int,
        /**
         * Returns date added of this ProductLot.
         * @return date added of this ProductLot.
         */
        val dateAdded: String,
        /**
         * Returns quantity of this ProductLot.
         * @return quantity of this ProductLot.
         */
        val quantity: Int,
        /**
         * Returns product in this ProductLot.
         * @return product in this ProductLot.
         */
        val product: Product?, private val unitCost: Double) {

    /**
     * Returns cost of this ProductLot.
     * @return cost of this ProductLot.
     */
    fun unitCost(): Double {
        return unitCost
    }

    /**
     * Returns the description of this ProductLot in Map format.
     * @return the description of this ProductLot in Map format.
     */
    fun toMap(): Map<String?, String?> {
        val map = HashMap<String?, String?>()
        map["id"] = id.toString() + ""
        map["dateAdded"] = DateTimeStrategy.format(dateAdded)
        map["quantity"] = quantity.toString() + ""
        map["productName"] = product?.name
        map["cost"] = unitCost.toString() + ""
        return map
    }

    companion object {

        /**
         * Static value for UNDEFINED ID.
         */
        val UNDEFINED_ID = -1
    }
}
