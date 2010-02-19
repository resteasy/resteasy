package org.jboss.resteasy.star.bpm.test;

import junit.framework.Assert;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.core.executors.ApacheHttpClientExecutor;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataOutput;
import org.jboss.resteasy.spi.Link;
import org.jboss.resteasy.star.bpm.ProcessDefinitionResource;
import org.jboss.resteasy.star.bpm.ProcessEngineResource;
import org.jboss.resteasy.test.BaseResourceTest;
import org.jbpm.api.Configuration;
import org.jbpm.api.ProcessEngine;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import java.io.InputStream;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class BasicTest extends BaseResourceTest
{
   protected static ProcessEngine processEngine;


   @BeforeClass
   public static void init()
   {
      processEngine = Configuration.getProcessEngine();
      ProcessEngineResource pe = new ProcessEngineResource(processEngine);
      dispatcher.getRegistry().addSingletonResource(pe);

   }

   @Test
   public void testTransitions() throws Exception
   {
      String mediaType = "bpm/jpdl";
      String file = "jpdl/state.jpdl.xml";
      transitions(mediaType, file);
   }

   @Test
   public void testBpmnTransitions() throws Exception
   {
      String mediaType = "bpm/bpmn";
      String file = "bpmn/state.bpmn.xml";
      transitions(mediaType, file);
   }
   private void transitions(String mediaType, String file)
           throws Exception
   {
      InputStream jpdl = Thread.currentThread().getContextClassLoader().getResourceAsStream(file);
      Assert.assertNotNull(jpdl);

      ApacheHttpClientExecutor executor = new ApacheHttpClientExecutor();

      ClientRequest request = executor.createRequest("http://localhost:8081/bpm/definitions");
      request.body(mediaType, jpdl);
      Link definition = request.create();
      Assert.assertNotNull(definition);
      ClientResponse response = null;

      response = definition.request().get();
      System.out.println(response.getEntity(String.class));
      Assert.assertEquals(200, response.getStatus());
      Link instanceFactory = response.getLinkHeader().getLinkByTitle("instances");
      response = instanceFactory.request().post();
      Assert.assertEquals(201, response.getStatus());
      Link instance = response.getLocation();
      Assert.assertNotNull(instance);

      System.out.println("LinkHeader: " + response.getLinkHeader());

      Link next = response.getLinkHeader().getLinkByTitle("continue");
      Assert.assertNotNull(next);
      System.out.println("next: " + next.getHref());
      response = next.request().post();
      System.out.println("after next");
      Assert.assertEquals(204, response.getStatus());
   }

   @Test
   public void testVariables() throws Exception
   {
      String mediaType = "bpm/jpdl";
      String file = "jpdl/state.jpdl.xml";
      InputStream jpdl = Thread.currentThread().getContextClassLoader().getResourceAsStream(file);
      Assert.assertNotNull(jpdl);

      ApacheHttpClientExecutor executor = new ApacheHttpClientExecutor();

      ClientRequest request = executor.createRequest("http://localhost:8081/bpm/definitions");
      request.body(mediaType, jpdl);
      Link definition = request.create();
      Assert.assertNotNull(definition);
      ClientResponse response = null;

      response = definition.request().head();
      Assert.assertEquals(200, response.getStatus());
      Link instanceFactory = response.getLinkHeader().getLinkByTitle("instances");

      MultipartFormDataOutput form = new MultipartFormDataOutput();
      form.addFormData("order", "<order><total>$199.99</total></order>", MediaType.APPLICATION_XML_TYPE);
      response = instanceFactory.request()
              .body(MediaType.MULTIPART_FORM_DATA_TYPE, form)
              .post();
      Assert.assertEquals(201, response.getStatus());
      System.out.println(response.getLinkHeader().toString());
      Link instance = response.getLocation();
      Assert.assertNotNull(instance);

      Link next = response.getLinkHeader().getLinkByTitle("continue");
      Assert.assertNotNull(next);


      Link variables = response.getLinkHeader().getLinkByTitle("variables");
      Link newVariables = response.getLinkHeader().getLinkByTitle("variable-template");

      response = variables.request().head();
      Assert.assertEquals(200, response.getStatus());
      System.out.println(response.getLinkHeader().toString());
      Link order = response.getLinkHeader().getLinkByTitle("order");
      String xml = order.request().getTarget(String.class);
      System.out.println("Order: " + xml);
      request = newVariables.request();
      response = request.pathParameter("var", "customer")
             .body(MediaType.APPLICATION_XML_TYPE, "<customer><name>bill</name></customer>")
              .put();
      Assert.assertEquals(201, response.getStatus());
      response = request.pathParameter("var", "customer")
             .body(MediaType.APPLICATION_XML_TYPE, "<customer><name>bill burke</name></customer>")
              .put();
      Assert.assertEquals(204, response.getStatus());


      System.out.println("next: " + next.getHref());
      response = next.request().post();
      System.out.println("after next");
      Assert.assertEquals(204, response.getStatus());

   }

}
