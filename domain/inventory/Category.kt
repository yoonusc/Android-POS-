package com.ionob.pos.domain.inventory

import java.math.BigDecimal
import java.util.HashMap

/**
 * Product or item represents the real product in store.
 *
 * @author yoonus
 */
open class Category(
        val id: Int,
        var name: String?,
        var categoryID:Int?,
        var image: String?
        )


