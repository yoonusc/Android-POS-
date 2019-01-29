package com.ionob.pos.domain.sale;

import java.math.BigDecimal;

/**
 * Sale that loads only total and orders.
 * It's for overview report that doesn't need LineItem's information.
 * NOTE: This Sale instance throws NullPointerException
 * when calling method that involve with LineItem.
 * 
 * @author Ionob Team
 *
 */
public class QuickLoadSale extends Sale {
	
	private BigDecimal total;
	private BigDecimal orders;
	
	/**
	 * 
	 * @param id ID of this sale.
	 * @param startTime
	 * @param endTime
	 * @param status
	 * @param total
	 * @param orders numbers of lineItem in this Sale.
	 */
	public QuickLoadSale(int id, String startTime, String endTime, String status, BigDecimal total, BigDecimal orders) {
		super(id, startTime, endTime, status);
		this.total = total;
		this.orders = orders;
	}
	
	@Override
	public BigDecimal getOrders() {
		return orders;
	}
	
	@Override
	public BigDecimal getTotal() {
		return total;
	}

}
