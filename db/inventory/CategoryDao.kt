package com.ionob.pos.db.inventory

import com.ionob.pos.domain.inventory.Category
import com.ionob.pos.domain.inventory.Product
import com.ionob.pos.domain.inventory.ProductLot

/**
 * DAO for Inventory.
 *
 * @author yoonus
 */
interface CategoryDao {

    /**
     * Returns list of all products in inventory.
     * @return list of all products in inventory.
     */
    val allCategory: List<Category>
    fun addCategory(category: Category): Int
    fun editCatogory(product: Category?): Boolean
    fun getCategoryById(id: Int): Category
    fun  getCategoryByName(name: String): List<Category>
    fun searchCategory(search: String): List<Category>
    fun clearCategory()
}
