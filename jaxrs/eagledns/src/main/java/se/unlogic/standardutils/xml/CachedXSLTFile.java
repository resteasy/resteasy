/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.xml;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;
import java.io.File;

public class CachedXSLTFile extends CachedXSLTBase{

	private File file;
	private URIResolver uriResolver;

	public void reloadStyleSheet() throws TransformerConfigurationException{
		this.cacheStyleSheet(file);
	}

	public CachedXSLTFile(File f) throws TransformerConfigurationException{
		this.file = f;
		this.cacheStyleSheet(f);
	}

	public CachedXSLTFile(File file, URIResolver uriResolver) throws TransformerConfigurationException {

		this.file =  file;
		this.uriResolver = uriResolver;
		this.cacheStyleSheet(file);
	}

	private void cacheStyleSheet(File f) throws TransformerConfigurationException{
		Source xsltSource = new StreamSource(f);
		TransformerFactory transFact = TransformerFactory.newInstance();

		if(uriResolver != null){
			transFact.setURIResolver(uriResolver);
		}

		this.templates = transFact.newTemplates(xsltSource);
		this.file = f;
	}

	@Override
	public String toString() {

		return "CachedXSLTFile: " + file;
	}
}
