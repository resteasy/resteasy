package org.jboss.resteasy.test.util;

import org.jboss.resteasy.spi.MarshalledEntity;
import org.jboss.resteasy.test.util.resource.TypesTestProvider;
import org.jboss.resteasy.test.util.resource.TypesTestProviderSubclass;
import org.jboss.resteasy.util.Types;
import org.junit.Test;

import javax.ws.rs.ext.ExceptionMapper;
import java.lang.reflect.Type;

import static org.junit.Assert.assertEquals;

/**
 * @tpSubChapter Util tests
 * @tpChapter Unit tests
 * @tpTestCaseDetails Test for Types class.
 * @tpSince RESTEasy 3.0.16
 */
public class TypesBuiltInTest {

    /**
     * @tpTestDetails Check getActualTypeArgumentsOfAnInterface method of Types class for basic provider.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetInterfaceArgumentFromSimpleType() {
        Type[] parameters = Types.getActualTypeArgumentsOfAnInterface(TypesTestProvider.class, ExceptionMapper.class);
        assertEquals("Wrong count of parameters", 1, parameters.length);
        assertEquals("Wrong type of exception", NullPointerException.class, (Class<?>) parameters[0]);

        parameters = Types.getActualTypeArgumentsOfAnInterface(TypesTestProvider.class, MarshalledEntity.class);
        assertEquals("Wrong count of parameters", 1, parameters.length);
        assertEquals("Wrong type of parameter", Integer.class, (Class<?>) parameters[0]);
    }

    /**
     * @tpTestDetails Check getActualTypeArgumentsOfAnInterface method of Types class for provider with parent class.
     *                Provider subclasses are not defined by the spec, but we need to be able to recognize them for proxied providers to be identified.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetInterfaceArgumentFromSubclass() {
        Type[] parameters = Types.getActualTypeArgumentsOfAnInterface(TypesTestProviderSubclass.class, ExceptionMapper.class);
        assertEquals("Wrong count of parameters", 1, parameters.length);
        assertEquals("Wrong type of exception", NullPointerException.class, (Class<?>) parameters[0]);

        parameters = Types.getActualTypeArgumentsOfAnInterface(TypesTestProviderSubclass.class, MarshalledEntity.class);
        assertEquals("Wrong count of parameters", 1, parameters.length);
        assertEquals("Wrong type of parameter", Integer.class, (Class<?>) parameters[0]);
    }
}
