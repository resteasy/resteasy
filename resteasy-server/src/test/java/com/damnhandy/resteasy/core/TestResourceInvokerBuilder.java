/**
 * 
 */
package com.damnhandy.resteasy.core;

import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.damnhandy.resteasy.annotations.HttpMethod;
import com.damnhandy.resteasy.annotations.WebResource;

/**
 * @author Ryan J. McDonough
 * Mar 30, 2007
 *
 */
public class TestResourceInvokerBuilder {

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
	 * Test method for {@link com.damnhandy.resteasy.core.ResourceInvokerBuilder#createResourceInvoker(java.lang.Class, com.damnhandy.resteasy.annotations.WebResource)}.
	 */
	@Test
	public void testCreateResourceInvoker() {
		Class<?> resourceClass = DummyResource.class;
		WebResource webResource = resourceClass.getAnnotation(WebResource.class);
		ResourceInvoker invoker = ResourceInvokerBuilder.createResourceInvoker(resourceClass,webResource);
		Assert.assertTrue(invoker.getTargetClass().equals(resourceClass));
		Assert.assertTrue(invoker.getMethods().size() == 4);
		MethodKey key = new MethodKey(HttpMethod.POST,null,"application/xml","application/xml");
		MethodMapping postMethod = invoker.getMethods().get(key);
		Assert.assertTrue(postMethod.getResponseMediaType().equals("application/xml"));
		Assert.assertTrue(postMethod.getMethod().getName().equals("updateFoo"));
	}

	/**
	 * Test method for {@link com.damnhandy.resteasy.core.ResourceInvokerBuilder#createEntityResourceInvoker(java.lang.Class, java.lang.Class, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testCreateEntityResourceInvoker() {
		//fail("Not yet implemented");
	}

}
