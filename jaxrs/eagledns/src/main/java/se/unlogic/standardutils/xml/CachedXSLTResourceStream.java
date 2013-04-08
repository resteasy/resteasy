/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.xml;

import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.InputStream;

public class CachedXSLTResourceStream extends CachedXSLTBase{

	private Class<?> resourceClass;
	private String path;

	public CachedXSLTResourceStream(Class<?> resourceClass, String path) throws TransformerConfigurationException {
		this.cacheStyleSheet(resourceClass, path);
	}

	public void reloadStyleSheet() throws TransformerConfigurationException {
		this.cacheStyleSheet(this.resourceClass,this.path);
	}

	private void cacheStyleSheet(Class<?> resourceClass, String path) throws TransformerConfigurationException{
		InputStream inputStream = resourceClass.getResourceAsStream(path);
		Source xsltSource = new StreamSource(inputStream);
		TransformerFactory transFact = TransformerFactory.newInstance();
		Templates templates = transFact.newTemplates(xsltSource);

		try {inputStream.close();} catch (IOException e) {}

		this.templates = templates;
		this.resourceClass = resourceClass;
		this.path = path;
	}

	@Override
	public String toString() {

		return "CachedXSLTResourceStream: " + resourceClass + " " + path;
	}
}
