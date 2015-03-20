package org.jboss.resteasy.test.client;

import junit.framework.Assert;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.internal.ClientConfiguration;
import org.jboss.resteasy.client.jaxrs.internal.ClientWebTarget;
import org.jboss.resteasy.specimpl.LinkImpl;
import org.jboss.resteasy.specimpl.ResteasyUriBuilder;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.ext.RuntimeDelegate;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ClientBuilderTest
{
   @Test
   public void RESTEASY_1163_resteasyclient_should_use_ProviderFactory_to_create_UriBuilder_instances() throws Exception {
      class MustNotBeUsedRuntimeDelegate extends ResteasyProviderFactory {
         @Override
         public UriBuilder createUriBuilder() {
            throw new AssertionError("shouldn't call global RuntimeDelegate to obtain UriBuilder instance");
         }
      }
      final Link TESTLINK = Link.fromUri("http://dummy.local/someparam/myname").build();

      RuntimeDelegate original = RuntimeDelegate.getInstance();
      try {
         RuntimeDelegate.setInstance(new MustNotBeUsedRuntimeDelegate());

         final Client client = new ResteasyClientBuilder().build();

         // ctor 1
         client.target("http://dummy.local/{someparam}/{myname}/");
         // ctor 2
         client.target(new URI("http://dummy.local/someparam/myname/"));
         // ctor 3
         client.target(new ResteasyUriBuilder().uri("http://dummy.local/{someparam}/{myname}/"));
         // ctor 4
         client.target(TESTLINK);
      } finally {
         RuntimeDelegate.setInstance(original);
      }
   }

   @Test
   public void RESTEASY_1163_resteasyclient_should_clone_uriBuilder_on_resolveTemplate() throws Exception {
      Field fldUriBuilder = ClientWebTarget.class.getDeclaredField("uriBuilder");
      fldUriBuilder.setAccessible(true);

      /**
       * Emulate a UriBuilder implementation that doesn't return copies on resolveTemplate(), like Jersey's UriBuilder
       */
      class MisbehavingUriBuilder extends ResteasyUriBuilder {
         @Override
         public UriBuilder resolveTemplate(String name, Object value) throws IllegalArgumentException {
            this.uri("changed");
            return this;
         }

         @Override
         public UriBuilder resolveTemplates(Map<String, Object> templateValues) throws IllegalArgumentException {
            this.uri("changed");
            return this;
         }

         @Override
         public UriBuilder resolveTemplate(String name, Object value, boolean encodeSlashInPath) throws IllegalArgumentException {
            this.uri("changed");
            return this;
         }

         @Override
         public UriBuilder resolveTemplateFromEncoded(String name, Object value) throws IllegalArgumentException {
            this.uri("changed");
            return this;
         }

         @Override
         public UriBuilder resolveTemplates(Map<String, Object> templateValues, boolean encodeSlashInPath) throws IllegalArgumentException {
            this.uri("changed");
            return this;
         }

         @Override
         public UriBuilder resolveTemplatesFromEncoded(Map<String, Object> templateValues) throws IllegalArgumentException {
            this.uri("changed");
            return this;
         }
      }

      final WebTarget webTarget = new ResteasyClientBuilder().build().target("http://dummy.local/{someparam}/");
      fldUriBuilder.set(webTarget, new MisbehavingUriBuilder().uri("http://dummy.local/{someparam}/"));

      WebTarget resolvedWebTarget;

      resolvedWebTarget = webTarget.resolveTemplate("someparam", "someval");
      Assert.assertEquals("http://dummy.local/{someparam}/", webTarget.getUriBuilder().toTemplate());

      resolvedWebTarget = webTarget.resolveTemplate("someparam", "someval", true);
      Assert.assertEquals("http://dummy.local/{someparam}/", webTarget.getUriBuilder().toTemplate());

      resolvedWebTarget = webTarget.resolveTemplateFromEncoded("someparam", "someval");
      Assert.assertEquals("http://dummy.local/{someparam}/", webTarget.getUriBuilder().toTemplate());

      resolvedWebTarget = webTarget.resolveTemplates(new HashMap<String, Object>() {{
         put("someparam", "someval");
      }});
      Assert.assertEquals("http://dummy.local/{someparam}/", webTarget.getUriBuilder().toTemplate());

      resolvedWebTarget = webTarget.resolveTemplates(new HashMap<String, Object>() {{
         put("someparam", "someval");
      }}, true);
      Assert.assertEquals("http://dummy.local/{someparam}/", webTarget.getUriBuilder().toTemplate());

      resolvedWebTarget = webTarget.resolveTemplatesFromEncoded(new HashMap<String, Object>() {{
         put("someparam", "someval");
      }});
      Assert.assertEquals("http://dummy.local/{someparam}/", webTarget.getUriBuilder().toTemplate());
   }

   @Test
   public void entityStringThrowsExceptionWhenUnparsableTest() throws Exception {
      try {
         Entity.entity("entity", "\\//\\");
         Assert.fail();
      } catch (IllegalArgumentException e) {
      }
   }

   @Test
   public void testBuilder() throws Exception
   {
      String property = "prop";
      Client client = ClientBuilder.newClient();
      client.property(property, property);
      Configuration config = client.getConfiguration();
      client = ClientBuilder.newClient(config);

   }

   public static void inner() throws Exception
   {
      Feature feature = new Feature() {
         @Override
         public boolean configure(FeatureContext context)
         {
            return false;
         }
      };

      System.out.println("is static: " + Modifier.isStatic(feature.getClass().getModifiers()));
      Client client = ClientBuilder.newClient();
      client.register(feature.getClass());

   }

   //@Test
   public void testInnerFeature() throws Exception
   {
      // TCK uses anonymous non-static inner classes to test.  BOGUS POOP!
      System.out.println("non-static");
      inner();
      System.out.println("non-static");
      Feature feature = new Feature() {
         @Override
         public boolean configure(FeatureContext context)
         {
            return false;
         }
      };
      System.out.println("is static: " + Modifier.isStatic(feature.getClass().getModifiers()));

      Client client = ClientBuilder.newClient();
      client.register(feature.getClass());


   }

   public static class FeatureReturningFalse implements Feature {
      @Override
      public boolean configure(FeatureContext context) {
         // false returning feature is not to be registered
         return false;
      }
   }

   @Test
   public void testDoubleClassRegistration()
   {
      Client client = ClientBuilder.newClient();
      int count = client.getConfiguration().getClasses().size();
      client.register(FeatureReturningFalse.class).register(FeatureReturningFalse.class);
      Assert.assertEquals(count + 1, client.getConfiguration().getClasses().size());
      client.close();

   }

   @Test
   public void testDoubleRegistration()
   {
      Client client = ClientBuilder.newClient();
      int count = client.getConfiguration().getInstances().size();
      Object reg = new FeatureReturningFalse();
      client.register(reg);
      client.register(reg);
      Assert.assertEquals(count + 1, client.getConfiguration().getInstances().size());
      client.close();

   }



}
