package org.jboss.resteasy.tests.signature;

import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.security.keys.KeyRepository;
import org.jboss.resteasy.security.keys.KeyStoreKeyRepository;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class SignatureApplication extends Application
{
   private HashSet<Class<?>> classes = new HashSet<Class<?>>();
   private KeyRepository repository;

   public SignatureApplication(@Context Dispatcher dispatcher)
   {
      classes.add(SignedResource.class);

      System.out.println("APPLICATION!!!!!!!!! " + dispatcher);
      InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("test.jks");
      repository = new KeyStoreKeyRepository(is, "password");
      dispatcher.getDefaultContextObjects().put(KeyRepository.class, repository);
   }

   @Override
   public Set<Class<?>> getClasses()
   {
      return classes;
   }
}
