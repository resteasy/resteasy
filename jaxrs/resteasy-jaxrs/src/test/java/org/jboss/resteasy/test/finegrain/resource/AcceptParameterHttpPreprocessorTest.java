/*
 * JBoss, the OpenSource J2EE webOS
 * 
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.resteasy.test.finegrain.resource;

import org.jboss.resteasy.core.AcceptParameterHttpPreprocessor;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.spi.HttpRequest;
import org.junit.Assert;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;

public class AcceptParameterHttpPreprocessorTest
{

   @Test
   public void simple() throws Exception
   {

      String acceptParamName = "accept";
      AcceptParameterHttpPreprocessor processor = new AcceptParameterHttpPreprocessor(acceptParamName);

      String type = MediaType.TEXT_XML;
      HttpRequest request = MockHttpRequest.get("foo?" + acceptParamName + "=" + type);

      MediaType mediaType = MediaType.valueOf(type);

      processor.preProcess(request);

      List<MediaType> list = request.getHttpHeaders().getAcceptableMediaTypes();

      Assert.assertEquals("Incorrect acceptable list size", 1, list.size());
      Assert.assertEquals("Incorrect media type extracted", mediaType, list.get(0));
   }

   @Test
   public void aLittleMoreComplicated() throws Exception
   {

      String acceptParamName = "bar";
      AcceptParameterHttpPreprocessor processor = new AcceptParameterHttpPreprocessor(acceptParamName);

      List<MediaType> expected = Arrays.asList(MediaType.TEXT_XML_TYPE, MediaType.TEXT_PLAIN_TYPE, MediaType.TEXT_HTML_TYPE, MediaType.APPLICATION_XHTML_XML_TYPE);

      HttpRequest request = MockHttpRequest.get("foo?" + acceptParamName + "=" + expected.get(0) + "," + expected.get(1));
      request.getHttpHeaders().getAcceptableMediaTypes().add(expected.get(2));
      request.getHttpHeaders().getAcceptableMediaTypes().add(expected.get(3));

      processor.preProcess(request);

      List<MediaType> actual = request.getHttpHeaders().getAcceptableMediaTypes();

      Assert.assertEquals("Incorrect acceptable media type extracted", expected, actual);
   }

   @Test
   public void withoutParam() throws Exception
   {

      String acceptParamName = "baz";
      AcceptParameterHttpPreprocessor processor = new AcceptParameterHttpPreprocessor(acceptParamName);

      List<MediaType> expected = Arrays.asList(MediaType.TEXT_PLAIN_TYPE, MediaType.TEXT_HTML_TYPE);

      HttpRequest request = MockHttpRequest.get("foo");
      request.getHttpHeaders().getAcceptableMediaTypes().add(expected.get(0));
      request.getHttpHeaders().getAcceptableMediaTypes().add(expected.get(1));

      processor.preProcess(request);

      List<MediaType> actual = request.getHttpHeaders().getAcceptableMediaTypes();

      Assert.assertEquals("Incorrect acceptable media type extracted", expected, actual);
   }

   @Test
   public void complex() throws Exception
   {

      String acceptParamName = "bar";
      AcceptParameterHttpPreprocessor processor = new AcceptParameterHttpPreprocessor(acceptParamName);

      List<MediaType> expected = Arrays.asList(
              MediaType.valueOf("application/xhtml+xml"),
              MediaType.valueOf("text/html"),
              MediaType.valueOf("application/xml;q=0.9"),
              MediaType.valueOf("*/*;q=0.8")
      );

      String param1 = URLEncoder.encode("application/xml;q=0.9,application/xhtml+xml,*/*;q=0.8", "UTF-8");
      String param2 = URLEncoder.encode("text/html", "UTF-8");
      HttpRequest request = MockHttpRequest.get(
              "foo?" + acceptParamName + "=" + param1 + "&" +
                      acceptParamName + "=" + param2);

      processor.preProcess(request);

      List<MediaType> actual = request.getHttpHeaders().getAcceptableMediaTypes();

      Assert.assertEquals("Incorrect acceptable media type extracted", expected, actual);
   }
}
