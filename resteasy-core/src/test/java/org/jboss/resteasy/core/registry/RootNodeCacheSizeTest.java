package org.jboss.resteasy.core.registry;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Method;

import jakarta.ws.rs.core.MediaType;

import org.jboss.resteasy.core.InjectorFactoryImpl;
import org.jboss.resteasy.core.ResourceMethodInvoker;
import org.jboss.resteasy.core.providerfactory.ResteasyProviderFactoryImpl;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.plugins.server.resourcefactory.POJOResourceFactory;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.InjectorFactory;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.metadata.DefaultResourceClass;
import org.jboss.resteasy.spi.metadata.DefaultResourceMethod;
import org.jboss.resteasy.spi.metadata.ResourceBuilder;
import org.jboss.resteasy.spi.metadata.ResourceClass;
import org.jboss.resteasy.spi.metadata.ResourceMethod;
import org.junit.jupiter.api.Test;

public class RootNodeCacheSizeTest {

    @Test
    public void testRootNodeCacheSize() throws Exception {
        MyRootNode rootNode = new MyRootNode();
        rootNode.adjustRoot();

        for (int i = 0; i < 2050; i++) {
            rootNode.match(MockHttpRequest.get("" + i).contentType(MediaType.TEXT_PLAIN_TYPE), 0);
        }

        // Default in RootNode is CACHE_SIZE = 2048;
        assertEquals(2, rootNode.cacheSize(),
                () -> "Cache is expected to be cleared when size exceeded 2048 items");
        for (int i = 0; i < 10; i++) {
            rootNode.match(MockHttpRequest.get("" + i).contentType(MediaType.valueOf("text/html;boundary=from" + i)), 0);
        }
        //MediaType with parameters won't be cached
        assertEquals(2, rootNode.cacheSize(), () -> "Unexpected cache item");
    }

    public class MyRootNode extends RootNode {
        public int cacheSize() {
            return cache.size();
        }

        public void adjustRoot() {
            root = new MySegmentNode("");
        }
    }

    public class MySegmentNode extends SegmentNode {
        public MySegmentNode(final String segment) {
            super(segment);
        }

        public String foo() {
            return "foo";
        }

        @Override
        public MatchCache match(HttpRequest request, int start) {

            // Create sample ResourceMethodInvoker
            Class<?> clazz = MySegmentNode.class;
            Method method = null;
            try {
                method = MySegmentNode.class.getMethod("foo");
            } catch (NoSuchMethodException e) {
                // ignore, method foo is defined
            }
            ResourceClass resourceClass = new DefaultResourceClass(MySegmentNode.class, "path");
            ResourceMethod resourceMethod = new DefaultResourceMethod(resourceClass, method, method);
            ResteasyProviderFactory providerFactory = new ResteasyProviderFactoryImpl();
            InjectorFactory injectorFactory = new InjectorFactoryImpl();
            ResourceBuilder resourceBuilder = new ResourceBuilder();
            POJOResourceFactory resourceFactory = new POJOResourceFactory(resourceBuilder, clazz);
            ResourceMethodInvoker resourceMethodInvoker = new ResourceMethodInvoker(resourceMethod, injectorFactory,
                    resourceFactory, providerFactory);

            MatchCache match = new MatchCache();
            match.match = new SegmentNode.Match(new MyMethodExpression(), null);
            match.invoker = resourceMethodInvoker;
            // returned match needs to be compliant with this if clause from RootNode
            // if (match.match != null &&
            //      match.match.expression.getNumGroups() == 0 &&
            //      match.invoker instanceof ResourceMethodInvoker) {
            return match;
        }
    }

    public class MyMethodExpression extends MethodExpression {

        public MyMethodExpression() {
            super(null, "foo", null);
        }

        @Override
        public int getNumGroups() {
            return 0;
        }
    }
}
