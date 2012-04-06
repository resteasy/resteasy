package org.jboss.resteasy.tests.smime;

import org.jboss.resteasy.security.PemUtils;

import javax.ws.rs.core.Application;
import java.io.InputStream;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class SMIMEApplication extends Application
{
   private Set<Object> resources = new HashSet<Object>();

   public SMIMEApplication() throws Exception
   {
      InputStream privatePem = Thread.currentThread().getContextClassLoader().getResourceAsStream("private.pem");
      PrivateKey privateKey = PemUtils.decodePrivateKey(privatePem);

      InputStream certPem = Thread.currentThread().getContextClassLoader().getResourceAsStream("cert.pem");
      X509Certificate cert = PemUtils.decodeCertificate(certPem);

      SMIMEResource resource = new SMIMEResource(privateKey, cert);
      resources.add(resource);
   }

   @Override
   public Set<Object> getSingletons()
   {
      return resources;
   }
}
