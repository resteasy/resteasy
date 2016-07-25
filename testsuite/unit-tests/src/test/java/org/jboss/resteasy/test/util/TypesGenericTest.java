package org.jboss.resteasy.test.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.resteasy.test.util.resource.TypesGenericBar;
import org.jboss.resteasy.test.util.resource.TypesGenericFooBar;
import org.jboss.resteasy.test.util.resource.TypesGenericSubClass;
import org.jboss.resteasy.test.util.resource.TypesGenericSub;
import org.jboss.resteasy.test.util.resource.TypesGenericAnotherFooBar;
import org.jboss.resteasy.test.util.resource.TypesGenericAnotherBar;
import org.jboss.resteasy.test.util.resource.TypesGenericClassFooBar;
import org.jboss.resteasy.test.util.resource.TypesGenericClassBar;
import org.jboss.resteasy.test.util.resource.TypesGenericClassSub;
import org.jboss.resteasy.util.Types;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * @tpSubChapter Util tests
 * @tpChapter Unit tests
 * @tpTestCaseDetails Test for Types class for generic types.
 * @tpSince RESTEasy 3.0.16
 */
public class TypesGenericTest {

    protected final Logger logger = LogManager.getLogger(TypesGenericTest.class.getName());

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

        Assert.assertEquals("Expected type for the class doesn't match", types[0], Float.class);

        types = Types.findParameterizedTypes(TypesGenericSubClass.class, TypesGenericBar.class);
        for (Type t : types) {
            logger.debug(t);
        }

        Assert.assertEquals("Expected type for the class doesn't match", types[0], Float.class);

        types = Types.findParameterizedTypes(TypesGenericSub.class, TypesGenericBar.class);
        for (Type t : types) {
            logger.debug(t);
        }

        Assert.assertEquals("Expected type for the class doesn't match", types[0], Float.class);

        types = Types.findParameterizedTypes(TypesGenericAnotherFooBar.class, TypesGenericAnotherBar.class);
        Assert.assertEquals("No parametrized type expected", 0, types.length);
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

        Assert.assertEquals("The method implemented by the class an returned by getImplementingMethod() is not the expected one",
                implented, actual);
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

        Assert.assertEquals("Expected type for the class doesn't match", types[0], Float.class);

        types = Types.findParameterizedTypes(TypesGenericClassSub.class, TypesGenericClassBar.class);
        for (Type t : types) {
            logger.debug(t);
        }

        Assert.assertEquals("Expected type for the class doesn't match", types[0], Float.class);

    }
}
