/*
 * Hibernate Validator, declare and validate application constraints
 *
 * License: Apache License, Version 2.0
 * See the license.txt file in the root directory or <http://www.apache.org/licenses/LICENSE-2.0>.
 */
package org.jboss.resteasy.test.resteasy1296;

import static org.junit.Assert.assertTrue;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.resteasy1296.HikeResource;
import org.jboss.resteasy.resteasy1296.HikingApplication;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * RESTEASY-1296
 * 
 * @author Gunnar Morling
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 */
@RunWith(Arquillian.class)
public class ValidationThroughRestIT {

	private static final String WAR_FILE_NAME = ValidationThroughRestIT.class.getSimpleName() + ".war";

	@Deployment(testable=false)
	public static Archive<?> createTestArchive() {
		return ShrinkWrap.create( WebArchive.class, WAR_FILE_NAME )
				.addClasses( HikingApplication.class )
				.addClasses( HikeResource.class )
				.addAsWebInfResource( EmptyAsset.INSTANCE, "beans.xml" );
	}

	@Test
	public void validationOfFieldAndParameterOfEjbResource() {
      Client client = ClientBuilder.newClient();
      Builder builder = client.target("http://localhost:8080:/ValidationThroughRestIT/hiking-manager/hikes/createHike").request();
      builder.accept(MediaType.TEXT_PLAIN_TYPE);
      Response response = builder.post(Entity.entity("-1", MediaType.APPLICATION_JSON_TYPE));
      String responseBody = response.readEntity(String.class);
		System.out.println( "response: " + responseBody );
		assertTrue( responseBody.contains( "must be greater than or equal to 1" ) );
		assertTrue( responseBody.contains( "may not be null" ) );
	}
}
