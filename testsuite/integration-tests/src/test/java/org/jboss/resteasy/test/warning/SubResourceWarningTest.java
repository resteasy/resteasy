package org.jboss.resteasy.test.warning;

import static org.jboss.resteasy.test.ContainerConstants.DEFAULT_CONTAINER_QUALIFIER;

import java.util.Map;
import java.util.Set;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.resteasy.setup.LoggingSetupTask;
import org.jboss.resteasy.test.core.interceptors.resource.TestResource1;
import org.jboss.resteasy.test.core.interceptors.resource.TestResource2;
import org.jboss.resteasy.test.core.interceptors.resource.TestSubResource;
import org.jboss.resteasy.test.warning.resource.SubResourceWarningResource;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Miscellaneous
 * @tpChapter Integration tests
 * @tpSince RESTEasy 4.0.0
 *          Created by rsearls on 9/11/17.
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
@ServerSetup(SubResourceWarningTest.WarnLoggingSetupTask.class)
@Tag("NotForBootableJar")
public class SubResourceWarningTest {

    public static class WarnLoggingSetupTask extends LoggingSetupTask {
        public WarnLoggingSetupTask() {
            super(Map.of("WARN", Set.of("org.jboss.resteasy")));
        }
    }

    // check server.log msg count before app is deployed.  Deploying causes messages to be logged.
    private static int preTestCnt = TestUtil.getWarningCount("have the same path, [test", false, DEFAULT_CONTAINER_QUALIFIER);

    @Deployment
    public static Archive<?> deploySimpleResource() {
        WebArchive war = TestUtil.prepareArchive(SubResourceWarningTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, SubResourceWarningResource.class,
                TestResource1.class, TestResource2.class, TestSubResource.class);
    }

    /**
     * Confirms that 2 warning messages about this incorrect coding is printed to the server.log
     * Must check for path because warning text, RESTEASY002195, exist in log for a previous test
     * in the suite.
     *
     * @throws Exception
     */
    @Test
    public void testWarningMsg() throws Exception {
        int cnt = TestUtil.getWarningCount("have the same path, [test", false, DEFAULT_CONTAINER_QUALIFIER);
        Assertions.assertEquals(preTestCnt + 2, cnt, "Improper log WARNING count");
    }
}
