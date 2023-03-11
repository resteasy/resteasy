package org.jboss.resteasy.test.security;

import java.io.File;
import java.io.IOException;

import org.jboss.resteasy.utils.TestUtil;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;

/**
 * @tpSubChapter Security
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test that Bearer token is correctly loaded from ClientConfigProvider impl and used for outgoing requests.
 */
public class ClientConfigProviderBearerTokenTest {

    @Test
    public void testClientConfigProviderBearerToken() throws IOException {
        Assume.assumeFalse("Skip on Windows due to large class path. See RESTEASY-2992.", TestUtil.isWindows());
        String jarPath = ClientConfigProviderTestJarHelper.createClientConfigProviderTestJarWithBearerToken();

        Process process = ClientConfigProviderTestJarHelper.runClientConfigProviderBearerTestJar(
                ClientConfigProviderTestJarHelper.TestType.TEST_BEARER_TOKEN_IS_USED, jarPath);
        String line = ClientConfigProviderTestJarHelper.getResultOfProcess(process);
        Assert.assertEquals("200", line);
        process.destroy();

        process = ClientConfigProviderTestJarHelper.runClientConfigProviderBearerTestJar(
                ClientConfigProviderTestJarHelper.TestType.TEST_BEARER_TOKEN_IGNORED_IF_BASIC_SET_BY_USER, jarPath);
        line = ClientConfigProviderTestJarHelper.getResultOfProcess(process);
        Assert.assertEquals("Credentials set by user had precedence", line);
        process.destroy();
        Assert.assertTrue(new File(jarPath).delete());
    }
}
