/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.xml;

import java.util.List;

public class ClassXMLInfo {

	private String elementName;
	private List<FieldXMLInfo> fields;

	public ClassXMLInfo(String elementName, List<FieldXMLInfo> field) {
		super();
		this.elementName = elementName;
		this.fields = field;
	}

	public String getElementName() {
		return elementName;
	}

	public List<FieldXMLInfo> getFields() {
		return fields;
	}
}
