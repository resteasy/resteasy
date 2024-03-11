package org.jboss.resteasy.test.util;

import org.jboss.resteasy.spi.util.Types;
import org.jboss.resteasy.test.util.resource.InterfaceTypeUtilC;
import org.jboss.resteasy.test.util.resource.InterfaceTypeUtilD;
import org.jboss.resteasy.test.util.resource.InterfaceTypeUtilE;
import org.jboss.resteasy.test.util.resource.InterfaceTypeUtilI;
import org.jboss.resteasy.test.util.resource.InterfaceTypeUtilJ;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @tpSubChapter Util tests
 * @tpChapter Unit tests
 * @tpTestCaseDetails Test for org.jboss.resteasy.spi.util.Types class.
 * @tpSince RESTEasy 3.0.16
 */
public class InterfaceTypeUtilTest {

    /**
     * @tpTestDetails Test for getTemplateParameterOfInterface method
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testType() throws Exception {
        Assertions.assertNull(Types.getTemplateParameterOfInterface(InterfaceTypeUtilD.class, InterfaceTypeUtilJ.class),
                "Types util class works incorrectly");
        Assertions.assertEquals(InterfaceTypeUtilC.class,
                Types.getTemplateParameterOfInterface(InterfaceTypeUtilE.class, InterfaceTypeUtilI.class),
                "Types util class works incorrectly");
    }
}
