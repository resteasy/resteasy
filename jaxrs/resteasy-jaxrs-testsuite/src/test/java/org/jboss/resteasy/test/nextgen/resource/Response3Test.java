package org.jboss.resteasy.test.nextgen.resource;

import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.annotation.Priority;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.RuntimeDelegate;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class Response3Test extends BaseResourceTest
{
   public abstract static class TemplateFilter implements ContainerResponseFilter {

      public static final String OPERATION = "OPERATION";
      public static final String PROPERTYNAME = "getSetProperty";
      public static final String HEADER = "HEADER";

      protected ContainerRequestContext requestContext;
      protected ContainerResponseContext responseContext;

      @Override
      public void filter(ContainerRequestContext requestContext,
                         ContainerResponseContext responseContext) throws IOException {
         this.requestContext = requestContext;
         this.responseContext = responseContext;
         String operation = getHeaderString();
         Method[] methods = getClass().getMethods();
         for (Method method : methods)
            if (operation.equalsIgnoreCase(method.getName())) {
               try {
                  method.invoke(this);
                  return;
               } catch (Exception e) {
                  e.printStackTrace();
                  responseContext.setStatus(Response.Status.SERVICE_UNAVAILABLE
                          .getStatusCode());
                  setEntity(e.getMessage());
                  return;
               }
            }
         operationMethodNotFound(operation);
      }

      protected void operationMethodNotFound(String operation) {
         responseContext.setStatus(Response.Status.SERVICE_UNAVAILABLE.getStatusCode());
         setEntity("Operation " + operation + " not implemented");
      }

      // ////////////////////////////////////////////////////////////////////
      protected static <T> String collectionToString(Collection<T> collection) {
         StringBuilder sb = new StringBuilder();
         for (T item : collection) {
            String replace = item.toString().toLowerCase().replace("_", "-")
                    .replace(" ", "");
            sb.append(replace).append(" ");
         }
         return sb.toString();
      }

      protected boolean assertTrue(boolean conditionTrue, Object... msg) {
         if (conditionTrue)
            return false;
         StringBuilder sb = new StringBuilder();
         if (msg != null)
            for (Object str : msg)
               sb.append(str).append(" ");
         setEntity(sb.toString());
         responseContext.setStatus(Response.Status.NOT_ACCEPTABLE.getStatusCode());
         return true;
      }

      // might be replaced with ctx.getStringHeader()
      protected String getHeaderString() {
         MultivaluedMap<String, Object> headers = responseContext.getHeaders();
         return (String) headers.getFirst(OPERATION);
      }

      protected void setEntity(String entity) {
         responseContext.setEntity(entity, null, MediaType.TEXT_PLAIN_TYPE);
      }

   }

   @Provider
   @Priority(500)
// reverse order
   public static class ResponseFilter extends TemplateFilter {
      public static final String COOKIENAME = "CookieName";
      public static final String ENTITY = "ResponseFilterEntity";
      public static final String NULL = "NULL";
      public static final String RELATION = "relation";

      public void getAllowedMethods() {
         Set<String> set = responseContext.getAllowedMethods();
         setEntity(collectionToString(set));
         for (String s : set)
            if (!s.toUpperCase().equals(s)) {
               setEntity(s + " is not uppercase");
               break;
            }
      }

      public void getCookies() {
         Map<String, NewCookie> cookies = responseContext.getCookies();
         setEntity(collectionToString(cookies.keySet()));
      }

      public void getCookiesIsReadOnly() {
         NewCookie cookie = new NewCookie(COOKIENAME, COOKIENAME);
         Map<String, NewCookie> cookies = responseContext.getCookies();
         if (assertTrue(!cookies.containsKey(COOKIENAME), COOKIENAME,
                 "is already present"))
            return;
         try {
            cookies.put(COOKIENAME, cookie);
         } catch (Exception e) {
            // Not mandatory, but possible as cookies is read-only
         }
         cookies = responseContext.getCookies();
         if (assertTrue(!cookies.containsKey(COOKIENAME),
                 "#getCookies is Not readOnly"))
            return;
         setEntity("getCookies is read-only as expected");
      }

      public void getDate() {
         Date date = responseContext.getDate();
         long milis = date == null ? 0 : date.getTime();
         setEntity(String.valueOf(milis));
      }

      public void getEntity() {
         byte[] entity = (byte[]) responseContext.getEntity();
         if (entity != null)
            setEntity(new String(entity) + new String(entity));
         else
            setEntity(NULL);
      }

      public void getEntityAnnotations() {
         Annotation[] annotations = responseContext.getEntityAnnotations();
         if (annotations != null && annotations.length != 0) {
            String[] names = new String[annotations.length];
            for (int i = 0; i != annotations.length; i++)
               names[i] = annotations[i].annotationType().getName();
            setEntity(collectionToString(Arrays.asList(names)));
         } else
            setEntity(NULL);
      }

      public void getEntityClass() {
         Class<?> clazz = responseContext.getEntityClass();
         setEntity(clazz.getName());
      }

      public void getEntityStream() throws IOException {
         OutputStream stream = responseContext.getEntityStream();
         if (stream == null)
            setEntity(NULL);
         else
            setEntity(ENTITY);
      }

      public void getEntityTag() {
         EntityTag tag = responseContext.getEntityTag();
         setEntity(tag == null ? NULL : tag.getValue());
      }

      public void getEntityType() {
         Type type = responseContext.getEntityType();
         String name = NULL;
         if (type instanceof Class)
            name = ((Class<?>) type).getName();
         else if (type != null)
            name = type.getClass().getName();
         setEntity(name);
      }

      public void getHeaders() {
         MultivaluedMap<String, Object> headers = responseContext.getHeaders();
         setEntity(collectionToString(headers.keySet()));
      }

      public void getHeadersIsMutable() {
         MultivaluedMap<String, Object> headers = responseContext.getHeaders();
         if (assertTrue(!headers.containsKey(HEADER), HEADER,
                 "header is alredy in header map"))
            return;
         headers.add(HEADER, HEADER);
         headers = responseContext.getHeaders();
         if (assertTrue(headers.containsKey(HEADER), HEADER,
                 "header is not in header map"))
            return;
         // second filter run
      }

      public void getHeaderStringOperation() {
         String header = responseContext.getHeaderString(OPERATION);
         setEntity(header);
      }

      public void getHeaderStringHeader() {
         String header = responseContext.getHeaderString(HEADER);
         setEntity(header == null ? NULL : header);
      }

      public void getLanguage() {
         Locale locale = responseContext.getLanguage();
         setEntity(locale == null ? NULL : locale.toString());
      }

      public void getLastModified() {
         Date date = responseContext.getLastModified();
         setEntity(date == null ? NULL : String.valueOf(date.getTime()));
      }

      public void getLength() {
         int len = responseContext.getLength();
         setEntity(String.valueOf(len));
      }

      public void getLink() {
         Link link = responseContext.getLink(RELATION);
         setLinkForGetLink(link);
      }

      public void getLinkBuilder() {
         Link.Builder builder = responseContext.getLinkBuilder(RELATION);
         if (builder != null) {
            Link link = builder.build();
            setLinkForGetLink(link);
         } else
            setEntity(NULL);
      }

      private void setLinkForGetLink(Link link) {
         String entity = NULL;
         if (link != null && link.getUri() != null)
            entity = link.getUri().toASCIIString();
         setEntity(entity);
      }

      public void getLocation() {
         URI uri = responseContext.getLocation();
         setEntity(uri == null ? NULL : uri.toASCIIString());
      }

      public void getMediaType() {
         MediaType type = responseContext.getMediaType();
         setEntity(type == null ? NULL : type.toString());
      }

      public void getStatus() {
         int status = responseContext.getStatus();
         responseContext.setStatus(Response.Status.OK.getStatusCode());
         setEntity(String.valueOf(status));
      }

      public void getStatusNotSet() {
         getStatus();
      }

      public void getStatusInfo() {
         Response.StatusType type = responseContext.getStatusInfo();
         if (type == null) {
            setEntity(NULL);
            responseContext.setStatus(Response.Status.OK.getStatusCode());
            return;
         }
         int status = type.getStatusCode();
         responseContext.setStatus(Response.Status.OK.getStatusCode());
         setEntity(String.valueOf(status));
      }

      public void getStatusInfoNotSet() {
         getStatusInfo();
      }

      public void getStringHeaders() {
         MultivaluedMap<String, String> map = responseContext.getStringHeaders();
         List<String> list = map.get(HEADER);
         setEntity(list == null ? NULL : list.size() == 1 ? list.iterator()
                 .next() : collectionToString(list));
      }

      public void hasEntity() {
         boolean has = responseContext.hasEntity();
         setEntity(String.valueOf(has));
      }

      public void hasLink() {
         boolean has = responseContext.hasLink(RELATION);
         setEntity(String.valueOf(has));
      }

      public void setEntity() {
         Annotation[] annotations = getClass().getAnnotations();
         MediaType type = MediaType.APPLICATION_SVG_XML_TYPE;
         responseContext.setEntity(ENTITY, annotations, type);
      }

      public void setEntityStream() {
         final OutputStream stream = responseContext.getEntityStream();
         OutputStream byteStream = new OutputStream() {
            @Override
            public void write(byte[] b) throws IOException {
               stream.write(ENTITY.getBytes());
               stream.write(b);
            }

            @Override
            public void write(int b) throws IOException {
               write(intToByteArray(b));
            }

            @Override
            public synchronized void write(byte[] b, int off, int len)
                    throws IOException {
               write(b);
            }

            public final byte[] intToByteArray(int value) {
               return new byte[] { (byte) (value & 0xff) };
            }
         };
         responseContext.setEntityStream(byteStream);
         StringBuilder sb = new StringBuilder(ENTITY.length() + 4);
         for (int i = 0; i < ENTITY.length() + 3; i += 2)
            sb.append("OK");
         setEntity(sb.toString());
      }

      public void setStatus() {
         String entity = (String) responseContext.getEntity();
         int status = Integer.parseInt(entity);
         responseContext.setStatus(status);
      }

      public void setStatusInfo() {
         String entity = (String) responseContext.getEntity();
         final int status = Integer.parseInt(entity);
         Response.StatusType type = new Response.StatusType() {

            @Override
            public int getStatusCode() {
               return status;
            }

            @Override
            public String getReasonPhrase() {
               return null;
            }

            @Override
            public Response.Status.Family getFamily() {
               return Response.Status.Family.familyOf(status);
            }
         };
         responseContext.setStatusInfo(type);
      }

      public void setStringBeanRuntime() {
         // pass
      }

      public void setOriginalRuntime() {
         // pass
      }
   }

   @Provider
   @Priority(100)
// reverse order, should be second
   public static class SecondResponseFilter extends TemplateFilter {
      @Override
      protected void operationMethodNotFound(String operation) {
         // the check is to apply on ResponseFilter only
         // here, it is usually not found.
      }

      public void getHeadersIsMutable() {
         MultivaluedMap<String, Object> headers = responseContext.getHeaders();
         if (assertTrue(headers.containsKey(HEADER), HEADER,
                 "header is not in header map"))
            return;
         setEntity(HEADER + " found as expected");
      }

      public void setEntity() {
         MediaType type = responseContext.getMediaType();
         if (assertTrue(MediaType.APPLICATION_SVG_XML_TYPE.equals(type),
                 "Unexpected mediatype", type))
            return;

         Annotation[] annotations = responseContext.getEntityAnnotations();
         for (Annotation annotation : annotations) {
            Class<?> clazz = annotation.annotationType();
            if (assertTrue(clazz == Provider.class
                    || clazz == Priority.class, "Annotation", clazz,
                    "was unexpected"))
               return;
         }
      }
   }

   @Path("resource")
   public static class Resource {

      @Context
      UriInfo info;

      @POST
      @Path("hasentity")
      public Response hasEntity(String entity) {
         Response.ResponseBuilder builder = createResponseWithHeader();
         if (entity != null && entity.length() != 0)
            builder = builder.entity(entity);
         Response response = builder.build();
         return response;
      }

      private Response.ResponseBuilder createResponseWithHeader() {
         // get value of @Path(value)
         List<PathSegment> segments = info.getPathSegments();
         PathSegment last = segments.get(segments.size() - 1);
         // convert the value to ContextOperation
         Response.ResponseBuilder builder = Response.ok();
         // set a header with ContextOperation so that the filter knows what to
         // do
         builder = builder.header(ResponseFilter.OPERATION, last.getPath()
                 .toUpperCase());
         return builder;
      }


   }




   static Client client;

   @BeforeClass
   public static void setup() throws Exception
   {
      addPerRequestResource(Resource.class);
      deployment.getProviderFactory().register(ResponseFilter.class);
      deployment.getProviderFactory().register(SecondResponseFilter.class);
      client = ClientBuilder.newClient();
   }

   @AfterClass
   public static void cleanup() throws Exception
   {
      client.close();
   }

   @Test
   public void testException()
   {
      System.out.println(new Exception().getStackTrace()[0].getMethodName());
   }

   @Test
   public void testHasEntity()
   {
      Response response = client.target(generateURL("/resource/hasentity")).request("*/*")
              .header("OPERATION", "hasentity").post(Entity.entity("entity", MediaType.WILDCARD_TYPE));
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals(response.getMediaType(), MediaType.TEXT_PLAIN_TYPE);
      System.out.println(response.readEntity(String.class));
      response.close();

   }

}
