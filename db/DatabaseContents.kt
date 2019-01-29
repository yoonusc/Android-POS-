package com.ionob.pos.db

/**
 * Enum for name of tables in database.
 *
 * @author Ionob Team
 */
enum class DatabaseContents
/**
 * Constructs DatabaseContents.
 * @param name name of this content for using in database.
 */
private constructor(val nam: String?) {

    DATABASE("com.refresh.db1"),
    TABLE_PRODUCT_CATALOG("product_catalog"),
    TABLE_PRODUCT_CATEGORY("category"),
    TABLE_STOCK("stock"),
    TABLE_SALE("sale"),
    TABLE_SALE_LINEITEM("sale_lineitem"),
    TABLE_STOCK_SUM("stock_sum"),
    LANGUAGE("language"),
    TABLE_CUSTOMER("customer"),
    TABLE_TAX("tax");

    override fun toString(): String {
        return nam!!
    }
}
