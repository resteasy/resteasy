package org.jboss.resteasy.test.injection;

import org.jboss.resteasy.cdi.ResteasyCdiExtension;
import org.jboss.resteasy.test.injection.resource.SessionBeanInterfaceFoo;
import org.jboss.resteasy.test.injection.resource.SessionBeanInterfaceFooLocal;
import org.jboss.resteasy.test.injection.resource.SessionBeanInterfaceFooLocal2;
import org.jboss.resteasy.test.injection.resource.SessionBeanInterfaceFooLocal3;
import org.jboss.resteasy.test.injection.resource.SessionBeanInterfaceMockBean;
import org.jboss.resteasy.test.injection.resource.SessionBeanInterfaceMockProcessSessionBean;
import org.junit.Before;
import org.junit.Test;

import javax.enterprise.inject.spi.Bean;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertTrue;

/**
 * @tpSubChapter Injection tests
 * @tpChapter Unit tests
 * @tpTestCaseDetails Session bean interface test.
 * @tpSince RESTEasy 3.0.16
 */
public class SessionBeanInterfaceTest {
    private ResteasyCdiExtension extension;

    @Before
    public void prepare() {
        extension = new ResteasyCdiExtension();
    }

    /**
     * @tpTestDetails Interface is selected
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testJaxrsAnnotatedInterfaceSelected() {
        Set<Type> types = new HashSet<Type>();
        types.add(SessionBeanInterfaceFooLocal.class);
        types.add(SessionBeanInterfaceFooLocal2.class);
        types.add(SessionBeanInterfaceFooLocal3.class);
        types.add(Object.class);
        Bean<Object> bean = new SessionBeanInterfaceMockBean<>(SessionBeanInterfaceFoo.class, types);
        extension.observeSessionBeans(new SessionBeanInterfaceMockProcessSessionBean<>(bean));
        assertTrue("Wrong interface was return by ResteasyCdiExtension", extension.getSessionBeanInterface().get(SessionBeanInterfaceFoo.class).equals(SessionBeanInterfaceFooLocal3.class));
    }

    /**
     * @tpTestDetails Unmarshaller test
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testNoInterfaceSelected() {
        Set<Type> types = new HashSet<>();
        types.add(SessionBeanInterfaceFoo.class);
        types.add(Object.class);
        Bean<Object> bean = new SessionBeanInterfaceMockBean<>(SessionBeanInterfaceFoo.class, types);
        extension.observeSessionBeans(new SessionBeanInterfaceMockProcessSessionBean<>(bean));
        assertTrue("Any interface should not be returned by ResteasyCdiExtension", extension.getSessionBeanInterface().isEmpty());
    }
}
