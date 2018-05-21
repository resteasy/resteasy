package org.jboss.resteasy.test.tracing;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.test.sourceProvider.resource.Book;
import org.jboss.resteasy.test.sourceProvider.resource.BookResource;
import org.jboss.resteasy.test.sourceProvider.resource.SourceProviderApp;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

@RunWith(Arquillian.class)
@RunAsClient
public class BasicTracingTest {
    static WebArchive war;

    @Deployment
    public static Archive<?> createDeployment() {
        war = TestUtil.prepareArchive(BasicTracingTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, SourceProviderApp.class,
                BookResource.class, Book.class);
    }

    @Test
    public void testBasic() {
        war.as(ZipExporter.class).exportTo(new File("/tmp/" + war.getName()));
    }
}
