/*
 * JBoss, Home of Professional Open Source
 * Copyright 2017, Red Hat Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.resteasy.test.response;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.management.Notification;
import javax.management.NotificationEmitter;
import javax.management.NotificationListener;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation.Builder;

import org.apache.commons.io.IOUtils;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.test.response.resource.TestResourceImpl;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.Assert;

@RunWith(Arquillian.class)
@RunAsClient
public class ResponseStreamPrematurelyClosedTest {

    static Client client;

    @Deployment
    public static Archive<?> deploy() throws Exception {
        WebArchive war = TestUtil.prepareArchive(ResponseStreamPrematurelyClosedTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, TestResourceImpl.class);
    }

    @BeforeClass
    public static void init() {
        client = ClientBuilder.newClient();
    }

    @AfterClass
    public static void after() throws Exception {
        client.close();
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, ResponseStreamPrematurelyClosedTest.class.getSimpleName());
    }

    @Test
    public void testStream() throws Exception {
        Builder builder = client.target(generateURL("/test/document/abc/content")).request();

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            if (! TestUtil.isIbmJdk()) {
                //builder.get().readEntity explicitly on the same line below and not saved in any temp variable
                //to let the JVM try finalizing the ClientResponse object
                InputStream ins = builder.get().readEntity(InputStream.class);
                //suggest jvm to do gc and wait the gc notification
                final CountDownLatch coutDown = new CountDownLatch(1);

                List<GarbageCollectorMXBean> gcbeans = ManagementFactory.getGarbageCollectorMXBeans();
                NotificationListener listener = new NotificationListener() {
                    public void handleNotification(Notification notification, Object handback) {
                        coutDown.countDown();
                    }
                };
                try {
                    for (GarbageCollectorMXBean gcbean : gcbeans) {
                        NotificationEmitter emitter = (NotificationEmitter) gcbean;
                        emitter.addNotificationListener(listener, null, null);
                    }
                    System.gc();
                    coutDown.await(10, TimeUnit.SECONDS);

                    IOUtils.copy(ins, baos);
                    Assert.assertEquals(10000000, baos.size());
                } finally {
                    //remove the listener
                    for (GarbageCollectorMXBean gcbean : gcbeans) {
                        ((NotificationEmitter) gcbean).removeNotificationListener(listener);
                    }
                }
            } else { // workaround for Ibm jdk - doesn't allow to use NotificationEmitter with GarbageCollectorMXBean
                //builder.get().readEntity explicitly on the same line below and not saved in any temp variable
                //to let the JVM try finalizing the ClientResponse object
                IOUtils.copy(builder.get().readEntity(InputStream.class), baos);
                Assert.assertEquals(10000000, baos.size());
            }
        }
    }
}
