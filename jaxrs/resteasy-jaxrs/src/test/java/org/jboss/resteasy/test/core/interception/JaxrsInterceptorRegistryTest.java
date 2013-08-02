package org.jboss.resteasy.test.core.interception;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.junit.Assert.assertEquals;

import org.jboss.resteasy.core.interception.JaxrsInterceptorRegistry;
import org.junit.Assert;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.NameBinding;
import javax.ws.rs.Path;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JaxrsInterceptorRegistryTest
{

    @NameBinding
    @Retention(RUNTIME)
    public static @interface JaxrsInterceptorRegistryTestNameBinding {
        
    }
    
    @JaxrsInterceptorRegistryTestNameBinding
    @Provider
    public static class JaxrsInterceptorRegistryTestFilter implements ContainerRequestFilter {
        @Override
        public void filter(ContainerRequestContext requestContext) throws IOException
        {

        }
    }
    
    @Path("/")
    public static class JaxrsInterceptorRegistryTestResource {
        
        @JaxrsInterceptorRegistryTestNameBinding
        @GET
        public void get() {
            
        }
    }

    @Test
    public void shouldUseNameBindingAnnotation() throws Exception {
        final List<Class<? extends Annotation>> bound = new ArrayList<Class<? extends Annotation>>();
        JaxrsInterceptorRegistry<JaxrsInterceptorRegistryTestFilter> jaxrsInterceptorRegistry = new JaxrsInterceptorRegistry<JaxrsInterceptorRegistryTestFilter>(null, JaxrsInterceptorRegistryTestFilter.class);
        jaxrsInterceptorRegistry.new AbstractInterceptorFactory(JaxrsInterceptorRegistryTestFilter.class)
        {
            {
                setPrecedence(JaxrsInterceptorRegistryTestFilter.class);
                bound.addAll(nameBound);
            }

            @Override
            protected void initialize()
            {
                
            }

            @Override
            protected Object getInterceptor()
            {
                return null;
            }
        };
        
        assertEquals(JaxrsInterceptorRegistryTestNameBinding.class, bound.get(0));
    }

   @Test
   public void testOrder()
   {
      List<JaxrsInterceptorRegistry.Match> matches = new ArrayList<JaxrsInterceptorRegistry.Match>();
      matches.add(new JaxrsInterceptorRegistry.Match(null, 200));
      matches.add(new JaxrsInterceptorRegistry.Match(null, 100));
      Collections.sort(matches, new JaxrsInterceptorRegistry.AscendingPrecedenceComparator());
      Assert.assertEquals(matches.get(0).order, 100);
      Assert.assertEquals(matches.get(1).order, 200);

   }


}
