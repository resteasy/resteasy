package com.restfully.shop.features;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import java.io.IOException;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class OneTimePasswordGenerator implements ClientRequestFilter
{
   protected String user;
   protected String secret;

   public OneTimePasswordGenerator(String user, String secret)
   {
      this.user = user;
      this.secret = secret;
   }

   @Override
   public void filter(ClientRequestContext requestContext) throws IOException
   {
      String otp = OTP.generateToken(secret);
      requestContext.getHeaders().putSingle(HttpHeaders.AUTHORIZATION, user + " " + otp);
   }
}
