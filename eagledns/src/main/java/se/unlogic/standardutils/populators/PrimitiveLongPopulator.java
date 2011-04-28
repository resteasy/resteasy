/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.populators;

import se.unlogic.standardutils.validation.StringFormatValidator;


public class PrimitiveLongPopulator extends LongPopulator {

	public PrimitiveLongPopulator() {
		super();
	}

	public PrimitiveLongPopulator(String populatorID, StringFormatValidator formatValidator) {
		super(populatorID, formatValidator);
	}

	public PrimitiveLongPopulator(String populatorID) {
		super(populatorID);
	}

	@Override
	public Class<? extends Long> getType() {

		return long.class;
	}

	@Override
	public Long getValue(String value) {

		if(value == null){
			return 0l;
		}

		return super.getValue(value);
	}
}
