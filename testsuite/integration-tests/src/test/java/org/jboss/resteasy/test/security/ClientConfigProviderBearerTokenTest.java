package org.jboss.resteasy.test.security;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * @tpSubChapter Security
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test that Bearer token is correctly loaded from ClientConfigProvider impl and used for outgoing requests.
 */
public class ClientConfigProviderBearerTokenTest {

    @Test
    public void testClientConfigProviderBearerToken() throws IOException {
        String jarPath = ClientConfigProviderTestJarHelper.createClientConfigProviderTestJarWithBearerToken();

        Process process = ClientConfigProviderTestJarHelper.runClientConfigProviderBearerTestJar(ClientConfigProviderTestJarHelper.TestType.TEST_BEARER_TOKEN_IS_USED, jarPath);
        String line = ClientConfigProviderTestJarHelper.getResultOfProcess(process);
        Assert.assertEquals("200", line);
        process.destroy();

        process = ClientConfigProviderTestJarHelper.runClientConfigProviderBearerTestJar(ClientConfigProviderTestJarHelper.TestType.TEST_BEARER_TOKEN_IGNORED_IF_BASIC_SET_BY_USER, jarPath);
        line = ClientConfigProviderTestJarHelper.getResultOfProcess(process);
        Assert.assertEquals("Credentials set by user had precedence", line);
        process.destroy();
        Assert.assertTrue(new File(jarPath).delete());
    }
}
