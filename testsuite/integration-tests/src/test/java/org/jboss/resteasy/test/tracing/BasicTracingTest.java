package org.jboss.resteasy.test.tracing;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters;
import org.jboss.resteasy.specimpl.MultivaluedMapImpl;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@RunWith(Arquillian.class)
@RunAsClient
public class BasicTracingTest {
    static WebArchive war;

    @Deployment
    public static Archive<?> createDeployment() {
        war = TestUtil.prepareArchive(BasicTracingTest.class.getSimpleName());
        war.addAsResource(new File(BasicTracingTest.class.getClassLoader().getResource("org/jboss/resteasy/test/tracing/logging.properties").getFile()), "logging.properties");
        Map<String, String> params = new HashMap<>();
        params.put(ResteasyContextParameters.RESTEASY_TRACING_TYPE, ResteasyContextParameters.RESTEASY_TRACING_TYPE_ALL);
        params.put(ResteasyContextParameters.RESTEASY_TRACING_THRESHOLD, ResteasyContextParameters.RESTEASY_TRACING_LEVEL_VERBOSE);

        return TestUtil.finishContainerPrepare(war, params, TracingApp.class,
                TracingConfigResource.class, HttpMethodOverride.class);
    }

    @Test
    public void testBasic() {
//        System.out.println("::: " + getClass().getClassLoader().getResource("logging.properties").getFile());
//        war.as(ZipExporter.class).exportTo(new File("/tmp/" + war.getName()), true);
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

