package com.ionob.pos.db.inventory

import java.util.ArrayList

import android.content.ContentValues
import android.content.Context
import android.util.Log
import com.ionob.pos.db.AndroidDatabase

import com.ionob.pos.domain.inventory.Product
import com.ionob.pos.domain.inventory.ProductLot
import com.ionob.pos.db.Database
import com.ionob.pos.db.DatabaseContents
import com.ionob.pos.db.NoDaoSetException
import com.ionob.pos.db.customer.CustomerDbao
import com.ionob.pos.domain.inventory.Category
import java.math.BigDecimal

/**
 * DAO used by android for category.
 *
 * @author yoonus
 */
class CategoryDbo
/**
 * Constructs InventoryDaoAndroid.
 * @param database database for use in InventoryDaoAndroid.
 */
(private val database: Database) : CategoryDao {

    override val allCategory: List<Category>
        get() = getAllCategory(" WHERE 1=1")


    override fun addCategory(category: Category): Int {
        val content = ContentValues()
        content.put("category_name", category.name)
        content.put("category_id", category.categoryID)
        content.put("image", category.image)
        val id = database.insert(DatabaseContents.TABLE_PRODUCT_CATEGORY.toString(), content)
        return id
    }

    /**
     * Converts list of object to list of product.
     * @param objectList list of object.
     * @return list of product.
     */
    private fun toCategory(objectList: List<Any>): List<Category> {
        val list = ArrayList<Category>()
        for (`object` in objectList) {
            val content = `object` as ContentValues
            list.add(Category(
                    content.getAsInteger("_id")!!,
                    content.getAsString("category_name"),
                    content.getAsInteger("category_id"),
                    content.getAsString("image").toString()
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
    private fun getAllCategory(condition: String): List<Category> {
        val queryString = "SELECT * FROM " + DatabaseContents.TABLE_PRODUCT_CATEGORY.toString()+condition + " ORDER BY category_name"
        Log.d("query",queryString)
        return toCategory(database.select(queryString)!!)
    }

    /**
     * Returns product from inventory finds by specific reference.
     * @param reference reference value.
     * @param value value for search.
     * @return list of product.
     */
    private fun getProductBy(reference: String, value: String): List<Category> {
        val condition = " WHERE $reference = $value ;"
        return getAllCategory(condition)
    }

    /**
     * Returns product from inventory finds by similar name.
     * @param reference reference value.
     * @param value value for search.
     * @return list of product.
     */
    private fun getSimilarProductBy(reference: String, value: String): List<Category> {
        val condition = " WHERE $reference LIKE '%$value%' ;"
        return getAllCategory(condition)
    }


    override fun getCategoryById(catid: Int): Category {
        return getProductBy("category_id", catid.toString() + "")[0]
    }

//    override fun getCategoryByLineid(id: Int): Category {
//        return getProductBy("_id", id.toString() + "")[0]
//    }


    override fun editCatogory(category: Category?): Boolean {
        val content = ContentValues()
        content.put("_id", category?.id)
        content.put("category_name", category?.name)
        content.put("category_id", category?.categoryID)
        content.put("image", category?.image)
        return database.update(DatabaseContents.TABLE_PRODUCT_CATEGORY.toString(), content)
    }


    override fun getCategoryByName(name: String): List<Category> {
        return getSimilarProductBy("category_name", name)
    }

    override fun searchCategory(search: String): List<Category> {
        val condition = " WHERE category_name LIKE '%$search%' ;"
        return getAllCategory(condition)
    }


    override fun clearCategory() {
        database.execute("DELETE FROM " + DatabaseContents.TABLE_PRODUCT_CATEGORY)
    }


    companion object {
        private var instance: CategoryDbo? = null


        @Throws(NoDaoSetException::class)
        fun getInstance(context: Context): CategoryDbo {
            if (CategoryDbo.instance == null) CategoryDbo.instance = CategoryDbo(AndroidDatabase(context))
            return CategoryDbo.instance as CategoryDbo
        }
    }



}
