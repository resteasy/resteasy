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
package org.jboss.resteasy.test.resteasy1365;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.resteasy1365.HeadContentLengthApplication;
import org.jboss.resteasy.resteasy1365.SimpleResource;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


/**
 * RESTEASY-1365
 *
 * @author Ivo Studensky
 */
@RunWith(Arquillian.class)
public class HeadContentLengthTest {

	private static final String WAR_FILE_NAME = HeadContentLengthTest.class.getSimpleName() + ".war";

	@Deployment(testable=false)
	public static Archive<?> createTestArchive() {
		return ShrinkWrap.create( WebArchive.class, WAR_FILE_NAME )
				.addClasses( HeadContentLengthApplication.class )
				.addClasses( SimpleResource.class )
				.addAsWebInfResource( EmptyAsset.INSTANCE, "beans.xml" );
	}

	@Test
	public void testHeadContentLength() {
		Client client = ClientBuilder.newClient();
		Builder builder = client.target("http://localhost:8080:/HeadContentLengthTest/headcontentlength/simpleresource").request();
		builder.accept(MediaType.TEXT_PLAIN_TYPE);

		Response getResponse = builder.get();
		String responseBody = getResponse.readEntity(String.class);
		Assert.assertEquals("hello", responseBody);
		int getResponseLength = getResponse.getLength();
		Assert.assertEquals(5, getResponseLength);

		Response headResponse = builder.head();
		int headResponseLength = headResponse.getLength();
		Assert.assertEquals(getResponseLength, headResponseLength);
	}

}
