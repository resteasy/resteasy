package org.jboss.resteasy.specimpl;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import javax.ws.rs.core.UriBuilder;

public class ResteasyUriBuilderTest {

  @Test
  public void testParseHierarchicalUri() {
    ResteasyUriBuilder builder = new ResteasyUriBuilder();
    assertSame(builder, builder.uri("foo/bar:id"));
    builder = new ResteasyUriBuilder();
    assertSame(builder, builder.uri("/bar:id"));
    builder = new ResteasyUriBuilder();
    assertSame(builder, builder.uri("foo:bar/bar:id"));
    builder = new ResteasyUriBuilder();
    assertSame(builder, builder.uri("foo:/bar"));
    builder = new ResteasyUriBuilder();
    assertSame(builder, builder.uri("foo:bar"));
  }
  
  @Test
  public void testUriWithFragment() {
    UriBuilder builder = ResteasyUriBuilder.fromTemplate("http://domain.com/path#fragment=with/allowed/special-chars");
    assertEquals("http://domain.com/path#fragment=with/allowed/special-chars", builder.build().toString());

    builder = ResteasyUriBuilder.fromTemplate("http://domain.com/path#fragment%with[forbidden}special<chars");
    assertEquals("http://domain.com/path#fragment%25with%5Bforbidden%7Dspecial%3Cchars", builder.build().toString());
  }
}
