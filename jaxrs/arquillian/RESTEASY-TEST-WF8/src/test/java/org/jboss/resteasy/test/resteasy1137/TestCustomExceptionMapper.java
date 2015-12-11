package org.jboss.resteasy.test.resteasy1137;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.internal.ClientResponse;
import org.jboss.resteasy.resteasy1137.TestApplication;
import org.jboss.resteasy.resteasy1137.TestClassConstraint;
import org.jboss.resteasy.resteasy1137.TestClassValidator;
import org.jboss.resteasy.resteasy1137.TestReport;
import org.jboss.resteasy.resteasy1137.TestResource;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * RESTEASY-1137
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Jul 20, 2015
 */
@RunWith(Arquillian.class)
public class TestCustomExceptionMapper
{
   @Deployment
   public static Archive<?> createTestArchive()
   {
      WebArchive war = ShrinkWrap.create(WebArchive.class, "RESTEASY-1137.war")
            .addClasses(TestApplication.class, TestResource.class)
            .addClasses(TestClassConstraint.class, TestClassValidator.class, TestReport.class)
            .addAsLibrary("1137/validation-versioning.jar", "validation-versioning.jar")
            .addAsWebInfResource("1137/web.xml", "web.xml")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
            ;
      System.out.println(war.toString(true));
      return war;
   }
   
   @Test
   public void testExceptionMapperInputViolations() throws Exception
   {  
      Client client = ClientBuilder.newClient();
      Builder builder = client.target("http://localhost:8080/RESTEASY-1137/test/all/a/b/c").request();
      builder.accept(MediaType.APPLICATION_XML);
      ClientResponse response = (ClientResponse) builder.get();
      System.out.println("status: " + response.getStatus());
      Assert.assertEquals(444, response.getStatus());
      TestReport report = response.readEntity(TestReport.class);
      countViolations(report, 1, 1, 1, 1, 0);
   }
   
   @Test
   public void testExceptionMapperOutputViolations() throws Exception
   {
      Client client = ClientBuilder.newClient();
      Builder builder = client.target("http://localhost:8080/RESTEASY-1137/test/all/abc/defg/hijkl").request();
      builder.accept(MediaType.APPLICATION_XML);
      ClientResponse response = (ClientResponse) builder.get();
      System.out.println("status: " + response.getStatus());
      Assert.assertEquals(444, response.getStatus());
      TestReport report = response.readEntity(TestReport.class);
      countViolations(report, 0, 0, 0, 0, 1);
   }
   
   
   protected boolean countViolations(TestReport report, int fieldCount, int propertyCount, int classCount, int parameterCount, int returnValueCount)
   {
      return report.getFieldViolations() == fieldCount
            && report.getPropertyViolations() == propertyCount
            && report.getClassViolations() == classCount
            && report.getParameterViolations() == parameterCount
            && report.getReturnValueViolations() == returnValueCount;
   }
}