/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.dao;

import se.unlogic.standardutils.dao.querys.GeneratedKeyCollector;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


public class ColumnKeyCollectorWrapper<T> implements GeneratedKeyCollector {

	private ArrayList<ColumnKeyCollector<T>> columnKeyCollectors;
	private T bean;

	public ColumnKeyCollectorWrapper(ArrayList<ColumnKeyCollector<T>> columnKeyCollectors, T bean) {

		this.columnKeyCollectors = columnKeyCollectors;
		this.bean = bean;
	}

	public void collect(ResultSet rs) throws SQLException {

		for(ColumnKeyCollector<T> keyCollector : columnKeyCollectors){

			keyCollector.collect(bean, rs);
		}
	}
}
