package org.jboss.resteasy.test.interception;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.NameBinding;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;

import org.jboss.resteasy.core.interception.jaxrs.JaxrsInterceptorRegistryImpl;
import org.jboss.resteasy.spi.interception.JaxrsInterceptorRegistry;
import org.junit.Assert;
import org.junit.Test;

/**
 * @tpSubChapter Interception tests
 * @tpChapter Unit tests
 * @tpTestCaseDetails Unit test for JaxrsInterceptorRegistry class.
 * @tpSince RESTEasy 3.0.16
 */
public class JaxrsInterceptorRegistryTest {

    /**
     * @tpTestDetails Test for using name binding annotation.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void shouldUseNameBindingAnnotation() throws Exception {
        final List<Class<? extends Annotation>> bound = new ArrayList<Class<? extends Annotation>>();
        JaxrsInterceptorRegistryImpl<JaxrsInterceptorRegistryTestFilter> jaxrsInterceptorRegistry = new JaxrsInterceptorRegistryImpl<JaxrsInterceptorRegistryTestFilter>(
                null, JaxrsInterceptorRegistryTestFilter.class);
        jaxrsInterceptorRegistry.new AbstractInterceptorFactory(JaxrsInterceptorRegistryTestFilter.class) {
            @Override
            protected void initialize() {
            }

            {
                setPrecedence(JaxrsInterceptorRegistryTestFilter.class);
                bound.addAll(nameBound);
            }

            @Override
            protected Object getInterceptor() {
                return null;
            }
        };

        assertEquals("JaxrsInterceptorRegistryTestNameBinding was not used", JaxrsInterceptorRegistryTestNameBinding.class,
                bound.get(0));
    }

    /**
     * @tpTestDetails Test for JaxrsInterceptorRegistry.Match class
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testOrder() {
        List<JaxrsInterceptorRegistry.Match> matches = new ArrayList<JaxrsInterceptorRegistry.Match>();
        matches.add(new JaxrsInterceptorRegistry.Match(null, 200));
        matches.add(new JaxrsInterceptorRegistry.Match(null, 100));
        Collections.sort(matches, new JaxrsInterceptorRegistry.AscendingPrecedenceComparator());
        Assert.assertEquals("Wrong order in list", matches.get(0).order, 100);
        Assert.assertEquals("Wrong order in list", matches.get(1).order, 200);

    }

    @NameBinding
    @Retention(RUNTIME)
    public @interface JaxrsInterceptorRegistryTestNameBinding {
    }

    @JaxrsInterceptorRegistryTestNameBinding
    @Provider
    public static class JaxrsInterceptorRegistryTestFilter implements ContainerRequestFilter {
        @Override
        public void filter(ContainerRequestContext requestContext) throws IOException {

        }
    }

    @Path("/")
    public static class JaxrsInterceptorRegistryTestResource {

        @JaxrsInterceptorRegistryTestNameBinding
        @GET
        public void get() {

        }
    }

}
