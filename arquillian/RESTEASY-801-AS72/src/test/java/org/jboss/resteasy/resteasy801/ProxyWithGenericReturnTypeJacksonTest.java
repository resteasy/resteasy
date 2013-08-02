package org.jboss.resteasy.resteasy801;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="holger.morch@navteq.com">Holger Morch</a>
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Sep 28, 2012
 */
@RunWith(Arquillian.class)
public class ProxyWithGenericReturnTypeJacksonTest
{
   protected ResteasyDeployment deployment;
   
	@Deployment
	public static Archive<?> createTestArchive()
	{
		WebArchive war = ShrinkWrap.create(WebArchive.class, "RESTEASY-801.war")
				.addClasses(JaxRsActivator.class)
				.addClasses(AbstractParent.class, Type1.class, Type2.class)
				.addClasses(TestInvocationHandler.class)
				.addClasses(TestSubResourceIntf.class, TestSubResourceSubIntf.class, TestResource.class)
				;
		System.out.println(war.toString(true));
		return war;
	}

    @Test
    public void test() throws Exception {
        ClientRequest request = new ClientRequest("http://localhost:8080/RESTEASY-801/rest/test/one/");
        System.out.println("Sending request");
        ClientResponse<String> response = request.get(String.class);
        System.out.println("Received response: " + response.getEntity(String.class));
        Assert.assertEquals(200, response.getStatus());
        Assert.assertTrue("Type property is missing.", response.getEntity(String.class).contains("type"));

        request = new ClientRequest("http://localhost:8080/RESTEASY-801/rest/test/list/");
        System.out.println("Sending request");
        ClientResponse<String> response = request.get(String.class);
        System.out.println("Received response: " + response.getEntity(String.class));
        Assert.assertEquals(200, response.getStatus());
        Assert.assertTrue("Type property is missing.", response.getEntity(String.class).contains("type"));
    }   
}