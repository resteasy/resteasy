/**
 * 
 */
package com.damnhandy.resteasy.criteria;

import java.util.HashSet;
import java.util.Set;

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
@XmlType(name = "exampleType")
public class EntityExample<V> {
	@XmlElement(required=true)
	private V entity;
	private Set<String> excludedProperties = new HashSet<String>();
	@XmlAttribute(required = true)
	private boolean isLikeEnabled;
	@XmlAttribute
	private Character escapeCharacter;
	@XmlAttribute(required = true)
	private boolean isIgnoreCaseEnabled;
	@XmlAttribute(required = true)
	private MatchMode matchMode;
	
	
	public EntityExample() {
		
	}
	/**
	 * @return the entity
	 */
	protected final V getEntity() {
		return entity;
	}
	/**
	 * @param entity the entity to set
	 */
	protected final void setEntity(final V entity) {
		this.entity = entity;
	}
	/**
	 * @return the escapeCharacter
	 */
	protected final Character getEscapeCharacter() {
		return escapeCharacter;
	}
	/**
	 * @param escapeCharacter the escapeCharacter to set
	 */
	protected final void setEscapeCharacter(final Character escapeCharacter) {
		this.escapeCharacter = escapeCharacter;
	}
	/**
	 * @return the excludedProperties
	 */
	protected final Set<String> getExcludedProperties() {
		return excludedProperties;
	}

	/**
	 * @return the isIgnoreCaseEnabled
	 */
	protected final boolean isIgnoreCaseEnabled() {
		return isIgnoreCaseEnabled;
	}
	/**
	 * @param isIgnoreCaseEnabled the isIgnoreCaseEnabled to set
	 */
	protected final void setIgnoreCaseEnabled(boolean isIgnoreCaseEnabled) {
		this.isIgnoreCaseEnabled = isIgnoreCaseEnabled;
	}
	/**
	 * @return the isLikeEnabled
	 */
	protected final boolean isLikeEnabled() {
		return isLikeEnabled;
	}
	/**
	 * @param isLikeEnabled the isLikeEnabled to set
	 */
	protected final void setLikeEnabled(final boolean isLikeEnabled) {
		this.isLikeEnabled = isLikeEnabled;
	}
	/**
	 * @return the matchMode
	 */
	protected final MatchMode getMatchMode() {
		return matchMode;
	}
	/**
	 * @param matchMode the matchMode to set
	 */
	protected final void setMatchMode(final MatchMode matchMode) {
		this.matchMode = matchMode;
	}
	
}
