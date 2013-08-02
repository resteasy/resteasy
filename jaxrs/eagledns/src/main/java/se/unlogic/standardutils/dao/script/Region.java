/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.dao.script;
/**
 * This class represents a region (a sequence) between two symbols (sequences) in a sequence 
 * @author sikstromj
 *
 */
public class Region {

	private final Symbol start;
	private final Symbol end;

	public Region(Symbol start, Symbol end) {
		this.start = start;
		this.end = end;
	}

	public Integer getStart() {
		return this.start.getOffsets().getEnd();
	}

	public Integer getEnd() {
		return this.end.getOffsets().getStart();
	}
	
	
}
