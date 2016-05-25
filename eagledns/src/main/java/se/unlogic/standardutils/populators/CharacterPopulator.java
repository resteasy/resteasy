/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.populators;

import se.unlogic.standardutils.dao.BeanResultSetPopulator;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CharacterPopulator extends BaseStringPopulator<Character> implements BeanResultSetPopulator<Character>, BeanStringPopulator<Character> {

	public CharacterPopulator() {

		super();
	}

	private static final CharacterPopulator POPULATOR = new CharacterPopulator();

	public Character populate(ResultSet rs) throws SQLException {

		return rs.getString(1).charAt(0);
	}

	public static CharacterPopulator getPopulator() {

		return POPULATOR;
	}

	public Character getValue(String value) {

		return value.charAt(0);
	}

	@Override
	public boolean validateDefaultFormat(String value) {

		return true;
	}

	public Class<? extends Character> getType() {

		return Character.class;
	}
}
