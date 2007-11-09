/**
 * 
 */
package com.damnhandy.resteasy.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;
import com.damnhandy.resteasy.core.ResourceDispatcher;
import com.damnhandy.resteasy.core.ResourceInvoker;
import com.damnhandy.resteasy.test.mock.MockHttpServletResponse;
import com.damnhandy.resteasy.test.mock.MockHttpServletRequest;

/**
 * @author Ryan J. McDonough
 * Jan 15, 2007
 *
 */
public class TestResourceDispatcher {

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

    @Test
    public void testResourceInvocation() throws Exception {
        ResourceDispatcher dispatcher = ResourceDispatcher.getInstance();
        dispatcher.processClass(DummyResource.class);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/dummy");
        request.addParameter("echo", "hello world");
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setPathInfo("/dummy");
        ResourceInvoker invoker = ResourceDispatcher.getInstance().findResourceInvoker(request.getPathInfo());
        Assert.assertNotNull(invoker);
        invoker.invoke(request, response);



    }

}
