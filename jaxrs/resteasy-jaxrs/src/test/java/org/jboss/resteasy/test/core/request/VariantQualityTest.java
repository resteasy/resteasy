package org.jboss.resteasy.test.core.request;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

import org.jboss.resteasy.core.request.QualityValue;
import org.jboss.resteasy.core.request.VariantQuality;


/**
 @author Pascal S. de Kloe
 */
public class VariantQualityTest {

	@Test
	public void defaultQuality() {
		VariantQuality q = new VariantQuality();
		assertEquals(new BigDecimal("1.00000"), q.getOverallQuality());
		q.setMediaTypeQualityValue(null);
		q.setCharacterSetQualityValue(null);
		q.setEncodingQualityValue(null);
		q.setLanguageQualityValue(null);
		assertEquals(new BigDecimal("1.00000"), q.getOverallQuality());
	}


	@Test
	public void qualitySetters() {
		VariantQuality q = new VariantQuality();
		q.setMediaTypeQualityValue(QualityValue.valueOf("0.1"));
		assertEquals(new BigDecimal("0.10000"), q.getOverallQuality());
		q.setCharacterSetQualityValue(QualityValue.valueOf("0.2"));
		assertEquals(new BigDecimal("0.02000"), q.getOverallQuality());
		q.setEncodingQualityValue(QualityValue.valueOf("0.4"));
		assertEquals(new BigDecimal("0.00800"), q.getOverallQuality());
		q.setLanguageQualityValue(QualityValue.valueOf("0.8"));
		assertEquals(new BigDecimal("0.00640"), q.getOverallQuality());
	}


	@Test
	public void round5() {
		VariantQuality q = new VariantQuality();
		q.setMediaTypeQualityValue(QualityValue.valueOf("0.004"));
		q.setEncodingQualityValue(QualityValue.valueOf("0.008"));
		assertEquals(new BigDecimal("0.00003"), q.getOverallQuality());
	}

}
