/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.random;

import java.util.Random;

public class RandomUtils {

	private static final Random RANDOM = new Random();

	public static String getRandomString(int minLength, int maxLength){

		int length;

		if(minLength == maxLength){

			length = minLength;

		}else{

			length = RANDOM.nextInt(maxLength - minLength) + minLength;
		}

		char[] randomString = new char[length];

		for (int x = 0; x < length; x++) {
			int randDecimalAsciiVal = RANDOM.nextInt(25) + 97;
			randomString[x] = (char) randDecimalAsciiVal;
		}

		return new String(randomString);
	}

	public static int getRandomInt(int min, int max) {

		return RANDOM.nextInt(max) + min;
	}

	public static boolean getRandomBoolean() {

		return getRandomInt(0, 2) == 1;
	}
}
