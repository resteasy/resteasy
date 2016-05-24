/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.arrays;

public class ArrayUtils {

	public static <T> T[] toArray(T... values) {

		return values;
	}

	public static boolean isEmpty(Object[] array) {

		if(array == null || array.length == 0){
			
			return true;
		}
	
		for(Object value : array){
			
			if(value != null){
				
				return false;
			}
		}
		
		return true;
	}
}
