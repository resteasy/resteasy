/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.xsl;

import org.xml.sax.SAXException;
import se.unlogic.standardutils.settings.XMLSettingNode;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XSLUtils {

	private static Pattern XSL_VARIABLE_PATTERN = Pattern.compile("(?<=\\$)[\\w\\.]*(?=($|[\\W]))");

	/**
	 * Scans XSL documents for references to XSL variables such as {@literal <}xsl:value-of select="$foo"/{@literal>} and {@literal <}a href="{$foo}"/{@literal >}. Returns the variable names.
	 * 
	 * @param file Input XSL file
	 * @return
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	public static Set<String> getVariableReferenses(File file) throws SAXException, IOException, ParserConfigurationException {

		XMLSettingNode settingNode = new XMLSettingNode(file);

		List<String> tags = settingNode.getStrings("//@*[contains(.,'$')]");

		if(tags == null){
			
			return null;
		}
		
		LinkedHashSet<String> stringSet = new LinkedHashSet<String>();

		for (String tag : tags) {

			Matcher matcher = XSL_VARIABLE_PATTERN.matcher(tag);

			while (matcher.find()) {

				stringSet.add(matcher.group());
			}
		}

		return stringSet;
	}

	/**
	 * Scans XSL documents for declared XSL variables such as {@literal <}xsl:variable name="myvariable"/{@literal>}
	 * 
	 * @param file Input XSL file
	 * @return
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	public static List<String> getDeclaredVariables(File file) throws SAXException, IOException, ParserConfigurationException {

		XMLSettingNode settingNode = new XMLSettingNode(file);

		return settingNode.getStrings("//variable/@name");
	}
}
