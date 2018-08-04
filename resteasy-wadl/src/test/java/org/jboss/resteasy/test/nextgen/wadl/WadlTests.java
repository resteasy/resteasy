package org.jboss.resteasy.test.nextgen.wadl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.InetSocketAddress;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.plugins.server.sun.http.HttpContextBuilder;
import org.jboss.resteasy.test.TestPortProvider;
import org.jboss.resteasy.test.nextgen.wadl.resources.BasicResource;
import org.jboss.resteasy.test.nextgen.wadl.resources.ExtendedResource;
import org.jboss.resteasy.test.nextgen.wadl.resources.issues.RESTEASY1246;
import org.jboss.resteasy.wadl.ResteasyWadlDefaultResource;
import org.jboss.resteasy.wadl.ResteasyWadlWriter;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.sun.net.httpserver.HttpServer;

/**
 * @author <a href="mailto:l.weinan@gmail.com">Weinan Li</a>
 */
public class WadlTests extends WADLTestSetup {

   private static HttpServer httpServer;
   protected static HttpContextBuilder contextBuilder;
   private Client client;

   public Client getClient() {
      return client;
   }

   public void setClient(Client client) {
      this.client = client;
   }

   private static ResteasyWadlDefaultResource defaultResource = new ResteasyWadlDefaultResource();


   @BeforeClass
   public static void before() throws Exception {

      httpServer = HttpServer.create(new InetSocketAddress(TestPortProvider.getPort()), 10);
      contextBuilder = new HttpContextBuilder();
      contextBuilder.getDeployment().getActualResourceClasses().add(BasicResource.class);
//      contextBuilder.getDeployment().getActualResourceClasses().add(ResteasyWadlDefaultResource.class);
//      contextBuilder.getDeployment().getActualResourceClasses().add(ResteasyWadlExtendedResource.class);
      contextBuilder.getDeployment().getActualResourceClasses().add(RESTEASY1246.class);
      contextBuilder.getDeployment().getActualResourceClasses().add(ExtendedResource.class);

      contextBuilder.bind(httpServer);
//      defaultResource.getServices().put("/", ResteasyWadlGenerator.generateServiceRegistry(contextBuilder.getDeployment()));
      contextBuilder.getDeployment().getResources().add(defaultResource);


      httpServer.start();
   }

   @AfterClass
   public static void after() throws Exception {
        
      contextBuilder.cleanup();
      httpServer.stop(0);
      Thread.sleep(100);
   }
    
   @Before
   public void init() {
      setClient(ClientBuilder.newClient());
   }
    
   @After
   public void clean() {
      try {
         getClient().close();
         setClient(null);
      } catch (Exception e) {
         //ignore
      }
   }


   public WadlTests() {

   }


   @Test
   public void testBasicSet() throws Exception {
      WebTarget target = getClient().target(TestPortProvider.generateURL("/application.xml"));
      Response response = target.request().get();

      // get Application
      org.jboss.resteasy.wadl.jaxb.Application application = response.readEntity(org.jboss.resteasy.wadl.jaxb.Application.class);
      assertNotNull("application not null", application);
      assertEquals(1, application.getResources().size());

      // get BasicResource
      org.jboss.resteasy.wadl.jaxb.Resource basicResource = findResourceByName(application, "/basic");
      assertNotNull("basic resouce not null", basicResource);

      {
         // verify the existence of params
         WADLTestExistenceVerifier paramExistenceVerifier = new WADLTestExistenceVerifier();
         paramExistenceVerifier.createVerifier("name", "name2");
         paramExistenceVerifier.verify(basicResource.getParam(), org.jboss.resteasy.wadl.jaxb.Param.class, "getName");

         // verify existence of two methods: "get" and "post"
         WADLTestExistenceVerifier methodExistenceVerifier = new WADLTestExistenceVerifier();
         methodExistenceVerifier.createVerifier("get", "post");
         methodExistenceVerifier.verify(basicResource.getMethodOrResource(), org.jboss.resteasy.wadl.jaxb.Method.class, "getId");

         // verify 'post' method has expected id and name
         org.jboss.resteasy.wadl.jaxb.Method post = findMethodById(basicResource, "post");
         assertNotNull("post method not null", post);
         assertEquals("POST", post.getName());
         assertNotNull("post response not null", post.getResponse());
         assertNotNull("post response representation not null", post.getResponse().get(0).getRepresentation());

         // verify 'get' method
         org.jboss.resteasy.wadl.jaxb.Method get = findMethodById(basicResource, "get");
         assertEquals("GET", get.getName());
      }

      {
         // verify existence of resources
         WADLTestExistenceVerifier resourceExistenceVerifier = new WADLTestExistenceVerifier();
         String compositeResourceName = "composite/{pathParam}";

         resourceExistenceVerifier.createVerifier(compositeResourceName);
         resourceExistenceVerifier.verify(basicResource.getMethodOrResource(), org.jboss.resteasy.wadl.jaxb.Resource.class, "getPath");

         // verify resource 'intr/{foo}'
         org.jboss.resteasy.wadl.jaxb.Resource compositeResource = findResourceByName(basicResource, compositeResourceName);
         assertNotNull(compositeResource);
         assertEquals(compositeResourceName, compositeResource.getPath());

         WADLTestExistenceVerifier paramExistenceVerifier = new WADLTestExistenceVerifier();
         paramExistenceVerifier.createVerifier("pathParam", "matrixParam");
         paramExistenceVerifier.verify(compositeResource.getParam(), org.jboss.resteasy.wadl.jaxb.Param.class, "getName");

         WADLTestExistenceVerifier methodExistenceVerifier = new WADLTestExistenceVerifier();
         methodExistenceVerifier.createVerifier("composite");
         methodExistenceVerifier.verify(compositeResource.getMethodOrResource(), org.jboss.resteasy.wadl.jaxb.Method.class, "getId");

         org.jboss.resteasy.wadl.jaxb.Method compositeMethod = findMethodById(compositeResource, "composite");

         // verify response
         assertTrue(compositeResourceName + " response contains respresentation", compositeMethod.getResponse().get(0).getRepresentation().size() > 0);
         assertEquals("text/plain", compositeMethod.getResponse().get(0).getRepresentation().get(0).getMediaType());

         WADLTestExistenceVerifier requestVerifier = new WADLTestExistenceVerifier();
         requestVerifier.createVerifier("headerParam", "queryParam", "Cookie");
         requestVerifier.verify(compositeMethod.getRequest().getParam(), org.jboss.resteasy.wadl.jaxb.Param.class, "getName");
      }


   }

   @Test
   public void testResteasy1246() throws Exception {
      WebTarget target = getClient().target(TestPortProvider.generateURL("/application.xml"));
      Response response = target.request().get();
      // get Application
      org.jboss.resteasy.wadl.jaxb.Application application = response.readEntity(org.jboss.resteasy.wadl.jaxb.Application.class);
      org.jboss.resteasy.wadl.jaxb.Method multipleProvides1 = findMethodById(findResourceByName(findResourceByName(application, "/issues/1246"), "/provides1"), "multipleProvides1");
      assertEquals("Multiple representations should be present", 2, multipleProvides1.getResponse().get(0).getRepresentation().size());
      org.jboss.resteasy.wadl.jaxb.Method multipleProvides2 = findMethodById(findResourceByName(findResourceByName(application, "/issues/1246"), "/provides2"), "multipleProvides2");
      assertEquals("Multiple representations should be present", 2, multipleProvides2.getResponse().get(0).getRepresentation().size());
   }

   @Test
   public void extendedTest() {
      ResteasyWadlWriter.ResteasyWadlGrammar wadlGrammar = new ResteasyWadlWriter.ResteasyWadlGrammar();
      wadlGrammar.includeGrammars("application-grammars.xml");
      wadlGrammar.enableSchemaGeneration();
      defaultResource.getWadlWriter().setWadlGrammar(wadlGrammar);

      testGrammarGeneration();
      // test again to make sure the grammar generation is re-entrant
      testGrammarGeneration();

      {
         org.jboss.resteasy.wadl.jaxb.Application application;

         WebTarget target = getClient().target(TestPortProvider.generateURL("/application.xml"));
         Response response = target.request().get();
         application = response.readEntity(org.jboss.resteasy.wadl.jaxb.Application.class);


         WebTarget target2 = getClient().target(TestPortProvider.generateURL("/wadl-extended/"+application.getGrammars().getInclude().get(0).getHref()));
         target2.request().get();
      }
   }

   private void testGrammarGeneration() {
      org.jboss.resteasy.wadl.jaxb.Application application;

      WebTarget target = getClient().target(TestPortProvider.generateURL("/application.xml"));
      Response response = target.request().get();
      application = response.readEntity(org.jboss.resteasy.wadl.jaxb.Application.class);
      assertNotNull("application not null", application);
      assertNotNull(application.getGrammars());
      assertEquals(2, application.getGrammars().getInclude().size());
   }
}
