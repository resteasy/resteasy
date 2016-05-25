/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.enums;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class EnumUtils {

	public static <Type extends Enum<Type>> boolean isEnum(Class<Type> e, String s) {
		try {
			Enum.valueOf(e, s);
			return true;
		} catch (IllegalArgumentException ex) {
			return false;
		} catch (NullPointerException ex) {
			return false;
		}
	}

	public static <Type extends Enum<Type>> Type toEnum(Class<Type> e, String s) {
		try {
			return Type.valueOf(e, s);
		} catch (IllegalArgumentException ex) {
			return null;
		} catch (NullPointerException ex) {
			return null;
		}
	}

	public static <Type extends Enum<Type>> ArrayList<Type> toEnum(Class<Type> e, String[] values) {

		ArrayList<Type> enumValues = new ArrayList<Type>();

		for (String value : values) {

			Type enumValue = toEnum(e, value);

			if (enumValue != null) {
				enumValues.add(enumValue);
			}
		}

		if (enumValues.isEmpty()) {
			return null;
		} else {
			return enumValues;
		}
	}

	public static <Type extends Enum<Type>> Type toEnum(Type[] es, int ordinal) {

		for (Type typeValue : es) {

			if (typeValue.ordinal() == ordinal) {

				return typeValue;
			}
		}

		return null;
	}

	public static Enum<?> getInstanceFromField(Field field) {

		Object[] enumValues = field.getType().getEnumConstants();

		return (Enum<?>) enumValues[0];
	}

	public static Enum<?>[] getValuesFromField(Field field) {

		Object[] enumValues = field.getType().getEnumConstants();

		return (Enum[]) enumValues;
	}
}
