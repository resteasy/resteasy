/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.populators;

import se.unlogic.standardutils.dao.querys.PreparedStatementQuery;
import se.unlogic.standardutils.validation.StringFormatValidator;

import java.sql.SQLException;
import java.util.UUID;


public class UUIDPopulator extends BaseStringPopulator<UUID> implements QueryParameterPopulator<UUID> {


	public UUIDPopulator() {
		super();
	}

	public UUIDPopulator(String populatorID, StringFormatValidator formatValidator) {
		super(populatorID, formatValidator);
	}

	public UUIDPopulator(String populatorID) {
		super(populatorID);
	}

	@Override
	protected boolean validateDefaultFormat(String value) {

		try {
			UUID.fromString(value);

			return true;

		} catch (IllegalArgumentException e) {}

		return false;
	}

	public Class<? extends UUID> getType() {

		return UUID.class;
	}

	public UUID getValue(String value) {

		return UUID.fromString(value);
	}

	public void populate(PreparedStatementQuery query, int paramIndex, Object uuid) throws SQLException {
		
		if(uuid == null){
			query.setObject(paramIndex, null);
			return;
		}
		
		query.setString(paramIndex, ((UUID) uuid).toString());
	}

}
