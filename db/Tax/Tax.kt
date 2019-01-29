package com.ionob.pos.db.Tax

open class Tax (

        val tax_id: Int,
        var tax_name:String?,
        var tax_percent:Double?,
        var tax_mode: Boolean
)
