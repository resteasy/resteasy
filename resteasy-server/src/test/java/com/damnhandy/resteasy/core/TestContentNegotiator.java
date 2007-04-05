/**
 * 
 */
package com.damnhandy.resteasy.core;

import junit.framework.Assert;

import org.junit.Test;

import com.damnhandy.resteasy.core.ContentNegotiator;


/**
 * @author ryan
 *
 */
public class TestContentNegotiator {

	@Test
	public void testCompareEqualQualityValues() {
		QualityValue xml1 = new QualityValue("application/xml",0.9f);
		QualityValue xml2 = new QualityValue("application/xml",0.9f);
		Assert.assertTrue(xml1.equals(xml2));
	}
	
	@Test
	public void testCompareDifferentQualityValues() {
		QualityValue xml1 = new QualityValue("application/xml",0.9f);
		QualityValue pdf1 = new QualityValue("application/pdf",0.9f);
		Assert.assertFalse(xml1.equals(pdf1));
	}
	
	@Test
	public void testCompareDifferentQualityValueHashCodes() {
		QualityValue xml1 = new QualityValue("application/xml",1.0f);
		QualityValue pdf1 = new QualityValue("application/pdf",1.0f);
		Assert.assertFalse(xml1.hashCode() == pdf1.hashCode());
	}
	
	@Test
	public void testParseAcceptHeader() throws Exception {
		String header = "text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5";
		QualityValue[] result = ContentNegotiator.parseAcceptHeader(header);
		Assert.assertTrue(result.length > 0);
	}
	
	/**
	 * No Accept header value is found and the defaults to the best server type
	 * @throws Exception
	 */
	@Test
	public void testNegotiateMediaTypeNoClientHeader() throws Exception {
		String serverValues = "application/foo;q=0.95,application/xml;q=0.9,application/json;q=0.8";
		QualityValue[] qualityOfSource = ContentNegotiator.parseAcceptHeader(serverValues);
		String header = null;
		String type = ContentNegotiator.negotiateMediaType(header, qualityOfSource);
		Assert.assertTrue(type.equals("application/foo"));
	}	
	
	/**
	 * Finds only 1 common type that both the client and server can agree on and uses that one
	 */
	@Test
	public void testNegotiateMediaTypeOneMatch() throws Exception {
		String serverValues = "application/foo;q=0.95,application/xml;q=0.9,application/json;q=0.8";
		QualityValue[] qualityOfSource = ContentNegotiator.parseAcceptHeader(serverValues);
		String header = "text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5";
		String type = ContentNegotiator.negotiateMediaType(header, qualityOfSource);
		Assert.assertTrue(type.equals("application/xml"));
	}
	
	/**
	 * Multiple matches are found and the server trie to select the most appropriate type
	 * @throws Exception
	 */
	@Test
	public void testNegotiateMediaTypeBestMatch() throws Exception {
		String serverValues = "application/xml;q=1.0,application/json;q=0.8,text/plain;q=0.01";
		QualityValue[] qualityOfSource = ContentNegotiator.parseAcceptHeader(serverValues);
		String header = "application/json;q=1.0,application/xml;q=0.7,text/plain;q=0.6,image/png,*/*;q=0.5";
		String type = ContentNegotiator.negotiateMediaType(header, qualityOfSource);
		Assert.assertTrue(type.equals("application/json"));
	}
}
