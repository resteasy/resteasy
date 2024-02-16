package org.jboss.resteasy.test.util;

import java.util.List;
import java.util.Map;

import org.jboss.logging.Logger;
import org.jboss.resteasy.util.GroupParameterParser;
import org.jboss.resteasy.util.ParameterParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @tpSubChapter Util tests
 * @tpChapter Unit tests
 * @tpTestCaseDetails Test for GroupParameterParser and ParameterParser class.
 * @tpSince RESTEasy 3.0.16
 */
public class GroupParameterParserTest {
    protected final Logger logger = Logger.getLogger(GroupParameterParserTest.class.getName());

    /**
     * @tpTestDetails Test for GroupParameterParser class.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGroups() throws Exception {
        String params = "a=b; c=d; e=\"f,;\" , one=two; three=\"four\"";

        GroupParameterParser parser = new GroupParameterParser();
        List<Map<String, String>> groups = parser.parse(params, ';', ',');
        logger.debug(String.format("Groups: %s", groups));
        Assertions.assertEquals(2, groups.size(), "Wrong number of groups");
    }

    /**
     * @tpTestDetails Test for ParameterParser class, base parameters are used.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testSetAttribute() throws Exception {
        String header = "v=1   ;z=33333   ;b=xxxxxxx";
        ParameterParser parser = new ParameterParser();
        String output = parser.setAttribute(header.toCharArray(), 0, header.length(), ';', "b", "");
        logger.debug(String.format("Parsed output: <%s>", output));
        Assertions.assertEquals("v=1   ;z=33333   ;b=", output, "Parsed output is wrong");
    }

    /**
     * @tpTestDetails Test for ParameterParser class, complex parameters are used.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testSetAttributeComplex() throws Exception {
        String header = "v=1   ;z=33333   ;b=xxxxxxx   ;   foo=bar   ";
        ParameterParser parser = new ParameterParser();
        String output = parser.setAttribute(header.toCharArray(), 0, header.length(), ';', "b", "");
        logger.debug(String.format("Parsed output: <%s>", output));
        Assertions.assertEquals("v=1   ;z=33333   ;b=;   foo=bar   ", output, "Parsed output is wrong");
    }

}
