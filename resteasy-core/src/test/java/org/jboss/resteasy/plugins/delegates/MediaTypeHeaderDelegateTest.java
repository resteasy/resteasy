package org.jboss.resteasy.plugins.delegates;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.MediaType;
import org.junit.Before;
import org.junit.Test;

public class MediaTypeHeaderDelegateTest {

  private MediaTypeHeaderDelegate delegate;

  @Before
  public void setUp() throws Exception {
    delegate = new MediaTypeHeaderDelegate();
  }

  @Test
  public void testParseStripsTrailingSemicolonWhenParsingDodgyContentType() {
    MediaTypeHeaderDelegate.parse("application/json;");

    assertEquals("application/json", delegate.toString(new MediaType("application", "json")));
  }
}