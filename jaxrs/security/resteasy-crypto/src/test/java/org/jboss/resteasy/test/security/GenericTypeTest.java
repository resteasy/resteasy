package org.jboss.resteasy.test.security;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.GenericType;

import org.jboss.resteasy.security.smime.PKCS7SignatureInput;
import org.junit.Test;

import junit.framework.Assert;

/**
 * Unit tests for RESTEASY-1260.
 * 
 * @author <a href="mailto:ron.sigal@jboss.com">Ron Sigal</a>
 * @date January 18, 2016
 */
public class GenericTypeTest
{
	@Test
	public void testGenericType() throws Exception
	{
		GenericType<List<String>> stringListType = new GenericType<List<String>>() {};
		System.out.println("type: " + stringListType.getType());
		System.out.println("raw type: " + stringListType.getRawType());
		PKCS7SignatureInput<List<String>> input = new PKCS7SignatureInput<List<String>>();
		input.setType(stringListType);
		Field field = PKCS7SignatureInput.class.getDeclaredField("entity");
		field.setAccessible(true);
		List<String> list = new ArrayList<String>();
		list.add("abc");
		field.set(input, list);
		List<String> list2 = input.getEntity(stringListType, null);
		System.out.println("list2: " + list2);
		Assert.assertEquals(list, list2);
	}
}
