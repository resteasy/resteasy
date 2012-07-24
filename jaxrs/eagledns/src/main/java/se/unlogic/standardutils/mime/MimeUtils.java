/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.mime;

import se.unlogic.standardutils.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.InvalidPropertiesFormatException;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

public class MimeUtils {

	private static String UnknownMimeType = "application/x-unknown";
	private static Properties MimeTypes = new Properties();;

	static{
		try {
			MimeTypes.load(MimeUtils.class.getResourceAsStream("mimetypes.properties"));
		} catch (IOException e) {}
	}

	public static String getMimeType(File file){
		return getMimeType(file.getName());
	}

	public static String getMimeType(String filename){

		String fileExtension = FileUtils.getFileExtension(filename);

		if(fileExtension == null){
			return UnknownMimeType;
		}else{
			return MimeTypes.getProperty(fileExtension.toLowerCase(),UnknownMimeType);
		}
	}

	public static int getMimeTypeCount(){
		return MimeTypes.size();
	}

	public static Set<Entry<Object, Object>> getMimeTypes(){
		return MimeTypes.entrySet();
	}

	public static void loadMimeTypes(InputStream inputStream) throws IOException{
		MimeTypes.clear();
		MimeTypes.load(inputStream);
	}

	/* JDK 1.6 only!
	public void loadMimeTypes(Reader reader) throws IOException {
		MimeTypes.load(reader);
	}
	*/

	public void loadMimeTypesFromXML(InputStream in) throws IOException, InvalidPropertiesFormatException {
		MimeTypes.loadFromXML(in);
	}
}
