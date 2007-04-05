package com.damnhandy.resteasy.core;

/**
 * Utility class
 * 
 * @author Ryan J. McDonough
 * @since 1.0
 *
 */
public class QualityValue implements Comparable<QualityValue> {
	private String mediaType;
	private float factor = 0.8f;
	private int hashCode = -1;

	/**
	 * 
	 * @param mediaType
	 * @param factor
	 */
	public QualityValue(String mediaType, float factor) {
		this.mediaType = mediaType;
		this.factor = factor;
	}
	
	
	/**
	 * @return the factor
	 */
	public float getFactor() {
		return factor;
	}

	/**
	 * @return the mediaType
	 */
	public String getMediaType() {
		return mediaType;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append(mediaType).append(":").append(factor);
		return b.toString();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		if(hashCode == -1) {
			final int PRIME = 31;
			hashCode = super.hashCode();
			hashCode = PRIME * hashCode + Float.floatToIntBits(factor);
			hashCode = PRIME * hashCode + ((mediaType == null) ? 0 : mediaType.hashCode());
		}
		return hashCode;
	}
	
	@Override
	public boolean equals(Object object) {
		if(object == null) {
			return false;
		}
		if(object == this) {
			return true;
		}
		if(object instanceof QualityValue) {
			final QualityValue qFactor = (QualityValue) object;
			return Float.floatToIntBits(qFactor.factor) == Float.floatToIntBits(factor) && 
				   qFactor.mediaType.equals(this.mediaType);
		}
		return false;
	}
	/*
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(QualityValue other) {
		if (this.factor > other.factor) {
			return -1;
		}
			
		if (this.factor < other.factor) {
			return 1;
		}
		return 0;
	}


}