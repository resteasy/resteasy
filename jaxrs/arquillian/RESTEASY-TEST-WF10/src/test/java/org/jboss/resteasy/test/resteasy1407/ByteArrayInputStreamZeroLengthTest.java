package org.jboss.resteasy.test.resteasy1407;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.resteasy1407.ByteArrayInputStreamZeroLengthApplication;
import org.jboss.resteasy.resteasy1407.ByteArrayInputStreamZeroLengthService;
import org.jboss.resteasy.resteasy1407.ByteArrayInputStreamZeroLengthServiceImpl;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.io.ByteArrayInputStream;

/**
 * RESTEASY-1407
 *
 * @author Dmitrii Tikhomirov
 */

@RunWith(Arquillian.class)
public class ByteArrayInputStreamZeroLengthTest {

    private static final String WAR_FILE_NAME = ByteArrayInputStreamZeroLengthTest.class.getSimpleName() + ".war";
    private static final String URL = "http://127.0.0.1:8080/ByteArrayInputStreamZeroLengthTest/application/message";

    @Deployment(testable=false)
    public static Archive<?> createTestArchive() {
        final WebArchive war = ShrinkWrap.create(WebArchive.class, WAR_FILE_NAME);
        war.addClasses(ByteArrayInputStreamZeroLengthApplication.class)
                .addClasses(ByteArrayInputStreamZeroLengthService.class)
                .addClasses(ByteArrayInputStreamZeroLengthServiceImpl.class)
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        return war;
    }

    @Test
    public void testByteArrayInputStreamZeroLength() {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(URL);
        ResteasyWebTarget t = (ResteasyWebTarget)target;
        ByteArrayInputStreamZeroLengthService customerProxy = t.proxy(ByteArrayInputStreamZeroLengthService.class);
        Response response = customerProxy.upload(new ByteArrayInputStream(new byte[0]));
        response.close();
        Assert.assertEquals(200,response.getStatus());

    }
}
