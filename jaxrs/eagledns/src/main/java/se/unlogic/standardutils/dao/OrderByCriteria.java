/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.dao;

import se.unlogic.standardutils.dao.enums.Order;

public class OrderByCriteria<T> {

	private Order order;
	private Column<T, ?> column;

	public OrderByCriteria(Order order, Column<T, ?> column) {

		super();
		this.order = order;
		this.column = column;
	}

	public Order getOrder() {

		return order;
	}

	public Column<T, ?> getColumn() {

		return column;
	}

}
