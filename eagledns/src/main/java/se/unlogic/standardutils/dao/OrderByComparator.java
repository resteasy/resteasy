/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.dao;

import se.unlogic.standardutils.dao.annotations.OrderBy;

import java.util.Comparator;

public class OrderByComparator implements Comparator<OrderBy> {

	public int compare(OrderBy o1, OrderBy o2) {

		if (o1.priority() < o2.priority()) {
			
			return 1;
			
		} else if (o1.priority() == o2.priority()) {
			
			return 0;
			
		} else {
			
			return -1;
		}
	}
}
