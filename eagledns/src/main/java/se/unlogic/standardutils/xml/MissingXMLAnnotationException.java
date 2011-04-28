/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.xml;

public class MissingXMLAnnotationException extends RuntimeException {

	private static final long serialVersionUID = -5127582859037405055L;
	private Class<?> clazz;

	public MissingXMLAnnotationException(Class<?> clazz) {

		super("Class " + clazz + " is missing the XMLElement annotation and can therefore not be converted to an element");

		this.clazz = clazz;
	}

	public Class<?> getBeanClass() {
		return clazz;
	}
}
