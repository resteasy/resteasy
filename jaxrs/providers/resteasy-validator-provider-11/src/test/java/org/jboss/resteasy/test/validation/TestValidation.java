package org.jboss.resteasy.test.validation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.jboss.resteasy.test.TestPortProvider.generateURL;

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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.validation.constraintvalidation.SupportedValidationTarget;
import javax.validation.constraintvalidation.ValidationTarget;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
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

import junit.framework.Assert;

import org.jboss.resteasy.api.validation.ResteasyConstraintViolation;
import org.jboss.resteasy.api.validation.ResteasyViolationException;
import org.jboss.resteasy.api.validation.Validation;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.ClientResponseFailure;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.junit.Test;

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
      @Path("{unused}")
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
      @Path("{unused}/{unused}")
      @FooConstraint(min=4,max=5)
      public Foo post( @FooConstraint(min=3,max=5) Foo foo)
      {
         return foo;
      }
   }
   
   @Documented
   @Constraint(validatedBy = TestClassValidatorSubInheritance.class)
   @Target({TYPE})
   @Retention(RUNTIME)
   public @interface TestClassInheritanceSubConstraint {
      String message() default "u must have value {value}";
      Class<?>[] groups() default {};
      Class<? extends Payload>[] payload() default {};
      String value();
   }
   
   @Documented
   @Constraint(validatedBy = TestClassValidatorSuperInheritance.class)
   @Target({TYPE})
   @Retention(RUNTIME)
   public @interface TestClassInheritanceSuperConstraint {
      String message() default "t must have length > {value}";
      Class<?>[] groups() default {};
      Class<? extends Payload>[] payload() default {};
      int value();
   }
   
   public static class TestClassValidatorSubInheritance implements ConstraintValidator<TestClassInheritanceSubConstraint, InterfaceTestSub>
   {
      String pattern;

      public void initialize(TestClassInheritanceSubConstraint constraintAnnotation)
      {
         pattern = constraintAnnotation.value();
      }

      public boolean isValid(InterfaceTestSub value, ConstraintValidatorContext context)
      {
         System.out.println(this + "u: " + value.u);
         System.out.println("pattern: " + pattern + ", matches: " + value.u.matches(pattern));
         return value.u.matches(pattern);
      }
   }
   
   public static class TestClassValidatorSuperInheritance implements ConstraintValidator<TestClassInheritanceSuperConstraint, InterfaceTestSuper>
   {
      int length;

      public void initialize(TestClassInheritanceSuperConstraint constraintAnnotation)
      {
         length = constraintAnnotation.value();
      }

      public boolean isValid(InterfaceTestSuper value, ConstraintValidatorContext context)
      {
         System.out.println(this + " t: " + value.t);
         return value.t.length() >= length;
      }
   }
   
   @Path("/")
   public interface InterfaceTest
   {
      @Path("/inherit")
      @POST
      @Size(min=2,max=3) String postInherit(@Size(min=2,max=4) String s);
      
      @Path("/override")
      @POST
      @Size(min=2,max=3) String postOverride(@Size(min=2,max=4) String s);
   }
   
   @Path("/")
   @TestClassInheritanceSuperConstraint(3)
   public static class InterfaceTestSuper implements InterfaceTest
   {
//      @PathParam("t")
      static String t;
      
	   public String postInherit(String s)
	   {
		   return s;
	   }
	   public String postOverride(String s)
	   {
		   return s;
	   }
	   public String concat()
	   {
	      return t + t;
	   }
   }
   
   @Path("/")
   @TestClassInheritanceSubConstraint("[a-c]+")
   public static class InterfaceTestSub extends InterfaceTestSuper
   {
//      @PathParam("u")
      static String u;
      
	   @Pattern(regexp="[a-c]+") public String postOverride(String s)
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
      
      @Path("locator")
      public SubResource sub()
      {
         System.out.println("return new SubResource()");
         return new SubResource();
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
      
      @Path("")
      public static class SubResource
      {
         @Path("sublocator/{s}")
         public SubSubResource sub(@PathParam("s") String s)
         {
            SubSubResource ssr = new SubSubResource();
            ssr.setS(s);
            System.out.println("returning new SubSubResource()");
            return ssr;
         }
      }
      
      @Path("")
      public static class SubSubResource
      {
         @Size(min=2,max=3) String s;
         
         public SubSubResource()
         {
         }
         
         public void setS(String s)
         {
            this.s = s;
         }
         
         @POST
         public void subSub()
         {
         }
      }
   }
   

   
   @Documented
   @Constraint(validatedBy = CrossParameterValidator.class)
   @Target({METHOD})
   @Retention(RUNTIME)
   public @interface CrossParameterConstraint
   {
      String message() default "Parameters must total <= {value}";
      Class<?>[] groups() default {};
      Class<? extends Payload>[] payload() default {};
      int value();
   }
   
   @SupportedValidationTarget(ValidationTarget.PARAMETERS)
   public static class CrossParameterValidator implements ConstraintValidator<CrossParameterConstraint, Object[]>
   {
      private CrossParameterConstraint constraintAnnotation;
      
      @Override
      public void initialize(CrossParameterConstraint constraintAnnotation)
      {
         this.constraintAnnotation = constraintAnnotation;
      }
      @Override
      public boolean isValid(Object[] value, ConstraintValidatorContext context)
      {
         int sum = 0;
         
         for (int i = 0; i < value.length; i++)
         {
            if (!(value[i] instanceof Integer))
            {
               return false;
            }
            sum += Integer.class.cast(value[i]);
         }
         return sum <= constraintAnnotation.value();
      }
   }

   @Path("/{s}/{t}")
   public static class TestSubResourceWithCrossParameterConstraint
   {
      @POST
      @CrossParameterConstraint(7)
      public void test(@PathParam("s") int s, @PathParam("t") int t)
      {
      }
   }
   

   @Path("proxy")
   public static interface TestProxyInterface
   {
      @GET
      @Produces("text/plain")
      @Size(min=2, max=4)
      public String g();
      
      @POST
      @Path("{s}")
      public void s(@PathParam("s") String s);
   }
   
   @Path("proxy")
   public static class TestProxyResource implements TestProxyInterface
   {
      static private String s;
      
      @GET
      @Produces("text/plain")
      @Size(min=2, max=4)
      public String g()
      {
         return s;
      }

      @POST
      @Path("{s}")
      public void s(@PathParam("s") String s)
      {
         TestProxyResource.s = s;
      }
   }
   
  interface OtherGroup {}
   
   @Documented
   @Constraint(validatedBy = TestClassValidator.class)
   @Target({TYPE})
   @Retention(RUNTIME)
   public @interface OtherGroupConstraint
   {
      String message() default "Concatenation of s and t must have length > {value}";
      Class<?>[] groups() default {};
      Class<? extends Payload>[] payload() default {};
      String value() default "";
   }
   
   public static class OtherGroupValidator<T> implements ConstraintValidator<OtherGroupConstraint, T>
   {
      public void initialize(OtherGroupConstraint constraintAnnotation)
      {
      }
      
      @Override
      public boolean isValid(T value, ConstraintValidatorContext context)
      {
         return false;
      }
   }
   
   @Path("/")
   @OtherGroupConstraint(groups=OtherGroup.class)
   public static class TestResourceWithOtherGroups
   {
      @Size(min=2, groups=OtherGroup.class)
      String s = "abc";
      
      String t;
      
      @POST
      @Path("test/{t}/{u}")
      @Size(min=2, groups={OtherGroup.class})
      public String test(@Size(min=2, groups=OtherGroup.class) @PathParam("u") String u)
      {
         return u;
      }
      
      @PathParam("t")
      public void setT(String t)
      {
         this.t = t;
      }
      
      @Size(min=2, groups=OtherGroup.class)
      public String getT()
      {
         return t;
      }
   }
   
   public static void before(Class<?> resourceClass) throws Exception
   {
      after();
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
//   @Ignore
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
//   @Ignore
   public void testFieldInvalid() throws Exception
   {
      before(TestResourceWithInvalidField.class);
      ClientRequest request = new ClientRequest(generateURL("/"));
      ClientResponse<?> response = request.post();
      Assert.assertEquals(400, response.getStatus());
      Object entity = response.getEntity(String.class);
      ResteasyViolationException e = new ResteasyViolationException(String.class.cast(entity));
      countViolations(e, 1, 1, 0, 0, 0, 0);
      ResteasyConstraintViolation cv = e.getFieldViolations().iterator().next();
      System.out.println("cv: " + cv);
      Assert.assertEquals("size must be between 2 and 4", cv.getMessage());
      Assert.assertEquals("abcde", cv.getValue());
      after();
   }

   @Test
//   @Ignore
   public void testPropertyValid() throws Exception
   {
      before(TestResourceWithProperty.class);
      ClientRequest request = new ClientRequest(generateURL("/abc/unused"));
      ClientResponse<?> response = request.post();
      Assert.assertEquals(204, response.getStatus());
      response.releaseConnection();
      after();
   }

   @Test
//   @Ignore
   public void testPropertyInvalid() throws Exception
   {
      before(TestResourceWithProperty.class);
      ClientRequest request = new ClientRequest(generateURL("/abcdef/unused"));
      ClientResponse<?> response = request.post(String.class);
      Assert.assertEquals(400, response.getStatus());
      Object entity = response.getEntity();
      ResteasyViolationException e = new ResteasyViolationException(String.class.cast(entity));
      countViolations(e, 1, 0, 1, 0, 0, 0);
      ResteasyConstraintViolation cv = e.getPropertyViolations().iterator().next();
      Assert.assertEquals("size must be between 2 and 4", cv.getMessage());
      Assert.assertEquals("abcdef", cv.getValue());
      after();
   }

   @Test
//   @Ignore
   public void testFieldAndProperty() throws Exception
   {
      before(TestResourceWithFieldAndProperty.class);

      // Valid
      ClientRequest request = new ClientRequest(generateURL("/abc/wxyz"));
      ClientResponse<?> response = request.post();
      Assert.assertEquals(204, response.getStatus());
      response.releaseConnection();

      // Invalid
      request = new ClientRequest(generateURL("/a/uvwxyz"));
      response = request.post(String.class);
      Assert.assertEquals(400, response.getStatus());
      Object entity = response.getEntity();
      ResteasyViolationException e = new ResteasyViolationException(String.class.cast(entity));
      countViolations(e, 2, 1, 1, 0, 0, 0);
      ResteasyConstraintViolation cv = e.getFieldViolations().iterator().next();
      Assert.assertEquals("size must be between 2 and 4", cv.getMessage());
      Assert.assertEquals("a", cv.getValue());
      cv = e.getPropertyViolations().iterator().next();
      Assert.assertEquals("size must be between 3 and 5", cv.getMessage());
      Assert.assertEquals("uvwxyz", cv.getValue());
      after();
   }

   @Test
//   @Ignore
   public void testClassConstraint() throws Exception
   {
      before(TestResourceWithClassConstraint.class);

      // Valid
      ClientRequest request = new ClientRequest(generateURL("/abc/xyz"));
      ClientResponse<?> response = request.post();      
      Assert.assertEquals(204, response.getStatus());
      response.releaseConnection();

      // Invalid
      request = new ClientRequest(generateURL("/a/b"));
      response = request.post(String.class);
      Assert.assertEquals(400, response.getStatus());
      Object entity = response.getEntity();
      ResteasyViolationException e = new ResteasyViolationException(String.class.cast(entity));
      countViolations(e, 1, 0, 0, 1, 0, 0);
      ResteasyConstraintViolation cv = e.getClassViolations().iterator().next();
      Assert.assertEquals("Concatenation of s and t must have length > 5", cv.getMessage());
      Assert.assertEquals("TestResourceWithClassConstraint(\"a\", \"b\")", cv.getValue());
      System.out.println(cv.getValue());
      after();
   }

   @Test
//   @Ignore
   public void testGraph() throws Exception
   {
      before(TestResourceWithGraph.class);

      // Valid
      ClientRequest request = new ClientRequest(generateURL("/abcd/vwxyz"));
      ClientResponse<?> response = request.post();      
      Assert.assertEquals(204, response.getStatus());
      response.releaseConnection();

      // Invalid
      request = new ClientRequest(generateURL("/abc/xyz"));
      response = request.post(String.class);
      Assert.assertEquals(400, response.getStatus());
      Object entity = response.getEntity();
      ResteasyViolationException e = new ResteasyViolationException(String.class.cast(entity));
      countViolations(e, 2, 1, 1, 0, 0, 0);
      ResteasyConstraintViolation cv = e.getFieldViolations().iterator().next();
      Assert.assertTrue(cv.getMessage().startsWith("size must be between 4 and"));
      Assert.assertEquals("abc", cv.getValue());
      cv = e.getPropertyViolations().iterator().next();
      Assert.assertTrue(cv.getMessage().startsWith("size must be between 5 and"));
      Assert.assertEquals("xyz", cv.getValue());
      after();
   }

   @Test
//   @Ignore
   public void testArray() throws Exception
   {
      before(TestResourceWithArray.class);

      // Valid
      ClientRequest request = new ClientRequest(generateURL("/abcde"));
      ClientResponse<?> response = request.post();      
      Assert.assertEquals(204, response.getStatus());
      response.releaseConnection();

      // Invalid
      request = new ClientRequest(generateURL("/abc"));
      response = request.post(String.class);
      Assert.assertEquals(400, response.getStatus());
      Object entity = response.getEntity();
      ResteasyViolationException e = new ResteasyViolationException(String.class.cast(entity));
      System.out.println("exception: " + e);
      countViolations(e, 1, 0, 1, 0, 0, 0);
      ResteasyConstraintViolation cv = e.getPropertyViolations().iterator().next();
      Assert.assertTrue(cv.getMessage().startsWith("size must be between 5 and"));
      Assert.assertEquals("abc", cv.getValue());
      after();
   }

   @Test
//   @Ignore
   public void testList() throws Exception
   {
      before(TestResourceWithList.class);

      // Valid
      ClientRequest request = new ClientRequest(generateURL("/abcde"));
      ClientResponse<?> response = request.post();      
      Assert.assertEquals(204, response.getStatus());
      response.releaseConnection();

      // Invalid
      request = new ClientRequest(generateURL("/abc"));
      response = request.post(String.class);
      Assert.assertEquals(400, response.getStatus());
      Object entity = response.getEntity();
      ResteasyViolationException e = new ResteasyViolationException(String.class.cast(entity));
      countViolations(e, 1, 0, 1, 0, 0, 0);
      ResteasyConstraintViolation cv = e.getPropertyViolations().iterator().next();
      Assert.assertTrue(cv.getMessage().startsWith("size must be between 5 and"));
      Assert.assertEquals("abc", cv.getValue());
      after();
   }

   @Test
//   @Ignore
   public void testMap() throws Exception
   {
      before(TestResourceWithMap.class);

      // Valid
      ClientRequest request = new ClientRequest(generateURL("/abcde"));
      ClientResponse<?> response = request.post();      
      Assert.assertEquals(204, response.getStatus());
      response.releaseConnection();

      // Invalid
      request = new ClientRequest(generateURL("/abc"));
      response = request.post(String.class);
      Assert.assertEquals(400, response.getStatus());
      Object entity = response.getEntity();
      ResteasyViolationException e = new ResteasyViolationException(String.class.cast(entity));
      countViolations(e, 1, 0, 1, 0, 0, 0);
      ResteasyConstraintViolation cv = e.getPropertyViolations().iterator().next();
      Assert.assertTrue(cv.getMessage().startsWith("size must be between 5 and"));
      Assert.assertEquals("abc", cv.getValue());
      after();
   }

   @Test
//   @Ignore
   public void testMapOfListOfArrayOfStrings() throws Exception
   {
      before(TestResourceWithMapOfListOfArrayOfStrings.class);

      // Valid
      ClientRequest request = new ClientRequest(generateURL("/abcde"));
      ClientResponse<?> response = request.post();      
      Assert.assertEquals(204, response.getStatus());
      response.releaseConnection();

      // Invalid
      request = new ClientRequest(generateURL("/abc"));
      response = request.post(String.class);
      Assert.assertEquals(400, response.getStatus());
      Object entity = response.getEntity();
      ResteasyViolationException e = new ResteasyViolationException(String.class.cast(entity));
      System.out.println("exception: " + e);
      countViolations(e, 1, 0, 1, 0, 0, 0);
      ResteasyConstraintViolation cv = e.getPropertyViolations().iterator().next();
      Assert.assertTrue(cv.getMessage().startsWith("size must be between 5 and"));
      Assert.assertEquals("abc", cv.getValue());
      after();
   }

   @Test
//   @Ignore
   public void testParameters() throws Exception
   {
      beforeFoo(TestResourceWithParameters.class);
      
      // Valid native constraint
      ClientRequest request = new ClientRequest(generateURL("/native"));
      request.body("application/foo", new Foo("a"));
      ClientResponse<?> response = request.post();      
      Assert.assertEquals(204, response.getStatus());
      response.releaseConnection();

      // Valid imposed constraint
      request = new ClientRequest(generateURL("/imposed"));
      request.body("application/foo", new Foo("abcde"));
      response = request.post();      
      Assert.assertEquals(204, response.getStatus());
      response.releaseConnection();

      // Valid native and imposed constraints.
      request = new ClientRequest(generateURL("/nativeAndImposed"));
      request.body("application/foo", new Foo("abc"));
      response = request.post();      
      Assert.assertEquals(204, response.getStatus());
      response.releaseConnection();

      // Invalid native constraint
      request = new ClientRequest(generateURL("/native"));
      request.body("application/foo", new Foo("abcdef"));
      response = request.post(String.class);
      Assert.assertEquals(400, response.getStatus());
      Object entity = response.getEntity();
      ResteasyViolationException e = new ResteasyViolationException(String.class.cast(entity));
      countViolations(e, 1, 0, 0, 0, 1, 0);
      ResteasyConstraintViolation cv = e.getParameterViolations().iterator().next();
      Assert.assertTrue(cv.getMessage().equals("s must have length: 1 <= length <= 3"));
      Assert.assertEquals("Foo[abcdef]", cv.getValue());
      
      // Invalid imposed constraint
      request = new ClientRequest(generateURL("/imposed"));
      request.body("application/foo", new Foo("abcdef"));
      response = request.post(String.class);
      Assert.assertEquals(400, response.getStatus());
      entity = response.getEntity();
      e = new ResteasyViolationException(String.class.cast(entity));
      countViolations(e, 1, 0, 0, 0, 1, 0);
      cv = e.getParameterViolations().iterator().next();
      Assert.assertTrue(cv.getMessage().equals("s must have length: 3 <= length <= 5"));
      Assert.assertEquals("Foo[abcdef]", cv.getValue());

      // Invalid native and imposed constraints
      request = new ClientRequest(generateURL("/nativeAndImposed"));
      request.body("application/foo", new Foo("abcdef"));
      response = request.post(String.class);
      Assert.assertEquals(400, response.getStatus());
      entity = response.getEntity();
      e = new ResteasyViolationException(String.class.cast(entity));
      countViolations(e, 2, 0, 0, 0, 2, 0);
      Iterator<ResteasyConstraintViolation> it = e.getParameterViolations().iterator(); 
      ResteasyConstraintViolation cv1 = it.next();
      ResteasyConstraintViolation cv2 = it.next();
      if (cv1.toString().indexOf('1') < 0)
      {
         ResteasyConstraintViolation temp = cv1;
         cv1 = cv2;
         cv2 = temp;
      }
      Assert.assertTrue(cv1.getMessage().equals("s must have length: 1 <= length <= 3"));
      Assert.assertEquals("Foo[abcdef]", cv1.getValue());
      Assert.assertTrue(cv2.getMessage().equals("s must have length: 3 <= length <= 5"));
      Assert.assertEquals("Foo[abcdef]", cv2.getValue());
      
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
      response = request.post(String.class);
      Assert.assertEquals(400, response.getStatus());
      entity = response.getEntity();
      e = new ResteasyViolationException(String.class.cast(entity));
      countViolations(e, 6, 0, 0, 0, 6, 0);
      List<String> list = getMessages(new ArrayList<ResteasyConstraintViolation>(e.getViolations()));
      Assert.assertTrue(list.contains("size must be between 2 and 3; pppp"));
      Assert.assertTrue(list.contains("size must be between 2 and 3; mmmm"));
      Assert.assertTrue(list.contains("size must be between 2 and 3; qqqq"));
      Assert.assertTrue(list.contains("size must be between 2 and 3; ffff"));
      Assert.assertTrue(list.contains("size must be between 2 and 3; hhhh"));
      Assert.assertTrue(list.contains("size must be between 2 and 3; cccc"));
      after();
   }

   @Test
//   @Ignore
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
      Object entity = response.getEntity(String.class);
      System.out.println("entity: " + entity);
      ResteasyViolationException e = new ResteasyViolationException(String.class.cast(entity));
      countViolations(e, 1, 0, 0, 0, 0, 1);
      ResteasyConstraintViolation cv = e.getReturnValueViolations().iterator().next();
      Assert.assertTrue(cv.getMessage().equals("s must have length: 1 <= length <= 3"));
      Assert.assertEquals("Foo[abcdef]", cv.getValue());

      // Invalid imposed constraint
      request = new ClientRequest(generateURL("/imposed"));
      request.body("application/foo", new Foo("abcdef"));
      response = request.post(Foo.class);
      Assert.assertEquals(500, response.getStatus());
      entity = response.getEntity(String.class);
      e = new ResteasyViolationException(String.class.cast(entity));
      countViolations(e, 1, 0, 0, 0, 0, 1);
      cv = e.getReturnValueViolations().iterator().next();
      Assert.assertTrue(cv.getMessage().equals("s must have length: 3 <= length <= 5"));
      Assert.assertEquals("Foo[abcdef]", cv.getValue());

      // Invalid native and imposed constraints
      request = new ClientRequest(generateURL("/nativeAndImposed"));
      request.body("application/foo", new Foo("abcdef"));
      response = request.post(Foo.class); 
      Assert.assertEquals(500, response.getStatus());
      entity = response.getEntity(String.class);
      e = new ResteasyViolationException(String.class.cast(entity));
      countViolations(e, 2, 0, 0, 0, 0, 2);
      Iterator<ResteasyConstraintViolation> it = e.getReturnValueViolations().iterator(); 
      ResteasyConstraintViolation cv1 = it.next();
      ResteasyConstraintViolation cv2 = it.next();
      if (cv1.toString().indexOf('1') < 0)
      {
         ResteasyConstraintViolation temp = cv1;
         cv1 = cv2;
         cv2 = temp;
      }
      Assert.assertTrue(cv1.getMessage().equals("s must have length: 1 <= length <= 3"));
      Assert.assertEquals("Foo[abcdef]", cv1.getValue());
      Assert.assertTrue(cv2.getMessage().equals("s must have length: 3 <= length <= 5"));
      Assert.assertEquals("Foo[abcdef]", cv2.getValue());
      after();
   }

   @Test
//   @Ignore
   public void testViolationsBeforeReturnValue() throws Exception
   {
      beforeFoo(TestResourceWithAllFivePotentialViolations.class);

      // Valid
      ClientRequest request = new ClientRequest(generateURL("/abc/wxyz/unused/unused"));
      Foo foo = new Foo("pqrs");
      request.body("application/foo", foo);
      ClientResponse<?> response = request.post(Foo.class);     
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals(foo, response.getEntity());

      // Invalid: Should have 1 each of field, property, class, and parameter violations,
      //          and no return value violations.
      request = new ClientRequest(generateURL("/a/z/unused/unused"));
      foo = new Foo("p");
      request.body("application/foo", foo);
      response = request.post(Foo.class);
      Assert.assertEquals(400, response.getStatus());
      Object entity = response.getEntity(String.class);
      ResteasyViolationException e = new ResteasyViolationException(String.class.cast(entity));
      countViolations(e, 4, 1, 1, 1, 1, 0);
      ResteasyConstraintViolation cv = e.getFieldViolations().iterator().next();
      Assert.assertEquals("size must be between 2 and 4", cv.getMessage());
      Assert.assertEquals("a", cv.getValue());
      cv = e.getPropertyViolations().iterator().next();
      Assert.assertEquals("size must be between 3 and 5", cv.getMessage());
      Assert.assertEquals("z", cv.getValue());
      cv = e.getClassViolations().iterator().next();
      Assert.assertEquals("Concatenation of s and t must have length > 5", cv.getMessage());
      Assert.assertTrue(cv.getValue().startsWith("org.jboss.resteasy.test.validation.TestValidation$TestResourceWithAllFivePotentialViolations@"));
      cv = e.getParameterViolations().iterator().next();
      Assert.assertEquals("s must have length: 3 <= length <= 5", cv.getMessage());
      Assert.assertEquals("Foo[p]", cv.getValue());
      after();
   }
   
   @Test
//   @Ignore
   public void testInheritence() throws Exception
   {
      before(InterfaceTestSub.class);

      {
         // Valid - inherited annotations
         InterfaceTestSuper.t = "aaa";
         InterfaceTestSub.u = "bbb";
         ClientRequest request = new ClientRequest(generateURL("/inherit"));
         request.body(MediaType.TEXT_PLAIN_TYPE, "ccc");
         ClientResponse<?> response = request.post(String.class);
         Assert.assertEquals(200, response.getStatus());
         Assert.assertEquals("ccc", response.getEntity());
      }
      
      {
         // Valid - overridden annotations
         InterfaceTestSuper.t = "aaa";
         InterfaceTestSub.u = "bbb";
         ClientRequest request = new ClientRequest(generateURL("/override"));
         request.body(MediaType.TEXT_PLAIN_TYPE, "ccc");
         ClientResponse<?> response = request.post(String.class);     
         Assert.assertEquals(200, response.getStatus());
         Assert.assertEquals("ccc", response.getEntity());
      }
      
      {
         // Invalid - inherited class, parameter annotations
         InterfaceTestSuper.t = "a";
         InterfaceTestSub.u = "d";
         ClientRequest request = new ClientRequest(generateURL("/inherit"));
         request.body(MediaType.TEXT_PLAIN_TYPE, "e");
         ClientResponse<?> response = request.post(String.class);
         System.out.println("status: " + response.getStatus());
         Assert.assertEquals(400, response.getStatus());
         Object entity = response.getEntity();
         ResteasyViolationException e = new ResteasyViolationException(String.class.cast(entity));
         countViolations(e, 3, 0, 0, 2, 1, 0);
      }
      
      {
         // Invalid - overridden class, parameter annotations
         InterfaceTestSuper.t = "a";
         InterfaceTestSub.u = "d";
         ClientRequest request = new ClientRequest(generateURL("/override"));
         request.body(MediaType.TEXT_PLAIN_TYPE, "e");
         ClientResponse<?> response = request.post(String.class);
         System.out.println("status: " + response.getStatus());
         Assert.assertEquals(400, response.getStatus());
         Object entity = response.getEntity();
         ResteasyViolationException e = new ResteasyViolationException(String.class.cast(entity));
         countViolations(e, 3, 0, 0, 2, 1, 0);
      }
      
      {
         // Invalid - inherited return value annotations
         InterfaceTestSuper.t = "aaa";
         InterfaceTestSub.u = "bbb";
         ClientRequest request = new ClientRequest(generateURL("/inherit"));
         request.body(MediaType.TEXT_PLAIN_TYPE, "eeee");
         ClientResponse<?> response = request.post(String.class);
         System.out.println("status: " + response.getStatus());
         Assert.assertEquals(500, response.getStatus());
         Object entity = response.getEntity();
         ResteasyViolationException e = new ResteasyViolationException(String.class.cast(entity));
         countViolations(e, 1, 0, 0, 0, 0, 1);
      }
      
      {
         // Invalid - overridden return value annotations
         InterfaceTestSuper.t = "aaa";
         InterfaceTestSub.u = "bbb";
         ClientRequest request = new ClientRequest(generateURL("/override"));
         request.body(MediaType.TEXT_PLAIN_TYPE, "eeee");
         ClientResponse<?> response = request.post(String.class);
         System.out.println("status: " + response.getStatus());
         Assert.assertEquals(500, response.getStatus());
         Object entity = response.getEntity();
         ResteasyViolationException e = new ResteasyViolationException(String.class.cast(entity));
         countViolations(e, 2, 0, 0, 0, 0, 2);
      }
      after();
   }
   
   @Test
//   @Ignore
   public void testLocators() throws Exception
   {
      beforeFoo(TestResourceWithSubLocators.class);
      
      // Sub-resource locator returns resource with valid field.
      ClientRequest request = new ClientRequest(generateURL("/validField"));
      ClientResponse<?> response = request.post();
      Assert.assertEquals(204, response.getStatus());
      response.releaseConnection();

      // Sub-resource locator returns resource with invalid field.
      request = new ClientRequest(generateURL("/invalidField"));
      response = request.post(String.class);
      Assert.assertEquals(400, response.getStatus());
      Object entity = response.getEntity();
      ResteasyViolationException e = new ResteasyViolationException(String.class.cast(entity));
      countViolations(e, 1, 1, 0, 0, 0, 0);
      ResteasyConstraintViolation cv = e.getFieldViolations().iterator().next();
      Assert.assertEquals("size must be between 2 and 4", cv.getMessage());
      Assert.assertEquals("abcde", cv.getValue());

      // Sub-resource locator returns resource with valid property.
      // Note: The resource TestResourceWithProperty has a @PathParam annotation used by a setter,
      //       but it is not used when TestResourceWithProperty is used a sub-resource.  Hence "unused".
      request = new ClientRequest(generateURL("/property/abc/unused"));
      response = request.post();
      Assert.assertEquals(204, response.getStatus());
      response.releaseConnection();
      
      // Sub-resource locator returns resource with invalid property.
      request = new ClientRequest(generateURL("/property/abcdef/unused"));
      response = request.post(String.class);
      Assert.assertEquals(400, response.getStatus());
      entity = response.getEntity();
      e = new ResteasyViolationException(String.class.cast(entity));
      countViolations(e, 1, 0, 1, 0, 0, 0);
      cv = e.getPropertyViolations().iterator().next();
      Assert.assertEquals("size must be between 2 and 4", cv.getMessage());
      Assert.assertEquals("abcdef", cv.getValue());

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
      entity = response.getEntity(String.class);
      e = new ResteasyViolationException(String.class.cast(entity));
      countViolations(e, 4, 1, 1, 1, 1, 0);
      cv = e.getFieldViolations().iterator().next();
      Assert.assertEquals("size must be between 2 and 4", cv.getMessage());
      Assert.assertEquals("a", cv.getValue());
      cv = e.getPropertyViolations().iterator().next();
      Assert.assertEquals("size must be between 3 and 5", cv.getMessage());
      cv = e.getClassViolations().iterator().next();
      Assert.assertEquals("Concatenation of s and t must have length > 5", cv.getMessage());
      Assert.assertTrue(cv.getValue().startsWith("org.jboss.resteasy.test.validation.TestValidation$TestResourceWithAllFivePotentialViolations@"));
      
      // Sub-sub-resource locator returns resource with valid property.
      request = new ClientRequest(generateURL("/locator/sublocator/abc"));
      response = request.post();
      Assert.assertEquals(204, response.getStatus());
      response.releaseConnection();

      // Sub-resource locator returns resource with invalid property.
      request = new ClientRequest(generateURL("/locator/sublocator/abcdef"));
      response = request.post(String.class);
      Assert.assertEquals(400, response.getStatus());
      entity = response.getEntity(String.class);
      e = new ResteasyViolationException(String.class.cast(entity));
      countViolations(e, 1, 1, 0, 0, 0, 0);
      cv = e.getFieldViolations().iterator().next();
      Assert.assertEquals("size must be between 2 and 3", cv.getMessage());
      Assert.assertEquals("abcdef", cv.getValue());
      
      after();
   }

   @Test
//   @Ignore
   public void testAsynch() throws Exception
   {
      beforeFooAsynch(TestResourceWithAllFivePotentialViolations.class);
      
      // Submit asynchronous job with violations prior to execution of resource method.
      ClientRequest request = new ClientRequest(generateURL("/a/z/unused/unused?asynch=true"));
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
      Object entity = response.getEntity(String.class);
      ResteasyViolationException e = new ResteasyViolationException(String.class.cast(entity));
      countViolations(e, 4, 1, 1, 1, 1, 0);
      ResteasyConstraintViolation cv = e.getFieldViolations().iterator().next();
      Assert.assertEquals("size must be between 2 and 4", cv.getMessage());
      Assert.assertEquals("a", cv.getValue());
      cv = e.getPropertyViolations().iterator().next();
      Assert.assertEquals("size must be between 3 and 5", cv.getMessage());
      Assert.assertEquals("z", cv.getValue());
      cv = e.getClassViolations().iterator().next();
      Assert.assertEquals("Concatenation of s and t must have length > 5", cv.getMessage());
      Assert.assertTrue(cv.getValue().startsWith("org.jboss.resteasy.test.validation.TestValidation$TestResourceWithAllFivePotentialViolations@"));
      cv = e.getParameterViolations().iterator().next();
      Assert.assertEquals("s must have length: 3 <= length <= 5", cv.getMessage());
      Assert.assertEquals("Foo[p]", cv.getValue());

      // Delete job.
      request = new ClientRequest(jobUrl);
      response = request.delete();
      Assert.assertEquals(HttpServletResponse.SC_NO_CONTENT, response.getStatus());
      response.releaseConnection();
      
      // Submit asynchronous job with violations in result of resource method.
      request = new ClientRequest(generateURL("/abc/xyz/unused/unused?asynch=true"));
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
      entity = response.getEntity(String.class);
      e = new ResteasyViolationException(String.class.cast(entity));
      countViolations(e, 1, 0, 0, 0, 0, 1);
      cv = e.getReturnValueViolations().iterator().next();
      Assert.assertEquals("s must have length: 4 <= length <= 5", cv.getMessage());
      Assert.assertEquals("Foo[pqr]", cv.getValue());
      
      // Delete job.
      request = new ClientRequest(jobUrl);
      response = request.delete();
      Assert.assertEquals(HttpServletResponse.SC_NO_CONTENT, response.getStatus());
      response.releaseConnection();

      after();
   }
      
   @Test
   public void testCrossParameterConstraint() throws Exception
   {
      before(TestSubResourceWithCrossParameterConstraint.class);

      // Valid
      ClientRequest request = new ClientRequest(generateURL("/2/3"));
      ClientResponse<?> response = request.post();
      Assert.assertEquals(204, response.getStatus());
      response.releaseConnection();
     
      // Invalid
      request = new ClientRequest(generateURL("/5/7"));
      response = request.post();
      Assert.assertEquals(400, response.getStatus());
      String header = response.getResponseHeaders().getFirst(Validation.VALIDATION_HEADER);
      Assert.assertNotNull(header);
      Assert.assertTrue(Boolean.valueOf(header));
      Object entity = response.getEntity(String.class);
      System.out.println("entity: " + entity);
      ResteasyViolationException e = new ResteasyViolationException(String.class.cast(entity));
      countViolations(e, 1, 0, 0, 0, 1, 0);
      ResteasyConstraintViolation violation = e.getParameterViolations().iterator().next();
      System.out.println("violation: " + violation);
      Assert.assertEquals("Parameters must total <= 7", violation.getMessage());
      System.out.println("violation value: " + violation.getValue());
      Assert.assertEquals("[5, 7]", violation.getValue());
      after();
   }
   
   
   @Test
   public void testProxy() throws Exception
   {
      before(TestProxyResource.class);

      // Valid
      TestProxyInterface client = ProxyFactory.create(TestProxyInterface.class, generateURL("/"));
      client.s("abcd");
      String result = client.g();
      Assert.assertEquals("abcd", result);
      
      // Invalid
      client.s("abcde");
      try
      {
         result = client.g();
      }
      catch (ClientResponseFailure e)
      {
         ClientResponse<?> response = e.getResponse();
         System.out.println("status: " + response.getStatus());
         String header = response.getResponseHeaders().getFirst(Validation.VALIDATION_HEADER);
         Assert.assertNotNull(header);
         Assert.assertTrue(Boolean.valueOf(header));
         Object entity = response.getEntity(String.class);
         System.out.println("entity: " + entity);
         ResteasyViolationException e1 = new ResteasyViolationException(String.class.cast(entity));
         countViolations(e1, 1, 0, 0, 0, 0, 1);
         ResteasyConstraintViolation violation = e1.getReturnValueViolations().iterator().next();
         System.out.println("violation: " + violation);
         Assert.assertEquals("size must be between 2 and 4", violation.getMessage());
         Assert.assertEquals("abcde", violation.getValue());
      }
      catch (Exception e)
      {
         Assert.fail("expected ClientResponseFailure");
      }
      finally
      {
         after();
      }
   }
   
   @Test
   public void testOtherGroups() throws Exception
   {
      before(TestResourceWithOtherGroups.class);

      // Test invalid field, property, parameter, and class.
      ClientRequest request = new ClientRequest(generateURL("/test/a/z"));
      ClientResponse<?> response = request.post(String.class);
      Assert.assertEquals(200, response.getStatus());
      Object entity = response.getEntity();
      System.out.println("entity: " + entity);
      Assert.assertEquals("z", entity);
      after();
   }
   
   private void countViolations(ResteasyViolationException e, int totalCount, int fieldCount, int propertyCount, int classCount, int parameterCount, int returnValueCount)
   {
      Assert.assertEquals(totalCount,       e.getViolations().size());
      Assert.assertEquals(fieldCount,       e.getFieldViolations().size());
      Assert.assertEquals(propertyCount,    e.getPropertyViolations().size());
      Assert.assertEquals(classCount,       e.getClassViolations().size());
      Assert.assertEquals(parameterCount,   e.getParameterViolations().size());
      Assert.assertEquals(returnValueCount, e.getReturnValueViolations().size());
   }
   
   private List<String> getMessages(List<ResteasyConstraintViolation> rcvs)
   {
      List<String> list = new ArrayList<String>();
      for (Iterator<ResteasyConstraintViolation> it = rcvs.iterator(); it.hasNext(); )
      {
         ResteasyConstraintViolation rcv = it.next();
         list.add(rcv.getMessage() + "; " + rcv.getValue());
      }
      return list;
   }
}
