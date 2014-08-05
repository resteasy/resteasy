package org.jboss.resteasy.test.resteasy1073;

import java.io.File;

import javax.ws.rs.core.MediaType;

import junit.framework.Assert;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.resteasy1073.TestApplication;
import org.jboss.resteasy.resteasy1073.TestResource;
import org.jboss.resteasy.resteasy1073.TestWrapper;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * RESTEASY-1073.
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Created July 19, 2014
 */
@RunWith(Arquillian.class)
public class TestExternalParameterEntity
{  
   @Deployment(name="war_expand", order=1)
   public static Archive<?> createTestArchive1()
   {
      WebArchive war = ShrinkWrap.create(WebArchive.class, "RESTEASY-1073-expand.war")
            .addClasses(TestApplication.class)
            .addClasses(TestResource.class, TestWrapper.class)
            .addAsWebInfResource("web_expand.xml", "web.xml")
            ;
      System.out.println(war.toString(true));
      return war;
   }

   @Deployment(name="war_no_expand", order=2)
   public static Archive<?> createTestArchive2()
   {
      WebArchive war = ShrinkWrap.create(WebArchive.class, "RESTEASY-1073-no-expand.war")
            .addClasses(TestApplication.class)
            .addClasses(TestResource.class, TestWrapper.class)
            .addAsWebInfResource("web_no_expand.xml", "web.xml")
            ;
      System.out.println(war.toString(true));
      return war;
   }
   
   private String passwdFile = new File("src/test/resources/passwd").getAbsolutePath();
   private String dtdFile = new File("src/test/resources/test.dtd").getAbsolutePath();
      
   private String text =
"<!DOCTYPE foo [\r" +
"  <!ENTITY % file SYSTEM \"" + passwdFile + "\">\r" +
"  <!ENTITY % start \"<![CDATA[\">\r" + 
"  <!ENTITY % end \"]]>\">\r" +
"  <!ENTITY % dtd SYSTEM \"" + dtdFile + "\">\r" +
"%dtd;\r" +
"]>\r" +
"<testWrapper><name>&xxe;</name></testWrapper>";
   
   @Test
   public void testExternalParameterEntityExpand() throws Exception
   {
      ClientRequest request = new ClientRequest("http://localhost:8080/RESTEASY-1073-expand/test");
      System.out.println(text);
      request.body(MediaType.APPLICATION_XML, text);
      ClientResponse<?> response = request.post();
      Assert.assertEquals(200, response.getStatus());
      String entity = response.getEntity(String.class);
      System.out.println("Result: " + entity);
      Assert.assertEquals("root:x:0:0:root:/root:/bin/bash", entity.trim());
   }
   
   @Test
   public void testExternalParameterEntityNoExpand() throws Exception
   {
      ClientRequest request = new ClientRequest("http://localhost:8080/RESTEASY-1073-no-expand/test");
      System.out.println(text);
      request.body(MediaType.APPLICATION_XML, text);
      ClientResponse<?> response = request.post();
      Assert.assertEquals(200, response.getStatus());
      String entity = response.getEntity(String.class);
      System.out.println("Result: " + entity);
      Assert.assertEquals("", entity.trim());
   }
}
