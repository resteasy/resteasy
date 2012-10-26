package org.jboss.resteasy.tests;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.engines.ApacheHttpClient4Engine;
import org.jboss.resteasy.security.BouncyIntegration;
import org.jboss.resteasy.security.KeyTools;
import org.jboss.resteasy.security.smime.PKCS7SignatureInput;
import org.jboss.resteasy.security.smime.SignedOutput;
import org.junit.Assert;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.SignatureException;
import java.security.cert.X509Certificate;

/**
 * Should expose features not available in AS7
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Path("/test")
public class NewFeaturesResource
{
   private X509Certificate cert;
   private PrivateKey privateKey;

   public static final ObjectMapper DEFAULT_MAPPER = new ObjectMapper();
   public static final ObjectMapper WRAPPED_MAPPER = new ObjectMapper();

   // this whole block makes sure Jackson is exported
   static
   {
      DEFAULT_MAPPER.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
      //DEFAULT_MAPPER.enable(SerializationConfig.Feature.INDENT_OUTPUT);
      DEFAULT_MAPPER.enable(DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY);


      WRAPPED_MAPPER.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
      //WRAPPED_MAPPER.enable(SerializationConfig.Feature.INDENT_OUTPUT);
      WRAPPED_MAPPER.enable(SerializationConfig.Feature.WRAP_ROOT_VALUE);
      WRAPPED_MAPPER.enable(DeserializationConfig.Feature.UNWRAP_ROOT_VALUE);
      WRAPPED_MAPPER.enable(DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY);

   }

   public NewFeaturesResource()
   {
      try
      {
         BouncyIntegration.init();
         KeyPair keyPair = KeyPairGenerator.getInstance("RSA", "BC").generateKeyPair();
         privateKey = keyPair.getPrivate();
         cert = KeyTools.generateTestCertificate(keyPair);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   @GET
   @Path("signed/pkcs7-signature")
   @Produces("application/pkcs7-signature")
   public SignedOutput get()
   {
      SignedOutput output = new SignedOutput("hello world", "text/plain");
      output.setCertificate(cert);
      output.setPrivateKey(privateKey);
      return output;
   }

   // make sure resteasy-crypto is exported
   @GET
   @Path("signed/text")
   @Produces("text/plain")
   public SignedOutput getText()
   {
      // just allocate a client to test that resteasy client is available and Apache HC4 is exposed.
      ResteasyClient client = new ResteasyClient();
      ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager();
      cm.setMaxTotal(100);
      cm.setDefaultMaxPerRoute(100);
      HttpClient httpClient = new DefaultHttpClient(cm);
      client.httpEngine(new ApacheHttpClient4Engine(httpClient));

      SignedOutput output = new SignedOutput("hello world", "text/plain");
      output.setCertificate(cert);
      output.setPrivateKey(privateKey);
      return output;
   }

}
