/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.validation;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Arrays;
import java.util.List;

public class ValidationException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1221745166857804542L;
	private final List<ValidationError> errors;

	public ValidationException(List<ValidationError> errors) {
		super();

		if (errors == null) {
			throw new NullPointerException();
		}

		this.errors = errors;
	}

	public ValidationException(ValidationError... errors) {
		super();

		if (errors == null) {
			throw new NullPointerException();
		}

		this.errors = Arrays.asList(errors);
	}

	public List<ValidationError> getErrors() {
		return errors;
	}

	public final Element toXML(Document doc) {
		Element validationException = doc.createElement("validationException");

		for (ValidationError validationError : errors) {
			if (validationError != null) {
				validationException.appendChild(validationError.toXML(doc));
			}
		}

		return validationException;
	}
}
