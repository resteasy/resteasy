package org.jboss.resteasy.test.jose.jws;

import org.jboss.resteasy.jwt.JWTContextResolver;
import org.jboss.resteasy.jwt.JsonSerialization;
import org.jboss.resteasy.jwt.JsonWebToken;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Providers;
import java.io.ByteArrayOutputStream;

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
