package org.jboss.resteasy.security;

import org.jboss.resteasy.spi.NotImplementedYetException;
import org.jboss.resteasy.spi.ResteasyUriInfo;
import org.jboss.resteasy.util.Base64;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.cert.X509Certificate;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class UriSignature
{
   public static URI sign(PrivateKey key, URI uri, SigningAlgorithm algorithm) throws Exception
   {
      Signature signature = Signature.getInstance(algorithm.getJavaSecNotation());
      signature.initSign(key);
      signature.update(uri.toString().getBytes("UTF-8"));
      byte[] sig = signature.sign();
      String base64 = Base64.encodeBytes(sig);

      return UriBuilder.fromUri(uri).queryParam("sig", base64).queryParam("algorithm", algorithm.getRfcNotation()).build();
   }

   public static boolean verify(X509Certificate cert, String uriString) throws Exception
   {
      throw new NotImplementedYetException();
   }
}
