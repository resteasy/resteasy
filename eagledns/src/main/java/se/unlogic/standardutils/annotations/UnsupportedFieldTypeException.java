/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.annotations;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class UnsupportedFieldTypeException extends RuntimeException {

	private static final long serialVersionUID = -6723843186067887845L;
	private final Class<?> beanClass;
	private final Class<? extends Annotation> annotation;
	private final Field field;

	public UnsupportedFieldTypeException(String message, Field field, Class<? extends Annotation> annotation, Class<?> beanClass) {
		super(message);

		this.beanClass = beanClass;
		this.annotation = annotation;
		this.field = field;
	}

	public Class<?> getBeanClass() {
		return beanClass;
	}

	public Field getField() {
		return field;
	}

	public Class<? extends Annotation> getAnnotation() {
		return annotation;
	}
}
