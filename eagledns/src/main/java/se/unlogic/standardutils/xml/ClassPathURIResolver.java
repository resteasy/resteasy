/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.xml;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;
import java.net.URL;


public class ClassPathURIResolver implements URIResolver {

	private static final ClassPathURIResolver CLASS_PATH_URI_RESOLVER = new ClassPathURIResolver();

	public static ClassPathURIResolver getInstance(){

		return CLASS_PATH_URI_RESOLVER;
	}

	public static final String PREFIX = "classpath://";

	public Source resolve(String href, String base) throws TransformerException {

		if(href.startsWith(PREFIX) && href.length() > PREFIX.length()){

			URL url = getURL(href);

			if(url != null){

				return new StreamSource(url.toString());
			}
			
			throw new TransformerException("Unable to resolve href " + href);
		}

		return null;
	}

	public static URL getURL(String href) {

		String classPath = "/" + href.substring(PREFIX.length());

		return ClassPathURIResolver.class.getResource(classPath);
	}
}
