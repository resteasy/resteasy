package org.jboss.resteasy.test.cdi;

import org.jboss.resteasy.test.cdi.extensions.resource.CDIExtensionsBoston;
import org.jboss.resteasy.test.cdi.extensions.resource.CDIExtensionsBostonHolder;
import org.jboss.resteasy.test.cdi.extensions.resource.CDIExtensionsBostonlLeaf;
import org.jboss.resteasy.test.cdi.extensions.resource.CDIExtensionsResource;
import org.jboss.resteasy.test.cdi.injection.resource.CDIInjectionBookResource;
import org.jboss.resteasy.test.cdi.util.Utilities;
import org.junit.Test;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.util.AnnotationLiteral;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @tpSubChapter Util tests
 * @tpChapter Unit tests
 * @tpTestCaseDetails Basic unit test for Utilities class used for many CDI integration tests.
 * @tpSince RESTEasy 3.0.16
 */
public class UtilitiesTest {
    private static Logger log = Logger.getLogger(UtilitiesTest.class.getName());

    @RequestScoped
    @CDIExtensionsBoston
    static class C {
    }

    /**
     * @tpTestDetails Test for getQualifiers method.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetQualifiers() {
        log.info("entering testGetQualifiers()");
        Set<Annotation> qualifiers = Utilities.getQualifiers(C.class);
        log.info("qualifiers: " + qualifiers);
        assertEquals("Wrong count of qualifieers", 1, qualifiers.size());
        HashSet<Class<?>> qualifierClasses = new HashSet<Class<?>>();
        Iterator<Annotation> it = qualifiers.iterator();
        while (it.hasNext()) {
            Annotation a = it.next();
            log.info("a type: " + a.annotationType());
            qualifierClasses.add(a.annotationType());
        }
        assertTrue("CDIExtensionsBoston is not qualifier", qualifierClasses.contains(CDIExtensionsBoston.class));
        log.info("testAnnotation() PASSES");
    }

    /**
     * @tpTestDetails Test for hasQualifier method.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testHasQualifier() {
        log.info("entering testHasQualifier()");
        assertTrue("CDIInjectionBookResource should have qualifier", Utilities.hasQualifier(CDIInjectionBookResource.class, RequestScoped.class));
        assertTrue("CDIExtensionsBostonHolder should have boston qualifier", Utilities.isBoston(CDIExtensionsBostonHolder.class));
        assertTrue("CDIExtensionsBostonlLeaf should have boston qualifier", Utilities.isBoston(CDIExtensionsBostonlLeaf.class));
        assertFalse("CDIExtensionsResource should not have boston qualifier", Utilities.isBoston(CDIExtensionsResource.class));
    }

    interface i1 {
    }

    interface i2 extends i1 {
    }

    static class c1 implements i1 {
    }

    static class c2 extends c1 implements i2 {
    }

    /**
     * @tpTestDetails Test for getTypeClosure method.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testTypeClosure() throws Exception {
        log.info("entering testTypeClosure()");
        Set<Type> set = Utilities.getTypeClosure(c2.class);
        log.info("Set: " + set);
        assertEquals("Set of getTypeClosure for c2 class don't contains correct classes", 5, set.size());
        assertTrue("Set of getTypeClosure for c2 class don't contains correct classes",
                set.contains(i1.class) && set.contains(i2.class) && set.contains(c1.class)
                        && set.contains(c2.class) && set.contains(Object.class));
    }

    /**
     * @tpTestDetails Test for isAnnotationPresent method.
     * @tpSince RESTEasy 3.0.16
     */
    @SuppressWarnings("serial")
    @Test
    public void testAnnotationPresent() throws Exception {
        assertTrue("Wrong annotations for CDIExtensionsBoston class", Utilities.isAnnotationPresent(C.class,
                new AnnotationLiteral<CDIExtensionsBoston>() {
        }.annotationType()));

        assertTrue("Wrong annotations for AnnotationLiteral class", Utilities.isAnnotationPresent(C.class,
                new AnnotationLiteral<RequestScoped>() {
        }.annotationType()));

        assertFalse("Wrong annotations for SessionScoped class", Utilities.isAnnotationPresent(C.class,
                new AnnotationLiteral<SessionScoped>() {
        }.annotationType()));
    }
}

