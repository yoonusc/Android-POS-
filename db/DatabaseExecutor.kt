package com.ionob.pos.db

/**
 * Uses to directly access to database.
 *
 * @author Ionob Team
 */
class DatabaseExecutor private constructor() {

    /**
     * Drops all data in database.
     */
    fun dropAllData() {
        execute("DELETE FROM " + DatabaseContents.TABLE_PRODUCT_CATALOG + ";")
        execute("DELETE FROM " + DatabaseContents.TABLE_SALE + ";")
        execute("DELETE FROM " + DatabaseContents.TABLE_SALE_LINEITEM + ";")
        execute("DELETE FROM " + DatabaseContents.TABLE_STOCK + ";")
        execute("DELETE FROM " + DatabaseContents.TABLE_STOCK_SUM + ";")
        execute("VACUUM;")
    }

    /**
     * Directly execute to database.
     * @param queryString query string for execute.
     */
    private fun execute(queryString: String) {
        database!!.execute(queryString)
    }

    companion object {

        private var database: Database? = null
        private var instance: DatabaseExecutor? = null

        /**
         * Sets database for use in DatabaseExecutor.
         * @param db database.
         */
        internal fun setDatabase(db: Database) {
            database = db
        }

        internal fun getInstance(): DatabaseExecutor {
            if (instance == null)
                instance = DatabaseExecutor()
            return instance as DatabaseExecutor
        }
    }


}
