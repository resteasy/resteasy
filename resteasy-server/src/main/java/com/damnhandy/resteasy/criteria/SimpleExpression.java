/**
 * 
 */
package com.damnhandy.resteasy.criteria;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Ryan J. McDonough
 * Feb 6, 2007
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "simpleExpression")
public class SimpleExpression {
	@XmlElement(required=true)
	private String propertyName;
	@XmlElement(required=true)
	private Object value;
	@XmlAttribute(required=true)
	private boolean ignoreCase;
	@XmlAttribute(required=true)
	private String operation;
	
	public SimpleExpression() {}
	
	/**
	 * @param propertyName
	 * @param value
	 * @param ignoreCase
	 * @param operation
	 */
	public SimpleExpression(final String propertyName, final Object value, boolean ignoreCase) {
		this(propertyName,value,ignoreCase,"=");
	}
	
	
	/**
	 * @param propertyName
	 * @param value
	 * @param ignoreCase
	 * @param op
	 */
	public SimpleExpression(final String propertyName, final Object value, boolean ignoreCase, final String op) {
		this.propertyName = propertyName;
		this.value = value;
		this.ignoreCase = ignoreCase;
		this.operation = op;
	}

	/**
	 * @return the ignoreCase
	 */
	protected final boolean isIgnoreCase() {
		return ignoreCase;
	}

	/**
	 * @param ignoreCase the ignoreCase to set
	 */
	protected final void setIgnoreCase(final boolean ignoreCase) {
		this.ignoreCase = ignoreCase;
	}

	/**
	 * @return the op
	 */
	protected final String getOperation() {
		return operation;
	}

	/**
	 * @param op the op to set
	 */
	protected final void setOperation(final String op) {
		this.operation = op;
	}

	/**
	 * @return the propertyName
	 */
	protected final String getPropertyName() {
		return propertyName;
	}

	/**
	 * @param propertyName the propertyName to set
	 */
	protected final void setPropertyName(final String propertyName) {
		this.propertyName = propertyName;
	}

	/**
	 * @return the value
	 */
	protected final Object getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	protected final void setValue(final Object value) {
		this.value = value;
	}
	
	public void toQueryString(StringBuilder builder) {
		builder.append(getPropertyName()).append(" ").append(getOperation());
		builder.append(getValue());
	}
	
}
