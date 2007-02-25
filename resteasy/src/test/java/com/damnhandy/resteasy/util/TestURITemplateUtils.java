/**
 * 
 */
package com.damnhandy.resteasy.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.damnhandy.resteasy.helper.URITemplateHelper;

/**
 * @author Ryan J. McDonough
 * Feb 23, 2007
 *
 */
public class TestURITemplateUtils {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link com.damnhandy.resteasy.helper.URITemplateHelper#extractURLTemplateNames(java.lang.String)}.
	 */
	@Test
	public void testExtractURLTemplateNames() {
		String path = "/contacts/{contactId}";
		Map<String,Integer> names = URITemplateHelper.extractURLTemplateNames(path);
		Assert.assertTrue(names.size() == 1);
		Assert.assertTrue(names.get("contactId").equals(new Integer(1)));
	}

	/**
	 * Test method for {@link com.damnhandy.resteasy.helper.URITemplateHelper#replaceURLTemplateIDs(java.lang.String)}.
	 */
	@Test
	public void testReplaceURLTemplateIDs() {
		Map<String,Class<?>> types = new HashMap<String,Class<?>>();
		types.put("contactId", String.class);
		String path = "/contacts/{contactId}";
		String result = URITemplateHelper.replaceURLTemplateIDs(path,types);
		System.out.println("Revised Path: "+result);
		Assert.assertFalse(path.equals(result));
	}
	
	@Test
	public void testComplexReplaceURLTemplateIDs() {
		Map<String,Class<?>> types = new HashMap<String,Class<?>>();
		types.put("contactId", Integer.class);
		types.put("addressId", Long.class);
		String path = "/contacts/{contactId}/addresses/{addressId}";
		String result = URITemplateHelper.replaceURLTemplateIDs(path,types);
		System.out.println("Revised Path: "+result);
		Assert.assertFalse(path.equals(result));
	}

	/**
	 * Test method for {@link com.damnhandy.resteasy.helper.URITemplateHelper#extractURLParameterValues(java.lang.String, java.util.regex.Pattern)}.
	 */
	@Test
	public void testExtractURLParameterValues() {
		Map<String,Class<?>> types = new HashMap<String,Class<?>>();
		types.put("contactId", Integer.class);
		types.put("addressId", Long.class);
		String path = "/contacts/{contactId}/addresses/{addressId}";
		String requestedPath = "/contacts/33445/addresses/12";
		String result = URITemplateHelper.replaceURLTemplateIDs(path,types);
		Assert.assertTrue("/contacts/(\\d+)/addresses/(\\d+)$".equals(result));
		Map<Integer,String> positions = URITemplateHelper.extractURLParameterValues(requestedPath, Pattern.compile(result));
		Assert.assertFalse(positions.isEmpty());
		Assert.assertTrue(positions.get(new Integer(1)).equals("33445"));
		Assert.assertTrue(positions.get(new Integer(2)).equals("12"));
	}
	
	/**
	 * Test method for {@link com.damnhandy.resteasy.helper.URITemplateHelper#extractURLParameterValues(java.lang.String, java.util.regex.Pattern)}.
	 */
	@Test
	public void testExtractURLParameterValuesMixedTypes() {
		Map<String,Class<?>> types = new HashMap<String,Class<?>>();
		types.put("contactId", Integer.class);
		types.put("addressId", String.class);
		String path = "/contacts/{contactId}/addresses/{addressId}";
		String requestedPath = "/contacts/33445/addresses/12";
		String result = URITemplateHelper.replaceURLTemplateIDs(path,types);
		Assert.assertTrue("/contacts/(\\d+)/addresses/([^}]+)$".equals(result));
		Map<Integer,String> positions = URITemplateHelper.extractURLParameterValues(requestedPath, Pattern.compile(result));
		Assert.assertFalse(positions.isEmpty());
		Assert.assertTrue(positions.get(new Integer(1)).equals("33445"));
		Assert.assertTrue(positions.get(new Integer(2)).equals("12"));
	}

}
