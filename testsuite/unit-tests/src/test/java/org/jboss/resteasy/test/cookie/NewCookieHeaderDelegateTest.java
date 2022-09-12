package org.jboss.resteasy.test.cookie;

import static org.junit.Assert.assertEquals;

import jakarta.ws.rs.core.NewCookie;

import org.jboss.resteasy.plugins.delegates.NewCookieHeaderDelegate;
import org.junit.Before;
import org.junit.Test;

public class NewCookieHeaderDelegateTest
{

   private NewCookieHeaderDelegate delegate;

   @Before
   public void setUp() throws Exception
   {
      delegate = new NewCookieHeaderDelegate();
   }

   @Test
   public void testParseIgnoresUnknownCookieAttributes()
   {

      String expectedCookieName = "JSESSIONID";
      String expectedCookieValue = "1fn1creezbh0117ej8n463jjwm";
      NewCookie newCookie = delegate.fromString(expectedCookieName + "=" + expectedCookieValue + ";Path=/path;UnknownAttribute=AnyValue");

      assertEquals(expectedCookieName, newCookie.getName());
      assertEquals(expectedCookieValue, newCookie.getValue());
   }

}