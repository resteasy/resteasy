/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.populators;

import se.unlogic.standardutils.dao.BeanResultSetPopulator;

import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BlobPopulator implements BeanResultSetPopulator<Blob> {

	public static final BlobPopulator POPULATOR = new BlobPopulator();

	public static BlobPopulator getPopulator() {
		return POPULATOR;
	}

	public Blob populate(ResultSet rs) throws SQLException {
		return rs.getBlob(1);
	}
}
