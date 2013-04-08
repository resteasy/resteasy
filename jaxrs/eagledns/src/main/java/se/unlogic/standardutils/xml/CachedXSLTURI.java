/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.xml;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;
import java.net.URI;

public class CachedXSLTURI extends CachedXSLTBase {

	private URI uri;
	private URIResolver uriResolver;

	public CachedXSLTURI(URI uri) throws TransformerConfigurationException {
		super();
		this.uri = uri;
		this.reloadStyleSheet();
	}

	public CachedXSLTURI(URI uri, URIResolver uriResolver) throws TransformerConfigurationException {

		this.uri = uri;
		this.uriResolver = uriResolver;
		this.reloadStyleSheet();
	}

	public void reloadStyleSheet() throws TransformerConfigurationException {

		TransformerFactory transFact = TransformerFactory.newInstance();

		if(uriResolver != null){
			transFact.setURIResolver(uriResolver);
		}

		this.templates = transFact.newTemplates(new StreamSource(uri.toString()));
	}

	@Override
	public String toString() {

		return "CachedXSLTURI: " + uri;
	}
}
