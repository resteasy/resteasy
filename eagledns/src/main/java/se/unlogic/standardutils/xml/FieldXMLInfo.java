/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.xml;

import se.unlogic.standardutils.string.Stringyfier;

import java.lang.reflect.Field;

public class FieldXMLInfo {

	private final String name;
	private final Field field;
	private final FieldType fieldType;
	private final boolean cdata;
	private final boolean elementable;
	private final boolean list;
	private final boolean array;
	private final boolean skipSubElement;
	private final String childName;
	private final Stringyfier valueFormatter;

	public FieldXMLInfo(String name, Field field, FieldType fieldType, boolean cdata, boolean elementable, boolean list, boolean array, String childName, boolean skipSubElement, Stringyfier valueFormatter) {

		super();
		this.name = name;
		this.field = field;
		this.fieldType = fieldType;
		this.cdata = cdata;
		this.elementable = elementable;
		this.list = list;
		this.array = array;
		this.childName = childName;
		this.valueFormatter = valueFormatter;
		this.skipSubElement = skipSubElement;
	}

	public String getName() {

		return name;
	}

	public Field getField() {

		return field;
	}

	public FieldType getFieldType() {

		return fieldType;
	}

	public boolean isCDATA() {

		return cdata;
	}

	public boolean isList() {

		return list;
	}

	public boolean isElementable() {

		return elementable;
	}

	public String getChildName() {

		return childName;
	}

	public Stringyfier getValueFormatter() {

		return valueFormatter;
	}

	
	public boolean skipSubElement() {
	
		return skipSubElement;
	}

	
	public boolean isArray() {
	
		return array;
	}
}
