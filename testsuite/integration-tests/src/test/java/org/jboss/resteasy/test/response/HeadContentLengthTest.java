/*
 * JBoss, Home of Professional Open Source
 * Copyright 2016, Red Hat Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.resteasy.test.response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.category.NotForForwardCompatibility;
import org.jboss.resteasy.test.response.resource.SimpleResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @tpSubChapter Parameters
 * @tpChapter Integration tests
 * @tpTestCaseDetails HEAD requests always return non-null Content-Length
 * @tpInfo RESTEASY-1365
 * @tpSince RESTEasy 3.0.19
 * @author Ivo Studensky
 */
@RunWith(Arquillian.class)
@RunAsClient
public class HeadContentLengthTest {

	static Client client;

	@Deployment
	public static Archive<?> deploy() throws Exception {
		WebArchive war = TestUtil.prepareArchive(HeadContentLengthTest.class.getSimpleName());
		return TestUtil.finishContainerPrepare(war, null, SimpleResource.class);
	}

	@BeforeClass
	public static void init() {
		client = ClientBuilder.newClient();
	}

	@AfterClass
	public static void after() throws Exception {
		client.close();
	}

	private String generateURL(String path) {
		return PortProviderUtil.generateURL(path, HeadContentLengthTest.class.getSimpleName());
	}

	/**
	 * @tpTestDetails HEAD requests always return non-null Content-Length
	 * @tpSince RESTEasy 3.0.19
	 */
	@Test
	@Category({NotForForwardCompatibility.class})
	public void testHeadContentLength() {
		Builder builder = client.target(generateURL("/simpleresource")).request();
		builder.accept(MediaType.TEXT_PLAIN_TYPE);

		Response getResponse = builder.get();
		String responseBody = getResponse.readEntity(String.class);
		Assert.assertEquals("The response body doesn't match the expected", "hello", responseBody);
		int getResponseLength = getResponse.getLength();
		Assert.assertEquals("The response length doesn't match the expected", 5, getResponseLength);

		Response headResponse = builder.head();
		int headResponseLength = headResponse.getLength();
		Assert.assertEquals("The response length from GET and HEAD request doesn't match" , getResponseLength, headResponseLength);
	}

}
