package com.ionob.pos.ui.sale.model



data class PriceListModel(var id:Int) {

    var priceListId: Int = 0
    var name: String?=null
    var description: String?=null
    var isEnforcePriceLimit: Boolean = false
    var currencyId: Int = 0
    var isSopriceList: Boolean = false
    var isTaxIncluded: Boolean = false
    var validFrom: Long = 0
    var stdPrecision: Int = 0
    var pricePrecision: Int = 0
    var isDefault: Boolean = false
}
