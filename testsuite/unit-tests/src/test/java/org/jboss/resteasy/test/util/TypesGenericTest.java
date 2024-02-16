package org.jboss.resteasy.test.util;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

import org.jboss.logging.Logger;
import org.jboss.resteasy.spi.util.Types;
import org.jboss.resteasy.test.util.resource.TypesGenericAnotherBar;
import org.jboss.resteasy.test.util.resource.TypesGenericAnotherFooBar;
import org.jboss.resteasy.test.util.resource.TypesGenericBar;
import org.jboss.resteasy.test.util.resource.TypesGenericClassBar;
import org.jboss.resteasy.test.util.resource.TypesGenericClassFooBar;
import org.jboss.resteasy.test.util.resource.TypesGenericClassSub;
import org.jboss.resteasy.test.util.resource.TypesGenericFooBar;
import org.jboss.resteasy.test.util.resource.TypesGenericSub;
import org.jboss.resteasy.test.util.resource.TypesGenericSubClass;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @tpSubChapter Util tests
 * @tpChapter Unit tests
 * @tpTestCaseDetails Test for Types class for generic types.
 * @tpSince RESTEasy 3.0.16
 */
public class TypesGenericTest {

    protected final Logger logger = Logger.getLogger(TypesGenericTest.class.getName());

    /**
     * @tpTestDetails Check findParameterizedTypes method of Types class for corrected parametrized type.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testInterfaceGenericTypeDiscovery() throws Exception {
        Type[] types = Types.findParameterizedTypes(TypesGenericFooBar.class, TypesGenericBar.class);
        for (Type t : types) {
            logger.debug(t);
        }

        Assertions.assertEquals(types[0], Float.class, "Expected type for the class doesn't match");

        types = Types.findParameterizedTypes(TypesGenericSubClass.class, TypesGenericBar.class);
        for (Type t : types) {
            logger.debug(t);
        }

        Assertions.assertEquals(types[0], Float.class, "Expected type for the class doesn't match");

        types = Types.findParameterizedTypes(TypesGenericSub.class, TypesGenericBar.class);
        for (Type t : types) {
            logger.debug(t);
        }

        Assertions.assertEquals(types[0], Float.class, "Expected type for the class doesn't match");

        types = Types.findParameterizedTypes(TypesGenericAnotherFooBar.class, TypesGenericAnotherBar.class);
        Assertions.assertEquals(0, types.length, "No parametrized type expected");
    }

    /**
     * @tpTestDetails Check getImplementingMethod method of Types class for implemented methods.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testFindImplementedMethod() throws Exception {
        Class bar = TypesGenericFooBar.class.getInterfaces()[0].getInterfaces()[0];
        Method barMethod = null;
        for (Method m : bar.getMethods()) {
            if (!m.isSynthetic() && m.getName().equals("bar")) {
                barMethod = m;
            }
        }

        Method implented = Types.getImplementingMethod(TypesGenericFooBar.class, barMethod);

        Method actual = null;
        for (Method m : TypesGenericFooBar.class.getMethods()) {
            if (!m.isSynthetic() && m.getName().equals("bar")) {
                if (m.getParameterTypes()[0].equals(Float.class)) {
                    actual = m;
                }
            }
        }

        Assertions.assertEquals(implented, actual,
                "The method implemented by the class an returned by getImplementingMethod() is not the expected one");
    }

    /**
     * @tpTestDetails Check findParameterizedTypes method of Types class for generic type discovery.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testClassGenericTypeDiscovery() throws Exception {
        Type[] types = Types.findParameterizedTypes(TypesGenericClassFooBar.class, TypesGenericClassBar.class);
        for (Type t : types) {
            logger.debug(t);
        }

        Assertions.assertEquals(types[0], Float.class, "Expected type for the class doesn't match");

        types = Types.findParameterizedTypes(TypesGenericClassSub.class, TypesGenericClassBar.class);
        for (Type t : types) {
            logger.debug(t);
        }

        Assertions.assertEquals(types[0], Float.class, "Expected type for the class doesn't match");

    }
}
