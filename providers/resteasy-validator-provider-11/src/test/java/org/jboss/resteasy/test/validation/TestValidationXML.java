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
import java.lang.reflect.Type;
import java.util.Iterator;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import javax.validation.constraints.Size;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import junit.framework.Assert;

import org.jboss.resteasy.api.validation.ResteasyConstraintViolation;
import org.jboss.resteasy.api.validation.Validation;
import org.jboss.resteasy.api.validation.ViolationReport;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.junit.Test;

/**
*
* @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
* @version $Revision: 1.1 $
*
* Created August 7, 2013
*/
public class TestValidationXML
{
   protected static ResteasyDeployment deployment;
   protected static Dispatcher dispatcher;

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

   public static class TestClassValidator implements ConstraintValidator<TestClassConstraint, TestResourceWithAllFivePotentialViolations>
   {
      int length;

      public void initialize(TestClassConstraint constraintAnnotation)
      {
         length = constraintAnnotation.value();
      }

      public boolean isValid(TestResourceWithAllFivePotentialViolations value, ConstraintValidatorContext context)
      {
         return value.s.length() + value.u.length() >= length;
      }

   }

   @Documented
   @Constraint(validatedBy = TestClassValidator.class)
   @Target({TYPE})
   @Retention(RUNTIME)
   public @interface TestClassConstraint {
      String message() default "Concatenation of s and u must have length > {value}";
      Class<?>[] groups() default {};
      Class<? extends Payload>[] payload() default {};
      int value();
   }

   @Path("/{s}/{t}/{u}")
   @TestClassConstraint(5)
   public static class TestResourceWithAllFivePotentialViolations
   {
      @Size(min=2, max=4)
      @PathParam("s")
      private String s;

      @Size(min=2, max=4)
      @PathParam("t")
      private String t;

      private String u;

      @Size(min=3, max=5)
      public String getU()
      {
         return u;
      }

      @PathParam("u")
      public void setU(String u)
      {
         this.u = u;
      }

      @POST
      @FooConstraint(min=4,max=5)
      public Foo post(@FooConstraint(min=3,max=5) Foo foo)
      {
         return foo;
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

   public static void after() throws Exception
   {
      EmbeddedContainer.stop();
      dispatcher = null;
      deployment = null;
   }

   //////////////////////////////////////////////////////////////////////////////
   @Test
   public void testStandardXML() throws Exception
   {
      doTestXML(MediaType.APPLICATION_XML);
   }

   @Test
   public void testStandardJSON() throws Exception
   {
      doTestJSON(MediaType.APPLICATION_JSON);
   }

   @Test
   public void testText() throws Exception
   {
      doTestText(MediaType.TEXT_PLAIN);
   }

   @Test
   public void testWildcard() throws Exception
   {
      doTestText(MediaType.WILDCARD);
   }
   
   @Test
   public void tesXML_NoQ_JSON() throws Exception
   {
      doTestXML(MediaType.APPLICATION_XML + "," + MediaType.APPLICATION_JSON + ";q=.5");
   }

   @Test
   public void tesXML_JSON_NoQ() throws Exception
   {
      doTestJSON(MediaType.APPLICATION_XML + ";q=.5" + "," + MediaType.APPLICATION_JSON);
   }
   
   @Test
   public void testXML_GT_JSON() throws Exception
   {
      doTestXML(MediaType.APPLICATION_XML + ";q=1," + MediaType.APPLICATION_JSON + ";q=.5");
   }
    
   @Test
   public void testXML_LT_JSON() throws Exception
   {
      doTestJSON(MediaType.APPLICATION_XML + ";q=.5," + MediaType.APPLICATION_JSON + ";q=1");
   }
   
   //////////////////////////////////////////////////////////////////////////////
   protected void doTestXML(String mediaType) throws Exception
   {
      doTestXML_pre(mediaType);
      doTestXML_post(mediaType);
   }

   protected void doTestJSON(String mediaType) throws Exception
   {
      doTestJSON_pre(mediaType);
      doTestJSON_post(mediaType);
   }

   protected void doTestText(String mediaType) throws Exception
   {
      doTestText_pre(mediaType);
      doTestText_post(mediaType);
   }

   //////////////////////////////////////////////////////////////////////////////
   protected void doTestXML_pre(String mediaType) throws Exception
   {
      beforeFoo(TestResourceWithAllFivePotentialViolations.class);

      {
         // Text form
         ClientRequest request = new ClientRequest(generateURL("/a/b/c"));
         Foo foo = new Foo("p");
         request.body("application/foo", foo);
         request.accept(mediaType);
         ClientResponse<?> response = request.post(Foo.class);
         Assert.assertEquals(400, response.getStatus());
         String entity = response.getEntity(String.class);
         System.out.println("report: " + entity);
         String start = "<violationReport>";
         String fieldViolation1 = "<fieldViolations><constraintType>FIELD</constraintType><path>s</path><message>size must be between 2 and 4</message><value>a</value></fieldViolations>";
         String fieldViolation2 = "<fieldViolations><constraintType>FIELD</constraintType><path>t</path><message>size must be between 2 and 4</message><value>b</value></fieldViolations>";
         String propertyViolation = "<propertyViolations><constraintType>PROPERTY</constraintType><path>u</path><message>size must be between 3 and 5</message><value>c</value></propertyViolations>";
         String classViolationStart = "<classViolations><constraintType>CLASS</constraintType><path></path><message>Concatenation of s and u must have length &gt; 5</message><value>org.jboss.resteasy.test.validation.TestValidationXML$TestResourceWithAllFivePotentialViolations";
         String classViolationEnd = "</value></classViolations>";
         String parameterViolation = "<parameterViolations><constraintType>PARAMETER</constraintType><path>post.arg0</path><message>s must have length: 3 &lt;= length &lt;= 5</message><value>Foo[p]</value></parameterViolations>";
         String end = "</violationReport>";
         Assert.assertTrue(entity.contains(start));
         Assert.assertTrue(entity.contains(fieldViolation1));
         Assert.assertTrue(entity.contains(fieldViolation2));
         Assert.assertTrue(entity.contains(propertyViolation));
         Assert.assertTrue(entity.contains(classViolationStart));
         Assert.assertTrue(entity.contains(classViolationEnd));
         Assert.assertTrue(entity.contains(parameterViolation));
         Assert.assertTrue(entity.contains(end));
      }

      {
         // Unmarshal report,
         ClientRequest request = new ClientRequest(generateURL("/a/b/c"));
         Foo foo = new Foo("p");
         request.body("application/foo", foo);
         request.accept(MediaType.APPLICATION_XML);
         ClientResponse<?> response = request.post(Foo.class);
         Assert.assertEquals(400, response.getStatus());
         ViolationReport report = response.getEntity(ViolationReport.class);
         System.out.println("report: " + report);
         countViolations(report, 4, 2, 1, 1, 1, 0);
         Iterator<ResteasyConstraintViolation> iterator = report.getFieldViolations().iterator();
         ResteasyConstraintViolation cv1 = iterator.next();
         ResteasyConstraintViolation cv2 = iterator.next();
         if (!("a").equals(cv1.getValue()))
         {
            ResteasyConstraintViolation tmp = cv1;
            cv1 = cv2;
            cv2 = tmp;
         }
         Assert.assertEquals("size must be between 2 and 4", cv1.getMessage());
         Assert.assertEquals("a", cv1.getValue());
         Assert.assertEquals("size must be between 2 and 4", cv2.getMessage());
         Assert.assertEquals("b", cv2.getValue());
         ResteasyConstraintViolation cv = report.getPropertyViolations().iterator().next();
         Assert.assertEquals("size must be between 3 and 5", cv.getMessage());
         Assert.assertEquals("c", cv.getValue());
         cv = report.getClassViolations().iterator().next();
         Assert.assertEquals("Concatenation of s and u must have length > 5", cv.getMessage());
         System.out.print("value: " + cv.getValue());
         Assert.assertTrue(cv.getValue().startsWith("org.jboss.resteasy.test.validation.TestValidationXML$TestResourceWithAllFivePotentialViolations@"));
         cv = report.getParameterViolations().iterator().next();
         Assert.assertEquals("s must have length: 3 <= length <= 5", cv.getMessage());
         Assert.assertEquals("Foo[p]", cv.getValue());
      }

      after();
   }

   protected void doTestXML_post(String mediaType) throws Exception
   {
      beforeFoo(TestResourceWithAllFivePotentialViolations.class);

      {
         // Text form
         ClientRequest request = new ClientRequest(generateURL("/abc/pqr/xyz"));
         Foo foo = new Foo("123");
         request.body("application/foo", foo);
         request.accept(mediaType);
         ClientResponse<?> response = request.post(Foo.class);
         Assert.assertEquals(500, response.getStatus());
         String entity = response.getEntity(String.class);
         System.out.println("report: " + entity);
         String expected = "<violationReport><returnValueViolations><constraintType>RETURN_VALUE</constraintType><path>post.&lt;return value&gt;</path><message>s must have length: 4 &lt;= length &lt;= 5</message><value>Foo[123]</value></returnValueViolations></violationReport>";
         Assert.assertTrue(entity.contains(expected));
      }

      {
         // Unmarshal report,
         ClientRequest request = new ClientRequest(generateURL("/abc/pqr/xyz"));
         Foo foo = new Foo("123");
         request.body("application/foo", foo);
         request.accept(MediaType.APPLICATION_XML);
         ClientResponse<?> response = request.post(Foo.class);
         Assert.assertEquals(500, response.getStatus());
         ViolationReport report = response.getEntity(ViolationReport.class);
         System.out.println("report: " + report);
         countViolations(report, 1, 0, 0, 0, 0, 1);
         ResteasyConstraintViolation cv = report.getReturnValueViolations().iterator().next();
         System.out.println("message: " + cv.getMessage());
         System.out.println("value: " + cv.getValue());
         Assert.assertEquals("s must have length: 4 <= length <= 5", cv.getMessage());
         Assert.assertEquals(foo.toString(), cv.getValue());
      }

      after();
   }
   
   protected void doTestJSON_pre(String mediaType) throws Exception
   {
      beforeFoo(TestResourceWithAllFivePotentialViolations.class);

      {
         // Text form
         ClientRequest request = new ClientRequest(generateURL("/a/b/c"));
         Foo foo = new Foo("p");
         request.body("application/foo", foo);
         request.accept(mediaType);
         ClientResponse<?> response = request.post(Foo.class);
         Assert.assertEquals(400, response.getStatus());
         String entity = response.getEntity(String.class);
         System.out.println("report: " + entity);
         String start = "{\"exception\":null,\"fieldViolations\":[";
         String fieldViolation1 = "{\"constraintType\":\"FIELD\",\"path\":\"s\",\"message\":\"size must be between 2 and 4\",\"value\":\"a\"}";
         String fieldViolation2 = "{\"constraintType\":\"FIELD\",\"path\":\"t\",\"message\":\"size must be between 2 and 4\",\"value\":\"b\"}";
         String propertyViolation = "\"propertyViolations\":[{\"constraintType\":\"PROPERTY\",\"path\":\"u\",\"message\":\"size must be between 3 and 5\",\"value\":\"c\"}]";
         String classViolationStart = "\"classViolations\":[{\"constraintType\":\"CLASS\",\"path\":\"\",\"message\":\"Concatenation of s and u must have length > 5\",\"value\":\"org.jboss.resteasy.test.validation.TestValidationXML$TestResourceWithAllFivePotentialViolations@";
         String classViolationEnd = "}]";
         String parameterViolation = "\"parameterViolations\":[{\"constraintType\":\"PARAMETER\",\"path\":\"post.arg0\",\"message\":\"s must have length: 3 <= length <= 5\",\"value\":\"Foo[p]\"}]";
         String returnValueViolation = "\"returnValueViolations\":[]";
         String end = "}";
         Assert.assertTrue(entity.contains(start));
         Assert.assertTrue(entity.contains(fieldViolation1));
         Assert.assertTrue(entity.contains(fieldViolation2));
         Assert.assertTrue(entity.contains(propertyViolation));
         Assert.assertTrue(entity.contains(classViolationStart));
         Assert.assertTrue(entity.contains(classViolationEnd));
         Assert.assertTrue(entity.contains(parameterViolation));
         Assert.assertTrue(entity.contains(returnValueViolation));
         Assert.assertTrue(entity.contains(end));
      }

      {
         // Unmarshal report,
         ClientRequest request = new ClientRequest(generateURL("/a/b/c"));
         Foo foo = new Foo("p");
         request.body("application/foo", foo);
         request.accept(MediaType.APPLICATION_XML);
         ClientResponse<?> response = request.post(Foo.class);
         Assert.assertEquals(400, response.getStatus());
         ViolationReport report = response.getEntity(ViolationReport.class);
         System.out.println("report: " + report);
         countViolations(report, 4, 2, 1, 1, 1, 0);
         Iterator<ResteasyConstraintViolation> iterator = report.getFieldViolations().iterator();
         ResteasyConstraintViolation cv1 = iterator.next();
         ResteasyConstraintViolation cv2 = iterator.next();
         if (!("a").equals(cv1.getValue()))
         {
            ResteasyConstraintViolation tmp = cv1;
            cv1 = cv2;
            cv2 = tmp;
         }
         Assert.assertEquals("size must be between 2 and 4", cv1.getMessage());
         Assert.assertEquals("a", cv1.getValue());
         Assert.assertEquals("size must be between 2 and 4", cv2.getMessage());
         Assert.assertEquals("b", cv2.getValue());
         ResteasyConstraintViolation cv = report.getPropertyViolations().iterator().next();
         Assert.assertEquals("size must be between 3 and 5", cv.getMessage());
         Assert.assertEquals("c", cv.getValue());
         cv = report.getClassViolations().iterator().next();
         Assert.assertEquals("Concatenation of s and u must have length > 5", cv.getMessage());
         System.out.print("value: " + cv.getValue());
         Assert.assertTrue(cv.getValue().startsWith("org.jboss.resteasy.test.validation.TestValidationXML$TestResourceWithAllFivePotentialViolations@"));
         cv = report.getParameterViolations().iterator().next();
         Assert.assertEquals("s must have length: 3 <= length <= 5", cv.getMessage());
         Assert.assertEquals("Foo[p]", cv.getValue());
      }

      after();
   }


   protected void doTestJSON_post(String mediaType) throws Exception
   {
      beforeFoo(TestResourceWithAllFivePotentialViolations.class);

      {
         // Text form
         ClientRequest request = new ClientRequest(generateURL("/abc/pqr/xyz"));
         Foo foo = new Foo("123");
         request.body("application/foo", foo);
         request.accept(mediaType);
         ClientResponse<?> response = request.post(Foo.class);
         Assert.assertEquals(500, response.getStatus());
         String entity = response.getEntity(String.class);
         System.out.println("report: " + entity);
         String start = "\"exception\":null";
         String fieldViolation = "\"fieldViolations\":[]";
         String propertyViolation = "\"propertyViolations\":[]";
         String classViolation = "\"classViolations\":[]";
         String parameterViolation = "\"parameterViolations\":[],";
         String returnValueViolation = "\"returnValueViolations\":[{\"constraintType\":\"RETURN_VALUE\",\"path\":\"post.<return value>\",\"message\":\"s must have length: 4 <= length <= 5\",\"value\":\"Foo[123]\"";
         Assert.assertTrue(entity.contains(start));
         Assert.assertTrue(entity.contains(fieldViolation));
         Assert.assertTrue(entity.contains(propertyViolation));
         Assert.assertTrue(entity.contains(classViolation));
         Assert.assertTrue(entity.contains(parameterViolation));
         Assert.assertTrue(entity.contains(returnValueViolation));
      }

      {
         // Unmarshal report,
         ClientRequest request = new ClientRequest(generateURL("/abc/pqr/xyz"));
         Foo foo = new Foo("123");
         request.body("application/foo", foo);
         request.accept(MediaType.APPLICATION_XML);
         ClientResponse<?> response = request.post(Foo.class);
         Assert.assertEquals(500, response.getStatus());
         ViolationReport report = response.getEntity(ViolationReport.class);
         System.out.println("report: " + report);
         countViolations(report, 1, 0, 0, 0, 0, 1);
         ResteasyConstraintViolation cv = report.getReturnValueViolations().iterator().next();
         System.out.println("message: " + cv.getMessage());
         System.out.println("value: " + cv.getValue());
         Assert.assertEquals("s must have length: 4 <= length <= 5", cv.getMessage());
         Assert.assertEquals(foo.toString(), cv.getValue());
      }

      after();
   }
   
   protected void doTestText_pre(String mediaType) throws Exception
   {
      beforeFoo(TestResourceWithAllFivePotentialViolations.class);
      ClientRequest request = new ClientRequest(generateURL("/a/b/c"));
      Foo foo = new Foo("p");
      request.body("application/foo", foo);
      request.accept(mediaType);
      ClientResponse<?> response = request.post(Foo.class);
      Assert.assertEquals(400, response.getStatus());
      String entity = response.getEntity(String.class);
      System.out.println("report:");
      System.out.println(entity);
      String fieldViolation1 =
            "[FIELD]\r" +
                  "[s]\r" +
                  "[size must be between 2 and 4]\r" +
                  "[a]\r";
      String fieldViolation2 =
            "[FIELD]\r" +
                  "[t]\r" +
                  "[size must be between 2 and 4]\r" +
                  "[b]\r";
      String propertyViolation =
            "[PROPERTY]\r" +
                  "[u]\r" +
                  "[size must be between 3 and 5]\r" +
                  "[c]\r";
      String classViolation =
            "[CLASS]\r" +
                  "[]\r" +
                  "[Concatenation of s and u must have length > 5]\r" +
                  "[org.jboss.resteasy.test.validation.TestValidationXML$TestResourceWithAllFivePotentialViolations";
      String parameterViolation =
            "[PARAMETER]\r" +
                  "[post.arg0]\r" +
                  "[s must have length: 3 <= length <= 5]\r" +
                  "[Foo[p]]";
      Assert.assertTrue(entity.contains(fieldViolation1));
      Assert.assertTrue(entity.contains(fieldViolation2));
      Assert.assertTrue(entity.contains(propertyViolation));
      Assert.assertTrue(entity.contains(classViolation));
      Assert.assertTrue(entity.contains(parameterViolation));
      after();
   }
   
   protected void doTestText_post(String mediaType) throws Exception
   {
      beforeFoo(TestResourceWithAllFivePotentialViolations.class);
      ClientRequest request = new ClientRequest(generateURL("/abc/pqr/xyz"));
      Foo foo = new Foo("123");
      request.body("application/foo", foo);
      request.accept(mediaType);
      ClientResponse<?> response = request.post(Foo.class);
      Assert.assertEquals(500, response.getStatus());
      String entity = response.getEntity(String.class);
      System.out.println("report: " + entity);
      String returnValueViolation =
            "[RETURN_VALUE]\r" +
                  "[post.<return value>]\r" +
                  "[s must have length: 4 <= length <= 5]\r" +
                  "[Foo[123]]\r\r";
      Assert.assertTrue(entity.equals(returnValueViolation));
      after();
   }
   
   private void countViolations(ViolationReport e, int totalCount, int fieldCount, int propertyCount, int classCount, int parameterCount, int returnValueCount)
   {
      // Assert.assertEquals(totalCount, e.getViolations().size());
      Assert.assertEquals(fieldCount, e.getFieldViolations().size());
      Assert.assertEquals(propertyCount, e.getPropertyViolations().size());
      Assert.assertEquals(classCount, e.getClassViolations().size());
      Assert.assertEquals(parameterCount, e.getParameterViolations().size());
      Assert.assertEquals(returnValueCount, e.getReturnValueViolations().size());
   }
}