package org.jboss.resteasy.test.xxe;

import junit.framework.Assert;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.util.Hashtable;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * @author <a href="mailto:ron.sigal@jboss.com">Ron Sigal</a>
 * @date Jan 6, 2012
 */
public class TestXXE
{
   protected static ResteasyDeployment deployment;
   protected static Dispatcher dispatcher;

   @Path("/")
   public static class TestResource
   {
      @Consumes("application/xml")
      @POST
      public String doPost(Document doc)
      {
         Node node = doc.getDocumentElement();
         System.out.println("name: " + node.getNodeName());
         NodeList children = doc.getDocumentElement().getChildNodes();

         node = children.item(0);
         System.out.println("name: " + node.getNodeName());
         children = node.getChildNodes();

         node = children.item(0);
         System.out.println("name: " + node.getNodeName());
         children = node.getChildNodes();

         System.out.println(node.getNodeValue());
         return node.getNodeValue();
      }
   }

   public static void before(String expandEntityReferences) throws Exception
   {
      Hashtable<String,String> initParams = new Hashtable<String,String>();
      Hashtable<String,String> contextParams = new Hashtable<String,String>();
      contextParams.put(ResteasyContextParameters.RESTEASY_EXPAND_ENTITY_REFERENCES, expandEntityReferences);
      deployment = EmbeddedContainer.start(initParams, contextParams);
      dispatcher = deployment.getDispatcher();
      deployment.getRegistry().addPerRequestResource(TestResource.class);
   }

   public static void after() throws Exception
   {
      EmbeddedContainer.stop();
      dispatcher = null;
      deployment = null;
   }

   @Test
   public void testXXEWithoutExpansion() throws Exception
   {
      before("false");
      ClientRequest request = new ClientRequest(generateURL("/"));
      String filename = "src/test/java/org/jboss/resteasy/test/xxe/testpasswd";
      String str = "<?xml version=\"1.0\"?>\r" +
                   "<!DOCTYPE foo\r" +
                   "[<!ENTITY xxe SYSTEM \"" + filename + "\">\r" +
                   "]>\r" + 
                   "<search><user>&xxe;</user></search>";
      System.out.println(str);
      request.body("application/xml", str);
      ClientResponse<?> response = request.post();
      Assert.assertEquals(204, response.getStatus());
      String entity = response.getEntity(String.class);
      System.out.println("result: " + entity);
      Assert.assertEquals(entity, null);
      after();
   }

   @Test
   public void testXXEWithExpansion() throws Exception
   {
      before("true");
      ClientRequest request = new ClientRequest(generateURL("/"));
      String filename = "src/test/java/org/jboss/resteasy/test/xxe/testpasswd";
      String str = "<?xml version=\"1.0\"?>\r" +
                   "<!DOCTYPE foo\r" +
                   "[<!ENTITY xxe SYSTEM \"" + filename + "\">\r" +
                   "]>\r" + 
                   "<search><user>&xxe;</user></search>";
      System.out.println(str);
      request.body("application/xml", str);
      ClientResponse<?> response = request.post();
      Assert.assertEquals(200, response.getStatus());
      String entity = response.getEntity(String.class);
      System.out.println("result: " + entity);
      Assert.assertEquals("xx:xx:xx:xx:xx:xx:xx", entity);
      after();
   }
}
