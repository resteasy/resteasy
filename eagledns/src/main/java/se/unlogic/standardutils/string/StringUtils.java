/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.string;

import se.unlogic.standardutils.reflection.ReflectionUtils;
import se.unlogic.standardutils.streams.StreamUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.InvalidPropertiesFormatException;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.UUID;

public class StringUtils {

	private static Properties LATIN1_ESCAPE_CHARACTERS = null;

	static {
		LATIN1_ESCAPE_CHARACTERS = new Properties();

		InputStream inputStream = StringUtils.class.getResourceAsStream("latin-1-escaped-character-set.properties");

		try {
			LATIN1_ESCAPE_CHARACTERS.load(inputStream);
		} catch (InvalidPropertiesFormatException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			StreamUtils.closeStream(inputStream);
		}
	}

	/**
	 * Unescapes escaped HTML characters
	 * 
	 * Based on a fixed set of escaped HTML characters from the latin-1 set
	 * 
	 * @param sequence
	 *            - the secuence of characters to unescape characters in
	 * @return - a sequenced without escaped HTML characters
	 */
	public static String unEscapeHTML(String sequence) {

		return replaceCharacters(sequence, LATIN1_ESCAPE_CHARACTERS);
	}

	/**
	 * Replaces characters in a character sequence
	 * 
	 * @param sequence
	 *            - the secuence of characters to replace characters in
	 * @param characterSet
	 *            - a set of key=value character mappings, e.g. &num;=#. Occurences of key, e.g. "&num;" will be replaced by value, e.g. "#"
	 * 
	 * @return - a sequenced with replaced characters
	 */
	public static String replaceCharacters(String sequence, Properties characterSet) {

		if (characterSet == null) {
			characterSet = LATIN1_ESCAPE_CHARACTERS;
		}

		for (Entry<Object, Object> entry : characterSet.entrySet()) {
			sequence = sequence.replaceAll(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
		}

		return sequence;
	}

	public static String removeHTMLTags(String sequence) {

		return sequence.replaceAll("\\<.*?>", "");
	}

	public static String parseUTF8(String encodedString)  {

		return new String(encodedString.getBytes(), StandardCharsets.UTF_8);
	}

	//TODO move
	public static boolean isValidUUID(String uuidstring) {

		try {
			UUID.fromString(uuidstring);
			return true;
		} catch (IllegalArgumentException e) {
			return false;
		}
	}

	//TODO move
	public static boolean isValidURL(String urlstring) {

		try {
			new URL(urlstring);
			return true;
		} catch (MalformedURLException e) {
			return false;
		}
	}

	public static String toQuotedCommaSeparatedString(List<? extends Object> list) {

		String arrayString = Arrays.deepToString(list.toArray());
		arrayString = arrayString.substring(1, arrayString.length() - 1);
		arrayString = "\"" + arrayString.replaceAll(", ", "\", \"") + "\"";
		return arrayString;
	}

	public static String toQuotedCommaSeparatedString(Object[] array) {

		String arrayString = Arrays.deepToString(array);
		arrayString = arrayString.substring(1, arrayString.length() - 1);
		arrayString = "\"" + arrayString.replaceAll(", ", "\", \"") + "\"";
		return arrayString;
	}

	public static String toCommaSeparatedString(Collection<? extends Object> list) {

		String arrayString = Arrays.deepToString(list.toArray());
		return arrayString.substring(1, arrayString.length() - 1);
	}

	public static String toCommaSeparatedString(Object[] array) {

		String arrayString = Arrays.deepToString(array);
		return arrayString.substring(1, arrayString.length() - 1);
	}

	public static String toCommaSeparatedString(List<? extends Object> list, Field field) throws IllegalArgumentException, IllegalAccessException {

		ReflectionUtils.fixFieldAccess(field);

		StringBuilder arrayString = new StringBuilder();

		for (int i = 0; i < list.size(); i++) {

			Object value = field.get(list.get(i));

			arrayString.append(value + ",");

		}

		return arrayString.substring(0, arrayString.length() - 1);

	}

	public static String readFileAsString(String filePath) throws java.io.IOException {

		StringBuffer fileData = new StringBuffer(1000);
		BufferedReader reader = new BufferedReader(new FileReader(filePath));
		char[] buf = new char[1024];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			fileData.append(buf, 0, numRead);
		}
		reader.close();
		return fileData.toString();
	}

	public static String readFileAsString(File file) throws java.io.IOException {

		StringBuffer fileData = new StringBuffer(1000);
		BufferedReader reader = new BufferedReader(new FileReader(file));
		char[] buf = new char[1024];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			fileData.append(buf, 0, numRead);
		}
		reader.close();
		return fileData.toString();
	}

	public static String readStreamAsString(InputStream inputStream) throws java.io.IOException {

		StringBuilder fileData = new StringBuilder(1000);
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		char[] buf = new char[1024];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			fileData.append(buf, 0, numRead);
		}
		reader.close();
		return fileData.toString();
	}

	public static boolean isEmpty(String string) {

		if (string == null) {
			return true;
		} else if (string.trim().length() == 0) {
			return true;
		} else {
			return false;
		}
	}

	public static String substring(String string, int maxChars, String suffix) {

		if (string.length() > maxChars) {
			return string.substring(0, maxChars - 1) + suffix;
		}

		return string;
	}

	public static String toLogFormat(String string, int maxLength) {

		if (string != null) {

			String returnString = substring(string, maxLength, "...");

			return returnString.replace("\n", " ").replace("\r", " ").replace("\t", " ");
		}

		return string;
	}

	public static String toSentenceCase(String string) {

		return string.substring(0, 1).toUpperCase() + string.toLowerCase().substring(1);
	}

	public static String toFirstLetterUppercase(String string) {

		return string.substring(0, 1).toUpperCase() + string.substring(1);
	}

	public static String toFirstLetterLowercase(String string) {

		return string.substring(0, 1).toLowerCase() + string.substring(1);
	}	
	
	public static String repeatString(String string, int repetitionCount) {

		StringBuilder stringBuilder = new StringBuilder();

		repeatString(string, repetitionCount, stringBuilder);
		
		return stringBuilder.toString();
	}

	public static void repeatString(String string, int repetitionCount, StringBuilder stringBuilder) {

		if (repetitionCount >= 1) {

			for (int i = 1; i <= repetitionCount; i++) {

				stringBuilder.append(string);
			}
		}
	}
	
	/**
	 *  Takes a string and splits it on linebreaks (\n) and also removes any linebreak (\n) and carriage return (\r) characters from the string segments
	 * 
	 * @param string the string to be split
	 * @return String array containing the split segments of the string
	 */
	public static ArrayList<String> splitOnLineBreak(String string, boolean removeDuplicates){
		
		if(!StringUtils.isEmpty(string)){

			ArrayList<String> stringList = new ArrayList<String>();

			String[] lines = string.split("\n");

			for(String line : lines){

				line = line.replace("\n", "");
				line = line.replace("\r", "");

				if(!StringUtils.isEmpty(line) && (!removeDuplicates || !stringList.contains(line))){

					stringList.add(line);
				}
			}

			if(!stringList.isEmpty()){

				return stringList;
			}
		}

		return null;
	}
	
	public static int countOccurrences(String string, String snippet) {
        
		int count = 0;
        int index = 0;
        
        while ((index = string.indexOf(snippet, index)) != -1) {
             ++index;
             ++count;
        }
        
        return count;
   }	
}
