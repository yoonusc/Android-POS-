package com.ionob.pos.ui.inventory

import com.ionob.pos.domain.inventory.Product
import java.math.BigDecimal

interface CilckCallBack {

    fun onClick(data: Product?)
    fun onPlusClicked(data: Product?)
    {}
    fun onMinuslicked(data: Product?)
    {}
    fun onQtyEntered(data: Product?,qty:BigDecimal)
    {}

}