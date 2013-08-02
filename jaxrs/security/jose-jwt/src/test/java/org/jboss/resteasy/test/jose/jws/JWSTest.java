package org.jboss.resteasy.test.jose.jws;

import junit.framework.Assert;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.jboss.resteasy.jose.jws.Algorithm;
import org.jboss.resteasy.jose.jws.JWSBuilder;
import org.jboss.resteasy.jose.jws.JWSHeader;
import org.jboss.resteasy.jose.jws.JWSInput;
import org.jboss.resteasy.jose.jws.crypto.HMACProvider;
import org.jboss.resteasy.jose.jws.crypto.RSAProvider;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.junit.Test;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.ws.rs.core.MediaType;
import java.security.KeyPair;
import java.security.KeyPairGenerator;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class JWSTest
{
   @Test
   public void testHeaderSerialization() throws Exception
   {
      ObjectMapper mapper = new ObjectMapper();
      mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
      mapper.enable(SerializationConfig.Feature.INDENT_OUTPUT);
      JWSHeader header = new JWSHeader(Algorithm.HS256, null, null);
      String val = header.toString();
      System.out.println(val);
      header = mapper.readValue(val, JWSHeader.class);
      val = mapper.writeValueAsString(header);
      System.out.println(val);

   }

   @Test
   public void testRSA() throws Exception
   {
      KeyPair keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();

      String encoded = new JWSBuilder()
              .content("Hello World".getBytes("UTF-8"))
              .rsa256(keyPair.getPrivate());

      System.out.println(encoded);

      JWSInput input = new JWSInput(encoded, ResteasyProviderFactory.getInstance());
      String msg = (String)input.readContent(String.class, null, null, MediaType.TEXT_PLAIN_TYPE);
      Assert.assertEquals("Hello World", msg);
      Assert.assertTrue(RSAProvider.verify(input, keyPair.getPublic()));

   }

   @Test
   public void testRSAWithContentType() throws Exception
   {
      KeyPair keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();

      String encoded = new JWSBuilder()
              .contentType(MediaType.TEXT_PLAIN_TYPE)
              .content("Hello World", MediaType.TEXT_PLAIN_TYPE)
              .rsa256(keyPair.getPrivate());

      System.out.println(encoded);

      JWSInput input = new JWSInput(encoded, ResteasyProviderFactory.getInstance());
      System.out.println(input.getHeader());
      String msg = (String)input.readContent(String.class);
      Assert.assertEquals("Hello World", msg);
      Assert.assertTrue(RSAProvider.verify(input, keyPair.getPublic()));

   }

   @Test
   public void testHMACWithContentType() throws Exception
   {
      SecretKey key = KeyGenerator.getInstance(HMACProvider.getJavaAlgorithm(Algorithm.HS256)).generateKey();

      String encoded = new JWSBuilder()
              .contentType(MediaType.TEXT_PLAIN_TYPE)
              .content("Hello World", MediaType.TEXT_PLAIN_TYPE)
              .hmac256(key);

      System.out.println(encoded);

      JWSInput input = new JWSInput(encoded, ResteasyProviderFactory.getInstance());
      System.out.println(input.getHeader());
      String msg = (String)input.readContent(String.class);
      Assert.assertEquals("Hello World", msg);
      Assert.assertTrue(HMACProvider.verify(input, key));

   }


}
