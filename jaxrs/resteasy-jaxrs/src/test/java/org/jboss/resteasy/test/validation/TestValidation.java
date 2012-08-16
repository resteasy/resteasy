package org.jboss.resteasy.test.validation;

import junit.framework.Assert;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.plugins.providers.validation.ViolationsContainer;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.spi.validation.ResteasyViolationException;
import org.jboss.resteasy.spi.validation.ViolationUtils;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.junit.Ignore;
import org.junit.Test;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;
import static org.jboss.resteasy.test.TestPortProvider.*;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Created Mar 16, 2012
 */
public class TestValidation
{
   protected static ResteasyDeployment deployment;
   protected static Dispatcher dispatcher;

   @Path("/")
   public static class TestResourceWithValidField
   {
      @SuppressWarnings("unused")
      @Size(min=2, max=4)
      private String s = "abc";

      @POST
      public void post()
      {
      }
   }

   @Path("/")
   public static class TestResourceWithInvalidField
   {
      @SuppressWarnings("unused")
      @Size(min=2, max=4)
      private String s = "abcde";

      @POST
      public void post()
      {
      }
   }

   @Path("/{s}")
   public static class TestResourceWithProperty
   {
      private String s;

      @POST
      public void post()
      {
      }

      @Size(min=2, max=4)  
      public String getS()
      {
         return s;
      }

      @PathParam("s") 
      public void setS(String s)
      {
         this.s = s;
      }
   }

   @Path("/{s}/{t}")
   public static class TestResourceWithFieldAndProperty
   {
      @SuppressWarnings("unused")
      @Size(min=2, max=4)
      @PathParam("s")
      private String s;

      private String t;

      @Size(min=3, max=5)  
      public String getT()
      {
         return t;
      }

      @PathParam("t") 
      public void setT(String t)
      {
         this.t = t;
      }

      @POST
      public void post()
      {
      }
   }

   public static class TestClassValidator implements ConstraintValidator<TestClassConstraint, TestResourceWithClassConstraint>
   {
      int length;

      public void initialize(TestClassConstraint constraintAnnotation)
      {
         length = constraintAnnotation.value();
      }

      public boolean isValid(TestResourceWithClassConstraint value, ConstraintValidatorContext context)
      {
         return value.s.length() + value.t.length() >= length;
      }

   }

   @Documented
   @Constraint(validatedBy = TestClassValidator.class)
   @Target({TYPE})
   @Retention(RUNTIME)
   public @interface TestClassConstraint {
      String message() default "Concatenation of s and t must have length > {value}";
      Class<?>[] groups() default {};
      Class<? extends Payload>[] payload() default {};
      int value();
   }

   @Path("/{s}/{t}")
   @TestClassConstraint(5)
   public static class TestResourceWithClassConstraint
   {
      @NotNull String s;
      @NotNull String t;

      public TestResourceWithClassConstraint(@PathParam("s") String s, @PathParam("t") String t)
      {
         this.s = s;
         this.t = t;  
      }

      @POST
      public void post()
      {
      }

      public String toString()
      {
         return "TestResourceWithClassConstraint(\"" + s + "\", \"" + t + "\")";
      }
   }

   public static class A
   {
      @Size(min=4) String s1;
      @Size(min=5) String s2;

      public A(String s1, String s2)
      {
         this.s1 = s1;
         this.s2 = s2;
      }
      public void setS2(String s)
      {
         this.s2 = s;
      }
      public String getS2()
      {
         return s2;
      }
   }

   public static class B
   {
      @Valid A a;

      public B(A a) {this.a = a;}
   }

   @Path("/{s}/{t}")
   public static class TestResourceWithGraph
   {
      @Valid B b;

      public TestResourceWithGraph(@PathParam("s") String s, @PathParam("t") String t)
      {
         b = new B(new A(s, t));
      }

      @POST
      public void post()
      {
      }
   }

   public static class OneString
   {
      @Size(min=5) String s;

      public OneString(String s)
      {
         this.s = s;
      }
      public String getS()
      {
         return s;
      }
      public void setString(String s)
      {
         this.s = s;
      }
   }

   public static class ArrayOfStrings
   {
      @Valid OneString[] strings;

      public ArrayOfStrings(String s)
      {
         strings = new OneString[]{new OneString(s)};
      }
   }

   @Path("/{s}")
   public static class TestResourceWithArray
   {
      @Valid ArrayOfStrings aos;


      public TestResourceWithArray(@PathParam("s") String s)
      {
         aos = new ArrayOfStrings(s);
      }

      @POST
      public void post()
      {
      }
   }

   public static class ListOfStrings
   {
      @Valid List<OneString> strings;

      public ListOfStrings(String s)
      {
         strings = new ArrayList<OneString>();
         strings.add(new OneString(s));
      }
   }

   @Path("/{s}")
   public static class TestResourceWithList
   {
      @Valid ListOfStrings los;

      public TestResourceWithList(@PathParam("s") String s)
      {
         los = new ListOfStrings(s);
      }

      @POST
      public void post()
      {
      }
   }

   public static class MapOfStrings
   {
      @Valid Map<String,OneString> strings;

      public MapOfStrings(String s)
      {
         strings = new HashMap<String,OneString>();
         strings.put(s, new OneString(s));
      }
   }

   @Path("/{s}")
   public static class TestResourceWithMap
   {
      @Valid MapOfStrings mos;

      public TestResourceWithMap(@PathParam("s") String s)
      {
         mos = new MapOfStrings(s);
      }

      @POST
      public void post()
      {
      }
   }

   public static class ListOfArrayOfStrings
   {
      @Valid List<ArrayOfStrings> list;

      public ListOfArrayOfStrings(String s)
      {
         list = new ArrayList<ArrayOfStrings>();
         list.add(new ArrayOfStrings(s));
      }
   }

   public static class MapOfListOfArrayOfStrings
   {
      @Valid Map<String, ListOfArrayOfStrings> map;

      public MapOfListOfArrayOfStrings(String s)
      {
         map = new HashMap<String, ListOfArrayOfStrings>();
         map.put(s, new ListOfArrayOfStrings(s));
      }
   }

   @Path("/{s}")
   public static class TestResourceWithMapOfListOfArrayOfStrings
   {
      @Valid MapOfListOfArrayOfStrings mlas;

      public TestResourceWithMapOfListOfArrayOfStrings(@PathParam("s") String s)
      {
         mlas = new MapOfListOfArrayOfStrings(s);
      }

      @POST
      public void post()
      {
      }
   }

   @FooConstraint(min=1,max=3)
   public static class Foo implements Serializable
   {
      private static final long serialVersionUID = -1068336400309384949L;
      private String s;

      public Foo(String s)
      {
         this.s = s;
      }
      public String toString()
      {
         return "Foo[" + s + "]";
      }
      public boolean equals(Object o)
      {
         if (o == null || !(o instanceof Foo))
         {
            return false;
         }
         return this.s.equals(Foo.class.cast(o).s);
      }
   }

   public static class FooValidator implements ConstraintValidator<FooConstraint, Foo>
   {
      int min;
      int max;

      public void initialize(FooConstraint constraintAnnotation)
      {
         min = constraintAnnotation.min();
         max = constraintAnnotation.max();
      }
      public boolean isValid(Foo value, ConstraintValidatorContext context)
      {
         return min <= value.s.length() && value.s.length() <= max;
      }
   }

   @Documented
   @Constraint(validatedBy = FooValidator.class)
   @Target({TYPE,PARAMETER,METHOD})
   @Retention(RUNTIME)
   public @interface FooConstraint {
      String message() default "s must have length: {min} <= length <= {max}";
      Class<?>[] groups() default {};
      Class<? extends Payload>[] payload() default {};
      int min();
      int max();
   }

   @Provider
   @Produces("application/foo")
   @Consumes("application/foo")
   public static class FooReaderWriter implements MessageBodyReader<Foo>, MessageBodyWriter<Foo>
   {
      public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
      {
         return Foo.class.equals(type);
      }
      public long getSize(Foo t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
      {
         return -1;
      }
      public void writeTo(Foo t, Class<?> type, Type genericType,
            Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders,
            OutputStream entityStream) throws IOException,
            WebApplicationException
      {
    	  byte[] b = t.s.getBytes();
    	  entityStream.write(b.length);
    	  entityStream.write(t.s.getBytes());
    	  entityStream.flush();
      }
      public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
      {
         return Foo.class.equals(type);
      }
      public Foo readFrom(Class<Foo> type, Type genericType,
            Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException
      {
    	  int length = entityStream.read();
    	  byte[] b = new byte[length]; 
    	  entityStream.read(b);
    	  String s = new String(b);
    	  return new Foo(s);
      }
   }

   @Path("/")
   public static class TestResourceWithParameters
   {  
      @POST
      @Path("/native")
      public void postNative(@Valid Foo foo)
      {
      }

      @POST
      @Path("/imposed")
      public void postImposed(@FooConstraint(min=3,max=5) Foo foo)
      {
      }

      @POST
      @Path("nativeAndImposed")
      public void postNativeAndImposed(@Valid @FooConstraint(min=3,max=5) Foo foo)
      {
      }
      
      @POST
      @Path("other/{p}")
      public void postOther(@Size(min=2,max=3) @PathParam("p")   String p,
    		                @Size(min=2,max=3) @MatrixParam("m") String m,
    		                @Size(min=2,max=3) @QueryParam("q")  String q,
    		                @Size(min=2,max=3) @FormParam("f")   String f,
    		                @Size(min=2,max=3) @HeaderParam("h") String h,
    		                @Size(min=2,max=3) @CookieParam("c") String c
    		                )
      {
      }
   }

   @Path("/")
   public static class TestResourceWithReturnValues
   {  
      @POST
      @Path("/native")
      @Valid
      public Foo postNative(Foo foo)
      {
         return foo;
      }

      @POST
      @Path("/imposed")
      @FooConstraint(min=3,max=5)
      public Foo postImposed(Foo foo)
      {
         return foo;
      }
      
      @POST
      @Path("nativeAndImposed")
      @Valid
      @FooConstraint(min=3,max=5)
      public Foo postNativeAndImposed(Foo foo)
      {
         return foo;
      }
   }
   
   public static class TestClassValidator2 implements ConstraintValidator<TestClassConstraint2, TestResourceWithAllFivePotentialViolations>
   {
      int length;

      public void initialize(TestClassConstraint2 constraintAnnotation)
      {
         length = constraintAnnotation.value();
      }

      public boolean isValid(TestResourceWithAllFivePotentialViolations value, ConstraintValidatorContext context)
      {
         return value.s.length() + value.t.length() >= length;
      }

   }

   @Documented
   @Constraint(validatedBy = TestClassValidator2.class)
   @Target({TYPE})
   @Retention(RUNTIME)
   public @interface TestClassConstraint2 {
      String message() default "Concatenation of s and t must have length > {value}";
      Class<?>[] groups() default {};
      Class<? extends Payload>[] payload() default {};
      int value();
   }
   
   @Path("/{s}/{t}")
   @TestClassConstraint2(5)
   public static class TestResourceWithAllFivePotentialViolations
   {
      @Size(min=2, max=4)
      @PathParam("s")
      private String s;

      private String t;

      @Size(min=3, max=5)  
      public String getT()
      {
         return t;
      }

      @PathParam("t") 
      public void setT(String t)
      {
         this.t = t;
      }

      @POST
      @FooConstraint(min=4,max=5)
      public Foo post( @FooConstraint(min=3,max=5) Foo foo)
      {
         return foo;
      }
   }
   
   public interface InterfaceTest
   {
	   @Path("/inherit")
	   @POST
	   @Size(min=2,max=3) String postInherit(@Size(min=2,max=3) String s);
	   
	   @Path("/override")
	   @POST
	   @Size(min=2,max=3) String postOverride(@Size(min=2,max=3) String s);
   }
   
   @Path("/")
   public static class InterFaceTestSuper implements InterfaceTest
   {
	   public String postInherit(String s)
	   {
		   return s;
	   }
	   public String postOverride(String s)
	   {
		   return s;
	   }
   }
   
   @Path("/")
   public static class InterfaceTestSub extends InterFaceTestSuper
   {
	   @Pattern(regexp="[a-z]+") public String postOverride(@Pattern(regexp="[a-z]+") String s)
	   {
		   return s;
	   }
   }
   
   @Path("/")
   public static class TestResourceWithSubLocators
   {
      @Path("validField")
      public TestResourceWithValidField validField()
      {
         return new TestResourceWithValidField();
      }
      
      @Path("invalidField")
      public TestResourceWithInvalidField invalidField()
      {
         return new TestResourceWithInvalidField();
      }
      
      @Path("property/{s}")
      public TestResourceWithProperty property(@PathParam("s") String s)
      {
         TestResourceWithProperty subResource = new TestResourceWithProperty();
         subResource.setS(s);
         return subResource;
      }
      
      @Path("everything/{s}/{t}")
      public TestResourceWithAllFivePotentialViolations everything(@PathParam("s") String s, @PathParam("t") String t)
      {
         TestResourceWithAllFivePotentialViolations subresource = new TestResourceWithAllFivePotentialViolations();
         try
         {
            Field field = TestResourceWithAllFivePotentialViolations.class.getDeclaredField("s");
            field.setAccessible(true);
            field.set(subresource, s);
            subresource.setT(t);
            return subresource;
         }
         catch (Exception e)
         {
            throw new WebApplicationException(e);
         }
      }
      
      @Path("sub/{s}")
      public static class SubResource
      {
         @Path("/")
         public SubSubResource sub(@PathParam("s") String s)
         {
            return new SubSubResource(s);
         }
      }
      
      @Path("")
      public static class SubSubResource
      {
         @Size(min=2,max=3) String s;
         
         public SubSubResource(String s)
         {
            this.s = s;
         }
         
         @POST
         public void subSub()
         {
         }
      }
      
      @Path("sub")
      public SubResource sub()
      {
         return new SubResource();
      }
   }

   public static void before(Class<?> resourceClass) throws Exception
   {
      deployment = EmbeddedContainer.start();
      dispatcher = deployment.getDispatcher();
      deployment.getRegistry().addPerRequestResource(resourceClass);
   }

   public static void beforeFoo(Class<?> resourceClass) throws Exception
   {
      before(resourceClass);
      deployment.getProviderFactory().registerProvider(FooReaderWriter.class);
      deployment.getProviderFactory().registerProvider(FooReaderWriter.class);
   }
   
   public static void beforeFooAsynch(Class<?> resourceClass) throws Exception
   {
      ResteasyDeployment deployment = new ResteasyDeployment();
      deployment.setAsyncJobServiceEnabled(true);
      EmbeddedContainer.start(deployment);
      dispatcher = deployment.getDispatcher();
      dispatcher.getRegistry().addPerRequestResource(resourceClass);
      deployment.getProviderFactory().registerProvider(FooReaderWriter.class);
      deployment.getProviderFactory().registerProvider(FooReaderWriter.class);
   }

   public static void after() throws Exception
   {
      EmbeddedContainer.stop();
      dispatcher = null;
      deployment = null;
   }

   @Test
   @Ignore
   public void testFieldValid() throws Exception
   {
      before(TestResourceWithValidField.class);
      ClientRequest request = new ClientRequest(generateURL("/"));
      ClientResponse<?> response = request.post();
      Assert.assertEquals(204, response.getStatus());
      response.releaseConnection();
      after();
   }

   @Test
   @Ignore
   public void testFieldInvalid() throws Exception
   {
      before(TestResourceWithInvalidField.class);
      ClientRequest request = new ClientRequest(generateURL("/"));
      ClientResponse<?> response = request.post(Serializable.class);
      Assert.assertEquals(400, response.getStatus());
      Object entity = response.getEntity();
      Assert.assertTrue(entity instanceof ResteasyViolationException);
      ResteasyViolationException e = ResteasyViolationException.class.cast(entity);
      countViolations(e, 1, 1, 0, 0, 0, 0);
      String cv = e.getFieldViolations().iterator().next();
      Assert.assertEquals("size must be between 2 and 4", ViolationUtils.getMessage(cv));
      Assert.assertEquals("abcde", ViolationUtils.getInvalidObject(cv));
      Assert.assertNull(getViolationsContainer(e));
      after();
   }

   @Test
   @Ignore
   public void testPropertyValid() throws Exception
   {
      before(TestResourceWithProperty.class);
      ClientRequest request = new ClientRequest(generateURL("/abc"));
      ClientResponse<?> response = request.post();
      Assert.assertEquals(204, response.getStatus());
      response.releaseConnection();
      after();
   }

   @Test
   @Ignore
   public void testPropertyInvalid() throws Exception
   {
      before(TestResourceWithProperty.class);
      ClientRequest request = new ClientRequest(generateURL("/abcdef"));
      ClientResponse<?> response = request.post(Serializable.class);
      Assert.assertEquals(400, response.getStatus());
      Object entity = response.getEntity();
      Assert.assertTrue(entity instanceof ResteasyViolationException);
      ResteasyViolationException e = ResteasyViolationException.class.cast(entity);
      countViolations(e, 1, 0, 1, 0, 0, 0);
      String cv = e.getPropertyViolations().iterator().next();
      Assert.assertEquals("size must be between 2 and 4", ViolationUtils.getMessage(cv));
      Assert.assertEquals("abcdef", ViolationUtils.getInvalidObject(cv));
      Assert.assertNull(getViolationsContainer(e));
      after();
   }

   @Test
   @Ignore
   public void testFieldAndProperty() throws Exception
   {
      before(TestResourceWithFieldAndProperty.class);

      // Valid
      ClientRequest request = new ClientRequest(generateURL("/abc/wxyz"));
      ClientResponse<?> response = request.post(Serializable.class);
      Assert.assertEquals(204, response.getStatus());
      response.releaseConnection();

      // Invalid
      request = new ClientRequest(generateURL("/a/uvwxyz"));
      response = request.post(Serializable.class);
      Assert.assertEquals(400, response.getStatus());
      Object entity = response.getEntity();
      Assert.assertTrue(entity instanceof ResteasyViolationException);
      ResteasyViolationException e = ResteasyViolationException.class.cast(entity);
      countViolations(e, 2, 1, 1, 0, 0, 0);
      String cv = e.getFieldViolations().iterator().next();
      Assert.assertEquals("size must be between 2 and 4", ViolationUtils.getMessage(cv));
      Assert.assertEquals("a", ViolationUtils.getInvalidObject(cv));
      cv = e.getPropertyViolations().iterator().next();
      Assert.assertEquals("size must be between 3 and 5", ViolationUtils.getMessage(cv));
      Assert.assertEquals("uvwxyz", ViolationUtils.getInvalidObject(cv));
      Assert.assertNull(getViolationsContainer(e));
      after();
   }

   @Test
   @Ignore
   public void testClassConstraint() throws Exception
   {
      before(TestResourceWithClassConstraint.class);

      // Valid
      ClientRequest request = new ClientRequest(generateURL("/abc/xyz"));
      ClientResponse<?> response = request.post(Serializable.class);      
      Assert.assertEquals(204, response.getStatus());
      response.releaseConnection();

      // Invalid
      request = new ClientRequest(generateURL("/a/b"));
      response = request.post(Serializable.class);
      Assert.assertEquals(400, response.getStatus());
      Object entity = response.getEntity();
      Assert.assertTrue(entity instanceof ResteasyViolationException);
      ResteasyViolationException e = ResteasyViolationException.class.cast(entity);
      countViolations(e, 1, 0, 0, 1, 0, 0);
      String cv = e.getClassViolations().iterator().next();
      Assert.assertEquals("Concatenation of s and t must have length > 5", ViolationUtils.getMessage(cv));
      Assert.assertEquals("TestResourceWithClassConstraint(\"a\", \"b\")", ViolationUtils.getInvalidObject(cv));
      System.out.println(ViolationUtils.getInvalidObject(cv));
      Assert.assertNull(getViolationsContainer(e));
      after();
   }

   @Test
   @Ignore
   public void testGraph() throws Exception
   {
      before(TestResourceWithGraph.class);

      // Valid
      ClientRequest request = new ClientRequest(generateURL("/abcd/vwxyz"));
      ClientResponse<?> response = request.post(Serializable.class);      
      Assert.assertEquals(204, response.getStatus());
      response.releaseConnection();

      // Invalid
      request = new ClientRequest(generateURL("/abc/xyz"));
      response = request.post(Serializable.class);
      Assert.assertEquals(400, response.getStatus());
      Object entity = response.getEntity();
      Assert.assertTrue(entity instanceof ResteasyViolationException);
      ResteasyViolationException e = ResteasyViolationException.class.cast(entity);
      countViolations(e, 2, 1, 1, 0, 0, 0);
      String cv = e.getFieldViolations().iterator().next();
      Assert.assertTrue(ViolationUtils.getMessage(cv).startsWith("size must be between 4 and"));
      Assert.assertEquals("abc", ViolationUtils.getInvalidObject(cv));
      cv = e.getPropertyViolations().iterator().next();
      Assert.assertTrue(ViolationUtils.getMessage(cv).startsWith("size must be between 5 and"));
      Assert.assertEquals("xyz", ViolationUtils.getInvalidObject(cv));
      Assert.assertNull(getViolationsContainer(e));
      after();
   }

   @Test
   @Ignore
   public void testArray() throws Exception
   {
      before(TestResourceWithArray.class);

      // Valid
      ClientRequest request = new ClientRequest(generateURL("/abcde"));
      ClientResponse<?> response = request.post(Serializable.class);      
      Assert.assertEquals(204, response.getStatus());
      response.releaseConnection();

      // Invalid
      request = new ClientRequest(generateURL("/abc"));
      response = request.post(Serializable.class);
      Assert.assertEquals(400, response.getStatus());
      Object entity = response.getEntity();
      Assert.assertTrue(entity instanceof ResteasyViolationException);
      ResteasyViolationException e = ResteasyViolationException.class.cast(entity);
      countViolations(e, 1, 0, 1, 0, 0, 0);
      String cv = e.getPropertyViolations().iterator().next();
      Assert.assertTrue(ViolationUtils.getMessage(cv).startsWith("size must be between 5 and"));
      Assert.assertEquals("abc", ViolationUtils.getInvalidObject(cv));
      Assert.assertNull(getViolationsContainer(e));
      after();
   }

   @Test
   @Ignore
   public void testList() throws Exception
   {
      before(TestResourceWithList.class);

      // Valid
      ClientRequest request = new ClientRequest(generateURL("/abcde"));
      ClientResponse<?> response = request.post(Serializable.class);      
      Assert.assertEquals(204, response.getStatus());
      response.releaseConnection();

      // Invalid
      request = new ClientRequest(generateURL("/abc"));
      response = request.post(Serializable.class);
      Assert.assertEquals(400, response.getStatus());
      Object entity = response.getEntity();
      Assert.assertTrue(entity instanceof ResteasyViolationException);
      ResteasyViolationException e = ResteasyViolationException.class.cast(entity);
      countViolations(e, 1, 0, 1, 0, 0, 0);
      String cv = e.getPropertyViolations().iterator().next();
      Assert.assertTrue(ViolationUtils.getMessage(cv).startsWith("size must be between 5 and"));
      Assert.assertEquals("abc", ViolationUtils.getInvalidObject(cv));
      Assert.assertNull(getViolationsContainer(e));
      after();
   }

   @Test
   @Ignore
   public void testMap() throws Exception
   {
      before(TestResourceWithMap.class);

      // Valid
      ClientRequest request = new ClientRequest(generateURL("/abcde"));
      ClientResponse<?> response = request.post(Serializable.class);      
      Assert.assertEquals(204, response.getStatus());
      response.releaseConnection();

      // Invalid
      request = new ClientRequest(generateURL("/abc"));
      response = request.post(Serializable.class);
      Assert.assertEquals(400, response.getStatus());
      Object entity = response.getEntity();
      Assert.assertTrue(entity instanceof ResteasyViolationException);
      ResteasyViolationException e = ResteasyViolationException.class.cast(entity);
      countViolations(e, 1, 0, 1, 0, 0, 0);
      String cv = e.getPropertyViolations().iterator().next();
      Assert.assertTrue(ViolationUtils.getMessage(cv).startsWith("size must be between 5 and"));
      Assert.assertEquals("abc", ViolationUtils.getInvalidObject(cv));
      Assert.assertNull(getViolationsContainer(e));
      after();
   }

   @Test
   @Ignore
   public void testMapOfListOfArrayOfStrings() throws Exception
   {
      before(TestResourceWithMapOfListOfArrayOfStrings.class);

      // Valid
      ClientRequest request = new ClientRequest(generateURL("/abcde"));
      ClientResponse<?> response = request.post(Serializable.class);      
      Assert.assertEquals(204, response.getStatus());
      response.releaseConnection();

      // Invalid
      request = new ClientRequest(generateURL("/abc"));
      response = request.post(Serializable.class);
      Assert.assertEquals(400, response.getStatus());
      Object entity = response.getEntity();
      Assert.assertTrue(entity instanceof ResteasyViolationException);
      ResteasyViolationException e = ResteasyViolationException.class.cast(entity);
      countViolations(e, 1, 0, 1, 0, 0, 0);
      String cv = e.getPropertyViolations().iterator().next();
      Assert.assertTrue(ViolationUtils.getMessage(cv).startsWith("size must be between 5 and"));
      Assert.assertEquals("abc", ViolationUtils.getInvalidObject(cv));
      Assert.assertNull(getViolationsContainer(e));
      after();
   }

   @Test
   @Ignore
   public void testParameters() throws Exception
   {
      beforeFoo(TestResourceWithParameters.class);

      // Valid native constraint
      ClientRequest request = new ClientRequest(generateURL("/native"));
      request.body("application/foo", new Foo("a"));
      ClientResponse<?> response = request.post(Serializable.class);      
      Assert.assertEquals(204, response.getStatus());
      response.releaseConnection();

      // Valid imposed constraint
      request = new ClientRequest(generateURL("/imposed"));
      request.body("application/foo", new Foo("abcde"));
      response = request.post(Serializable.class);      
      Assert.assertEquals(204, response.getStatus());
      response.releaseConnection();

      // Valid native and imposed constraints.
      request = new ClientRequest(generateURL("/nativeAndImposed"));
      request.body("application/foo", new Foo("abc"));
      response = request.post(Serializable.class);      
      Assert.assertEquals(204, response.getStatus());
      response.releaseConnection();

      // Invalid native constraint
      request = new ClientRequest(generateURL("/native"));
      request.body("application/foo", new Foo("abcdef"));
      response = request.post(Serializable.class);
      Assert.assertEquals(400, response.getStatus());
      Object entity = response.getEntity();
      Assert.assertTrue(entity instanceof ResteasyViolationException);
      ResteasyViolationException e = ResteasyViolationException.class.cast(entity);
      countViolations(e, 1, 0, 0, 0, 1, 0);
      String cv = e.getParameterViolations().iterator().next();
      Assert.assertTrue(ViolationUtils.getMessage(cv).equals("s must have length: 1 <= length <= 3"));
      Assert.assertEquals("Foo[abcdef]", ViolationUtils.getInvalidObject(cv));
      Assert.assertNull(getViolationsContainer(e));

      // Invalid imposed constraint
      request = new ClientRequest(generateURL("/imposed"));
      request.body("application/foo", new Foo("abcdef"));
      response = request.post(Serializable.class);
      Assert.assertEquals(400, response.getStatus());
      entity = response.getEntity();
      Assert.assertTrue(entity instanceof ResteasyViolationException);
      e = ResteasyViolationException.class.cast(entity);
      countViolations(e, 1, 0, 0, 0, 1, 0);
      cv = e.getParameterViolations().iterator().next();
      Assert.assertTrue(ViolationUtils.getMessage(cv).equals("s must have length: 3 <= length <= 5"));
      Assert.assertEquals("Foo[abcdef]", ViolationUtils.getInvalidObject(cv));
      Assert.assertNull(getViolationsContainer(e));

      // Invalid native and imposed constraints
      request = new ClientRequest(generateURL("/nativeAndImposed"));
      request.body("application/foo", new Foo("abcdef"));
      response = request.post(Serializable.class);
      Assert.assertEquals(400, response.getStatus());
      entity = response.getEntity();
      Assert.assertTrue(entity instanceof ResteasyViolationException);
      e = ResteasyViolationException.class.cast(entity);
      countViolations(e, 2, 0, 0, 0, 2, 0);
      Iterator<String> it = e.getParameterViolations().iterator(); 
      String cv1 = it.next();
      String cv2 = it.next();
      if (cv1.indexOf('1') < 0)
      {
         String temp = cv1;
         cv1 = cv2;
         cv2 = temp;
      }
      Assert.assertTrue(ViolationUtils.getMessage(cv1).equals("s must have length: 1 <= length <= 3"));
      Assert.assertEquals("Foo[abcdef]", ViolationUtils.getInvalidObject(cv1));
      Assert.assertTrue(ViolationUtils.getMessage(cv2).equals("s must have length: 3 <= length <= 5"));
      Assert.assertEquals("Foo[abcdef]", ViolationUtils.getInvalidObject(cv2));
      Assert.assertNull(getViolationsContainer(e));
      
      // Valid other parameters
      String url = generateURL("/other/ppp"); // path param
      url += ";m=mmm";                        // matrix param
      url += "?q=qqq";                        // query param
      request = new ClientRequest(url);
      request.formParameter("f", "fff");      // form param
      request.header("h", "hhh");             // header param
      request.cookie(new Cookie("c", "ccc")); // cookie param
      response = request.post();
      Assert.assertEquals(204, response.getStatus());
      response.releaseConnection();
      
      // Invalid other parameters
      url = generateURL("/other/pppp");        // path param
      url += ";m=mmmm";                        // matrix param
      url += "?q=qqqq";                        // query param
      request = new ClientRequest(url);
      request.formParameter("f", "ffff");      // form param
      request.header("h", "hhhh");             // header param
      request.cookie(new Cookie("c", "cccc")); // cookie param
      response = request.post(Serializable.class);
      Assert.assertEquals(400, response.getStatus());
      entity = response.getEntity();
      Assert.assertTrue(entity instanceof ResteasyViolationException);
      e = ResteasyViolationException.class.cast(entity);
      countViolations(e, 6, 0, 0, 0, 6, 0);
      Set<String> set = new HashSet<String>(e.getExceptions());
      Assert.assertTrue(set.contains("size must be between 2 and 3; pppp"));
      Assert.assertTrue(set.contains("size must be between 2 and 3; mmmm"));
      Assert.assertTrue(set.contains("size must be between 2 and 3; qqqq"));
      Assert.assertTrue(set.contains("size must be between 2 and 3; ffff"));
      Assert.assertTrue(set.contains("size must be between 2 and 3; hhhh"));
      Assert.assertTrue(set.contains("size must be between 2 and 3; cccc"));
      after();
   }

   @Test
   @Ignore
   public void testReturnValues() throws Exception
   {
      beforeFoo(TestResourceWithReturnValues.class);

      // Valid native constraint
      ClientRequest request = new ClientRequest(generateURL("/native"));
      Foo foo = new Foo("a");
      request.body("application/foo", foo);
      ClientResponse<?> response = request.post(Foo.class);     
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals(foo, response.getEntity());
      
      // Valid imposed constraint
      request = new ClientRequest(generateURL("/imposed"));
      foo = new Foo("abcde");
      request.body("application/foo", foo);
      response = request.post(Foo.class);      
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals(foo, response.getEntity());

      // Valid native and imposed constraints.
      request = new ClientRequest(generateURL("/nativeAndImposed"));
      foo = new Foo("abc");
      request.body("application/foo", foo);
      response = request.post(Foo.class);      
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals(foo, response.getEntity());

      // Invalid native constraint
      request = new ClientRequest(generateURL("/native"));
      request.body("application/foo", new Foo("abcdef"));
      response = request.post(Foo.class);
      Assert.assertEquals(500, response.getStatus());
      Object entity = response.getEntity();
      Assert.assertTrue(entity instanceof ResteasyViolationException);
      ResteasyViolationException e = ResteasyViolationException.class.cast(entity);
      countViolations(e, 1, 0, 0, 0, 0, 1);
      String cv = e.getReturnValueViolations().iterator().next();
      Assert.assertTrue(ViolationUtils.getMessage(cv).equals("s must have length: 1 <= length <= 3"));
      Assert.assertEquals("Foo[abcdef]", ViolationUtils.getInvalidObject(cv));

      // Invalid imposed constraint
      request = new ClientRequest(generateURL("/imposed"));
      request.body("application/foo", new Foo("abcdef"));
      response = request.post(Foo.class);
      Assert.assertEquals(500, response.getStatus());
      entity = response.getEntity();
      Assert.assertTrue(entity instanceof ResteasyViolationException);
      e = ResteasyViolationException.class.cast(entity);
      countViolations(e, 1, 0, 0, 0, 0, 1);
      cv = e.getReturnValueViolations().iterator().next();
      Assert.assertTrue(ViolationUtils.getMessage(cv).equals("s must have length: 3 <= length <= 5"));
      Assert.assertEquals("Foo[abcdef]", ViolationUtils.getInvalidObject(cv));
      Assert.assertNull(getViolationsContainer(e));

      // Invalid native and imposed constraints
      request = new ClientRequest(generateURL("/nativeAndImposed"));
      request.body("application/foo", new Foo("abcdef"));
      response = request.post(Foo.class); 
      Assert.assertEquals(500, response.getStatus());
      entity = response.getEntity();
      Assert.assertTrue(entity instanceof ResteasyViolationException);
      e = ResteasyViolationException.class.cast(entity);
      countViolations(e, 2, 0, 0, 0, 0, 2);
      Iterator<String> it = e.getReturnValueViolations().iterator(); 
      String cv1 = it.next();
      String cv2 = it.next();
      if (cv1.indexOf('1') < 0)
      {
         String temp = cv1;
         cv1 = cv2;
         cv2 = temp;
      }
      Assert.assertTrue(ViolationUtils.getMessage(cv1).equals("s must have length: 1 <= length <= 3"));
      Assert.assertEquals("Foo[abcdef]", ViolationUtils.getInvalidObject(cv1));
      Assert.assertTrue(ViolationUtils.getMessage(cv2).equals("s must have length: 3 <= length <= 5"));
      Assert.assertEquals("Foo[abcdef]", ViolationUtils.getInvalidObject(cv2));
      Assert.assertNull(getViolationsContainer(e));
      after();
   }

   @Test
   @Ignore
   public void testViolationsBeforeReturnValue() throws Exception
   {
      beforeFoo(TestResourceWithAllFivePotentialViolations.class);

      // Valid
      ClientRequest request = new ClientRequest(generateURL("/abc/wxyz"));
      Foo foo = new Foo("pqrs");
      request.body("application/foo", foo);
      ClientResponse<?> response = request.post(Foo.class);     
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals(foo, response.getEntity());

      // Invalid: Should have 1 each of field, property, class, and parameter violations,
      //          and no return value violations.
      request = new ClientRequest(generateURL("/a/z"));
      foo = new Foo("p");
      request.body("application/foo", foo);
      response = request.post(Foo.class);
      Assert.assertEquals(400, response.getStatus());
      Object entity = response.getEntity();
      Assert.assertTrue(entity instanceof ResteasyViolationException);
      ResteasyViolationException e = ResteasyViolationException.class.cast(entity);
      countViolations(e, 4, 1, 1, 1, 1, 0);
      String cv = e.getFieldViolations().iterator().next();
      Assert.assertEquals("size must be between 2 and 4", ViolationUtils.getMessage(cv));
      Assert.assertEquals("a", ViolationUtils.getInvalidObject(cv));
      cv = e.getPropertyViolations().iterator().next();
      Assert.assertEquals("size must be between 3 and 5", ViolationUtils.getMessage(cv));
      Assert.assertEquals("z", ViolationUtils.getInvalidObject(cv));
      cv = e.getClassViolations().iterator().next();
      Assert.assertEquals("Concatenation of s and t must have length > 5", ViolationUtils.getMessage(cv));
      Assert.assertTrue(ViolationUtils.getInvalidObject(cv).startsWith("org.jboss.resteasy.test.validation.TestValidation$TestResourceWithAllFivePotentialViolations@"));
      cv = e.getParameterViolations().iterator().next();
      Assert.assertEquals("s must have length: 3 <= length <= 5", ViolationUtils.getMessage(cv));
      Assert.assertEquals("Foo[p]", ViolationUtils.getInvalidObject(cv));
      Assert.assertNull(getViolationsContainer(e));
      after();
   }
   
   //@Test
   // Commenting out until inheritance issues are worked out in JAX-RS spec.
   @Ignore
   public void testInheritence() throws Exception
   {
      beforeFoo(InterfaceTestSub.class);

      // Valid - inherited annotations
      ClientRequest request = new ClientRequest(generateURL("/inherit"));
      request.body(MediaType.TEXT_PLAIN_TYPE, "abc");
      ClientResponse<?> response = request.post(String.class);     
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("abc", response.getEntity());
      
      // Valid - overridden annotations
      request = new ClientRequest(generateURL("/override"));
      request.body(MediaType.TEXT_PLAIN_TYPE, "abcde");
      response = request.post(String.class);     
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("abcde", response.getEntity());
   }
   
   @Test
   @Ignore
   public void testLocators() throws Exception
   {
      beforeFoo(TestResourceWithSubLocators.class);
      
//      ClientRequest request = null;
//      ClientResponse<?> response = null;
//      Object entity = null;
//      ResteasyViolationException e = null;
//      String cv = null;
      
      // Sub-resource locator returns resource with valid field.
      ClientRequest request = new ClientRequest(generateURL("/validField"));
      ClientResponse<?> response = request.post();
      Assert.assertEquals(204, response.getStatus());
      response.releaseConnection();

      // Sub-resource locator returns resource with invalid field.
      request = new ClientRequest(generateURL("/invalidField"));
      response = request.post(Serializable.class);
      Assert.assertEquals(400, response.getStatus());
      Object entity = response.getEntity();
      Assert.assertTrue(entity instanceof ResteasyViolationException);
      ResteasyViolationException e = ResteasyViolationException.class.cast(entity);
      countViolations(e, 1, 1, 0, 0, 0, 0);
      String cv = e.getFieldViolations().iterator().next();
      Assert.assertEquals("size must be between 2 and 4", ViolationUtils.getMessage(cv));
      Assert.assertEquals("abcde", ViolationUtils.getInvalidObject(cv));
      Assert.assertNull(getViolationsContainer(e));

      // Sub-resource locator returns resource with valid property.
      // Note: The resource TestResourceWithProperty has a @PathParam annotation used by a setter,
      //       but it is not used when TestResourceWithProperty is used a sub-resource.  Hence "unused".
      request = new ClientRequest(generateURL("/property/abc/unused"));
      response = request.post();
      Assert.assertEquals(204, response.getStatus());
      response.releaseConnection();
      
      // Sub-resource locator returns resource with invalid property.
      request = new ClientRequest(generateURL("/property/abcdef/unused"));
      response = request.post(Serializable.class);
      Assert.assertEquals(400, response.getStatus());
      entity = response.getEntity();
      Assert.assertTrue(entity instanceof ResteasyViolationException);
      e = ResteasyViolationException.class.cast(entity);
      countViolations(e, 1, 0, 1, 0, 0, 0);
      cv = e.getPropertyViolations().iterator().next();
      Assert.assertEquals("size must be between 2 and 4", ViolationUtils.getMessage(cv));
      Assert.assertEquals("abcdef", ViolationUtils.getInvalidObject(cv));
      Assert.assertNull(getViolationsContainer(e));

      // Valid
      request = new ClientRequest(generateURL("/everything/abc/wxyz/unused/unused"));
      Foo foo = new Foo("pqrs");
      request.body("application/foo", foo);
      response = request.post(Foo.class);     
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals(foo, response.getEntity());

      // Invalid: Should have 1 each of field, property, class, and parameter violations,and no return value violations.
      // Note: expect warning because TestResourceWithAllFivePotentialViolations is being used a sub-resource and it has an injectible field:
      //       WARN org.jboss.resteasy.core.ResourceLocator - Field s of subresource org.jboss.resteasy.test.validation.TestValidation$TestResourceWithAllFivePotentialViolations will not be injected according to spec
      request = new ClientRequest(generateURL("/everything/a/z/unused/unused"));
      foo = new Foo("p");
      request.body("application/foo", foo);
      response = request.post(Foo.class);
      Assert.assertEquals(400, response.getStatus());
      entity = response.getEntity();
      Assert.assertTrue(entity instanceof ResteasyViolationException);
      e = ResteasyViolationException.class.cast(entity);
      countViolations(e, 4, 1, 1, 1, 1, 0);
      cv = e.getFieldViolations().iterator().next();
      Assert.assertEquals("size must be between 2 and 4", ViolationUtils.getMessage(cv));
      Assert.assertEquals("a", ViolationUtils.getInvalidObject(cv));
      cv = e.getPropertyViolations().iterator().next();
      Assert.assertEquals("size must be between 3 and 5", ViolationUtils.getMessage(cv));
      cv = e.getClassViolations().iterator().next();
      Assert.assertEquals("Concatenation of s and t must have length > 5", ViolationUtils.getMessage(cv));
      Assert.assertTrue(ViolationUtils.getInvalidObject(cv).startsWith("org.jboss.resteasy.test.validation.TestValidation$TestResourceWithAllFivePotentialViolations@"));
      Assert.assertNull(getViolationsContainer(e));

      // Sub-sub-resource locator returns resource with valid property.
      request = new ClientRequest(generateURL("/sub/sub/abc"));
      response = request.post();
      Assert.assertEquals(204, response.getStatus());
      response.releaseConnection();

      // Sub-resource locator returns resource with invalid property.
      request = new ClientRequest(generateURL("/sub/sub/abcdef"));
      response = request.post(Serializable.class);
      Assert.assertEquals(400, response.getStatus());
      entity = response.getEntity();
      Assert.assertTrue(entity instanceof ResteasyViolationException);
      e = ResteasyViolationException.class.cast(entity);
      countViolations(e, 1, 1, 0, 0, 0, 0);
      cv = e.getFieldViolations().iterator().next();
      Assert.assertEquals("size must be between 2 and 3", ViolationUtils.getMessage(cv));
      Assert.assertEquals("abcdef", ViolationUtils.getInvalidObject(cv));
      Assert.assertNull(getViolationsContainer(e));
      
      after();
   }

   @Test
   @Ignore
   public void testAsynch() throws Exception
   {
      beforeFooAsynch(TestResourceWithAllFivePotentialViolations.class);
      
      // Submit asynchronous job with violations prior to execution of resource method.
      ClientRequest request = new ClientRequest(generateURL("/a/z?asynch=true"));
      Foo foo = new Foo("p");
      request.body("application/foo", foo);
      ClientResponse<?> response = request.post(Foo.class);
      Assert.assertEquals(HttpServletResponse.SC_ACCEPTED, response.getStatus());
      String jobUrl = response.getResponseHeaders().getFirst(HttpHeaders.LOCATION);
      System.out.println("JOB: " + jobUrl);
      response.releaseConnection();
      
      // Get result: Should have 1 each of field, property, class, and parameter violations,
      //             and no return value violations.
      request = new ClientRequest(jobUrl);
      response = request.get();
      while (HttpServletResponse.SC_ACCEPTED == response.getStatus())
      {
         Thread.sleep(1000);
         response.releaseConnection();
         response = request.get();
      }
      Assert.assertEquals(400, response.getStatus());
      Object entity = response.getEntity(Exception.class);
      Assert.assertTrue(entity instanceof ResteasyViolationException);
      ResteasyViolationException e = ResteasyViolationException.class.cast(entity);
      countViolations(e, 4, 1, 1, 1, 1, 0);
      String cv = e.getFieldViolations().iterator().next();
      Assert.assertEquals("size must be between 2 and 4", ViolationUtils.getMessage(cv));
      Assert.assertEquals("a", ViolationUtils.getInvalidObject(cv));
      cv = e.getPropertyViolations().iterator().next();
      Assert.assertEquals("size must be between 3 and 5", ViolationUtils.getMessage(cv));
      Assert.assertEquals("z", ViolationUtils.getInvalidObject(cv));
      cv = e.getClassViolations().iterator().next();
      Assert.assertEquals("Concatenation of s and t must have length > 5", ViolationUtils.getMessage(cv));
      Assert.assertTrue(ViolationUtils.getInvalidObject(cv).startsWith("org.jboss.resteasy.test.validation.TestValidation$TestResourceWithAllFivePotentialViolations@"));
      cv = e.getParameterViolations().iterator().next();
      Assert.assertEquals("s must have length: 3 <= length <= 5", ViolationUtils.getMessage(cv));
      Assert.assertEquals("Foo[p]", ViolationUtils.getInvalidObject(cv));
      Assert.assertNull(getViolationsContainer(e));

      // Delete job.
      request = new ClientRequest(jobUrl);
      response = request.delete();
      Assert.assertEquals(HttpServletResponse.SC_NO_CONTENT, response.getStatus());
      response.releaseConnection();
      
      // Submit asynchronous job with violations in result of resource method.
      request = new ClientRequest(generateURL("/abc/xyz?asynch=true"));
      foo = new Foo("pqr");
      request.body("application/foo", foo);
      response = request.post(Foo.class);
      Assert.assertEquals(HttpServletResponse.SC_ACCEPTED, response.getStatus());
      jobUrl = response.getResponseHeaders().getFirst(HttpHeaders.LOCATION);
      System.out.println("JOB: " + jobUrl);
      response.releaseConnection();

      // Get result: Should have no field, property, class, or parameter violations,
      //             and one return value violation.
      request = new ClientRequest(jobUrl);
      response = request.get();
      while (HttpServletResponse.SC_ACCEPTED == response.getStatus())
      {
         Thread.sleep(1000);
         response.releaseConnection();
         response = request.get();
      }
      Assert.assertEquals(500, response.getStatus());
      entity = response.getEntity(Exception.class);
      Assert.assertTrue(entity instanceof ResteasyViolationException);
      e = ResteasyViolationException.class.cast(entity);
      countViolations(e, 1, 0, 0, 0, 0, 1);
      cv = e.getReturnValueViolations().iterator().next();
      Assert.assertEquals("s must have length: 4 <= length <= 5", ViolationUtils.getMessage(cv));
      Assert.assertEquals("Foo[pqr]", ViolationUtils.getInvalidObject(cv));
      Assert.assertNull(getViolationsContainer(e));
      
      // Delete job.
      request = new ClientRequest(jobUrl);
      response = request.delete();
      Assert.assertEquals(HttpServletResponse.SC_NO_CONTENT, response.getStatus());
      response.releaseConnection();

      after();
   }
   
   private void countViolations(ResteasyViolationException e, int totalCount, int fieldCount, int propertyCount, int classCount, int parameterCount, int returnValueCount)
   {
      Assert.assertEquals(totalCount,       e.getExceptions().size());
      Assert.assertEquals(fieldCount,       e.getFieldViolations().size());
      Assert.assertEquals(propertyCount,    e.getPropertyViolations().size());
      Assert.assertEquals(classCount,       e.getClassViolations().size());
      Assert.assertEquals(parameterCount,   e.getParameterViolations().size());
      Assert.assertEquals(returnValueCount, e.getReturnValueViolations().size());
   }
   
   private ViolationsContainer<?> getViolationsContainer(ResteasyViolationException e) throws NoSuchFieldException, IllegalAccessException
   {
	   Field container = ResteasyViolationException.class.getDeclaredField("container");
	   container.setAccessible(true);
	   return ViolationsContainer.class.cast(container.get(e));
   }
}
