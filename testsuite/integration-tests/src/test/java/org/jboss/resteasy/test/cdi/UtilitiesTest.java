package org.jboss.resteasy.test.cdi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.context.SessionScoped;
import jakarta.enterprise.util.AnnotationLiteral;

import org.jboss.resteasy.test.cdi.extensions.resource.CDIExtensionsBoston;
import org.jboss.resteasy.test.cdi.extensions.resource.CDIExtensionsBostonHolder;
import org.jboss.resteasy.test.cdi.extensions.resource.CDIExtensionsBostonlLeaf;
import org.jboss.resteasy.test.cdi.extensions.resource.CDIExtensionsResource;
import org.jboss.resteasy.test.cdi.injection.resource.CDIInjectionBookResource;
import org.jboss.resteasy.test.cdi.util.Utilities;
import org.junit.jupiter.api.Test;

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
        assertEquals(1, qualifiers.size(), "Wrong count of qualifieers");
        HashSet<Class<?>> qualifierClasses = new HashSet<Class<?>>();
        Iterator<Annotation> it = qualifiers.iterator();
        while (it.hasNext()) {
            Annotation a = it.next();
            log.info("a type: " + a.annotationType());
            qualifierClasses.add(a.annotationType());
        }
        assertTrue(qualifierClasses.contains(CDIExtensionsBoston.class),
                "CDIExtensionsBoston is not qualifier");
        log.info("testAnnotation() PASSES");
    }

    /**
     * @tpTestDetails Test for hasQualifier method.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testHasQualifier() {
        log.info("entering testHasQualifier()");
        assertTrue(Utilities.hasQualifier(CDIInjectionBookResource.class, RequestScoped.class),
                "CDIInjectionBookResource should have qualifier");
        assertTrue(Utilities.isBoston(CDIExtensionsBostonHolder.class),
                "CDIExtensionsBostonHolder should have boston qualifier");
        assertTrue(Utilities.isBoston(CDIExtensionsBostonlLeaf.class), "CDIExtensionsBostonlLeaf should have boston qualifier");
        assertFalse(Utilities.isBoston(CDIExtensionsResource.class), "CDIExtensionsResource should not have boston qualifier");
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
        assertEquals(5, set.size(), "Set of getTypeClosure for c2 class don't contains correct classes");
        assertTrue(set.contains(i1.class) && set.contains(i2.class) && set.contains(c1.class)
                && set.contains(c2.class) && set.contains(Object.class),
                "Set of getTypeClosure for c2 class don't contains correct classes");
    }

    /**
     * @tpTestDetails Test for isAnnotationPresent method.
     * @tpSince RESTEasy 3.0.16
     */
    @SuppressWarnings("serial")
    @Test
    public void testAnnotationPresent() throws Exception {
        assertTrue(Utilities.isAnnotationPresent(C.class,
                new AnnotationLiteral<CDIExtensionsBoston>() {
                }.annotationType()),
                "Wrong annotations for CDIExtensionsBoston class");

        assertTrue(Utilities.isAnnotationPresent(C.class,
                new AnnotationLiteral<RequestScoped>() {
                }.annotationType()),
                "Wrong annotations for AnnotationLiteral class");

        assertFalse(Utilities.isAnnotationPresent(C.class,
                new AnnotationLiteral<SessionScoped>() {
                }.annotationType()),
                "Wrong annotations for SessionScoped class");
    }
}
