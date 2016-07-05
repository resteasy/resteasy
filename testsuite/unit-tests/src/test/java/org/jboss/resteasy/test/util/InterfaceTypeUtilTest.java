package org.jboss.resteasy.test.util;

import org.jboss.resteasy.test.util.resource.InterfaceTypeUtilC;
import org.jboss.resteasy.test.util.resource.InterfaceTypeUtilD;
import org.jboss.resteasy.test.util.resource.InterfaceTypeUtilE;
import org.jboss.resteasy.test.util.resource.InterfaceTypeUtilI;
import org.jboss.resteasy.test.util.resource.InterfaceTypeUtilJ;
import org.jboss.resteasy.util.Types;
import org.junit.Assert;
import org.junit.Test;

/**
 * @tpSubChapter Util tests
 * @tpChapter Unit tests
 * @tpTestCaseDetails Test for org.jboss.resteasy.util.Types class.
 * @tpSince EAP 7.0.0
 */
public class InterfaceTypeUtilTest {

    /**
     * @tpTestDetails Test for getTemplateParameterOfInterface method
     * @tpSince EAP 7.0.0
     */
    @Test
    public void testType() throws Exception {
        Assert.assertNull("Types util class works incorrectly", Types.getTemplateParameterOfInterface(InterfaceTypeUtilD.class, InterfaceTypeUtilJ.class));
        Assert.assertEquals("Types util class works incorrectly", InterfaceTypeUtilC.class, Types.getTemplateParameterOfInterface(InterfaceTypeUtilE.class, InterfaceTypeUtilI.class));
    }
}

