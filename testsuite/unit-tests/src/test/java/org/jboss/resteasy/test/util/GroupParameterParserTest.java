package org.jboss.resteasy.test.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.resteasy.util.GroupParameterParser;
import org.jboss.resteasy.util.ParameterParser;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Map;

/**
 * @tpSubChapter Util tests
 * @tpChapter Unit tests
 * @tpTestCaseDetails Test for GroupParameterParser and ParameterParser class.
 * @tpSince EAP 7.0.0
 */
public class GroupParameterParserTest {
    protected static final Logger logger = LogManager.getLogger(GroupParameterParserTest.class.getName());

    /**
     * @tpTestDetails Test for GroupParameterParser class.
     * @tpSince EAP 7.0.0
     */
    @Test
    public void testGroups() throws Exception {
        String params = "a=b; c=d; e=\"f,;\" , one=two; three=\"four\"";

        GroupParameterParser parser = new GroupParameterParser();
        List<Map<String, String>> groups = parser.parse(params, ';', ',');
        logger.info(String.format("Groups: %s", groups));
        Assert.assertEquals("Wrong number of groups", 2, groups.size());
    }

    /**
     * @tpTestDetails Test for ParameterParser class, base parameters are used.
     * @tpSince EAP 7.0.0
     */
    @Test
    public void testSetAttribute() throws Exception {
        String header = "v=1   ;z=33333   ;b=xxxxxxx";
        ParameterParser parser = new ParameterParser();
        String output = parser.setAttribute(header.toCharArray(), 0, header.length(), ';', "b", "");
        logger.info(String.format("Parsed output: <%s>", output));
        Assert.assertEquals("Parsed output is wrong", "v=1   ;z=33333   ;b=", output);
    }

    /**
     * @tpTestDetails Test for ParameterParser class, complex parameters are used.
     * @tpSince EAP 7.0.0
     */
    @Test
    public void testSetAttributeComplex() throws Exception {
        String header = "v=1   ;z=33333   ;b=xxxxxxx   ;   foo=bar   ";
        ParameterParser parser = new ParameterParser();
        String output = parser.setAttribute(header.toCharArray(), 0, header.length(), ';', "b", "");
        logger.info(String.format("Parsed output: <%s>", output));
        Assert.assertEquals("Parsed output is wrong", "v=1   ;z=33333   ;b=;   foo=bar   ", output);
    }

}
