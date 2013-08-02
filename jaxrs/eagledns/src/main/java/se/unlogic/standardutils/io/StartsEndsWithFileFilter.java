/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.io;

import java.io.File;
import java.io.FileFilter;


public class StartsEndsWithFileFilter implements FileFilter {

	private String prefix;
	private String suffix;

	public StartsEndsWithFileFilter(String prefix, String suffix) {

		super();
		this.prefix = prefix;
		this.suffix = suffix;
	}

	public boolean accept(File file) {

		if(file.getName().startsWith(prefix) && file.getName().endsWith(suffix)){

			return true;
		}

		return false;
	}
}
