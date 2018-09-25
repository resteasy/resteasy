package org.jboss.resteasy.test.util;

import org.jboss.resteasy.specimpl.ResteasyUriBuilderImpl;
import org.junit.Test;

import javax.ws.rs.core.UriBuilder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

/**
 * @tpSubChapter Util tests
 * @tpChapter Unit tests
 * @tpTestCaseDetails Test for ResteasyUriBuilderImpl class.
 * @tpSince RESTEasy 3.0.16
 */
public class ResteasyUriBuilderTest {

   private static final String URI_ERROR = "Uri function of ResteasyUriBuilderImpl should return builder itself.";
   private static final String URI_FRAGMENT_ERROR = "ResteasyUriBuilderImpl object encodes valid characters in the fragment uri";

   /**
    * @tpTestDetails Uri method of ResteasyUriBuilderImpl object should return same object.
    * @tpSince RESTEasy 3.0.16
    */
   @Test
   public void testParseHierarchicalUri() {
      ResteasyUriBuilderImpl builder = new ResteasyUriBuilderImpl();
      assertSame(URI_ERROR, builder, builder.uri("foo/bar:id"));
      builder = new ResteasyUriBuilderImpl();
      assertSame(URI_ERROR, builder, builder.uri("/bar:id"));
      builder = new ResteasyUriBuilderImpl();
      assertSame(URI_ERROR, builder, builder.uri("foo:bar/bar:id"));
      builder = new ResteasyUriBuilderImpl();
      assertSame(URI_ERROR, builder, builder.uri("foo:/bar"));
      builder = new ResteasyUriBuilderImpl();
      assertSame(URI_ERROR, builder, builder.uri("foo:bar"));
   }

   /**
    * @tpTestDetails ResteasyUriBuilderImpl object should not encode valid characters in the fragment uri
    * @tpTestDetails RESTEASY-1261
    * @tpSince RESTEasy 3.0.16
    */
   @Test
   public void testUriWithFragment() {
      UriBuilder builder = ResteasyUriBuilderImpl.fromTemplate("http://domain.com/path#fragment=with/allowed/special-chars");
      assertEquals(URI_FRAGMENT_ERROR, "http://domain.com/path#fragment=with/allowed/special-chars", builder.build().toString());

      builder = ResteasyUriBuilderImpl.fromTemplate("http://domain.com/path#fragment%with[forbidden}special<chars");
      assertEquals(URI_FRAGMENT_ERROR, "http://domain.com/path#fragment%25with%5Bforbidden%7Dspecial%3Cchars", builder.build().toString());
   }
}
