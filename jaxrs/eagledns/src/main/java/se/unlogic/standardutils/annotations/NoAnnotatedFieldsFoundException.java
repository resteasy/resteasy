/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.annotations;

import java.lang.annotation.Annotation;

public class NoAnnotatedFieldsFoundException extends RuntimeException {

	private static final long serialVersionUID = 5295557583550461676L;
	private final Class<?> beanClass;
	private final Class<? extends Annotation>[] annotations;

	public NoAnnotatedFieldsFoundException(Class<?> beanClass,Class<? extends Annotation>... annotations) {

		super("No annotated fields found in class " + beanClass + " with annotations " + annotations);

		this.beanClass = beanClass;
		this.annotations = annotations;
	}

	public Class<?> getBeanClass() {
		return beanClass;
	}

	public Class<? extends Annotation>[] getAnnotations() {
		return annotations;
	}
}
