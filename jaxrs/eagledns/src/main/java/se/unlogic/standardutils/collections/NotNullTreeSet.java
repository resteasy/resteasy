/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.collections;

import java.util.Collection;
import java.util.TreeSet;

public class NotNullTreeSet<Type> extends TreeSet<Type> {

	private static final long serialVersionUID = -7808520266670296566L;

	@Override
	public boolean add(Type o) {

		if(o == null){
			throw new NullPointerException(this.getClass() + " does not allow null values!");
		}
			
		return super.add(o);
	}

	@Override
	public boolean addAll(Collection<? extends Type> c) {

		if(c == null || c.contains(null)){
			throw new NullPointerException(this.getClass() + " does not allow null values!");
		}
		
		return super.addAll(c);
	}
}
