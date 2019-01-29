package com.ionob.pos.db

/**
 * Interface of CRUD operation.
 *
 * @author Ionob Team
 */
interface Database {

    /**
     * Selects something in database.
     * @param queryString query string for select in database.
     * @return list of object.
     */
    fun select(queryString: String): List<Any>?

    /**
     * Inserts something in database.
     * @param tableName name of table in database.
     * @param content string for using in database.
     * @return id of row data.
     */
    fun insert(tableName: String, content: Any): Int

    /**
     * Updates something in database.
     * @param tableName name of table in database.
     * @param content string for using in database.
     * @return true if updates success ; otherwise false.
     */
    fun update(tableName: String, content: Any): Boolean


    /**
     * Updates something in database.
     * @param tableName name of table in database.
     * @param content string for using in database.
     * @return true if updates success ; otherwise false.
     */
    fun updateStock(tableName: String, content: Any): Boolean


    /**
     * Deletes something in database.
     * @param tableName name of table in database.
     * @param id a specific id of row data for deletion.
     * @return true if deletion success ; otherwise false.
     */
    fun delete(tableName: String, id: Int): Boolean

    /**
     * Directly execute to database.
     * @param queryString query string for execute.
     * @return true if executes success ; otherwise false.
     */
    fun execute(queryString: String): Boolean
}
