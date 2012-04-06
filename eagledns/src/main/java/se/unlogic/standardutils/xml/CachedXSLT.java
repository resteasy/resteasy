/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.xml;

import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;

/**
 * An interface that is meant to wrap the raw {@link Templates} object and provide extra functionality such as reloading the stylesheet.
 * 
 * @author Robert "Unlogic" Olofsson (unlogic@unlogic.se)
 *
 */
public interface CachedXSLT {

	public void reloadStyleSheet() throws TransformerConfigurationException;
	public Transformer getTransformer() throws TransformerConfigurationException;
}
