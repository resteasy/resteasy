package org.jboss.resteasy.test.nextgen.resource;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Variant;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * RESTEASY-1227
 * 
 * @version $Revision: 1 $
 */
public class MediaTypeFromMessageBodyWriterTest
{
	protected static ResteasyDeployment deployment;
	protected static Dispatcher dispatcher;
    protected static ResteasyClient client;
	
    private static class Target {
        String path;
        String queryName;
        String queryValue;
        Target(String path, String queryName, String queryValue) {
           this.path = path;
           this.queryName = queryName;
           this.queryValue = queryValue;
        }
     }
     
     private static String ACCEPT_CHROME="text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8";
     private static String ACCEPT_FIREFOX="text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8";
     private static String ACCEPT_IE11="text/html, application/xhtml+xml, */*";
     private static Collection<Target> tgts = new ArrayList<Target>();
     private static Collection<String> accepts = new ArrayList<String>();
     
     static
     {
        tgts.add(new Target("java.util.TreeSet", null, null));
        tgts.add(new Target("fixed", "type", "text/plain"));
        tgts.add(new Target("fixed", "type", "application/xml"));
        tgts.add(new Target("variants", null, null));
        tgts.add(new Target("variantsObject", null, null));
        accepts.add(ACCEPT_CHROME);
        accepts.add(ACCEPT_FIREFOX);
        accepts.add(ACCEPT_IE11);
        accepts.add("foo/bar,text/plain");
        accepts.add("foo/bar,*/*");
        accepts.add("text/plain");
     }

	@Path("/")
	public static class MediaTypeFromMessageBodyWriterResource {

	   @GET
	   @Path("{type}")
	   public Object hello(@PathParam("type") final String type, @HeaderParam("Accept") final String accept)
	         throws Exception {
	      return Class.forName(type).newInstance();
	   }

	   @GET
	   @Path("fixed")
	   public Object fixedResponse(@QueryParam("type") @DefaultValue(MediaType.TEXT_PLAIN) final MediaType type) {
	      final List<Integer> body = Arrays.asList(1, 2, 3, 4, 5, 6);
	      return Response.ok(body, type).build();
	   }

	   @GET
	   @Path("variants")
	   public Response variantsResponse() {
	      final List<Integer> body = Arrays.asList(1, 2, 3, 4, 5, 6);
	      final List<Variant> variants = Variant
	            .mediaTypes(MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_XML_TYPE, MediaType.TEXT_PLAIN_TYPE).build();
	      return Response.ok(body).variants(variants).build();
	   }

	   @GET
	   @Path("variantsObject")
	   public Object variantsObjectResponse() {
	      final List<Integer> body = Arrays.asList(1, 2, 3, 4, 5, 6);
	      final List<Variant> variants = Variant
	            .mediaTypes(MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_XML_TYPE, MediaType.TEXT_PLAIN_TYPE).build();
	      return Response.ok(body).variants(variants).build();
	   }
	}
	
	@Provider
	@Produces(MediaType.TEXT_PLAIN + "; charset=UTF-8")
	public static class MediaTypeFromMessageBodyWriterListAsText implements MessageBodyWriter<Iterable<?>> {

	   @Override
	   public long getSize(final Iterable<?> t, final Class<?> type, final Type genericType, final Annotation[] annotations,
	         final MediaType mediaType) {
	      return -1L;
	   }

	   @Override
	   public boolean isWriteable(final Class<?> type, final Type genericType, final Annotation[] annotations,
	         final MediaType mediaType) {
	      return Iterable.class.isAssignableFrom(type);
	   }

	   @Override
	   public void writeTo(final Iterable<?> items, final Class<?> type, final Type genericType,
	         final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, Object> httpHeaders,
	         final OutputStream out) throws IOException {

	      if (items instanceof Collection) {
	         httpHeaders.putSingle("X-COUNT", Integer.toString(((Collection<?>) items).size()));
	      }
	   }
	}
	
	@Provider
	@Produces(MediaType.APPLICATION_XML + "; charset=UTF-8")
	public static class MediaTypeFromMessageBodyWriterListAsXML implements MessageBodyWriter<Iterable<?>> {

	   @Override
	   public long getSize(final Iterable<?> t, final Class<?> type, final Type genericType, final Annotation[] annotations,
	         final MediaType mediaType) {
	      return -1L;
	   }

	   @Override
	   public boolean isWriteable(final Class<?> type, final Type genericType, final Annotation[] annotations,
	         final MediaType mediaType) {
	      return Iterable.class.isAssignableFrom(type);
	   }

	   @Override
	   public void writeTo(final Iterable<?> items, final Class<?> type, final Type genericType,
	         final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, Object> httpHeaders,
	         final OutputStream out) throws IOException {

	      if (items instanceof Collection) {
	         httpHeaders.putSingle("X-COUNT", Integer.toString(((Collection<?>) items).size()));
	      }
	   }
	}

	@BeforeClass
	public static void before() throws Exception
	{
		deployment = EmbeddedContainer.start();
		dispatcher = deployment.getDispatcher();
		deployment.getRegistry().addPerRequestResource(MediaTypeFromMessageBodyWriterResource.class);
		deployment.getProviderFactory().register(MediaTypeFromMessageBodyWriterListAsText.class);
		deployment.getProviderFactory().register(MediaTypeFromMessageBodyWriterListAsXML.class);
		client = new ResteasyClientBuilder().build();
	}

	@AfterClass
	public static void after() throws Exception
	{
		client.close();
		EmbeddedContainer.stop();
		dispatcher = null;
		deployment = null;
	}

    @Test
    public void test() throws Exception {
       WebTarget base = client.target("http://localhost:8081");
       Response response = null;
       for (Target tgt : tgts) {
          for (String accept : accepts) {
        	  System.out.println("path: " + tgt.path);
        	  System.out.println("accept: " + accept);
             if (tgt.queryName != null) {
                response = base.path(tgt.path).queryParam(tgt.queryName, tgt.queryValue).request().header("Accept", accept).get();
             }
             else {
                response = base.path(tgt.path).request().header("Accept", accept).get();  
             }
             Assert.assertEquals(200, response.getStatus());
             String s = response.getHeaderString("X-COUNT");
             Assert.assertNotNull(s);
             response.close();
          }
       }
    }
}
