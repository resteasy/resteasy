package org.jboss.resteasy.test.jose.jws;

import org.jboss.resteasy.jwt.JsonSerialization;
import org.jboss.resteasy.jwt.JsonWebToken;
import org.junit.Test;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class JWTTest
{
   @Test
   public void testJWT() throws Exception
   {
      JsonWebToken token = new JsonWebToken().id("123");
      String json = JsonSerialization.toString(token, true);
      System.out.println(json);
      token = JsonSerialization.fromString(JsonWebToken.class, json);
      System.out.println("id: " + token.getId());
   }
}
