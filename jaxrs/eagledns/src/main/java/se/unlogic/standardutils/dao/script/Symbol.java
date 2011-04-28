/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.dao.script;
/**
 * This class represents a symbol (a sequence) within a sequence
 */
import java.util.Comparator;

public class Symbol {

	public Symbol(String key, Offsets offsets) {
		this.key = key;
		this.offsets = offsets;
	}

	private final String key;
	private final Offsets offsets;

	public Offsets getOffsets() {
		return this.offsets;
	}

	public String getKey() {
		return this.key;
	}
	
	public static class SymbolStartComparator implements Comparator<Symbol> {
		public int compare(Symbol o1, Symbol o2) {
			return o1.getOffsets().getStart().compareTo(o2.getOffsets().getStart());
		}
	}
}
