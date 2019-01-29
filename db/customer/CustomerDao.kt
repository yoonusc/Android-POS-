package com.ionob.pos.db.customer

import com.ionob.pos.domain.inventory.Product
import com.ionob.pos.domain.inventory.ProductLot
import com.ionob.pos.ui.sale.model.CustomerModel

/**
 * DAO for Inventory.
 *
 * @author Ionob Team
 */
interface CustomerDao {

    /**
     * Returns list of all products in inventory.
     * @return list of all products in inventory.
     */
    val allCustomer: List<CustomerModel>



    /**
     * Adds new customer in to db
     * @param customer the customer to be added.
     * @return id of record  that assigned from database.
     */
    fun addCustomer(customer: CustomerModel): Int



    /**
     * Edits customer
     * @param customer the customer to be edited.
     * @return true if  success ; otherwise false.
     */
    fun editCustomer(customer: CustomerModel?): Boolean

    /**
     * Returns customer from db .
     * @param id id of customer  .
     * @return customer
     */
    fun getCustomerByid(customerId: Int): CustomerModel


    /**
     * Returns list of customer
     * @param name name of customer .
     * @return list of customer finds by name.
     */
    fun getCustomerByName(name: String): List<CustomerModel>

    /**
     * Search product from string in inventory.
     * @param search string for searching.
     * @return list of product.
     */
    fun searchCustomer(search: String): List<CustomerModel>

    /**
     * Returns list of product in inventory finds by id.
     * @param id id of product.
     * @return list of product in inventory finds by id.
     */




    fun deleteall()



}
