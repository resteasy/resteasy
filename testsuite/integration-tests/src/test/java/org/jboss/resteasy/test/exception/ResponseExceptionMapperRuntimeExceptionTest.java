package org.jboss.resteasy.test.exception;

import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.microprofile.client.BuilderResolver;
import org.jboss.resteasy.test.exception.resource.ExceptionMapperRuntimeExceptionWithReasonMapper;
import org.jboss.resteasy.test.exception.resource.ResponseExceptionMapperRuntimeExceptionMapper;
import org.jboss.resteasy.test.exception.resource.ResponseExceptionMapperRuntimeExceptionResource;
import org.jboss.resteasy.test.exception.resource.ResponseExceptionMapperRuntimeExceptionResourceInterface;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import jakarta.ws.rs.WebApplicationException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.net.URL;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.6.0
 * @tpTestCaseDetails Regression test for RESTEASY-1847
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ResponseExceptionMapperRuntimeExceptionTest {

   @Deployment
   public static Archive<?> createTestArchive() {
      WebArchive war = TestUtil.prepareArchive(ResponseExceptionMapperRuntimeExceptionTest.class.getSimpleName());
      return TestUtil.finishContainerPrepare(war, null, ResponseExceptionMapperRuntimeExceptionMapper.class, ExceptionMapperRuntimeExceptionWithReasonMapper.class,
               ResponseExceptionMapperRuntimeExceptionResource.class, ResponseExceptionMapperRuntimeExceptionResourceInterface.class, ResponseExceptionMapper.class);
   }

   /**
    * @tpTestDetails Check ExceptionMapper for WebApplicationException
    * @tpSince RESTEasy 3.6.0
    */
   @Test
   public void testRuntimeApplicationException() throws Exception {
      ResponseExceptionMapperRuntimeExceptionResourceInterface service = BuilderResolver.instance()
            .newBuilder()
            .baseUrl(new URL(PortProviderUtil.generateURL("/test",
                  ResponseExceptionMapperRuntimeExceptionTest.class.getSimpleName())))
            .register(ResponseExceptionMapperRuntimeExceptionMapper.class)
            .build(ResponseExceptionMapperRuntimeExceptionResourceInterface.class);
      try {
         service.get();
         fail("Should not get here");
      } catch (RuntimeException e) {
         // assert test exception message
         assertEquals(ExceptionMapperRuntimeExceptionWithReasonMapper.REASON, e.getMessage());
      }
   }

   @Test
   public void testRuntimeAException() throws Exception {
      ResponseExceptionMapperRuntimeExceptionResourceInterface service = BuilderResolver.instance()
              .newBuilder()
              .baseUrl(new URL(PortProviderUtil.generateURL("/test",
                      ResponseExceptionMapperRuntimeExceptionTest.class.getSimpleName())))
              .build(ResponseExceptionMapperRuntimeExceptionResourceInterface.class);
      try {
         service.get();
         fail("Should not get here");
      } catch (WebApplicationException e) {
         String str = e.getResponse().readEntity(String.class);
         Assert.assertEquals("Test error occurred", str);
      }
   }


}
