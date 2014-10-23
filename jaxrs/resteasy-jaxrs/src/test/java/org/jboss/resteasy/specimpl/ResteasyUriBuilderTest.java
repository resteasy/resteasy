package org.jboss.resteasy.specimpl;

import static org.junit.Assert.assertSame;

import org.junit.Test;

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
}
